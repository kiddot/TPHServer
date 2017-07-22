package com.android.server.core.handler;

import com.android.server.core.PushCenter;
import com.android.server.core.connection.Connection;
import com.android.server.core.message.AckMessage;
import com.android.server.core.message.PushMessage;
import com.android.server.core.task.SingleUserPushTask;
import com.android.server.netty.codec.protocol.Packet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kiddo on 17-7-15.
 */

public final class ClientPushHandler extends BaseMessageHandler<PushMessage> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientPushHandler.class);
    private Connection mConnection;

    @Override
    public PushMessage decode(Packet packet, Connection connection) {
        mConnection = connection;
        return new PushMessage(packet, connection);
    }

    @Override
    public void handle(PushMessage message) {
        LOGGER.warn("receive client push message={}", message);
        System.out.println("receive client push message={}" + message + "autoAck:" + message.autoAck());
        if (message.autoAck()) {
            AckMessage.from(message).sendRaw();
            LOGGER.warn("send ack for push message={}", message);

        }
        System.out.println("PushCenter:" + message);
        message.send();
        //PushCenter.I.push(message, mConnection);
        //biz code write here
    }


    public MessageHandler get() {
        return this;
    }
}
