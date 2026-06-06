package com.common.core.redis;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisMovedDataException;
import redis.clients.jedis.util.JedisClusterCRC16;
import redis.clients.jedis.util.SafeEncoder;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class JedisClusterPipeline extends PipelineBase implements Closeable {

    // 部分字段没有对应的获取方法，只能采用反射来做
    // 你也可以去继承JedisCluster和JedisSlotBasedConnectionHandler来提供访问接口
    private static final Field FIELD_CONNECTION_HANDLER;
    private static final Field FIELD_CACHE;
    static {
        FIELD_CONNECTION_HANDLER = getField(BinaryJedisCluster.class, "connectionHandler");
        FIELD_CACHE = getField(JedisClusterConnectionHandler.class, "cache");
    }

    private JedisSlotBasedConnectionHandler connectionHandler;
    private JedisClusterInfoCache clusterInfoCache;

    // 根据顺序存储每个命令对应的Client
    private Queue<Client> clients = new LinkedList<Client>();

    // 用于缓存连接
    private Map<JedisPool, Jedis> jedisMap = new HashMap<>();

    // 是否有数据在缓存区
    private boolean hasDataInBuf = false;


    public static JedisClusterPipeline pipelined(RedisTemplate redisTemplate){
        return pipelined(redisTemplate,false);
    }

    public static JedisClusterPipeline pipelined(RedisTemplate redisTemplate, boolean refreshCluster){
        JedisCluster jedisCluster= (JedisCluster)redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
        return pipelined(jedisCluster,refreshCluster);
    }

    /**
     * 根据jedisCluster实例生成对应的JedisClusterPipeline
     * @param
     * @return
     */
    public static JedisClusterPipeline pipelined(JedisCluster jedisCluster){
        return pipelined(jedisCluster,false);
    }

    /**
     * 根据jedisCluster实例生成对应的JedisClusterPipeline
     * @param
     * @return
     */
    public static JedisClusterPipeline pipelined(JedisCluster jedisCluster, boolean refreshCluster) {
        JedisClusterPipeline pipeline = new JedisClusterPipeline();
        pipeline.setJedisCluster(jedisCluster);
        if(refreshCluster){
            pipeline.refreshCluster();
        }
        return pipeline;
    }



    public JedisClusterPipeline() {
    }

    /**
     * 设置cluster信息
     * @param jedis
     */
    public void setJedisCluster(JedisCluster jedis) {
        connectionHandler = getValue(jedis, FIELD_CONNECTION_HANDLER);
        clusterInfoCache = getValue(connectionHandler, FIELD_CACHE);
    }

    /**
     * 刷新集群信息，当集群信息发生变更时调用
     * @param
     * @return
     */
    public void refreshCluster() {
        connectionHandler.renewSlotCache();
    }

    /**
     * 同步读取所有数据. 与syncAndReturnAll()相比，sync()只是没有对数据做反序列化
     */
    public void sync() {
        innerSync(null);
    }

    /**
     * 同步读取所有数据 并按命令顺序返回一个列表
     *
     * @return 按照命令的顺序返回所有的数据
     */
    public List<Object> syncAndReturnAll() {
        List<Object> responseList = new ArrayList<Object>();

        innerSync(responseList);

        return responseList;
    }

    private void innerSync(List<Object> formatted) {
        HashSet<Client> clientSet = new HashSet<Client>();

        try {
            for (Client client : clients) {
                // 在sync()调用时其实是不需要解析结果数据的，但是如果不调用get方法，发生了JedisMovedDataException这样的错误应用是不知道的，因此需要调用get()来触发错误。
                // 其实如果Response的data属性可以直接获取，可以省掉解析数据的时间，然而它并没有提供对应方法，要获取data属性就得用反射，不想再反射了，所以就这样了

                try {
                    Object data =generateResponse(client.getOne()).get();
                    if (null != formatted) {
                        List<Object> datas = (List<Object>)data;
                        formatted.addAll(datas);
                    }

                    // size相同说明所有的client都已经添加，就不用再调用add方法了
                    if (clientSet.size() != jedisMap.size()) {
                        clientSet.add(client);
                    }
                }catch (Exception e){
                    if (null != formatted) {
                        formatted.add(e.getMessage());
                    }

                    if (e instanceof JedisMovedDataException) {
                        // if MOVED redirection occurred, rebuilds cluster's slot cache,
                        // recommended by Redis cluster specification
                        refreshCluster();
                    }

                    //response 需要pop出
                    generateResponse(e.getMessage());

                    client.close();
                    log.error("pipeline error:",e);
                }
            }
        } finally {
            if (clientSet.size() != jedisMap.size()) {
                // 所有还没有执行过的client要保证执行(flush)，防止放回连接池后后面的命令被污染
                for (Jedis jedis : jedisMap.values()) {
                    if (clientSet.contains(jedis.getClient())) {
                        continue;
                    }
                    flushCachedData(jedis);
                }
            }

            hasDataInBuf = false;
            close();
        }
    }

    /**
     * 清理资源
     */
    @Override
    public void close() {
        clean();
        clients.clear();

        for (Jedis jedis : jedisMap.values()) {
            if (hasDataInBuf) {
                flushCachedData(jedis);
            }
            jedis.close();
        }

        jedisMap.clear();
        hasDataInBuf = false;
    }

    private void flushCachedData(Jedis jedis) {
        try {
            jedis.getClient().getOne();
        } catch (RuntimeException ex) {
            log.error("getOne error:",ex);
        }
    }

    /**
     * 获取jedis client
     * @param key
     * @return
     */
    @Override
    protected Client getClient(String key) {
        byte[] bKey = SafeEncoder.encode(key);
        return getClient(bKey);
    }

    /**
     * 获取jedis client
     * @param key
     * @return
     */
    @Override
    protected Client getClient(byte[] key) {
        Jedis jedis = getJedis(JedisClusterCRC16.getSlot(key));

        Client client = jedis.getClient();
        clients.add(client);

        return client;
    }

    private Jedis getJedis(int slot) {
        JedisPool pool = clusterInfoCache.getSlotPool(slot);

        // 根据pool从缓存中获取Jedis
        Jedis jedis = jedisMap.get(pool);
        if (null == jedis) {
            jedis = pool.getResource();
            jedisMap.put(pool, jedis);
        }
        hasDataInBuf = true;
        return jedis;
    }

    private static Field getField(Class<?> cls, String fieldName) {
        try {
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);

            return field;
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException("cannot find or access field '" + fieldName + "' from " + cls.getName(), e);
        }
    }

    @SuppressWarnings({"unchecked" })
    private static <T> T getValue(Object obj, Field field) {
        try {
            return (T)field.get(obj);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error("get value fail", e);

            throw new RuntimeException(e);
        }
    }

}
