package com.github.q120011676.mycache;

/**
 * Created by say on 4/15/16.
 */
public interface CacheApi {

    Object get(Object key);

    void set(Object key, Object data);

    void del(Object key);

    void clean();
}
