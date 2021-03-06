package com.android.server.core.handler;

import com.android.server.common.event.UserOfflineEvent;
import com.android.server.common.event.UserOnlineEvent;
import com.android.server.core.connection.Connection;
import com.android.server.core.connection.SessionContext;
import com.android.server.core.message.BindUserMessage;
import com.android.server.core.message.ErrorMessage;
import com.android.server.core.message.OkMessage;
import com.android.server.netty.codec.protocol.Command;
import com.android.server.netty.codec.protocol.Packet;
import com.android.server.router.LocalRouter;
import com.android.server.router.LocalRouterManager;
import com.android.server.router.RouterCenter;
import com.android.server.utils.EventBus;
import com.android.server.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kiddo on 17-7-15.
 */

public final class BindUserHandler extends BaseMassageHandler<BindUserMessage> {
    //private BindValidator validator = BindValidatorFactory.create();
    public static final Logger LOGGER = LoggerFactory.getLogger(RouterCenter.class);


    @Override
    public BindUserMessage decode(Packet packet, Connection connection) {
        return new BindUserMessage(packet, connection);
    }

    @Override
    public void handle(BindUserMessage message) {
        if (message.getPacket().cmd == Command.BIND.cmd) {
            bind(message);
        } else {
            unbind(message);
        }
    }

    private void bind(BindUserMessage message) {
        if (Strings.isNullOrEmpty(message.userId)) {
            ErrorMessage.from(message).setReason("invalid param").close();
            LOGGER.warn("bind user failure for invalid param, conn={}", message.getConnection());
            return;
        }
        //1.绑定用户时先看下是否握手成功
        SessionContext context = message.getConnection().getSessionContext();
        if (context.handshakeOk()) {
            //处理重复绑定问题
            if (context.userId != null) {
                if (message.userId.equals(context.userId)) {
                    context.tags = message.tags;
                    OkMessage.from(message).setData("bind success").sendRaw();
                    LOGGER.warn("rebind user success, userId={}, session={}", message.userId, context);
                    return;
                } else {
                    unbind(message);
                }
            }
            //2.如果握手成功，就把用户链接信息注册到路由中心，本地和远程各一份
            boolean success = RouterCenter.I.register(message.userId, message.getConnection());
            if (success) {
                context.userId = message.userId;
                context.tags = message.tags;
                //EventBus.I.post(new UserOnlineEvent(message.getConnection(), message.userId));
                OkMessage.from(message).setData("bind success").sendRaw();
                LOGGER.warn("bind user success, userId={}, session={}", message.userId, context);
            } else {
                //3.注册失败再处理下，防止本地注册成功，远程注册失败的情况，只有都成功了才叫成功
                RouterCenter.I.unRegister(message.userId, context.getClientType());
                ErrorMessage.from(message).setReason("bind failed").close();
                LOGGER.warn("bind user failure, userId={}, session={}", message.userId, context);
            }
        } else {
            ErrorMessage.from(message).setReason("not handshake").close();
            LOGGER.warn("bind user failure not handshake, userId={}, conn={}", message.userId, message.getConnection());
        }
    }

    /**
     * 目前是以用户维度来存储路由信息的，所以在删除路由信息时要判断下是否是同一个设备
     * 后续可以修改为按设备来存储路由信息。
     *
     * @param message
     */
    private void unbind(BindUserMessage message) {
        if (Strings.isNullOrEmpty(message.userId)) {
            ErrorMessage.from(message).setReason("invalid param").close();
            LOGGER.warn("unbind user failure invalid param, session={}", message.getConnection().getSessionContext());
            return;
        }
        //1.解绑用户时先看下是否握手成功
        SessionContext context = message.getConnection().getSessionContext();
        if (context.handshakeOk()) {
            //2.先删除远程路由, 必须是同一个设备才允许解绑
            boolean unRegisterSuccess = true;
            int clientType = context.getClientType();
            String userId = context.userId;
            //RemoteRouterManager remoteRouterManager = RouterCenter.I.getRemoteRouterManager();
            //RemoteRouter remoteRouter = remoteRouterManager.lookup(userId, clientType);
//            if (remoteRouter != null) {
//                String deviceId = remoteRouter.getRouteValue().getDeviceId();
//                if (context.deviceId.equals(deviceId)) {//判断是否是同一个设备
//                    unRegisterSuccess = remoteRouterManager.unRegister(userId, clientType);
//                }
//            }
            //3.删除本地路由信息
            LocalRouterManager localRouterManager = RouterCenter.I.getLocalRouterManager();
            LocalRouter localRouter = localRouterManager.lookup(userId, clientType);
            if (localRouter != null) {
                String deviceId = localRouter.getRouteValue().getSessionContext().deviceId;
                if (context.deviceId.equals(deviceId)) {//判断是否是同一个设备
                    unRegisterSuccess = localRouterManager.unRegister(userId, clientType) && unRegisterSuccess;
                }
            }

            //4.路由删除成功，广播用户下线事件
            if (unRegisterSuccess) {
                context.userId = null;
                context.tags = null;
                EventBus.I.post(new UserOfflineEvent(message.getConnection(), userId));
                OkMessage.from(message).setData("unbind success").sendRaw();
                LOGGER.warn("unbind user success, userId={}, session={}", userId, context);
            } else {
                ErrorMessage.from(message).setReason("unbind failed").sendRaw();
                LOGGER.warn("unbind user failure, unRegister router failure, userId={}, session={}", userId, context);
            }
        } else {
            ErrorMessage.from(message).setReason("not handshake").close();
            LOGGER.warn("unbind user failure not handshake, userId={}, session={}", message.userId, context);
        }
    }


//    @Spi(order = 1)
//    public static class DefaultBindValidatorFactory implements BindValidatorFactory {
//        private final BindValidator validator = userId -> true;
//
//        @Override
//        public BindValidator get() {
//            return validator;
//        }
//    }
}
