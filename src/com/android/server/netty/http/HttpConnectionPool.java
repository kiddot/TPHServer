package com.android.server.netty.http;

import com.android.server.config.ConfigCenter;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by root on 17-7-17.
 */
public class HttpConnectionPool {
    private static final int maxConnPerHost = ConfigCenter.max_conn_per_host;

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpConnectionPool.class);

    private final AttributeKey<String> hostKey = AttributeKey.newInstance("host");

    private final Map<String, List<Channel>> channelPool = new HashMap<>();

    public synchronized Channel tryAcquire(String host) {
        List<Channel> channels = channelPool.get(host);
        if (channels == null || channels.isEmpty()) return null;
        Iterator<Channel> it = channels.iterator();
        while (it.hasNext()) {
            Channel channel = it.next();
            it.remove();
            if (channel.isActive()) {
                LOGGER.debug("tryAcquire channel success, host={}", host);
                channel.attr(hostKey).set(host);
                return channel;
            } else {//链接由于意外情况不可用了, 比如: keepAlive_timeout
                LOGGER.warn("tryAcquire channel false channel is inactive, host={}", host);
            }
        }
        return null;
    }

    public synchronized void tryRelease(Channel channel) {
        String host = channel.attr(hostKey).getAndSet(null);
        List<Channel> channels = channelPool.get(host);
        if (channels == null || channels.size() < maxConnPerHost) {
            channels = new ArrayList<>();
            channels.add(channel);
            LOGGER.debug("tryRelease channel success, host={}", host);
            channelPool.put(host, channels);
        } else {
            LOGGER.debug("tryRelease channel pool size over limit={}, host={}, channel closed.", maxConnPerHost, host);
            channel.close();
        }
    }

    public void attachHost(String host, Channel channel) {
        channel.attr(hostKey).set(host);
    }

    public void close() {
        for (List<Channel> list : channelPool.values()) {
            Iterator it = list.iterator();
            while(it.hasNext()) {
                Channel channel = (Channel) it.next();
                channel.close();
            }
        }
        channelPool.clear();
    }
}
