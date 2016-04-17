package com.github.q120011676.mycache.core;

import com.github.q120011676.mycache.ehcache.EhcacheHelper;
import com.github.q120011676.mycache.serializer.JavaSerializer;
import com.github.q120011676.mycache.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import java.io.IOException;

/**
 * Created by say on 4/15/16.
 */
public class RedisSubscriber extends JedisPubSub {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSubscriber.class);
    private Serializer serializer = new JavaSerializer();

    @Override
    public void onMessage(String channel, String message) {
        try {
            Command cmd = (Command) serializer.deserialize(message.getBytes());
            if (cmd != null) {
                EhcacheHelper ehcache = new EhcacheHelper(channel);
                switch (cmd.getOperator()) {
                    case Operator.CLEAN:
                        ehcache.clean();
                        break;
                    case Operator.SET:
                        ehcache.set(cmd.getKey(), cmd.getValue());
                        break;
                    case Operator.DEL:
                        ehcache.del(cmd.getKey());
                        break;
                    default:
                        LOGGER.warn("opt unknown '{}'" + cmd.getOperator());
                        break;
                }
            }
        } catch (IOException e) {
            LOGGER.warn(e.getLocalizedMessage());
        }
        super.onMessage(channel, message);
    }

}
