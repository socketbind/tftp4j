/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.packet;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 *
 * @author shevek
 */
public abstract class TftpPacket {

    // Substitute for netascii in the RFC. ISO_8859_1 is 8-bit transparent.
    public static final Charset CHARSET = Charsets.ISO_8859_1;
    private SocketAddress remoteAddress;

    @Nonnull
    public SocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(@Nonnull SocketAddress remoteAddress) {
        this.remoteAddress = Preconditions.checkNotNull(remoteAddress, "Remote address was null.");
    }

    /** At most this long, possibly less. */
    @Nonnegative
    public int getWireLength() {
        return 256;
    }

    @Nonnull
    public abstract TftpOpcode getOpcode();

    @Nonnull
    protected MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this).add("opcode", getOpcode());
    }

    public void toWire(@Nonnull ByteBuffer buffer) {
        buffer.putShort(getOpcode().getCode());
    }

    /** This is called after the opcode has already been read. */
    public abstract void fromWire(@Nonnull ByteBuffer buffer);

    protected static void putString(@Nonnull ByteBuffer buffer, @Nonnull String text) {
        buffer.put(text.getBytes(CHARSET));
        buffer.put((byte) 0);
    }

    @Nonnull
    protected static String getString(@Nonnull ByteBuffer buffer) {
        int start = buffer.position();
        int finish;
        int end = -1;
        for (finish = start; finish < buffer.limit(); finish++) {
            if (buffer.get(finish) == 0) {
                end = finish;
                finish++;
                break;
            }
        }
        // We didn't find a zero byte.
        if (end == -1)
            end = buffer.limit();

        byte[] bytes = new byte[end - start];
        buffer.get(bytes);
        buffer.position(finish);
        return new String(bytes, CHARSET);
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }
}
