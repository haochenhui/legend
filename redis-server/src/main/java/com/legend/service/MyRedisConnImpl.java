package com.legend.service;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.legend.dao.BaseRedisDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.connection.RedisListCommands.Position;
import org.springframework.data.redis.connection.SortParameters;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Tuple;


@Component(value = "myRedisConn")
public class MyRedisConnImpl implements MyRedisConn {
    @Autowired
    private BaseRedisDao baseRedisDao;

    private int selectedDB = 0;

    Logger logger = Logger.getLogger(MyRedisConnImpl.class);

    private JedisConnection getJedis(){
        return baseRedisDao.getJedis();
    }

    @Override
    public void setex(String key, int seconds, String value)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        try
        {
            conn.setEx(key.getBytes(CHARSET), Long.parseLong("" + seconds), value.getBytes(CHARSET));
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
    }

    @Override
    public Boolean set(String key, String value)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        try
        {
            conn.set(key.getBytes(MyRedisConn.CHARSET), value.getBytes(MyRedisConn.CHARSET));
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
            return false;
        } finally
        {
            conn.close();
        }
        return true;
    }

    private int getHCode(String key)
    {
        return key.charAt(0) % 16;
    }

    private void selectDB(JedisConnection conn, int idx)
    {
        conn.select(idx);
        // conn.select(0);
    }

    @Override
    public Boolean setnx(String key, String value)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        try
        {
            return conn.setNX(key.getBytes(CHARSET), value.getBytes(CHARSET));
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return false;
    }

    @Override
    public String get(String key)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        String rs = null;
        try
        {
            byte[] bs = conn.get(key.getBytes(MyRedisConn.CHARSET));
            if (bs == null)
            {
                return rs;
            }
            rs = new String(bs, MyRedisConn.CHARSET);
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public Boolean hset(String key, String field, String value)
    {
        value = valueFormat(value);
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        Boolean rs = null;
        try
        {
            rs = conn.hSet(key.getBytes(MyRedisConn.CHARSET), field.getBytes(MyRedisConn.CHARSET),
                    value.getBytes(MyRedisConn.CHARSET));
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public Boolean hset(String key, String field, byte[] value)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        Boolean rs = null;
        try
        {
            rs = conn.hSet(key.getBytes(MyRedisConn.CHARSET), field.getBytes(MyRedisConn.CHARSET), value);
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public String hget(String key, String field)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        String rs = null;
        try
        {
            byte[] bs = conn.hGet(key.getBytes(MyRedisConn.CHARSET), field.getBytes(MyRedisConn.CHARSET));
            if (bs == null)
            {
                return rs;
            }
            rs = new String(bs, MyRedisConn.CHARSET);
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        rs = valueReduction(rs);
        return rs;
    }

    @Override
    public byte[] hget4bytes(String key, String field)
    {

        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        byte[] bs = null;
        try
        {
            bs = conn.hGet(key.getBytes(MyRedisConn.CHARSET), field.getBytes(MyRedisConn.CHARSET));
            if (bs != null)
            {
                return bs;
            }
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return bs;

    }

    @Override
    public void hmset(String key, Map<String, String> tuple)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        if (tuple != null)
        {
            try
            {
                Map<byte[], byte[]> readyMap = new HashMap<byte[], byte[]>();
                Set<Entry<String, String>> entrySet = tuple.entrySet();
                for (Entry<String, String> entry : entrySet)
                {
                    byte[] keyx = entry.getKey().getBytes(MyRedisConn.CHARSET);
                    byte[] value = entry.getValue().getBytes(MyRedisConn.CHARSET);
                    readyMap.put(keyx, value);
                }
                conn.hMSet(key.getBytes(MyRedisConn.CHARSET), readyMap);
            } catch (UnsupportedEncodingException e)
            {
                logger.error("redis operation failed:CharSet error");
                e.printStackTrace();
            } finally
            {
                conn.close();
            }
        } else
        {
            throw new RuntimeException("tuple map is null");
        }
    }

    @Override
    public List<String> hmget(String key, String fields[])
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        List<String> list = new ArrayList<String>();
        if (fields != null)
        {
            try
            {
                byte[][] fields_bts = new byte[fields.length][];
                for (int i = 0; i < fields.length; i++)
                {
                    fields_bts[i] = fields[i].getBytes(CHARSET);
                }
                List<byte[]> hMGet = conn.hMGet(key.getBytes(CHARSET), fields_bts);
                if (hMGet == null)
                {
                    return null;
                } else if (hMGet.size() == 0)
                {
                    return list;
                }
                for (byte[] bs : hMGet)
                {
                    if (bs == null)
                    {
                        list.add(null);
                    } else
                    {
                        list.add(new String(bs, CHARSET));
                    }
                }
            } catch (UnsupportedEncodingException e)
            {
                logger.error("redis operation failed:CharSet error");
                e.printStackTrace();
            } finally
            {
                conn.close();
            }
        } else
        {
            throw new RuntimeException("fields map is null");
        }
        return list;
    }

    @Override
    public Long hdel(String key, String... fields)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        boolean res = true;
        if (fields != null)
        {
            @SuppressWarnings("unused")
            Long rs = null;
            try
            {
                byte[][] fields_bs = new byte[fields.length][];
                for (int i = 0; i < fields.length; i++)
                {
                    fields_bs[i] = fields[i].getBytes(CHARSET);
                    // res = conn.hDel(key.getBytes(MyRedisConn.CHARSET),
                    // fields_bs[i]);
                }
                rs = conn.hDel(key.getBytes(MyRedisConn.CHARSET), fields_bs);
            } catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
                logger.error("redis operation failed:CharSet error");
            } finally
            {
                conn.close();
            }
            return res == true ? 1l : 0l;
        } else
        {
            return null;
        }
    }

    @Override
    public Long hlen(String key)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));

        Long len = null;
        try
        {
            len = conn.hLen(key.getBytes(CHARSET));
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            logger.error("redis operation failed:CharSet error");
        } finally
        {
            conn.close();
        }
        return len;
    }

    @Override
    public Set<String> hkeys(String key)
    {

        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));

        Set<String> set_bs = new HashSet<String>();
        try
        {
            Set<byte[]> set = conn.hKeys(key.getBytes(CHARSET));
            if (set == null || set.size() == 0)
            {
                return null;
            }
            for (byte[] bs : set)
            {
                set_bs.add(new String(bs, CHARSET));
            }
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            logger.error("redis operation failed:CharSet error");
        } finally
        {
            conn.close();
        }
        return set_bs;
    }

    @Override
    public List<String> hvals(String key)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));

        List<String> strList = new ArrayList<String>();
        try
        {
            List<byte[]> hVals = conn.hVals(key.getBytes(CHARSET));
            if (hVals == null || hVals.size() == 0)
            {
                return null;
            }
            for (byte[] bs : hVals)
            {
                if (bs == null)
                {
                    strList.add(null);
                } else
                {
                    strList.add(new String(bs, CHARSET));
                }
            }
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            logger.error("redis operation failed:CharSet error");
        } finally
        {
            conn.close();
        }
        return strList;
    }

    @Override
    public Map<String, String> hgetAll(String key)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));

        Map<String, String> map_str = new HashMap<String, String>();
        try
        {
            Map<byte[], byte[]> hGetAll = conn.hGetAll(key.getBytes(CHARSET));
            if (hGetAll == null)
            {
                return null;
            }
            Set<Entry<byte[], byte[]>> entrySet = hGetAll.entrySet();
            for (Entry<byte[], byte[]> entry : entrySet)
            {
                map_str.put(new String(entry.getKey(), CHARSET), valueReduction(new String(entry.getValue(), CHARSET)));
            }
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            logger.error("redis operation failed:CharSet error");
        } finally
        {
            conn.close();
        }
        return map_str;
    }

    @Override
    public boolean expire(String key, long seconds)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));

        boolean rs = false;
        if (key != null)
        {
            try
            {
                rs = conn.expire(key.getBytes(CHARSET), seconds);
            } catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
                logger.error("redis operation failed:CharSet error");
            } finally
            {
                conn.close();
            }
        }
        return rs;
    }

    @Override
    public Long rpush(String key, String... as)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        Long rs = null;
        try
        {
            if (as != null && as.length > 0)
            {
                byte[][] bs = new byte[as.length][];
                for (int k = 0; k < as.length; k++)
                {
                    bs[k] = as[k].getBytes(CHARSET);
                }
                rs = conn.rPush(key.getBytes(MyRedisConn.CHARSET), bs);
            }

        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public Long lpush(String key, String... as)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        if (as != null)
        {
            Long rs = null;
            try
            {
                byte[][] fields_bs = new byte[as.length][];
                for (int i = 0; i < as.length; i++)
                {
                    fields_bs[i] = as[i].getBytes(CHARSET);
                }
                rs = conn.lPush(key.getBytes(MyRedisConn.CHARSET), fields_bs);
            } catch (UnsupportedEncodingException e)
            {
                logger.error("redis operation failed:CharSet error");
                e.printStackTrace();
            } finally
            {
                conn.close();
            }
            return rs;
        } else
        {
            return null;
        }
    }

    @Override
    public Long llen(String key)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        Long rs = null;
        try
        {
            rs = conn.lLen(key.getBytes(MyRedisConn.CHARSET));
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public List<String> lrange(String key, long l, long l1)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        List<String> list = new ArrayList<String>();
        try
        {
            List<byte[]> lRange = conn.lRange(key.getBytes(CHARSET), l, l1);
            if (lRange == null)
            {
                return null;
            } else if (lRange.size() == 0)
            {
                return list;
            }
            for (byte[] bs : lRange)
            {
                if (bs == null)
                {
                    list.add(null);
                } else
                {
                    list.add(new String(bs, CHARSET));
                }
            }
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return list;
    }

    @Override
    public String ltrim(String key, long l, long l1)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        String rs = null;
        try
        {
            conn.lTrim(key.getBytes(MyRedisConn.CHARSET), l, l1);
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public String lindex(String key, long l)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        String rs = null;
        try
        {
            rs = new String(conn.lIndex(key.getBytes(MyRedisConn.CHARSET), l), CHARSET);
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public String lset(String key, long l, String s1)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        String rs = null;
        try
        {
            conn.lSet(key.getBytes(MyRedisConn.CHARSET), l, s1.getBytes(MyRedisConn.CHARSET));
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public Long lrem(String key, long l, String s1)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        Long rs = null;
        try
        {
            rs = conn.lRem(key.getBytes(MyRedisConn.CHARSET), l, s1.getBytes(MyRedisConn.CHARSET));
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public String lpop(String key)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        String rs = null;
        try
        {
            rs = new String(conn.lPop(key.getBytes(MyRedisConn.CHARSET)), CHARSET);
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public String rpop(String key)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        String rs = null;
        try
        {
            rs = new String(conn.rPop(key.getBytes(MyRedisConn.CHARSET)), CHARSET);
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public Long sadd(String key, String... as)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        Long rs = null;
        try
        {
            if (as != null && as.length > 0)
            {
                byte[][] bs = new byte[as.length][];
                for (int k = 0; k < as.length; k++)
                {
                    bs[k] = as[k].getBytes(CHARSET);
                }
                rs = conn.sAdd(key.getBytes(MyRedisConn.CHARSET), bs);
            }
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public Set<String> smembers(String key)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long srem(String key, String... as)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn,getHCode(key));
        Long rs = null;
        try
        {
            if(as !=null && as.length>0)
            {
                byte[][] bs = new byte[as.length][];
                for(int k=0; k<as.length; k++)
                {
                    bs[k] = as[k].getBytes(CHARSET);
                }
                rs = conn.sRem(key.getBytes(MyRedisConn.CHARSET),bs) ;
            }
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public String spop(String key)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long scard(String key)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn,getHCode(key));
        Long rs= null;
        try{
            rs = conn.sCard(key.getBytes(CHARSET) );
        }catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public Boolean sismember(String key, String s1)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn,getHCode(key));
        Boolean rs = null;
        try
        {
            rs = conn.sIsMember(key.getBytes(MyRedisConn.CHARSET), s1.getBytes(MyRedisConn.CHARSET));
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public String srandmember(String key)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> srandmember(String key, int i)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long strlen(String s)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long zadd(String s, double d, String s1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long zadd(String key, Map<String, Double> map)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> zrange(String key, long l, long l1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long zrem(String key, String... as)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double zincrby(String key, double d, String s1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long zrank(String key, String s1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long zrevrank(String key, String s1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> zrevrange(String key, long l, long l1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Tuple> zrangeWithScores(String key, long l, long l1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Tuple> zrevrangeWithScores(String key, long l, long l1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long zcard(String key)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double zscore(String key, String s1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> sort(String key)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
//		List<String> rs = new ArrayList<>();
        List<String> rs = new ArrayList<String>();
        try
        {

            List<byte[]> res = conn.sort(key.getBytes(CHARSET), null);
            if (res != null && res.size() > 0)
            {
                for (int k = 0; k < res.size(); k++)
                {
                    rs.add(new String(res.get(k), CHARSET));
                }
            }
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public List<String> sort(String key, SortParameters sortingparams)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
//		List<String> rs = new ArrayList<>();
        List<String> rs = new ArrayList<String>();
        try
        {

            List<byte[]> res = conn.sort(key.getBytes(CHARSET), sortingparams);
            if (res != null && res.size() > 0)
            {
                for (int k = 0; k < res.size(); k++)
                {
                    rs.add(new String(res.get(k), CHARSET));
                }
            }
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public Long zcount(String key, double d, double d1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long zcount(String key, String s1, String s2)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> zrangeByScore(String key, double d, double d1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> zrangeByScore(String key, String s1, String s2)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double d, double d1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> zrangeByScore(String key, double d, double d1, int i, int j)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String s1, String s2)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> zrangeByScore(String s, String s1, String s2, int i, int j)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double d, double d1, int i, int j)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double d, double d1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double d, double d1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double d, double d1, int i, int j)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String s1, String s2, int i, int j)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String s1, String s2)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String s1, String s2)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String s1, String s2, int i, int j)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double d, double d1, int i, int j)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String s1, String s2, int i, int j)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long zremrangeByRank(String key, long l, long l1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long zremrangeByScore(String key, double d, double d1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long zremrangeByScore(String key, String s1, String s2)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long zlexcount(String key, String s1, String s2)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> zrangeByLex(String s, String s1, String s2)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> zrangeByLex(String key, String s1, String s2, int i, int j)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long zremrangeByLex(String key, String s1, String s2)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long linsert(String key, Position list_position, String s1, String s2)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        Long rs = null;
        try
        {

            rs = conn.lInsert(key.getBytes(CHARSET), list_position, s1.getBytes(CHARSET), s2.getBytes(CHARSET));
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public Long lpushx(String key, String s1)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        Long rs = null;
        try
        {
            rs = conn.lPushX(key.getBytes(CHARSET), s1.getBytes(CHARSET));
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public Long rpushx(String key, String s1)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        Long rs = null;
        try
        {
            rs = conn.rPushX(key.getBytes(CHARSET), s1.getBytes(CHARSET));
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public List<String> blpop(String key)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> blpop(int i, String s)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> brpop(String key)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> brpop(int i, String s)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long del(String key)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        Long rs = null;
        try
        {
            rs = conn.del(key.getBytes(CHARSET));
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public String echo(String key)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        String rs = null;
        try
        {
            rs = new String(conn.echo(key.getBytes(CHARSET)), CHARSET);
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public boolean move(String key, int i)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        boolean rs = false;
        try
        {
            rs = conn.move(key.getBytes(CHARSET), i);
        } catch (UnsupportedEncodingException e)
        {
            logger.error("redis operation failed:CharSet error");
            e.printStackTrace();
        } finally
        {
            conn.close();
        }
        return rs;
    }

    @Override
    public Long bitcount(String key)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long bitcount(String key, long l, long l1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScanResult<Entry<String, String>> hscan(String key, int i)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScanResult<String> sscan(String key, int i)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScanResult<Tuple> zscan(String s, int i)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScanResult<Entry<String, String>> hscan(String s, String s1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScanResult<String> sscan(String key, String s1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScanResult<Tuple> zscan(String key, String s1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long pfadd(String key, String... as)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long pfcount(String key)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void rename(String oldkey, String newkey)
    {
        logger.error("the command rename is not supported in the version !!!");
        return;
		/*
		 * JedisConnection conn = this.getJedis(); selectDB(conn,0); try {
		 * conn.rename(oldkey.getBytes(CHARSET), newkey.getBytes(CHARSET)); }
		 * catch (UnsupportedEncodingException e) {
		 * logger.error("redis operation failed:CharSet error"); } finally {
		 * conn.close(); }
		 */
    }

    public Set<String> keys(String pattern)
    {
        JedisConnection conn = this.getJedis();

        Set<String> set_bs = new HashSet<String>();
        try
        {
            Set<byte[]> set = conn.keys(pattern.getBytes(CHARSET));
            for (int i = 0; i < 16; i++)
            {
                selectDB(conn, i);
                if (set == null || set.size() == 0)
                {
                    return null;
                }
                for (byte[] bs : set)
                {
                    set_bs.add(new String(bs, CHARSET));
                }
            }
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            logger.error("redis operation failed:CharSet error");
        } finally
        {
            conn.close();
        }
        return set_bs;
    }

    @Override
    public JedisConnection getOriConn()
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, 0);
        return conn;
    }

    @Override
    public long incrBy(String key, long number)
    {
        JedisConnection conn = this.getJedis();
        selectDB(conn, getHCode(key));
        try
        {
            long len = conn.incrBy(key.getBytes(CHARSET), number);
            return len;
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            logger.error("redis operation failed:CharSet error");
        } finally
        {
            conn.close();
        }
        return 0;
    }

    public String valueFormat(String val)
    {
        if (null != val && !"".equals(val))
        {
            val = val.replaceAll("\r\n", "%5Cr%5Cn");
        }
        return val;
    }

    public String valueReduction(String val)
    {
        if (null != val && !"".equals(val))
        {
            val = val.replaceAll("%5Cr%5Cn", "\r\n");
        }
        return val;
    }


}


