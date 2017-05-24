/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.packet;

import com.google.common.base.MoreObjects;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 *
 * @author shevek
 */
public class TftpErrorPacket extends TftpPacket {

    private short errorCode;
    private String errorMessage;

    public TftpErrorPacket() {
    }

    public TftpErrorPacket(SocketAddress remoteAddress, short errorCode, String errorMessage) {
        setRemoteAddress(remoteAddress);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public TftpErrorPacket(SocketAddress remoteAddress, TftpErrorCode errorCode) {
        this(remoteAddress, errorCode.getCode(), errorCode.getDescription());
    }

    @Override
    public TftpOpcode getOpcode() {
        return TftpOpcode.ERROR;
    }

    public short getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(short errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public void toWire(ByteBuffer buffer) {
        super.toWire(buffer);
        buffer.putShort(getErrorCode());
        putString(buffer, getErrorMessage());
    }

    @Override
    public void fromWire(ByteBuffer buffer) {
        setErrorCode(buffer.getShort());
        setErrorMessage(getString(buffer));
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper().add("errorCode", getErrorCode()).add("errorMessage", getErrorMessage());
    }
}
