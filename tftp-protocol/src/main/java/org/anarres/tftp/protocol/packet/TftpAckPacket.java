/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.packet;

import com.google.common.base.MoreObjects;

import java.nio.ByteBuffer;

/**
 *
 * @author shevek
 */
public class TftpAckPacket extends TftpPacket {

    /** Cheat for an unsigned 2-byte value. */
    private char blockNumber;

    @Override
    public TftpOpcode getOpcode() {
        return TftpOpcode.ACK;
    }

    /**
     * @param blockNumber The block number, indexed from 1.
     */
    public void setBlockNumber(char blockNumber) {
        this.blockNumber = blockNumber;
    }

    /**
     * @return The block number, indexed from 1.
     */
    public char getBlockNumber() {
        return blockNumber;
    }

    @Override
    public void toWire(ByteBuffer buffer) {
        super.toWire(buffer);
        buffer.putChar(getBlockNumber());
    }

    @Override
    public void fromWire(ByteBuffer buffer) {
        setBlockNumber(buffer.getChar());
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper().add("blockNumber", (int) getBlockNumber());
    }
}
