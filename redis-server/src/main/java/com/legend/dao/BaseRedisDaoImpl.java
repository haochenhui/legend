package com.legend.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.JedisPoolConfig;


@Configuration
@Repository("baseRedisDao")
public class BaseRedisDaoImpl implements BaseRedisDao {

    @Value("${redis.host}")
    private String host;
    @Value("${redis.port}")
    private int port;
    @Value("${redis.timeout}")
    private int timeout;
    @Value("${redis.password}")
    private String password;

    @Bean
    public JedisPoolConfig getJedisPoolConfig(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(1000);
        jedisPoolConfig.setMaxIdle(100);
        jedisPoolConfig.setMinIdle(50);
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setTestOnReturn(true);
        jedisPoolConfig.setTestWhileIdle(true);
        jedisPoolConfig.setNumTestsPerEvictionRun(5);
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(60000);
        jedisPoolConfig.setMaxWaitMillis(10000);
        return jedisPoolConfig;
    }
    @Bean
    public JedisConnectionFactory getJedisConnectionFactory(){
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName(host);
        jedisConnectionFactory.setPassword(password);
        jedisConnectionFactory.setPort(port);
        jedisConnectionFactory.setTimeout(timeout);
        jedisConnectionFactory.setUsePool(true);
        jedisConnectionFactory.setPoolConfig(getJedisPoolConfig());
        return jedisConnectionFactory;
    }

    @Override
    public JedisConnection getJedis() {
        JedisConnection connection = (JedisConnection)getJedisConnectionFactory().getConnection();
        return connection;
    }

}

