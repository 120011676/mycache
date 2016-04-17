package com.github.q120011676.mycache.core;

/**
 * Created by say on 4/15/16.
 */
public interface MyCache {

    Object get(String region, Object key);

    void set(String region, Object key, Object data);

    void del(String region, Object key);

    void clean(String region);
}
