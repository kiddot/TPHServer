package com.android.server.netty.codec;

import com.android.server.netty.codec.protocol.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import static com.android.server.netty.codec.protocol.Packet.encodePacket;


/**
 * length(4)+cmd(1)+cc(2)+flags(1)+sessionId(4)+lrc(1)+body(n)
 *
 */
@ChannelHandler.Sharable
public final class PacketEncoder extends MessageToByteEncoder<Packet> {
    public static final PacketEncoder INSTANCE = new PacketEncoder();

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
        encodePacket(packet, out);
    }
}
