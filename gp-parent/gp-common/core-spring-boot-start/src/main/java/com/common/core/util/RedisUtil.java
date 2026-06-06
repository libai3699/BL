package com.common.core.util;

import com.alibaba.fastjson.JSON;
import com.common.core.redis.JedisClusterPipeline;
import com.common.core.redis.RedisBatchOperate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gp.common.base.constant.ActivityTypeConstants;
import com.gp.common.base.utils.BigDecimalUtils;
import com.gp.common.base.utils.DateUtils;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.dynamic.batch.CommandBatching;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * redis工具类
 *
 * @author axing
 * @date 2021/08/03
 */

@Data
@Slf4j
public class RedisUtil {

    private StringRedisTemplate stringRedisTemplate;

    private RedisTemplate<String, Object> redisTemplate;

    private boolean isCluster = false;

    //代付订单提交间隔时间
    public static Long AGENT_PAY_ORDER_TIME = 600L;
    //支付订单-订单号幂等时间
    public static Long PAY_ORDER_POWER_TIME = 86400L;
    //通知 支付结果 幂等控制时间
    public static Long NOTIFY_ORDER_TIME = 86400L;


    public RedisBatchOperate redisBatchOperate;

//    private RedisBatchOperate getRedisBatchOperate() {
//        if (null == redisBatchOperate) {
//            return factory.getCommands(RedisBatchOperate.class);
//        }
//        return redisBatchOperate;
//    }

