package com.legend.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.data.redis.connection.RedisListCommands;
import org.springframework.data.redis.connection.SortParameters;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Tuple;

/**
 * Created by hch on 2017/9/22.
 */
public interface MyRedisConn {
    String CHARSET = "UTF-8";

    void setex(String var1, int var2, String var3);

    Boolean set(String var1, String var2);

    Boolean setnx(String var1, String var2);

    String get(String var1);

    Boolean hset(String var1, String var2, String var3);

    Boolean hset(String var1, String var2, byte[] var3);

    String hget(String var1, String var2);

    byte[] hget4bytes(String var1, String var2);

    void hmset(String var1, Map<String, String> var2);

    Long hdel(String var1, String... var2);

    Long hlen(String var1);

    Set<String> hkeys(String var1);

    List<String> hvals(String var1);

    Map<String, String> hgetAll(String var1);

    Long rpush(String var1, String... var2);

    Long lpush(String var1, String... var2);

    Long llen(String var1);

    List<String> lrange(String var1, long var2, long var4);

    String ltrim(String var1, long var2, long var4);

    String lindex(String var1, long var2);

    String lset(String var1, long var2, String var4);

    Long lrem(String var1, long var2, String var4);

    String lpop(String var1);

    String rpop(String var1);

    Long sadd(String var1, String... var2);

    Set<String> smembers(String var1);

    Long srem(String var1, String... var2);

    String spop(String var1);

    Long scard(String var1);

    Boolean sismember(String var1, String var2);

    String srandmember(String var1);

    List<String> srandmember(String var1, int var2);

    Long strlen(String var1);

    Long zadd(String var1, double var2, String var4);

    Long zadd(String var1, Map<String, Double> var2);

    Set<String> zrange(String var1, long var2, long var4);

    Long zrem(String var1, String... var2);

    Double zincrby(String var1, double var2, String var4);

    Long zrank(String var1, String var2);

    Long zrevrank(String var1, String var2);

    Set<String> zrevrange(String var1, long var2, long var4);

    Set<Tuple> zrangeWithScores(String var1, long var2, long var4);

    Set<Tuple> zrevrangeWithScores(String var1, long var2, long var4);

    Long zcard(String var1);

    Double zscore(String var1, String var2);

    List<String> sort(String var1);

    List<String> sort(String var1, SortParameters var2);

    Long zcount(String var1, double var2, double var4);

    Long zcount(String var1, String var2, String var3);

    Set<String> zrangeByScore(String var1, double var2, double var4);

    Set<String> zrangeByScore(String var1, String var2, String var3);

    Set<String> zrevrangeByScore(String var1, double var2, double var4);

    Set<String> zrangeByScore(String var1, double var2, double var4, int var6, int var7);

    Set<String> zrevrangeByScore(String var1, String var2, String var3);

    Set<String> zrangeByScore(String var1, String var2, String var3, int var4, int var5);

    Set<String> zrevrangeByScore(String var1, double var2, double var4, int var6, int var7);

    Set<Tuple> zrangeByScoreWithScores(String var1, double var2, double var4);

    Set<Tuple> zrevrangeByScoreWithScores(String var1, double var2, double var4);

    Set<Tuple> zrangeByScoreWithScores(String var1, double var2, double var4, int var6, int var7);

    Set<String> zrevrangeByScore(String var1, String var2, String var3, int var4, int var5);

    Set<Tuple> zrangeByScoreWithScores(String var1, String var2, String var3);

    Set<Tuple> zrevrangeByScoreWithScores(String var1, String var2, String var3);

    Set<Tuple> zrangeByScoreWithScores(String var1, String var2, String var3, int var4, int var5);

    Set<Tuple> zrevrangeByScoreWithScores(String var1, double var2, double var4, int var6, int var7);

    Set<Tuple> zrevrangeByScoreWithScores(String var1, String var2, String var3, int var4, int var5);

    Long zremrangeByRank(String var1, long var2, long var4);

    Long zremrangeByScore(String var1, double var2, double var4);

    Long zremrangeByScore(String var1, String var2, String var3);

    Long zlexcount(String var1, String var2, String var3);

    Set<String> zrangeByLex(String var1, String var2, String var3);

    Set<String> zrangeByLex(String var1, String var2, String var3, int var4, int var5);

    Long zremrangeByLex(String var1, String var2, String var3);

    Long linsert(String var1, RedisListCommands.Position var2, String var3, String var4);

    Long lpushx(String var1, String var2);

    Long rpushx(String var1, String var2);

    List<String> blpop(String var1);

    List<String> blpop(int var1, String var2);

    List<String> brpop(String var1);

    List<String> brpop(int var1, String var2);

    Long del(String var1);

    String echo(String var1);

    boolean move(String var1, int var2);

    Long bitcount(String var1);

    Long bitcount(String var1, long var2, long var4);

    ScanResult<Map.Entry<String, String>> hscan(String var1, int var2);

    ScanResult<String> sscan(String var1, int var2);

    ScanResult<Tuple> zscan(String var1, int var2);

    ScanResult<Map.Entry<String, String>> hscan(String var1, String var2);

    ScanResult<String> sscan(String var1, String var2);

    ScanResult<Tuple> zscan(String var1, String var2);

    Long pfadd(String var1, String... var2);

    long pfcount(String var1);

    List<String> hmget(String var1, String[] var2);

    boolean expire(String var1, long var2);

    void rename(String var1, String var2);

    Set<?> keys(String var1);

    JedisConnection getOriConn();

    long incrBy(String var1, long var2);
}
