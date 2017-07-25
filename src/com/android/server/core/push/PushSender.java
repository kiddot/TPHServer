package com.android.server.core.push;


import com.android.server.common.Service;

import java.util.concurrent.FutureTask;

/**
 * Created by root on 17-7-18.
 */
public abstract class PushSender implements Service {

    /**
     * 推送push消息
     *
     * @param context 推送参数
     * @return FutureTask 可用于同步调用
     */
    abstract FutureTask<PushResult> send(PushContext context);

    /**
     * 推送push消息
     * @param context
     * @param userId
     * @param callback
     * @return
     */
    FutureTask<PushResult> send(String context, String userId, PushCallback callback) {
        return send(PushContext
                .build(context)
                .setUserId(userId)
                .setCallback(callback)
        );
    }

    FutureTask<PushResult> send(String context, String userId, AckModel ackModel, PushCallback callback) {
        return send(PushContext
                .build(context)
                .setAckModel(ackModel)
                .setUserId(userId)
                .setCallback(callback)
        );
    }
}
