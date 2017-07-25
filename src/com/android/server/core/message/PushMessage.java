package com.android.server.core.message;

import com.android.server.core.connection.Connection;
import com.android.server.netty.codec.protocol.JsonPacket;
import com.android.server.netty.codec.protocol.Packet;

import io.netty.channel.ChannelFutureListener;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.android.server.netty.codec.protocol.Command.PUSH;

/**
 * Created by kiddo on 17-7-15.
 */

public class PushMessage extends BaseMessage {
    public byte[] content;

    public String userId;
    public int clientType;
    public int timeout;

    public String taskId;
    public Set<String> tags;
    public String condition;

    public PushMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    public static PushMessage build(Connection connection) {
        if (connection.getSessionContext().isSecurity()) {
            return new PushMessage(new Packet(PUSH, genSessionId()), connection);
        } else {
            return new PushMessage(new JsonPacket(PUSH, genSessionId()), connection);
        }
    }

    public static PushMessage buildPacket(Connection connection) {
        return new PushMessage(new Packet(PUSH, genSessionId()), connection);
    }

    @Override
    public void decode(byte[] body) {
        content = body;
    }

    @Override
    public byte[] encode() {
        return content;
    }

    @Override
    public void decodeJsonBody(Map<String, Object> body) {
        String content = (String) body.get("content");
        if (content != null) {
            try {
                this.content = content.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Map<String, Object> encodeJsonBody() {
        if (content != null) {
            try {
                String cont = new String(content, "UTF-8");
                Object o = cont;
                return Collections.singletonMap("content", o);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean autoAck() {
        return packet.hasFlag(Packet.FLAG_AUTO_ACK);
    }

    public boolean needAck() {
        return packet.hasFlag(Packet.FLAG_BIZ_ACK) || packet.hasFlag(Packet.FLAG_AUTO_ACK);
    }

    @Override
    public void send(ChannelFutureListener listener) {
        super.send(listener);
        this.content = null;//释放内存
    }

    @Override
    public String toString() {
        return "PushMessage{" +
                "content='" + content.length + '\'' +
                ", packet=" + packet +
                '}';
    }

    public PushMessage setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public PushMessage setContent(byte[] content) {
        this.content = content;
        return this;
    }

    public PushMessage setClientType(int clientType) {
        this.clientType = clientType;
        return this;
    }

    public PushMessage addFlag(byte flag) {
        packet.addFlag(flag);
        return this;
    }

    public PushMessage setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public PushMessage setTags(Set<String> tags) {
        this.tags = tags;
        return this;
    }

    public PushMessage setCondition(String condition) {
        this.condition = condition;
        return this;
    }
}