    /**
     * 查询该词的剩余时间,当词的时间小于10s的时候,延长到20s, 小于5秒的时候,直接算失效, 大于20秒的直接处理
     * 防止可以因为时间过短导致程序使用的过程中直接过期
     *
     * @param key 键
     */
    public boolean getKeyTimeAndExtend(String key) {
        Long timeLeft = redisTemplate.getExpire(key);
        if (timeLeft == null) {
            return false;
        } else if (timeLeft >= 10) {
            return true;
        } else if (timeLeft >= 5) {
            redisTemplate.expire(key, 20, TimeUnit.SECONDS);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 发布订阅
     *
     * @param topc
     * @param msg
     */
    public void convertAndSendForStr(String topc, Object msg) {
        stringRedisTemplate.convertAndSend(topc, msg);
    }

    /**
     * 发布订阅
     *
     * @param topc
     * @param msg
     */
    public void convertAndSendForObj(String topc, Object msg) {
        redisTemplate.convertAndSend(topc, msg);
    }

    /**
     * redis锁 当指定时间结束后或当前线程执行完,才能执行下一个,否则其他线程只能等待
     *
     * @param lockName 锁的名字
     * @param time     时间
     * @param timeUnit 时间单位
     * @return {@link Boolean}
     */
    @SneakyThrows
    public <T> Object tryLock(String lockName, Integer time, TimeUnit timeUnit, Supplier<T> supplier) {
        while (!checkLock(lockName, time, timeUnit)) {
            log.warn("已经有线程执行了");
            Thread.sleep(500);
        }
        Object t = supplier.get();
        removeLock(lockName);
        return t;
    }

    /**
     * redis锁,当指定时间结束后或当前线程执行完,才能执行下一个,否则其他线程不执行
     *
     * @param lockName 锁的名字
     * @param time     时间
     * @param timeUnit 时间单位
     * @return {@link Boolean}
     */
    @SneakyThrows
    public <T> Object lock(String lockName, Integer time, TimeUnit timeUnit, Supplier<T> supplier) {
        if (checkLock(lockName, time, timeUnit)) {
            Object t = supplier.get();
            removeLock(lockName);
            return t;
        } else {
            log.warn("已经有线程执行了");
            return null;
        }
    }

    /**
     * redis锁,当指定时间结束后或当前线程执行完,才能执行下一个,否则其他线程不执行
     *
     * @param lockName 锁的名字
     * @param time     时间
     * @param timeUnit 时间单位
     * @return {@link Boolean}
     */
    @SneakyThrows
    public boolean lock(String lockName, Integer time, TimeUnit timeUnit) {
        if (checkLock(lockName, time, timeUnit)) {
            return true;
        } else {
            log.warn("已经有线程执行了");
            return false;
        }
    }


    /**
     * redis锁 redis锁,当指定时间结束后,才能执行下一个,否则其他线程不执行
     *
     * @param lockName 锁的名字
     * @param time     时间
     * @param timeUnit 时间单位
     * @return {@link Boolean}
     */
    @SneakyThrows
    public <T> Object lockWaitTime(String lockName, Integer time, TimeUnit timeUnit, Supplier<T> supplier) {
        if (checkLock(lockName, time, timeUnit)) {
            Object t = supplier.get();
            return t;
        } else {
            log.warn("已经有线程执行了");
            return null;
        }

    }


    /**
     * 删除锁
     *
     * @param lockName 锁的名字
     * @return {@link Boolean}
     */
    public Boolean removeLock(String lockName) {
        return stringRedisTemplate.delete(lockName);
    }

    /**
     * redis锁
     *
     * @param lockName 锁的名字
     * @param time     时间
     * @param timeUnit 时间单位
     * @return {@link Boolean}
     */
    public Boolean checkLock(String lockName, Integer time, TimeUnit timeUnit) {
        return stringRedisTemplate.opsForValue().setIfAbsent(lockName, "0", time, timeUnit);
    }

    /**
     * 批量查询
     *
     * @param keyFields
     * @return value
     */
    public <T> List<T> batchHashGetByFields(Map<String, List<Object>> keyFields) {
        List<T> result = Lists.newArrayList();
        if (isCluster) {
            return (List<T>) batchNewHashGet(keyFields);
        } else {
            keyFields.forEach((km, vm) -> {
                List<T> res = (List<T>) redisTemplate.opsForHash().multiGet(km, vm);
                if (res.size() > 0) {
                    result.addAll(res);
                }
            });
            result.removeAll(Collections.singleton(null));
        }
        return result;
    }

    /**
     * 批量查询
     *
     * @param key
     * @param fields
     * @return value
     */
    public <T> List<T> hashGetByFields(String key, List<Object> fields) {
        List<T> result = Lists.newArrayList();
        List<T> res = (List<T>) redisTemplate.opsForHash().multiGet(key, fields);
        if (res.size() > 0) {
            result.addAll(res);
        }
        result.removeAll(Collections.singleton(null));
        return result;
    }

    /**
     * 批量查询
     *
     * @param key
     * @param field
     * @return value
     */
    public <T> T hashGet(String key, Object field) {
        return (T) redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 保存所有的 key value
     *
     * @param map
     */
    public void batchHashPutAll(Map<String, Map<String, Object>> map) {
        if (isCluster) {
            hashPutAllByPipeline(map);
        } else {
            redisTemplate.executePipelined(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection src) throws DataAccessException {
                    map.forEach((km, vm) -> {
                                Map<byte[], byte[]> bm = vm.entrySet().stream().collect(Collectors.toMap(
                                        e -> serializerKey(e.getKey()),
//                                        e -> serializerValue(e.getValue())
                                        e -> {
                                            Object value = e.getValue();
//                                            if (value != null) {
//                                                if (value instanceof String) {
//                                                    return serializerKey((String) value);
//                                                } else {
//                                                    return serializerValue(value);
//                                                }
//                                            }else {
                                            return serializerValue(value);
//                                            }

                                        }
                                ));
                                src.hMSet(serializerKey(km), bm);
                            }
                    );
                    return null;
                }
            });
        }
    }

    /**
     * 保存所有的 key value
     */
    public void batchListPutAll(String key, List<Object> values) {
        if (isCluster) {
            listPutAllByPipeline(key, values);
        } else {
            redisTemplate.executePipelined(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection src) throws DataAccessException {
                    for (Object value : values) {
                        // 修复：使用 rPush 保持顺序，使用 serializerValue 正确序列化
                        src.rPush(serializerKey(key), serializerValue(value));
                    }
                    return null;
                }
            });
        }
    }


    /**
     * 删除key
     */
    public void batchHashDel(String key, List<String> hashKeys) {
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection src) throws DataAccessException {
                List<byte[]> collect = hashKeys.stream().map(hashKey -> serializerKey(hashKey)
                ).collect(Collectors.toList());

                byte[][] hKeys = new byte[collect.size()][];
                byte[][] bytes = collect.toArray(hKeys);

                src.hDel(serializerKey(key), bytes);
                return null;
            }
        });

    }

    /**
     * 利用 pipeline 批量 get 数据
     *
     * @param keyFields
     */
    public List<Object> hashGetByPipeline(Map<String, List<Object>> keyFields) {
        JedisClusterPipeline pipeline = null;
        try {
            pipeline = JedisClusterPipeline.pipelined(redisTemplate, true);
            JedisClusterPipeline finalPipeline = pipeline;
            keyFields.forEach((km, vm) -> {
                vm.stream().forEach(v -> {
                    finalPipeline.hmget(serializerKey(km), serializerKey(v.toString()));
                });
            });
            return pipeline.syncAndReturnAll().stream().filter(o -> ObjectUtils.isNotEmpty(o)).map(o -> {
                return this.deserializerValue((byte[]) o);
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("pipeline 批量查詢 hash 失败", e);
            throw e;
        } finally {
            pipeline.close();
        }
    }

    /**
     * 利用 pipeline 批量 put 数据
     *
     * @param map
     * @param <T>
     */
    <T> void hashPutAllByPipeline(Map<String, Map<String, T>> map) {
        JedisClusterPipeline pipeline = null;
        try {
            pipeline = JedisClusterPipeline.pipelined(redisTemplate, true);
            JedisClusterPipeline finalPipeline = pipeline;
            map.forEach((km, vm) -> {
                        Map<byte[], byte[]> bm = vm.entrySet().stream().collect(Collectors.toMap(
                                //key采用的是string序列化,value采用的是json序列化
                                e -> serializerKey(e.getKey()),
                                e -> {
                                    T value = e.getValue();
//                                    if (value != null) {
//                                        if (value instanceof String) {
//                                            return serializerKey((String) value);
//                                        } else {
//                                            return serializerValue(value);
//                                        }
//                                    }else {
                                    return serializerValue(value);
//                                    }

                                }

                        ));
                        finalPipeline.hmset(serializerKey(km), bm);
                    }
            );
            pipeline.sync();
        } catch (Exception e) {
            log.error("pipeline 批量修改 hash 失败", e);
            throw e;
        } finally {
            pipeline.close();
        }
    }

    /**
     * 利用 pipeline 批量 put 数据
     */
    <T> void listPutAllByPipeline(String key, List<Object> values) {
        JedisClusterPipeline pipeline = null;
        try {
            pipeline = JedisClusterPipeline.pipelined(redisTemplate, true);
            JedisClusterPipeline finalPipeline = pipeline;
            for (Object value : values) {
                // 修复：使用 rpush 保持顺序，使用 serializerValue 正确序列化
                finalPipeline.rpush(serializerKey(key), serializerValue(value));
            }
            pipeline.sync();
        } catch (Exception e) {
            log.error("pipeline 批量修改 List 失败", e);
            throw e;
        } finally {
            pipeline.close();
        }
    }

    @SneakyThrows
    public List<Object> batchNewHashGet(Map<String, List<Object>> keyFields) {
        List<Object> results = new ArrayList<>();
        List<RedisFuture<List<Object>>> futures = new ArrayList<>();
        keyFields.forEach((km, vm) -> {
            RedisBatchOperate redisBatchOperate = getRedisBatchOperate();
            futures.add(redisBatchOperate.hmget(serializerKey(km), vm, CommandBatching.flush()));
//            commands.flush();
        });
        for (RedisFuture<List<Object>> future : futures) {
            List<Object> keyValues = future.get(10000, TimeUnit.MILLISECONDS);
            if (org.apache.commons.collections4.CollectionUtils.isEmpty(keyValues)) {
                continue;
            }
            results.addAll(keyValues);
        }
        results.removeAll(Collections.singleton(null));
        return results.stream().map(v -> {
            return deserializerValue((byte[]) v);
        }).collect(Collectors.toList());
//            connection.close();
//            client.shutdown();
    }

    /**
     * 序列化 key
     *
     * @param key
     * @return
     */
    public byte[] serializerKey(String key) {
        return ((RedisSerializer<String>) redisTemplate.getKeySerializer()).serialize(key);
    }

    /**
     * 序列化 value
     *
     * @param value
     * @return
     */
    public byte[] serializerValue(Object value) {
        return ((RedisSerializer<Object>) redisTemplate.getValueSerializer()).serialize(value);
    }

    /**
     * 反序列化 value
     *
     * @param value
     * @return
     */
    public <T> T deserializerKey(byte[] value) {
        return (T) redisTemplate.getKeySerializer().deserialize(value);
    }

    /**
     * 反序列化 value
     *
     * @param value
     * @return
     */
    public <T> T deserializerValue(byte[] value) {
        return (T) redisTemplate.getValueSerializer().deserialize(value);
    }

    /**
     * 批量查询
     *
     * @param keys
     * @return value
     */
    public List<Object> batchGetHash(List<String> keys) {
        List<Object> result = redisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection src = (StringRedisConnection) connection;
                for (String key : keys) {
                    src.get(key);
                }
                return null;
            }
        });
        return result;
    }

    // ---------------------- key操作 ---------------------

    @Deprecated
    public Collection<String> keys(String s) {
        return stringRedisTemplate.keys(s);
    }

    /**
     * 给key附加过期时间
     */
    public Boolean expire(String key, Duration timeout) {
        return stringRedisTemplate.expire(key, timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 给key附加过期时间
     */
    public Boolean expire(String key, long timeout, TimeUnit timeUnit) {
        return stringRedisTemplate.expire(key, timeout, timeUnit);
    }

    /**
     * 给key指定到期时间
     */
    public Boolean expireAt(String key, Date expireAt) {
        return stringRedisTemplate.expireAt(key, expireAt);
    }

    /**
     * 移除指定key的过期时间
     */
    public Boolean persist(String key) {
        return stringRedisTemplate.persist(key);
    }


    /**
     * 修改key名
     */
    public void rename(String key, String newKey) {
        stringRedisTemplate.rename(key, newKey);
    }

    /**
     * 修改key名，如果key不存在，将报错
     */
    public Boolean renameIfAbsent(String key, String newKey) {
        return stringRedisTemplate.renameIfAbsent(key, newKey);
    }

    /**
     * 删除一个或多个key-value <br/> 请使用 {@link #unlink}
     */
    @Deprecated
    public Long delete(Collection<String> keys) {
        return stringRedisTemplate.delete(keys);
    }

    /**
     * 删除一个或多个key-value <br/> 请使用 {@link #unlink}
     */
    @Deprecated
    public Boolean delete(String keys) {
        return stringRedisTemplate.delete(keys);
    }

    /**
     * 是否存在key
     */
    public Boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 从当前数据库中随机返回一个 key，当数据库为空时，返回null。
     */
    public String randomKey() {
        return stringRedisTemplate.randomKey();
    }

    /**
     * 返回 key 所储存的值的类型，当key不存在时，返回类型是{@link DataType "none"}
     */
    public DataType type(String key) {
        return stringRedisTemplate.type(key);
    }

    /**
     * 删除一个或多个key-value，但是，相比DEL会产生阻塞，该命令会在另一个线程中回收内存，因此它是非阻塞的。
     */
    public Long unlink(Collection<String> keys) {
        return stringRedisTemplate.unlink(keys);
    }

    /**
     * 删除一个或多个key-value，但是，相比DEL会产生阻塞，该命令会在另一个线程中回收内存，因此它是非阻塞的。
     */
    public Boolean unlink(String keys) {
        return stringRedisTemplate.unlink(keys);
    }

    // ---------------------- string操作 ---------------------

    /**
     * 设置 String 类型 key-value
     */
    public void strSet(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置 String 类型 key-value 并添加过期时间
     *
     * @param timeout 过期时间
     */
    public void strSet(String key, String value, Duration timeout) {
        stringRedisTemplate.opsForValue().set(key, value, timeout);
    }

    /**
     * 设置 String 类型 key-value，将 value 设置到指定的偏移量上
     *
     * @see <a href="http://doc.redisfans.com/string/setrange.html">Document：SETRANGE</a>
     */
    public void strSetRange(String key, String value, long offset) {
        stringRedisTemplate.opsForValue().set(key, value, offset);
    }

    /**
     * 只在键 key 不存在的情况下，将键 key 的值设置为 value 。<br/> 若键 key 已经存在，则不做任何动作。
     */
    public Boolean strSetIfAbsent(String key, String value) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 只在键 key 不存在的情况下，将键 key 的值设置为 value 。<br/> 若键 key 已经存在， 则不做任何动作。<br/>添加过期时间
     *
     * @param timeout 过期时间
     */
    public Boolean strSetIfAbsent(String key, String value, Duration timeout) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeout);
    }

    /**
     * 对 key 所储存的字符串值，获取指定偏移量上的位(bit)。 <br/> 当 offset 比字符串值的长度大，或者 key 不存在时，返回false
     *
     * @see <a href="http://doc.redisfans.com/string/getbit.html">Document：GETBIT</a>
     */
    public Boolean strSetBit(String key, long offset, boolean value) {
        return stringRedisTemplate.opsForValue().setBit(key, offset, value);
    }

    /**
     * 获取 String 类型 key-value
     */
    public String strGet(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 获取 String 类型 value 指定的偏移量
     *
     * @see <a href="http://doc.redisfans.com/string/getrange.html">Document：GETRANGE</a>
     */
    public String strGetRange(String key, long start, long end) {
        return stringRedisTemplate.opsForValue().get(key, start, end);
    }

    /**
     * 对 key 所储存的字符串值，获取指定偏移量上的位(bit)。 <br/> 当 offset 比字符串值的长度大，或者 key 不存在时，返回false
     *
     * @see <a href="http://doc.redisfans.com/string/getbit.html">Document：GETBIT</a>
     */
    public Boolean strGetBit(String key, long offset) {
        return stringRedisTemplate.opsForValue().getBit(key, offset);
    }

    /**
     * 如果 key 存在则覆盖，并返回旧值，如果不存在，返回null并添加
     */
    public String strGetAndSet(String key, String value) {
        return stringRedisTemplate.opsForValue().getAndSet(key, value);
    }

    /**
     * 把Redis字符串当作位数组，并能对变长位宽和任意未字节对齐的指定整型位域进行寻址。
     *
     * @see <a href="http://www.redis.cn/commands/bitfield.html">Document：BITFIELD</a>
     * @since Redis Version: 3.2.0
     */
    public List<Long> strBitField(String key, BitFieldSubCommands command) {
        return stringRedisTemplate.opsForValue().bitField(key, command);
    }

    /**
     * 将返回一个列表，列表中包含了所有给定键的值，如果某个键不存在，那么这个键的值将以null表示
     */
    public List<String> strMGet(Collection<String> keys) {
        return stringRedisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 批量添加 key-value (重复的键会覆盖)
     */
    public void strMSet(Map<String, String> keyAndValue) {
        stringRedisTemplate.opsForValue().multiSet(keyAndValue);
    }

    /**
     * 批量添加 key-value 只有在键不存在时，才添加 map 中只要有一个key存在，则全部不添加
     */
    public Boolean strMSetIfAbsent(Map<String, String> keyAndValue) {
        return stringRedisTemplate.opsForValue().multiSetIfAbsent(keyAndValue);
    }

    /**
     * 对一个 key-value 的值进行加 1 操作，如果该 key 不存在 将创建一个key 并赋值 1 如果 key 储存的值不能被解释为数字，将报错
     */
    public Long strIncrement(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    /**
     * 对一个 key-value 的值进行加操作，如果该 key 不存在 将创建一个key 并赋值该 number
     */
    public Long strIncrement(String key, long number) {
        return stringRedisTemplate.opsForValue().increment(key, number);
    }

    /**
     * 对一个 key-value 的值进行加操作，如果该 key 不存在 将创建一个key 并赋值该 number
     */
    public Double strIncrement(String key, double number) {
        return stringRedisTemplate.opsForValue().increment(key, number);
    }

    /**
     * 对一个 key-value 的值进行减1操作，如果该 key 不存在 将创建一个key 并赋值1 如果 key 储存的值不能被解释为数字，将报错
     */
    public Long strDecrement(String key) {
        return stringRedisTemplate.opsForValue().decrement(key);
    }

    /**
     * 对一个 key-value 的值进行加操作，如果该 key 不存在 将创建一个key 并赋值1 如果 key 储存的值不能被解释为数字，将报错
     */
    public Long strDecrement(String key, long number) {
        return stringRedisTemplate.opsForValue().decrement(key, number);
    }

    /**
     * 对一个 key-value 的值进行追加操作，如果该 key 不存在 将创建一个key 并赋值value，返回value的长度
     */
    public Integer strAppend(String key, String value) {
        return stringRedisTemplate.opsForValue().append(key, value);
    }

    /**
     * 返回 key-value 的值的长度，如果该 key 不存在，返回 0
     */
    public Long strSize(String key) {
        return stringRedisTemplate.opsForValue().size(key);
    }

    // ---------------------- hash操作 ---------------------

    /**
     * 添加 Hash 键值对
     */
    public void hSet(String key, Object hashKey, Object value) {
        stringRedisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 批量添加 hash 的 键值对 有则覆盖,没有则添加
     */
    public void hMSet(String key, Map<?, ?> map) {
        stringRedisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 添加 hash 键值对. 不存在的时候才添加
     */
    public Boolean hSetIfAbsent(String key, Object hashKey, Object value) {
        return stringRedisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }

    /**
     * 删除指定 hash 的 HashKey
     *
     * @return 删除成功的 数量
     */
    public Long hDelete(String key, Object... hashKeys) {
        return stringRedisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 给指定 hash 的 hashkey 做加操作
     */
    public Long hIncrement(String key, Object hashKey, long number) {
        return stringRedisTemplate.opsForHash().increment(key, hashKey, number);
    }

    /**
     * 给指定 hash 的 hashkey 做加Flat操作
     */
    public Double hIncrement(String key, Object hashKey, Double number) {
        return stringRedisTemplate.opsForHash().increment(key, hashKey, number);
    }

    /**
     * 获取指定 key 下的 hashkey
     */
    public Object hGet(String key, Object hashKey) {
        return stringRedisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 获取指定 key 下的 hashkey 的值，如果 hashkey 不存在，则值会是 null
     */
    public List<Object> hMGet(String key, Collection<Object> hashKeys) {
        return stringRedisTemplate.opsForHash().multiGet(key, hashKeys);
    }

    /**
     * string获取 key 下的 所有  hashkey 和 value
     */

//    @SneakyThrows
//    public Map<Object, Object> hGetAll(String key) {
//        byte[] b=redisTemplate.dump(key);
//        RdbHashParser parser = new RdbHashParser(b);
//        return parser.read(null);
//    }
    @SneakyThrows
    public Map<Object, Object> hGetAll(String key) {
        Map<Object, Object> model;
        try (Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(key, ScanOptions.scanOptions().count(50).build())) {
            model = Maps.newHashMap();
            while (cursor.hasNext()) {
                Map.Entry<Object, Object> entry = cursor.next();
                model.put(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            throw e;
        }
        return model;
    }

    @SneakyThrows
    public <T> Map<Object, T> hGetAll(String key, Class<T> clazz) {
        Map<Object, T> model;
        try (Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(key, ScanOptions.scanOptions().count(50).build())) {
            model = Maps.newHashMap();
            while (cursor.hasNext()) {
                Map.Entry<Object, Object> entry = cursor.next();
                model.put(entry.getKey(), (T) entry.getValue());
            }
        } catch (Exception e) {
            throw e;
        }
        return model;
    }

    /**
     * 验证指定 key 下 有没有指定的 hashkey
     */
    public Boolean hExists(String key, Object hashKey) {
        return stringRedisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * 获取 key 下的 所有 hashkey 字段名 <br/> 已过期！请使用 {@link #hScan}
     */
    @Deprecated
    public Set<Object> hKeys(String key) {
        return stringRedisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取指定 hash 下面的 键值对 数量
     */
    public Long hSize(String key) {
        return stringRedisTemplate.opsForHash().size(key);
    }

    /**
     * 增量迭代 hash 下面的 键值对
     */
    public Cursor<Map.Entry<Object, Object>> hScan(String key, ScanOptions options) {
        return stringRedisTemplate.opsForHash().scan(key, options);
    }

    // ---------------------- list操作 ---------------------

    /**
     * 指定 list 从左入栈
     *
     * @return 当前队列的长度
     */
    public Long lLeftPush(String key, String value) {
        return stringRedisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * <p>
     * 将值 value 插入到列表 key 当中，位于值 pivot 之前。<br/> 当 pivot 不存在于列表 key 时，不执行任何操作。<br/> 当 key 不存在时， key 被视为空列表，不执行任何操作。<br/> 如果 key
     * 不是列表类型，返回一个错误。
     * </p>
     *
     * @return 当前队列的长度
     */
    public Long lLeftPush(String key, String pivot, String value) {
        return stringRedisTemplate.opsForList().leftPush(key, pivot, value);
    }

    /**
     * 从左边依次入栈
     */
    public Long lLeftPushAll(String key, String... values) {
        return stringRedisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 从左边依次入栈 导入顺序按照 Collection 顺序 如: a b c => c b a
     */
    public Long lLeftPushAll(String key, Collection<String> values) {
        return stringRedisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 指定 list 从左入栈，当 key 不存在时，什么也不做，返回 0 。
     *
     * @return 当前队列的长度
     */
    public Long leftPushIfPresent(String key, String value) {
        return stringRedisTemplate.opsForList().leftPushIfPresent(key, value);
    }

    /**
     * 指定 list 从左出栈 如果列表没有元素，会堵塞到列表一直有元素或者超时为止
     *
     * @return 出栈的值
     */
    public String lLeftPop(String key) {
        return stringRedisTemplate.opsForList().leftPop(key);
    }

    /**
     * 指定 list 从左出栈 如果列表没有元素，会堵塞到列表一直有元素或者超时为止
     *
     * @return 出栈的值
     */
    public Object lleftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }



    public List<Object> lPopMultiple(String key, Long count) {
        List<Object> result = new ArrayList<>();
        for (long i = 0; i < count; i++) {
            Object element = redisTemplate.opsForList().leftPop(key);
            if (element == null) {
                // 如果没有更多元素，可以提前结束
                break;
            }
            result.add(element);
        }
        return result;
    }

    /**
     * 指定 list 从左出栈 如果列表没有元素，会堵塞到列表一直有元素或者超时为止
     *
     * @return 出栈的值
     */
    public String lLeftPop(String key, Duration timeout) {
        return stringRedisTemplate.opsForList().leftPop(key, timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 指定 list 从右入栈
     *
     * @return 当前队列的长度
     */
    public Long lRightPush(String key, String value) {
        return stringRedisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * <p>
     * 将值 value 插入到列表 key 当中，位于值 pivot 之后。<br/> 当 pivot 不存在于列表 key 时，不执行任何操作。<br/> 当 key 不存在时， key 被视为空列表，不执行任何操作。<br/> 如果 key
     * 不是列表类型，返回一个错误。
     * </p>
     *
     * @return 当前队列的长度
     */
    public Long lRightPush(String key, String pivot, String value) {
        return stringRedisTemplate.opsForList().rightPush(key, pivot, value);
    }

    /**
     * 指定 list 从右入栈，当 key 不存在时，什么也不做，返回 0 。
     *
     * @return 当前队列的长度
     */
    public Long rightPushIfPresent(String key, String value) {
        return stringRedisTemplate.opsForList().rightPushIfPresent(key, value);
    }

    /**
     * 从右边依次入栈
     */
    public Long lRightPushAll(String key, String... values) {
        return stringRedisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 从右边依次入栈 导入顺序按照 Collection 顺序 如: a b c => a b c
     */
    public Long lRightPushAll(String key, Collection<String> values) {
        return stringRedisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 从右边依次入栈 导入顺序按照 Collection 顺序 如: a b c => a b c
     */
    public Long lrightPushAll(String key, Collection<?> values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 指定 list 从右出栈 如果列表没有元素，会堵塞到列表一直有元素或者超时为止
     *
     * @return 出栈的值
     */
    public String lRightPop(String key) {
        return stringRedisTemplate.opsForList().rightPop(key);
    }

    /**
     * 指定 list 从右出栈 如果列表没有元素，会堵塞到列表一直有元素或者超时为止
     *
     * @return 出栈的值
     */
    public String lRightPop(String key, Duration timeout) {
        return stringRedisTemplate.opsForList().rightPop(key, timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 根据下标获取值
     */
    public String lIndex(String key, long index) {
        return stringRedisTemplate.opsForList().index(key, index);
    }

    /**
     * 将列表 key 下标为 index 的元素的值设置为 value 。<br/> 当 index 参数超出范围，或对一个空列表( key 不存在)进行 LSET 时，返回false。
     */
    public void lSet(String key, long index, String value) {
        stringRedisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 获取指定列表数量
     */
    public Long lSize(String key) {
        return stringRedisTemplate.opsForList().size(key);
    }

    /**
     * 获取列表 指定下标内的所有值
     */
    public List<String> lRange(String key, long start, long end) {
        return stringRedisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 使用 SCAN 命令实现分页获取 Redis 键，并显式关闭 Cursor
     *
     * @param pattern  匹配的模式
     * @param page     页码，从 1 开始
     * @param pageSize 每页的大小
     * @return 符合条件的键列表
     */
    public List<String> scanKeysWithPagination(String pattern, int page, int pageSize) {
        List<String> result = new ArrayList<>();
        int keysToSkip = (page - 1) * pageSize;  // 要跳过的键数量

        // 创建 ScanOptions
        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(1000)  // 每次扫描返回的最大键数，可以根据数据量调整
                .build();

        Cursor<byte[]> cursor = null;
        try {
            // 执行 SCAN 操作，获取 Cursor
            cursor = stringRedisTemplate.executeWithStickyConnection(redisConnection ->
                    redisConnection.scan(options));

            if (cursor != null) {
                int currentKeyCount = 0;
                while (cursor.hasNext()) {
                    String key = new String(cursor.next());

                    // 跳过不属于当前页的键
                    if (currentKeyCount < keysToSkip) {
                        currentKeyCount++;
                        continue;
                    }

                    // 将符合条件的键添加到结果中，直到收集满一页
                    if (result.size() < pageSize) {
                        result.add(key);
                    } else {
                        break;
                    }
                }
            }

        } catch (Exception e) {
            // 捕获异常并记录日志
            System.err.println("Error during SCAN operation: " + e.getMessage());
        } finally {
            // 显式关闭 Cursor
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    System.err.println("Error closing Redis Cursor: " + e.getMessage());
                }
            }
        }

        return result;
    }

    /**
     * 删除 key 中 值为 value 的 count 个数.
     *
     * @return 成功删除的个数
     */
    public Long lDelete(String key, long count, Object value) {
        return stringRedisTemplate.opsForList().remove(key, count, value);
    }

    /**
     * 删除 列表 [start,end] 以外的所有元素
     */
    public void lTrim(String key, long start, long end) {
        stringRedisTemplate.opsForList().trim(key, start, end);
    }

    /**
     * 将 sourceKey 右出栈,并左入栈到 destinationKey
     *
     * @param sourceKey      右出栈的列表
     * @param destinationKey 左入栈的列表
     * @return 操作的值
     */
    public String lRightPopAndLeftPush(String sourceKey, String destinationKey) {
        return stringRedisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey);
    }

    /**
     * 将 sourceKey 右出栈,并左入栈到 destinationKey，{@link #lRightPopAndLeftPush}的阻塞版本
     *
     * @param sourceKey      右出栈的列表
     * @param destinationKey 左入栈的列表
     * @param timeout        等待超时的时间
     * @return 操作的值
     */
    public String lRightPopAndLeftPush(String sourceKey, String destinationKey, Duration timeout) {
        return stringRedisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey, timeout.toMillis(),
                TimeUnit.MILLISECONDS);
    }

    // ---------------------- set操作 无序不重复集合 ---------------------

    /**
     * 添加 set 元素
     */
    public Long sAdd(String key, String... values) {
        return stringRedisTemplate.opsForSet().add(key, values);
    }

    /**
     * 获取两个集合的差集
     */
    public Set<String> sDifference(String key, String otherkey) {
        return stringRedisTemplate.opsForSet().difference(key, otherkey);
    }

    /**
     * 获取 key 和 集合  collections 中的 key 集合的差集
     */
    public Set<String> sDifference(String key, Collection<String> otherKeys) {
        return stringRedisTemplate.opsForSet().difference(key, otherKeys);
    }

    /**
     * 将  key 与 otherkey 的差集 ,添加到新的 destKey 集合中
     *
     * @return 返回差集的数量
     */
    public Long sDifferenceAndStore(String key, String otherkey, String destKey) {
        return stringRedisTemplate.opsForSet().differenceAndStore(key, otherkey, destKey);
    }

    /**
     * 将 key 和 集合  collections 中的 key 集合的差集 添加到  destKey 集合中
     *
     * @return 返回差集的数量
     */
    public Long sDifferenceAndStore(String key, Collection<String> otherKeys, String destKey) {
        return stringRedisTemplate.opsForSet().differenceAndStore(key, otherKeys, destKey);
    }

    /**
     * 删除一个或多个集合中的指定值
     *
     * @return 成功删除数量
     */
    public Long sRemove(String key, Object... values) {
        return stringRedisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 随机移除一个元素,并返回出来
     */
    public String sRandomPop(String key) {
        return stringRedisTemplate.opsForSet().pop(key);
    }

    /**
     * 随机移除 count 个元素,并返回出来
     */
    public List<String> sRandomPop(String key, long count) {
        return stringRedisTemplate.opsForSet().pop(key, count);
    }

    /**
     * 随机获取一个元素
     */
    public String sRandom(String key) {
        return stringRedisTemplate.opsForSet().randomMember(key);
    }

    /**
     * 随机获取指定数量的元素,同一个元素可能会选中两次
     */
    public List<String> sRandom(String key, long count) {
        return stringRedisTemplate.opsForSet().randomMembers(key, count);
    }

    /**
     * 随机获取指定数量的元素,去重(同一个元素只能选择两一次)
     */
    public Set<String> sRandomDistinct(String key, long count) {
        return stringRedisTemplate.opsForSet().distinctRandomMembers(key, count);
    }

    /**
     * 将 key 中的 value 转入到 destKey 中
     *
     * @return 返回成功与否
     */
    public Boolean sMove(String key, String value, String destKey) {
        return stringRedisTemplate.opsForSet().move(key, value, destKey);
    }

    /**
     * 集合的数量。当集合 key 不存在时，返回 0 。
     */
    public Long sSize(String key) {
        return stringRedisTemplate.opsForSet().size(key);
    }

    /**
     * 判断 set 集合中 是否有 value
     */
    public Boolean sIsMember(String key, String value) {
        return stringRedisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 返回 key 和 othere 的并集
     */
    public Set<String> sUnion(String key, String otherKey) {
        return stringRedisTemplate.opsForSet().union(key, otherKey);
    }

    /**
     * 返回 key 和 otherKeys 的并集
     *
     * @param otherKeys key 的集合
     */
    public Set<String> sUnion(String key, Collection<String> otherKeys) {
        return stringRedisTemplate.opsForSet().union(key, otherKeys);
    }

    /**
     * 将 key 与 otherKey 的并集,保存到 destKey 中
     *
     * @return destKey 数量
     */
    public Long sUnionAndStore(String key, String otherKey, String destKey) {
        return stringRedisTemplate.opsForSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * 将 key 与 otherKey 的并集,保存到 destKey 中
     *
     * @return destKey 数量
     */
    public Long sUnionAndStore(String key, Collection<String> otherKeys, String destKey) {
        return stringRedisTemplate.opsForSet().unionAndStore(key, otherKeys, destKey);
    }

    /**
     * 返回 key 和 otherKeys 的交集
     */
    public Set<String> sIntersect(String key, String otherKey) {
        return stringRedisTemplate.opsForSet().intersect(key, otherKey);
    }

    /**
     * 返回 key 和 otherKeys 的交集
     *
     * @param otherKeys key 的集合
     */
    public Set<String> sIntersect(String key, Collection<String> otherKeys) {
        return stringRedisTemplate.opsForSet().intersect(key, otherKeys);
    }

    /**
     * 将 key 与 otherKey 的交集,保存到 destKey 中
     *
     * @return destKey 数量
     */
    public Long sIntersectAndStore(String key, String otherKey, String destKey) {
        return stringRedisTemplate.opsForSet().intersectAndStore(key, otherKey, destKey);
    }

    /**
     * 将 key 与 otherKey 的交集,保存到 destKey 中
     *
     * @return destKey 数量
     */
    public Long sIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
        return stringRedisTemplate.opsForSet().intersectAndStore(key, otherKeys, destKey);
    }

    /**
     * 返回集合中所有元素
     */
    public Set<String> sMembers(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    /**
     * 增量迭代返回集合中所有元素
     */
    public Cursor<String> sScan(String key, ScanOptions options) {
        return stringRedisTemplate.opsForSet().scan(key, options);
    }

    // ---------------------- zset操作 根据 socre 排序 ---------------------

    /**
     * 添加 ZSet 元素
     */
    public Boolean zAdd(String key, String value, double score) {
        return stringRedisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 批量添加 ZSet 元素 <br/>
     * <code>
     * Set tupless = new HashSet<>(); <br/> ZSetOperations.TypedTuple objectTypedTuple1 = new DefaultTypedTuple<>("zset-5",9.6);
     * <br/> tupless.add(objectTypedTuple1); <br/> ZSetOperations.TypedTuple objectTypedTuple2 = new
     * DefaultTypedTuple<>("zset-6",9.5); <br/> tupless.add(objectTypedTuple2);
     * </code>
     */
    public Long zAddAll(String key, Set<ZSetOperations.TypedTuple<String>> tuples) {
        return stringRedisTemplate.opsForZSet().add(key, tuples);
    }

    /**
     * Zset 删除一个或多个元素
     */
    public Long zRemove(String key, Object... values) {
        return stringRedisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 对指定的 zset 的 value 值 , socre 属性做增减操作
     */
    public Double zIncrementScore(String key, String value, double score) {
        return stringRedisTemplate.opsForZSet().incrementScore(key, value, score);
    }

    /**
     * 获取 key 中指定 value 的排名(从0开始,从小到大排序)
     */
    public Long zRank(String key, Object value) {
        return stringRedisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 获取 key 中指定 value 的排名(从0开始,从大到小排序)
     */
    public Long zReverseRank(String key, Object value) {
        return stringRedisTemplate.opsForZSet().reverseRank(key, value);
    }

    /**
     * 获取索引区间内的排序结果集合(从0开始,从小到大,只有列名)
     */
    public Set<String> zRange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取索引区间内的排序结果集合(从0开始,从小到大,带上分数)
     */
    public Set<ZSetOperations.TypedTuple<String>> zRangeWithScores(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    /**
     * 返回从小到大分数范围内的元素不带分数的集合
     */
    public Set<String> zRangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 返回从小到大分数范围内 指定 count 数量的元素不带分数的集合
     */
    public Set<String> zRangeByScore(String key, double min, double max, long offset, long count) {
        return stringRedisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
    }

    /**
     * 获取从小到大分数范围内的 [min,max] 的排序结果集合
     */
    public Set<ZSetOperations.TypedTuple<String>> zRangeByScoreWithScores(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
    }

    /**
     * 返回从小到大分数范围内 指定 count 数量的元素集合
     */
    public Set<ZSetOperations.TypedTuple<String>> zRangeByScoreWithScores(String key, double min, double max, long offset,
                                                                          long count) {
        return stringRedisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max, offset, count);
    }

    /**
     * 获取索引区间内的排序结果集合(从0开始,从大到小,只有列名)
     */
    public Set<String> zReverseRange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 获取分数范围内的 [min,max] 的排序结果集合 (从大到小,集合不带分数)
     */
    public Set<String> zReverseRangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }

    /**
     * 返回 分数范围内 指定 count 数量的元素集合, 并且从 offset 下标开始(从大到小,不带分数的集合)
     */
    public Set<String> zReverseRangeByScore(String key, double min, double max, long offset, long count) {
        return stringRedisTemplate.opsForZSet().reverseRangeByScore(key, min, max, offset, count);
    }

    /**
     * 获取索引区间内的排序结果集合(从0开始,从大到小,带上分数)
     */
    public Set<ZSetOperations.TypedTuple<String>> zReverseRangeWithScores(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    /**
     * 获取分数范围内的 [min,max] 的排序结果集合 (从大到小,集合带分数)
     */
    public Set<ZSetOperations.TypedTuple<String>> zReverseRangeByScoreWithScores(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max);
    }

    /**
     * 返回 分数范围内 指定 count 数量的元素集合, 并且从 offset 下标开始(从大到小,带分数的集合)
     */
    public Set<ZSetOperations.TypedTuple<String>> zReverseRangeByScoreWithScores(String key, double min, double max,
                                                                                 long offset, long count) {
        return stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max, offset, count);
    }

    /**
     * 返回指定分数区间 [min,max] 的元素个数
     */
    public Long zCount(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().count(key, min, max);
    }

    /**
     * 返回 zset 集合数量
     */
    public Long zSize(String key) {
        return stringRedisTemplate.opsForZSet().size(key);
    }

    /**
     * 获取指定成员的 score 值
     */
    public Double zScore(String key, Object value) {
        return stringRedisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 删除指定索引位置的成员,其中成员分数按( 从小到大 )
     */
    public Long zRemoveRange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().removeRange(key, start, end);
    }

    /**
     * 删除指定 分数范围 内的成员 [main,max],其中成员分数按( 从小到大 )
     */
    public Long zRemoveRangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    /**
     * key 和 other 两个集合的并集,保存在 destKey 集合中, 列名相同的 score 相加
     */
    public Long zUnionAndStore(String key, String otherKey, String destKey) {
        return stringRedisTemplate.opsForZSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * key 和 otherKeys 多个集合的并集,保存在 destKey 集合中, 列名相同的 score 相加
     */
    public Long zUnionAndStore(String key, Collection<String> otherKeys, String destKey) {
        return stringRedisTemplate.opsForZSet().unionAndStore(key, otherKeys, destKey);
    }

    /**
     * key 和 otherKeys 多个集合的并集,保存在 destKey 集合中, 列名相同的 score 相加
     */
    public Long zUnionAndStore(String key, Collection<String> otherKeys, String destKey,
                               RedisZSetCommands.Aggregate aggregate) {
        return stringRedisTemplate.opsForZSet().unionAndStore(key, otherKeys, destKey, aggregate);
    }

    /**
     * key 和 otherKeys 多个集合的并集,保存在 destKey 集合中, 列名相同的 score 相加
     */
    public Long zUnionAndStore(String key, Collection<String> otherKeys, String destKey,
                               RedisZSetCommands.Aggregate aggregate, RedisZSetCommands.Weights weights) {
        return stringRedisTemplate.opsForZSet().unionAndStore(key, otherKeys, destKey, aggregate, weights);
    }

    /**
     * key 和 otherKey 两个集合的交集,保存在 destKey 集合中
     */
    public Long zIntersectAndStore(String key, String otherKey, String destKey) {
        return stringRedisTemplate.opsForZSet().intersectAndStore(key, otherKey, destKey);
    }

    /**
     * key 和 otherKeys 多个集合的交集,保存在 destKey 集合中
     */
    public Long zIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
        return stringRedisTemplate.opsForZSet().intersectAndStore(key, otherKeys, destKey);
    }

    /**
     * key 和 otherKeys 多个集合的交集,保存在 destKey 集合中
     */
    public Long zIntersectAndStore(String key, Collection<String> otherKeys, String destKey,
                                   RedisZSetCommands.Aggregate aggregate) {
        return stringRedisTemplate.opsForZSet().intersectAndStore(key, otherKeys, destKey, aggregate);
    }

    /**
     * key 和 otherKeys 多个集合的交集,保存在 destKey 集合中
     */
    public Long zIntersectAndStore(String key, Collection<String> otherKeys, String destKey,
                                   RedisZSetCommands.Aggregate aggregate, RedisZSetCommands.Weights weights) {
        return stringRedisTemplate.opsForZSet().intersectAndStore(key, otherKeys, destKey, aggregate, weights);
    }


    public RedisConnectionFactory getConnectionFactory() {
        return stringRedisTemplate.getConnectionFactory();
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }


    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }


    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 判断是否为基础类型（String/Number/Boolean/Character）
     * 序列化器会对这些类型直接存纯字符串，无需 JSON 序列化
     */
    public static boolean isBasicType(Object value) {
        return value instanceof String
                || value instanceof Number
                || value instanceof Boolean
                || value instanceof Character;
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */

    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */

    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, timeUnit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }


    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }


    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     */
    public Object hget(String key, String item) {
        try {
            return redisTemplate.opsForHash().get(key, item);
        } catch (Exception e) {
            return hGet(key, item);
        }
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     */
    public <T> boolean hmset(String key, Map<String, T> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public <T> boolean hmset(String key, Map<String, T> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }


    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }


    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }


    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }


    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            return count == null ? 0 : count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0)
                expire(key, time);
            return count == null ? 0 : count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 获取set缓存的长度
     *
     * @param key 键
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */

    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取list缓存的长度
     *
     * @param key 键
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     */
    public boolean rSet(String key, Object value) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0)
                expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public <T> boolean lSetList(String key, List<T> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0)
                expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, long time, TimeUnit timeUnit, Collection value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0)
                expire(key, time, timeUnit);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */

    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */

    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    /**
     * redis 幂等锁
     *
     * @param redisKey 锁的名字
     * @param timeout  过期时间 秒
     * @return {@link Boolean}
     */
    public Boolean idempotenceLock(String redisKey, Long timeout) {
        return stringRedisTemplate.opsForValue().setIfAbsent(redisKey, String.valueOf(System.currentTimeMillis()), timeout, TimeUnit.SECONDS);
    }

    public void deletePatternMatchingKeys(String pattern) {
        log.info("deletePatternMatchingKeys, pattern: {}", pattern);
        // count 200：每次 SCAN 处理 200 个 key 平衡 RTT 次数与单次 CPU 占用
        // 原值 1000 在大库或集群下单次 RTT 仍可能 >50ms 引发抖动
        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(200)
                .build();
        long totalDeleted = 0;
        Cursor<byte[]> cursor = null;
        try {
            cursor = stringRedisTemplate.executeWithStickyConnection(
                    redisConnection -> redisConnection.scan(options));
            if (cursor != null) {
                List<String> batch = new ArrayList<>(500);
                while (cursor.hasNext()) {
                    batch.add(new String(cursor.next()));
                    if (batch.size() >= 500) {
                        stringRedisTemplate.delete(batch);
                        totalDeleted += batch.size();
                        batch.clear();
                    }
                }
                if (!batch.isEmpty()) {
                    stringRedisTemplate.delete(batch);
                    totalDeleted += batch.size();
                }
            }
        } catch (Exception e) {
            log.error("deletePatternMatchingKeys error, pattern: {}", pattern, e);
        } finally {
            if (cursor != null) {
                try { cursor.close(); } catch (Exception ignored) {}
            }
        }
        if (totalDeleted > 0) {
            log.info("deletePatternMatchingKeys, deleted {} keys matching pattern '{}'", totalDeleted, pattern);
        } else {
            log.warn("deletePatternMatchingKeys, no keys found matching pattern '{}'", pattern);
        }
    }

    // 获取存储在 Redis 中的列表
    public List<String> getList(String key) {
        try {
            List<Object> objects = redisTemplate.opsForList().range(key, 0, -1);
            return objects.stream()
                    .map(Object::toString) // 转换为字符串
                    .collect(Collectors.toList()); // 收集为 List<String>
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception as per your application's requirements
        }
        return null;
    }

    public void storeList(String key, List<String> values) {
        redisTemplate.delete(key);
        values.forEach(e -> {
            redisTemplate.opsForList().rightPush(key, e);
        });

    }




    /**
     * @param hashMaps type  1 每日, 2 永久 3 周 4 月  key redis key
     * @param amount
     */

    public void updateRedisValues(List<HashMap<String, String>> hashMaps, BigDecimal amount) {

        hashMaps.stream().forEach(map -> {
            String type = map.get("type");
            Integer typeInt = Integer.parseInt(type);
            String key = map.get("key");
            Long todayEndTime = 0l;
            //处理下首冲逻辑
            if (ActivityTypeConstants.DAY.equals(typeInt)) {
                todayEndTime = DateUtils.getTodayEndTime();
            }

            if (ActivityTypeConstants.WEEK.equals(typeInt)) {
                todayEndTime = DateUtils.getWeekEndTime();
            }
            if (ActivityTypeConstants.MONTH.equals(typeInt)) {
                todayEndTime = DateUtils.getMonthEndTime();
            }
            if (ActivityTypeConstants.First24.equals(typeInt)) {
                todayEndTime = DateUtils.getTodayEndTime();
            }


            Object totalRedis = stringRedisTemplate.opsForValue().get(key);
            if (totalRedis == null) {
                if (ActivityTypeConstants.FOREVER.equals(typeInt)||ActivityTypeConstants.First.equals(typeInt)||ActivityTypeConstants.Second.equals(typeInt)||ActivityTypeConstants.three.equals(typeInt)) {

                    //充值的要看看
                    stringRedisTemplate.opsForValue().set(key, BigDecimalUtils.trim(amount));
                } else {
                    // 设置过期时间
                    //打印日志
                    log.info("设置过期时间" + key + " " + todayEndTime);
                    if (todayEndTime > 0){
                        stringRedisTemplate.opsForValue().set(key, BigDecimalUtils.trim(amount));
                        this.expire(key, todayEndTime);
                    }else {
                    }
                }

            } else {
                //首次充值
                if(ActivityTypeConstants.First.equals(typeInt)||ActivityTypeConstants.Second.equals(typeInt)||ActivityTypeConstants.three.equals(typeInt)||ActivityTypeConstants.First24.equals(typeInt)){
                    return;
                }
                if (ActivityTypeConstants.FOREVER.equals(typeInt)) {
                    this.safeIncrement(key, amount.doubleValue());
                } else {
                    // 设置过期时间
                    //打印日志
                    log.info("设置过期时间" + key + " " + todayEndTime);
                    if (todayEndTime > 0){
                        this.safeIncrement(key, amount.doubleValue());;
                        this.expire(key, todayEndTime);
                    }else {
                    }
                }
            }

        });
    }

    public Double safeIncrement(String key, double delta) {
        try {
            return stringRedisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            Object oldValueObj = stringRedisTemplate.opsForValue().get(key);
            double current = (oldValueObj != null) ? parseToDouble(oldValueObj.toString()) : 0.0;
            double newValue = current + delta;
            log.info("Migrating old format for key: {}, value: {}, current: {}, newValue: {}", key, oldValueObj, current, newValue);
            stringRedisTemplate.opsForValue().set(key, String.valueOf(newValue));
            return newValue;
        }
    }

    private double parseToDouble(String value) {
        try {
            value = value.replace("\"", "");
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0.0;
        }
    }


    /**
     * 存储用户最近的10条记录ID（去重）
     *
     * @param recordIds 记录ID列表
     */
    public void storeRecentGame(String redisKey, List<Long> recordIds) {
        for (Long recordId : recordIds) {
            // 将记录ID插入到集合中
            stringRedisTemplate.opsForSet().add(redisKey, recordId.toString());
        }
        // 只保留最近的10条记录
        trimSetToSize(redisKey, 10);
        // 设置过期时间 半个月
        this.expire(redisKey, 3600 * 24 * 15);
    }

    /**
     * 获取用户最近的10条记录ID
     *
     * @return 记录ID列表
     */
    public List<String> getRecentGame(String redisKey) {
        Set<String> recentRecords = stringRedisTemplate.opsForSet().members(redisKey);
        return recentRecords.stream().limit(10).collect(Collectors.toList());
    }

    /**
     * 更新用户最近的10条记录ID（去重）
     *
     * @param gameId 新的记录ID
     */
    public void updateRecentGame(String redisKey, Long gameId) {
        // 将记录ID插入到集合中
        stringRedisTemplate.opsForSet().add(redisKey, gameId.toString());
        // 只保留最近的10条记录
        trimSetToSize(redisKey, 10);
        // 设置过期时间 半个月
        this.expire(redisKey, 3600 * 24 * 15);
    }

    /**
     * 修剪集合的大小
     *
     * @param key   Redis键
     * @param size  集合的最大大小
     */
    private void trimSetToSize(String key, int size) {
        while (stringRedisTemplate.opsForSet().size(key) > size) {
            stringRedisTemplate.opsForSet().pop(key);
        }
    }


    // ==================== 泛型对象和List的JSON存取 ====================
    /**
     * 存对象（支持任意类型，包括List）
     */
    public <T> void setObj(String key, T value, long timeout, TimeUnit unit) {
        String json = JSON.toJSONString(value);
        log.info("setObj key={} json={}", key, json);
        this.set(key, json, timeout, unit);
    }

    /**
     * 取对象
     */
    public <T> T getObj(String key, Class<T> clazz) {
        Object obj = this.get(key);
        if (obj instanceof String) {
            return JSON.parseObject((String) obj, clazz);
        }
        return null;
    }

    /**
     * 取List
     */
    public <T> List<T> getList(String key, Class<T> clazz) {
        Object obj = this.get(key);
        log.info("getList key={} objType={} objValue={}", key, obj == null ? "null" : obj.getClass().getName(), obj);
        if (obj == null) {
            return Collections.emptyList();
        }
        // redisTemplate 配置了 FastJSON2 序列化器时，get() 返回的是 JSONArray 而非 String
        // 统一转成 JSON 字符串再解析，兼容两种情况
        String jsonStr = (obj instanceof String) ? (String) obj : JSON.toJSONString(obj);
        List<T> result = JSON.parseArray(jsonStr, clazz);
        log.info("getList key={} parsed size={}", key, result == null ? "null" : result.size());
        return result == null ? Collections.emptyList() : result;
    }
}
