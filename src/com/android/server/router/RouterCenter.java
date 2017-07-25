package com.android.server.router;

import com.android.server.common.event.RouterChangeEvent;
import com.android.server.config.ConfigCenter;
import com.android.server.core.connection.Connection;
import com.android.server.utils.EventBus;
import com.android.server.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by root on 17-7-22.
 */
public final class RouterCenter {
    public static final Logger LOGGER = LoggerFactory.getLogger(RouterCenter.class);
    public static final RouterCenter I = new RouterCenter();

    private final LocalRouterManager localRouterManager = LocalRouterManager.getInstance();


    /**
     * 注册用户和链接
     *
     * @param userId
     * @param connection
     * @return
     */
    public boolean register(String userId, Connection connection) {
        ClientLocation location = ClientLocation
                .from(connection)
                .setHost(Utils.getLocalIp())
                .setPort(ConfigCenter.gateway_server_port);

        LocalRouter localRouter = new LocalRouter(connection);

        LocalRouter oldLocalRouter = null;
        try {
            oldLocalRouter = localRouterManager.register(userId, localRouter);
        } catch (Exception e) {
            LOGGER.error("register router ex, userId={}, connection={}", userId, connection);
        }

        if (oldLocalRouter != null) {
            //EventBus.I.post(new RouterChangeEvent(userId, oldLocalRouter));
            LOGGER.info("register router success, find old local router={}, userId={}", oldLocalRouter, userId);
        }

        return true;
    }

    public boolean unRegister(String userId, int clientType) {
        localRouterManager.unRegister(userId, clientType);
        return true;
    }

    public Router<?> lookup(String userId, int clientType) {
        LocalRouter local = localRouterManager.lookup(userId, clientType);
        if (local != null) return local;
        return null;
    }

    public LocalRouterManager getLocalRouterManager() {
        return localRouterManager;
    }

}
