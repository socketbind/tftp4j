package org.anarres.tftp.protocol.packet;

import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

public class TftpOAckPacket extends TftpPacket {

    private static final Logger LOG = LoggerFactory.getLogger(TftpOAckPacket.class);

    private Integer timeout;

    private Integer blockSize;

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Integer getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    @Nonnull
    @Override
    public TftpOpcode getOpcode() {
        return TftpOpcode.ACK_WITH_OPTIONS;
    }

    @Override
    public void fromWire(@Nonnull ByteBuffer buffer) {
        while (buffer.hasRemaining()) {
            String word = getString(buffer);
            if ("blksize".equalsIgnoreCase(word)) {
                blockSize = Integer.parseInt(getString(buffer));
            } else if ("timeout".equalsIgnoreCase(word)) {
                timeout = Integer.parseInt(getString(buffer));
            } else {
                skipString(buffer);
                LOG.error("Unknown TFTP command word " + word);
            }
        }
    }

    @Override
    public void toWire(@Nonnull ByteBuffer buffer) {
        super.toWire(buffer);

        if (timeout != null) {
            buffer.put("timeout".getBytes());
            buffer.put((byte) 0);
            buffer.put(String.valueOf(timeout).getBytes());
            buffer.put((byte) 0);
        }

        if (blockSize != null) {
            buffer.put("blksize".getBytes());
            buffer.put((byte) 0);
            buffer.put(String.valueOf(blockSize).getBytes());
            buffer.put((byte) 0);
        }
    }

    @Nonnull
    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper().add("timeout", timeout).add("blockSize", blockSize);
    }
}
