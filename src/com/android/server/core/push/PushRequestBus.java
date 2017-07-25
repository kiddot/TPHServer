package com.android.server.core.push;

import com.android.server.common.BaseService;
import com.android.server.common.ServerListener;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by root on 17-7-25.
 */
public class PushRequestBus extends BaseService {
    public static final PushRequestBus I = new PushRequestBus();
    private final Logger logger = LoggerFactory.getLogger(PushRequestBus.class);
    private final Map<Integer, PushRequest> reqQueue = new ConcurrentHashMap<>(1024);
    private ScheduledExecutorService scheduledExecutor;

    private PushRequestBus() {
    }

    public Future<?> put(int sessionId, PushRequest request) {
        reqQueue.put(sessionId, request);
        return scheduledExecutor.schedule(request, request.getTimeout(), TimeUnit.MILLISECONDS);
    }

    public PushRequest getAndRemove(int sessionId) {
        return reqQueue.remove(sessionId);
    }

    public void asyncCall(Runnable runnable) {
        scheduledExecutor.execute(runnable);
    }

    @Override
    protected void doStart(ServerListener listener) throws Throwable {
        scheduledExecutor = Executors.newScheduledThreadPool(3);
        listener.onSuccess();
    }

    @Override
    protected void doStop(ServerListener listener) throws Throwable {
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdown();
        }
        listener.onSuccess();
    }

    @Override
    public boolean syncStart() {
        return false;
    }

    @Override
    public boolean syncStop() {
        return false;
    }
}
