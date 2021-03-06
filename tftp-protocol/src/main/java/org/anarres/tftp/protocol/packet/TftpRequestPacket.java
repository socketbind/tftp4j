/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.packet;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

/**
 *
 * @author shevek
 */
public abstract class TftpRequestPacket extends TftpPacket {

    private static final Logger LOG = LoggerFactory.getLogger(TftpRequestPacket.class);
    private String filename;
    private TftpMode mode;
    private int blockSize = TftpDataPacket.BLOCK_SIZE;
    private boolean blockSizeOptionPresent = false;
    private int timeout = 5;
    private boolean timeoutOptionPresent = false;

    public String getFilename() {
        return filename;
    }

    public void setFilename(@Nonnull String filename) {
        this.filename = Preconditions.checkNotNull(filename, "Filename was null.");
    }

    public TftpMode getMode() {
        return mode;
    }

    public void setMode(@Nonnull TftpMode mode) {
        this.mode = mode;
    }

    @Nonnegative
    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(@Nonnegative int blockSize) {
        this.blockSize = blockSize;
    }

    public boolean isBlockSizeOptionPresent() {
        return blockSizeOptionPresent;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isTimeoutOptionPresent() {
        return timeoutOptionPresent;
    }

    @Override
    public void toWire(ByteBuffer buffer) {
        super.toWire(buffer);
        putString(buffer, getFilename());
        putString(buffer, getMode().name());
    }

    @Override
    public void fromWire(ByteBuffer buffer) {
        setFilename(getString(buffer));
        setMode(TftpMode.forMode(getString(buffer)));
        // This is enough to go on with, so we'll do our best with the rest.
        try {
            // RFC2348: blocksize option
            // TODO: Send an OACK to the client.
            while (buffer.hasRemaining()) {
                String word = getString(buffer);
                if ("blksize".equalsIgnoreCase(word)) {
                    blockSizeOptionPresent = true;
                    blockSize = Integer.parseInt(getString(buffer));
                    // TODO: Assert blockSize < 16K for safety.
                } else if ("timeout".equalsIgnoreCase(word)) {
                    timeoutOptionPresent = true;
                    timeout = Integer.parseInt(getString(buffer));
                } else if ("tsize".equalsIgnoreCase(word)) {
                    skipString(buffer);
                    LOG.error("Unhandled TFTP tsize");
                } else {
                    skipString(buffer);
                    LOG.error("Unknown TFTP command word " + word);
                }
            }
        } catch (Exception e) {
            LOG.warn("Failed to parse optional TFTP trailer: continuing anyway", e);
        }
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("filename", getFilename())
                .add("mode", getMode())
                .add("blockSize", getBlockSize())
                .add("blockSizeOptionPresent", isBlockSizeOptionPresent())
                .add("timeout", getTimeout())
                .add("timeoutOptionPresent", isTimeoutOptionPresent());
    }
}