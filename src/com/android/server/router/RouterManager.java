package com.android.server.router;

import java.util.Set;

/**
 * Created by root on 17-7-22.
 */
public interface RouterManager<R extends Router> {

    /**
     * 注册路由
     *
     * @param userId
     * @param router
     * @return
     */
    R register(String userId, R router);

    /**
     * 删除路由
     *
     * @param userId
     * @param clientType
     * @return
     */
    boolean unRegister(String userId, int clientType);

    /**
     * 查询路由
     *
     * @param userId
     * @return
     */
    Set<R> lookupAll(String userId);

    /**
     * 查询路由
     *
     * @param userId
     * @param clientType
     * @return
     */
    R lookup(String userId, int clientType);
}
