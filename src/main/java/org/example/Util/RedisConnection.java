package org.example.Util;

import redis.clients.jedis.*;

import java.time.Duration;

public class RedisConnection {

    private static final JedisPool jedisPool;

    static {
        JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMinIdle(2);
        jedisPoolConfig.setMaxIdle(5);

        jedisPoolConfig.setMaxWait(Duration.ofSeconds(30));
        jedisPoolConfig.setMinEvictableIdleDuration(Duration.ofMinutes(10));
        jedisPoolConfig.setTimeBetweenEvictionRuns(Duration.ofMinutes(5));

        JedisClientConfig jedisClientConfig= DefaultJedisClientConfig.builder()
                .connectionTimeoutMillis(2000)
                .socketTimeoutMillis(2000)
                .build();


        jedisPool=new JedisPool(jedisPoolConfig,"localhost",6379);
    }
    public static Jedis getJedis(){
        return jedisPool.getResource();
    }
}
