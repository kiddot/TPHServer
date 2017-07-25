package com.android.server.core.push;

import com.android.server.common.Constants;
import com.android.server.core.connection.Connection;
import com.android.server.core.message.PushMessage;
import com.android.server.router.LocalRouter;
import com.android.server.utils.Jsons;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by root on 17-7-25.
 */
public class PushRequest extends FutureTask<PushResult> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PushRequest.class);
    private static final Callable<PushResult> NONE = new Callable<PushResult>() {
        @Override
        public PushResult call() throws Exception {
            return new PushResult(PushResult.CODE_FAILURE);
        }
    };
    private enum Status {init, success, failure, offline, timeout}
    private final AtomicReference<Status> status = new AtomicReference<>(Status.init);
    private AckModel ackModel;
    private Set<String> tags;
    private String condition;
    private PushCallback callback;
    private String userId;
    private byte[] content;
    private int timeout;
    private Connection connection;
    private int sessionId;
    private Future<?> future;
    private PushResult result;

    public PushRequest() {
        super(NONE);
    }

    private void sendToClient(LocalRouter localRouter){
        LOGGER.warn("sendToClient");
        if (localRouter != null){
            connection = localRouter.getRouteValue();
        }

        if (localRouter == null || connection == null){
            offline();
            return;
        }

        PushMessage pushMessage = PushMessage.build(connection)
                .setClientType(localRouter.getClientType())
                .setContent(content)
                .setTags(tags)
                .addFlag(ackModel.flag)
                .setUserId(userId)
                .setTimeout(timeout - 500);

        pushMessage.send(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()){
                    LOGGER.warn("PushMessage isSuccess");
                }
            }
        });

        sessionId = pushMessage.getSessionId();
        future = PushRequestBus.I.put(sessionId, PushRequest.this);
    }

    public static PushRequest build(PushContext pushContext){
        byte[] content = pushContext.getContext();
        PushMsg pushMsg = pushContext.getPushMsg();
        if (pushMsg != null){
            String json = Jsons.toJson(pushMsg);
            if (json != null){
                content = json.getBytes(Constants.UTF_8);
            }
        }

        return new PushRequest()
                .setAckModel(pushContext.getAckModel())
                .setUserId(pushContext.getUserId())
                .setTags(pushContext.getTags())
                .setCondition(pushContext.getCondition())
                .setContent(content)
                .setTimeout(pushContext.getTimeout())
                .setCallback(pushContext.getCallback());
    }

    private void sumit(Status status){
        if (this.status.compareAndSet(Status.init, status)){
            boolean isTimeoutEnd = status == Status.timeout;//任务是否超时结束

            if (future != null && !isTimeoutEnd){
                future.cancel(true);
            }

            super.set(getResult());

            if (callback != null) {//回调callback
                if (isTimeoutEnd) {//超时结束时，当前线程已经是线程池里的线程，直接调用callback
                    callback.onResult(getResult());
                } else {//非超时结束时，当前线程为Netty线程池，要异步执行callback
                    PushRequestBus.I.asyncCall(this);//会执行run方法
                }
            }
        }
    }

    private PushResult getResult() {
        if (result == null) {
            result = new PushResult(status.get().ordinal())
                    .setUserId(userId)
                    .setConnection(connection);
        }
        return result;
    }

    private void offline() {
        //offline
    }

    public long getTimeout() {
        return timeout;
    }


    public PushRequest setCallback(PushCallback callback) {
        this.callback = callback;
        return this;
    }

    public PushRequest setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public PushRequest setContent(byte[] content) {
        this.content = content;
        return this;
    }

    public PushRequest setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public PushRequest setAckModel(AckModel ackModel) {
        this.ackModel = ackModel;
        return this;
    }

    public PushRequest setTags(Set<String> tags) {
        this.tags = tags;
        return this;
    }

    public PushRequest setCondition(String condition) {
        this.condition = condition;
        return this;
    }

    @Override
    public String toString() {
        return "PushRequest{" +
                "content='" + (content == null ? -1 : content.length) + '\'' +
                ", userId='" + userId + '\'' +
                ", timeout=" + timeout +
                ", connection=" + connection +
                '}';
    }
}
