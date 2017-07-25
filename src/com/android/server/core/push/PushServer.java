package com.android.server.core.push;

import com.android.server.common.ServerListener;
import com.android.server.router.LocalRouter;
import com.android.server.router.LocalRouterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.FutureTask;

/**
 * Created by root on 17-7-18.
 */
public class PushServer extends PushSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(PushServer.class);


    @Override
    FutureTask<PushResult> send(PushContext context) {
        if (context.isBroadcast()) {
            return send0(context.setUserId(null));
        } else if (context.getUserId() != null) {
            return send0(context);
        } else if (context.getUserIds() != null) {
            FutureTask<PushResult> task = null;
            for (String userId : context.getUserIds()) {
                task = send0(context.setUserId(userId));
            }
            return task;
        } else {
            LOGGER.warn("PushServer: param error.");
            return null;
        }
    }

    private FutureTask<PushResult> send0(PushContext ctx) {
        if (ctx.isBroadcast()) {
            //TODO: broadcast
            return null;
            //return PushRequest.build(factory, ctx).broadcast();
        } else {
            LocalRouterManager localRouterManager = LocalRouterManager.getInstance();
            Set<LocalRouter> localRouters = localRouterManager.lookupAll(ctx.getUserId());
            if (localRouters == null || localRouters.isEmpty()) {
                //TODO: 做离线处理
                LOGGER.warn("localRouters == null");
                return PushRequest.build(ctx).onOffline();
            }
            FutureTask<PushResult> task = null;
            for (LocalRouter localRouter : localRouters) {
                task = PushRequest.build(ctx).send(localRouter);
            }
            return task;
        }
    }

    @Override
    public void start(ServerListener listener) {

    }

    @Override
    public void stop(ServerListener listener) {

    }

    @Override
    public boolean syncStart() {
        return false;
    }

    @Override
    public boolean syncStop() {
        return false;
    }

    @Override
    public void init() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
