/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.anarres.tftp.protocol.engine.AbstractTftpReadTransfer;
import org.anarres.tftp.protocol.packet.TftpOAckPacket;
import org.anarres.tftp.protocol.packet.TftpPacket;
import org.anarres.tftp.protocol.resource.TftpData;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 *
 * @author shevek
 */
public class TftpReadTransfer extends AbstractTftpReadTransfer<Channel> {

    public TftpReadTransfer(
            @Nonnull SocketAddress remoteAddress,
            @Nonnull TftpData source, int blockSize, TftpOAckPacket oack) throws IOException {
        super(remoteAddress, source, blockSize, oack);
    }

    @Override
    public ByteBuffer allocate(Channel context, int length) {
        ByteBuf buf = Unpooled.buffer(length);
        return buf.nioBuffer(buf.writerIndex(), buf.writableBytes());
    }

    @Override
    public void send(Channel channel, TftpPacket packet) throws Exception {
        packet.setRemoteAddress(getRemoteAddress());
        channel.write(packet, channel.voidPromise());
    }

    @Override
    public void flush(Channel channel) throws Exception {
        channel.flush();
    }

    @Override
    public void close(Channel channel) throws Exception {
        channel.close().addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        super.close(channel);
    }
}
