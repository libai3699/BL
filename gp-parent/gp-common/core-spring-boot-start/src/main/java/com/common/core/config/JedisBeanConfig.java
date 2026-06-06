package com.common.core.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.common.core.id.service.impl.IdNoGenerateServiceImpl;
import com.common.core.prop.JedisProp;
import com.common.core.serial.FastJson2JsonRedisSerializer;
import com.common.core.util.BackRedisCache;
import com.common.core.util.RedisLock;
import com.common.core.util.RedisLockUtil;
import com.common.core.util.RedisUtil;
import com.gp.common.base.constant.RedisKey;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(JedisProp.class)
public class JedisBeanConfig {

    public static final String key = "O28piOOB67H7GMFRMTj3kw==";

    @Bean
    public JedisPoolConfig jedisPoolConfig(JedisProp jedisProp) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(jedisProp.getMaxTotal());
        jedisPoolConfig.setMaxIdle(jedisProp.getMaxIdle());
        jedisPoolConfig.setMaxWaitMillis(jedisProp.getMaxWaitMillis());
        jedisPoolConfig.setMinIdle(1);//设置最小空闲数
        jedisPoolConfig.setTestOnBorrow(jedisProp.getTestOnBorrow());
        jedisPoolConfig.setTestOnReturn(jedisProp.getTestOnReturn());
        jedisPoolConfig.setTestWhileIdle(jedisProp.getTestWhileIdle());
        return jedisPoolConfig;
    }


    public RedisClusterConfiguration redisClusterConfiguration(JedisProp jedisProp) {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        redisClusterConfiguration.setMaxRedirects(3);
        List<RedisNode> list = new ArrayList<>();
        for (JedisProp.Cluster cluster : jedisProp.getClusters()) {
            String ip = SecureUtil.aes(key.getBytes()).decryptStr(cluster.getIp());
            list.add(new RedisClusterNode(ip, cluster.getPort()));
        }
        redisClusterConfiguration.setClusterNodes(list);
        if (StrUtil.isNotBlank(jedisProp.getPass())) {
            String pass = SecureUtil.aes(key.getBytes()).decryptStr(jedisProp.getPass());
            redisClusterConfiguration.setPassword(pass);
        }
        return redisClusterConfiguration;
    }


    @Primary
    @Bean
    public JedisConnectionFactory jedisConnectionFactory(JedisProp jedisProp, JedisPoolConfig jedisPoolConfig) {
        JedisConnectionFactory jedisConnectionFactory;
        if (jedisProp.getIsCluster()) {//集群
            jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration(jedisProp), jedisPoolConfig);
        } else {
            jedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig);
            String host = SecureUtil.aes(key.getBytes()).decryptStr(jedisProp.getHost());
            jedisConnectionFactory.setHostName(host);
            jedisConnectionFactory.setUseSsl(jedisProp.isSsl());
            jedisConnectionFactory.setPort(jedisProp.getPort());
            if (StrUtil.isNotBlank(jedisProp.getPass())) {
                String pass = SecureUtil.aes(key.getBytes()).decryptStr(jedisProp.getPass());
                jedisConnectionFactory.setPassword(pass);
            }
        }
        jedisConnectionFactory.setDatabase(jedisProp.getDatabase());
        jedisConnectionFactory.setTimeout(20000);
        return jedisConnectionFactory;
    }


    @Bean("redisson")
    public RedissonClient redissonClient(JedisProp jedisProp) {
        Config config = new Config();
        if (jedisProp.getIsCluster()) {
            ClusterServersConfig clusterServersConfig = config.useClusterServers();
            for (JedisProp.Cluster cluster : jedisProp.getClusters()) {
                String host = SecureUtil.aes(key.getBytes()).decryptStr(cluster.getIp());
                if (jedisProp.isSsl()) {
                    clusterServersConfig.addNodeAddress(StrUtil.format(RedisKey.redissAddr, host, cluster.getPort()));
                } else {
                    clusterServersConfig.addNodeAddress(StrUtil.format(RedisKey.redisAddr, host, cluster.getPort()));
                }

            }
        } else {
            String host = SecureUtil.aes(key.getBytes()).decryptStr(jedisProp.getHost());
            if (jedisProp.isSsl()) {
                config.useSingleServer().setAddress(StrUtil.format(RedisKey.redissAddr, host, jedisProp.getPort()));
            } else {
                config.useSingleServer().setAddress(StrUtil.format(RedisKey.redisAddr, host, jedisProp.getPort()));
            }
        }
//        config.useSingleServer().setSslEnableEndpointIdentification(true);
        if (StrUtil.isNotBlank(jedisProp.getPass())) {
            String pass = SecureUtil.aes(key.getBytes()).decryptStr(jedisProp.getPass());
            config.useSingleServer().setPassword(pass);
        }
        config.useSingleServer().setDatabase(jedisProp.getDatabase());
        //默认3秒
        config.useSingleServer().setTimeout(10000);
        return Redisson.create(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setEnableTransactionSupport(false);
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        StringRedisSerializer keySerializer = new StringRedisSerializer();
//        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();
        FastJson2JsonRedisSerializer valueSerializer = new FastJson2JsonRedisSerializer(Object.class);
        redisTemplate.setKeySerializer(keySerializer);
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashKeySerializer(keySerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);
        return redisTemplate;
    }

    @Bean
    public RedisUtil redisUtil(StringRedisTemplate stringRedisTemplate, RedisTemplate<String, Object> redisTemplate,
                               JedisProp jedisProp) {
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setStringRedisTemplate(stringRedisTemplate);
        redisUtil.setRedisTemplate(redisTemplate);
        redisUtil.setCluster(jedisProp.getIsCluster());
        return redisUtil;
    }

    @Bean
    public IdNoGenerateServiceImpl idNoGenerateServiceImpl(StringRedisTemplate stringRedisTemplate) {
        IdNoGenerateServiceImpl idNoGenerateService = new IdNoGenerateServiceImpl();
        idNoGenerateService.setStringRedisTemplate(stringRedisTemplate);
        return idNoGenerateService;
    }


    @Bean
    public BackRedisCache backRedisCache(RedisTemplate redisTemplate) {
        BackRedisCache backRedisCache = new BackRedisCache();
        backRedisCache.setRedisTemplate(redisTemplate);
        return backRedisCache;
    }


    @Bean
    public RedisLock redisLock(RedissonClient redissonClient) {
        RedisLock redisLock = new RedisLock();
        redisLock.setRedissonClient(redissonClient);
        return redisLock;
    }


    @Bean
    public RedisLockUtil redisLockUtil(RedisLock redisLock) {
        RedisLockUtil redisLockUtil = new RedisLockUtil();
        redisLockUtil.setRedisLock(redisLock);
        return redisLockUtil;
    }


    public static void main(String[] args) {
        String host = SecureUtil.aes(key.getBytes()).encryptHex("43.242.129.15");
//        String host = SecureUtil.aes(key.getBytes()).decryptStr("947473cc574710e0c7cf79f8f46d85ea632d13e04d867d966801f4b26a9c2b8cd6457221d490d3b7fd9542318ad8bfe9509693bce874d132b54de0d161bc78bf");
        String password = SecureUtil.aes(key.getBytes()).decryptStr("e8ae072717435d10aca3d9a91c12a8063c329c5e1bbcd3ae0c0676d84911f7d4");
        System.out.println(host);
        System.out.println(password);
    }


}
