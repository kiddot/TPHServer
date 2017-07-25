package com.android.server.router;

import com.android.server.common.event.ConnectionCloseEvent;
import com.android.server.common.event.UserOfflineEvent;
import com.android.server.core.connection.Connection;
import com.android.server.core.connection.SessionContext;
import com.android.server.utils.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by root on 17-7-22.
 */
public final class LocalRouterManager implements RouterManager<LocalRouter> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalRouterManager.class);
    private static final Map<Integer, LocalRouter> EMPTY = new HashMap<>(0);

    /**
     * 本地路由表
     */
    private final Map<String, Map<Integer, LocalRouter>> routers = new ConcurrentHashMap<>();

    private static LocalRouterManager localRouterManager = null;
    private LocalRouterManager(){}

    public static LocalRouterManager getInstance(){
        synchronized (LocalRouterManager.class){
            if (localRouterManager == null){
                localRouterManager = new LocalRouterManager();
            }
        }
        return localRouterManager;
    }

    @Override
    public LocalRouter register(String userId, LocalRouter router) {
        LOGGER.warn("register local router success userId={}, router={}", userId, router);
        Map<Integer, LocalRouter> map = new HashMap<>(1);
        map.put(router.getClientType(), router);
        routers.put(userId, map);
        return router;
    }

    @Override
    public boolean unRegister(String userId, int clientType) {
        if (routers.get(userId) != null){
            routers.get(userId).remove(clientType);
            return true;
        } else {
            EMPTY.remove(clientType);
        }
        return true;
    }

    @Override
    public Set<LocalRouter> lookupAll(String userId) {
        if (routers.get(userId) != null){
            return new HashSet<>(routers.get(userId).values());
        } else {
            return null;
        }
    }

    @Override
    public LocalRouter lookup(String userId, int clientType) {
        if (routers.get(userId) != null){
            LOGGER.info("lookup local router userId={}, router={}", userId, routers.get(userId).get(clientType));
            return routers.get(userId).get(clientType);
        } else {
            return EMPTY.get(clientType);
        }
    }

    public Map<String, Map<Integer, LocalRouter>> routers() {
        return routers;
    }

    /**
     * 监听链接关闭事件，清理失效的路由
     *
     * @param event
     */
    @Subscribe
    void on(ConnectionCloseEvent event) {
        Connection connection = event.connection;
        if (connection == null) return;
        SessionContext context = connection.getSessionContext();

        String userId = context.userId;
        if (userId == null) return;

        EventBus.I.post(new UserOfflineEvent(event.connection, userId));
        int clientType = context.getClientType();
        LocalRouter localRouter = null ;
        if (routers.get(userId) == null){
            localRouter = EMPTY.get(clientType);
        }else {
            localRouter = routers.get(userId).get(clientType);
        }
        if (localRouter == null) return;

        String connId = connection.getId();
        //2.检测下，是否是同一个链接, 如果客户端重连，老的路由会被新的链接覆盖
        if (connId.equals(localRouter.getRouteValue().getId())) {
            //3.删除路由
            if (routers.get(userId) == null){
                localRouter = EMPTY.remove(clientType);
            }else {
                localRouter = routers.get(userId).remove(clientType);
            }
            LOGGER.info("clean disconnected local route, userId={}, route={}", userId, localRouter);
        } else { //如果不相等，则log一下
            LOGGER.info("clean disconnected local route, not clean:userId={}, route={}", userId, localRouter);
        }
    }
}
