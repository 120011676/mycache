package com.github.q120011676.mycache.core;

/**
 * Created by say on 4/15/16.
 */
public interface MyCache {

    Object get(Object key);
    void set(Object key);
    void del(Object key);
    void  clean();
}
