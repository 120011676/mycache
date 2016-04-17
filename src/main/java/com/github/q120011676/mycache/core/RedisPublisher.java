package com.github.q120011676.mycache.core;

import redis.clients.jedis.Jedis;

/**
 * Created by say on 4/17/16.
 */
public class RedisPublisher {

    private Jedis jedis;

    public RedisPublisher(Jedis jedis) {
        this.jedis = jedis;
    }

    public void push(String channel, String message) {
        this.jedis.publish(channel, message);
    }

    public void push(byte[] channel,byte[] message){
        this.jedis.publish(channel,message);
    }
}
