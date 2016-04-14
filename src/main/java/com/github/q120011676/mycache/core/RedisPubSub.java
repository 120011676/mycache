package com.github.q120011676.mycache.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by say on 4/15/16.
 */
public class RedisPubSub extends JedisPubSub {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisPubSub.class);

    @Override
    public void onMessage(String channel, String message) {
        if (message != null && message.length() <= 0) {
            LOGGER.warn("Message is empty.");
            return;
        }
        switch (message) {
            case Command.CLEAN:
                break;
            case Command.DEL:
                break;
            case Command.SET:
                break;
            default:
                LOGGER.warn("Unknown message type = " + message);
                break;
        }
        super.onMessage(channel, message);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        super.onPMessage(pattern, channel, message);
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        super.onSubscribe(channel, subscribedChannels);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        super.onUnsubscribe(channel, subscribedChannels);
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        super.onPUnsubscribe(pattern, subscribedChannels);
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        super.onPSubscribe(pattern, subscribedChannels);
    }
}
