/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.mina;

import org.anarres.tftp.protocol.engine.AbstractTftpReadTransfer;
import org.anarres.tftp.protocol.packet.TftpOAckPacket;
import org.anarres.tftp.protocol.packet.TftpPacket;
import org.anarres.tftp.protocol.resource.TftpData;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 *
 * @author shevek
 */
public class TftpReadTransfer extends AbstractTftpReadTransfer<IoSession> {

    public TftpReadTransfer(@Nonnull SocketAddress remoteAddress, @Nonnull TftpData source, int blockSize, TftpOAckPacket oAckPacket) throws IOException {
        super(remoteAddress, source, blockSize, oAckPacket);
    }

    @Override
    public ByteBuffer allocate(IoSession context, int length) {
        return IoBuffer.allocate(length).buf();
    }

    @Override
    public void send(IoSession session, TftpPacket packet) throws Exception {
        session.write(packet, getRemoteAddress());
    }

    @Override
    public void flush(IoSession session) throws Exception {
    }

    @Override
    public void close(IoSession session) throws Exception {
        session.close(false);
        super.close(session);
    }
}
