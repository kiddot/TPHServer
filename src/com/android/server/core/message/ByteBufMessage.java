package com.android.server.core.message;

import com.android.server.common.Constants;
import com.android.server.core.connection.Connection;
import com.android.server.netty.codec.protocol.Packet;

import java.io.UnsupportedEncodingException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kiddo on 17-7-15.
 */

public abstract class ByteBufMessage extends BaseMessage {
    private static final Logger LOGGER = LoggerFactory.getLogger(ByteBufMessage.class);

    public ByteBufMessage(Packet message, Connection connection) {
        super(message, connection);
    }

    @Override
    public void decode(byte[] body) {
        decode(Unpooled.wrappedBuffer(body));
    }

    @Override
    public byte[] encode() {
        ByteBuf body = connection.getChannel().alloc().heapBuffer();
        LOGGER.debug("body" + body.toString());
        try {
            encode(body);
            byte[] bytes = new byte[body.readableBytes()];
            body.readBytes(bytes);
            return bytes;
        } catch (Exception e){
            LOGGER.debug("ByteBufMessage:encode()"+e.getMessage());
        } finally{
            body.release();
        }
        return null;
    }

    public abstract void decode(ByteBuf body);

    public abstract void encode(ByteBuf body);

    public void encodeString(ByteBuf body, String field) {
        try {
            encodeBytes(body, field == null ? null : field.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void encodeByte(ByteBuf body, byte field) {
        body.writeByte(field);
    }

    public void encodeInt(ByteBuf body, int field) {
        body.writeInt(field);
    }

    public void encodeLong(ByteBuf body, long field) {
        body.writeLong(field);
    }

    public void encodeBytes(ByteBuf body, byte[] field) {
        if (field == null || field.length == 0) {
            body.writeShort(0);
        } else if (field.length < Short.MAX_VALUE) {
            body.writeShort(field.length).writeBytes(field);
        } else {
            body.writeShort(Short.MAX_VALUE).writeInt(field.length - Short.MAX_VALUE).writeBytes(field);
        }
    }

    public String decodeString(ByteBuf body) {
        byte[] bytes = decodeBytes(body);
        if (bytes == null) return null;
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decodeBytes(ByteBuf body) {
        int fieldLength = body.readShort();
        LOGGER.debug("fieldLength" + fieldLength + ",Short.MAX_VALUE:" +Short.MAX_VALUE);
        if (fieldLength == 0) return null;
        if (fieldLength == Short.MAX_VALUE) {
            fieldLength += body.readInt();
        }
        byte[] bytes = null;
        try{
            bytes = new byte[fieldLength];
            body.readBytes(bytes);
        } catch (Exception e){
            LOGGER.warn(e.getMessage() + e.getMessage());
        }
        return bytes;
    }

    public byte decodeByte(ByteBuf body) {
        return body.readByte();
    }

    public int decodeInt(ByteBuf body) {
        return body.readInt();
    }

    public long decodeLong(ByteBuf body) {
        return body.readLong();
    }
}
