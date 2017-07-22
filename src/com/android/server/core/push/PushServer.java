package com.android.server.core.push;

import com.android.server.common.ServerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.FutureTask;

/**
 * Created by root on 17-7-18.
 */
public class PushServer extends PushSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(PushServer.class);


    @Override
    FutureTask<PushResult> send(PushContext context) {
        return null;
    }

    private FutureTask<PushResult> send0(PushContext ctx) {
        if (ctx.isBroadcast()) {
            return null;
            //return PushRequest.build(factory, ctx).broadcast();
        } else {
//            Set<RemoteRouter> remoteRouters = CachedRemoteRouterManager.I.lookupAll(ctx.getUserId());
//            if (remoteRouters == null || remoteRouters.isEmpty()) {
//                return PushRequest.build(factory, ctx).onOffline();
//            }
//            FutureTask<PushResult> task = null;
//            for (RemoteRouter remoteRouter : remoteRouters) {
//                task = PushRequest.build(factory, ctx).send(remoteRouter);
//            }
            return null;
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
