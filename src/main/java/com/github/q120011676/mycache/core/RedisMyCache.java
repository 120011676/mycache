package com.github.q120011676.mycache.core;

import com.github.q120011676.mycache.CacheApi;
import com.github.q120011676.mycache.ehcache.EhcacheHelper;
import com.github.q120011676.mycache.serializer.JavaSerializer;
import com.github.q120011676.mycache.serializer.Serializer;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by say on 4/17/16.
 */
public class RedisMyCache implements MyCache {

    private static JedisPool POOL;
    private static Serializer SERIALIZER;
    private static CacheApi L1;
    private static CacheApi L2;
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisMyCache.class);

    private RedisMyCache() {
    }

    public static RedisMyCache create(Properties props) {
        JedisPoolConfig config = new JedisPoolConfig();

        String host = getProperty(props, "host", "127.0.0.1");
        String password = props.getProperty("password", null);

        int port = getProperty(props, "port", 6379);
        int timeout = getProperty(props, "timeout", 2000);
        int database = getProperty(props, "database", 0);

        config.setBlockWhenExhausted(getProperty(props, "blockWhenExhausted", true));
        config.setMaxIdle(getProperty(props, "maxIdle", 10));
        config.setMinIdle(getProperty(props, "minIdle", 5));
//		config.setMaxActive(getProperty(props, "maxActive", 50));
        config.setMaxWaitMillis(getProperty(props, "maxWait", 100));
        config.setTestWhileIdle(getProperty(props, "testWhileIdle", false));
        config.setTestOnBorrow(getProperty(props, "testOnBorrow", true));
        config.setTestOnReturn(getProperty(props, "testOnReturn", false));
        config.setNumTestsPerEvictionRun(getProperty(props, "numTestsPerEvictionRun", 10));
        config.setMinEvictableIdleTimeMillis(getProperty(props, "minEvictableIdleTimeMillis", 1000));
        config.setSoftMinEvictableIdleTimeMillis(getProperty(props, "softMinEvictableIdleTimeMillis", 10));
        config.setTimeBetweenEvictionRunsMillis(getProperty(props, "timeBetweenEvictionRunsMillis", 10));
        config.setLifo(getProperty(props, "lifo", false));

        POOL = new JedisPool(config, host, port, timeout, password, database);
L1 = new EhcacheHelper();
        SERIALIZER = new JavaSerializer();
        return new RedisMyCache();
    }

    private static String getProperty(Properties props, String key, String defaultValue) {
        return props.getProperty(key, defaultValue).trim();
    }

    private static int getProperty(Properties props, String key, int defaultValue) {
        try {
            return Integer.parseInt(props.getProperty(key, String.valueOf(defaultValue)).trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static boolean getProperty(Properties props, String key, boolean defaultValue) {
        return "true".equalsIgnoreCase(props.getProperty(key, String.valueOf(defaultValue)).trim());
    }

    @Override
    public Object get(String region, Object key) {
        return cache == null ? null : cache.get(key);
    }

    @Override
    public void set(String region, Object key, Object data) {
        Cache<Object, Object> cache = this.getCache(region);
        if (cache == null) {
            cache = this.createCache(region);
        }
        cache.put(key, data);
        Jedis jedis = POOL.getResource();
        Command cmd = new Command();
        cmd.setOperator(Operator.SET);
        cmd.setRegion(region);
        cmd.setKey(key);
        cmd.setValue(data);
        try {
            new RedisPublisher(jedis).push(region, new String(SERIALIZER.serialize(cmd)));
        } catch (IOException e) {
            LOGGER.warn(e.getLocalizedMessage());
        } finally {
            jedis.close();
        }
    }

    @Override
    public void del(String region, Object key) {
        Cache<Object, Object> cache = this.getCache(region);
        if (cache != null) {
            cache.remove(key);
        }
        Jedis jedis = POOL.getResource();
        Command cmd = new Command();
        cmd.setOperator(Operator.DEL);
        cmd.setRegion(region);
        cmd.setKey(key);
        try {
            new RedisPublisher(jedis).push(region, new String(SERIALIZER.serialize(cmd)));
        } catch (IOException e) {
            LOGGER.warn(e.getLocalizedMessage());
        } finally {
            jedis.close();
        }
    }

    @Override
    public void clean(String region) {
        this.delCache(region);
        Jedis jedis = POOL.getResource();
        Command cmd = new Command();
        cmd.setOperator(Operator.CLEAN);
        cmd.setRegion(region);
        try {
            new RedisPublisher(jedis).push(region, new String(SERIALIZER.serialize(cmd)));
        } catch (IOException e) {
            LOGGER.warn(e.getLocalizedMessage());
        } finally {
            jedis.close();
        }
    }
}
