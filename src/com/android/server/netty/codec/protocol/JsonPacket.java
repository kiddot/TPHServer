package com.android.server.netty.codec.protocol;


import com.android.server.utils.Jsons;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public final class JsonPacket extends Packet {

    public Map<String, Object> body;

    public JsonPacket() {
        super(Command.UNKNOWN);
        this.addFlag(FLAG_JSON_BODY);
    }

    public JsonPacket(Command cmd) {
        super(cmd);
        this.addFlag(FLAG_JSON_BODY);
    }

    public JsonPacket(Command cmd, int sessionId) {
        super(cmd, sessionId);
        this.addFlag(FLAG_JSON_BODY);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getBody() {
        return body;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void setBody(T body) {
        this.body = (Map<String, Object>) body;
    }

    @Override
    public int getBodyLength() {
        return body == null ? 0 : body.size();
    }

    @Override
    public Packet response(Command command) {
        return new JsonPacket(command, sessionId);
    }

    @Override
    public Object toFrame(Channel channel) {
        byte[] json = new byte[0];
        try {
            json = Jsons.toJson(this).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new TextWebSocketFrame(Unpooled.wrappedBuffer(json));
    }

    @Override
    public String toString() {
        return "JsonPacket{" +
                "cmd=" + cmd +
                ", cc=" + cc +
                ", flags=" + flags +
                ", sessionId=" + sessionId +
                ", lrc=" + lrc +
                ", body=" + body +
                '}';
    }
}