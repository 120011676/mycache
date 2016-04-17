package com.github.q120011676.mycache.ehcache;

import com.github.q120011676.mycache.CacheApi;
import com.github.q120011676.mycache.core.MyCache;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by say on 4/17/16.
 */
public class EhcacheHelper implements CacheApi {

    private static final String EHCACHE_FILE = "/ehcache.xml";
    private static CacheManager CACHE_MANAGER;
    private Cache<Object, Object> cache;
    private String name;


    private static final Logger LOGGER = LoggerFactory.getLogger(EhcacheHelper.class);

    static {
        XmlConfiguration xmlConfig = new XmlConfiguration(MyCache.class.getResource(EHCACHE_FILE));
        CACHE_MANAGER = CacheManagerBuilder.newCacheManager(xmlConfig);
    }

    public EhcacheHelper(String name) {
        this.name = name;
        Cache<Object, Object> c = CACHE_MANAGER.getCache(name, Object.class, Object.class);
        if (c == null) {
            c = CACHE_MANAGER.createCache(name, CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10)).build());
        }
        this.cache = c;
    }

    @Override
    public Object get(Object key) {
        return this.cache.get(key);
    }

    @Override
    public void set(Object key, Object data) {
        this.cache.put(key, data);
    }

    @Override
    public void del(Object key) {
        this.cache.remove(key);
    }

    @Override
    public void clean() {
        CACHE_MANAGER.removeCache(this.name);
    }
}
