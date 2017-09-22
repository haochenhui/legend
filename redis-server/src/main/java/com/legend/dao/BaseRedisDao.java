package com.legend.dao;

import org.springframework.data.redis.connection.jedis.JedisConnection;

/**
 * Created by hch on 2017/9/22.
 */
public interface BaseRedisDao {
    JedisConnection getJedis();
}
