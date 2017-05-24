/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.ReferenceCountUtil;
import org.anarres.tftp.protocol.engine.TftpTransfer;
import org.anarres.tftp.protocol.packet.*;
import org.anarres.tftp.protocol.resource.TftpData;
import org.anarres.tftp.protocol.resource.TftpDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class TftpServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(TftpServerHandler.class);
    private final TftpPipelineInitializer.SharedHandlers sharedHandlers;
    private final TftpDataProvider provider;
    private final int timeoutSeconds;

    public TftpServerHandler(@Nonnull TftpPipelineInitializer.SharedHandlers sharedHandlers, @Nonnull TftpDataProvider provider, int timeoutSeconds) {
        this.sharedHandlers = sharedHandlers;
        this.provider = provider;
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            final TftpPacket packet = (TftpPacket) msg;
            Channel channel = ctx.channel();

            switch (packet.getOpcode()) {
                case RRQ: {
                    TftpRequestPacket request = (TftpRequestPacket) packet;
                    TftpData source = provider.open(request.getFilename());
                    if (source == null) {
                        ctx.writeAndFlush(new TftpErrorPacket(packet.getRemoteAddress(), TftpErrorCode.FILE_NOT_FOUND), ctx.voidPromise());
                    } else {
                        TftpOAckPacket oack = null;

                        if (request.isBlockSizeOptionPresent() || request.isTimeoutOptionPresent()) {
                            oack = new TftpOAckPacket();

                            if (request.isBlockSizeOptionPresent()) {
                                oack.setBlockSize(request.getBlockSize());
                            }

                            if (request.isTimeoutOptionPresent()) {
                                oack.setTimeout(request.getTimeout());
                            }

                            oack.setRemoteAddress(packet.getRemoteAddress());
                        }

                        TftpTransfer<Channel> transfer = new TftpReadTransfer(packet.getRemoteAddress(), source, request.getBlockSize(), oack);

                        Bootstrap bootstrap = new Bootstrap()
                                .group(ctx.channel().eventLoop())
                                .channel(channel.getClass())
                                .handler(new TftpPipelineInitializer(sharedHandlers, new ReadTimeoutHandler(request.isTimeoutOptionPresent() ? request.getTimeout() : timeoutSeconds), new TftpTransferHandler(transfer)));

                        bootstrap.connect(packet.getRemoteAddress());
                    }

                    break;
                }
                case WRQ: {
                    ctx.writeAndFlush(new TftpErrorPacket(packet.getRemoteAddress(), TftpErrorCode.PERMISSION_DENIED), ctx.voidPromise());
                    break;
                }
                case ACK: {
                    break;
                }
                case DATA: {
                    LOG.warn("Unexpected TFTP " + packet.getOpcode() + " packet: " + packet);
                    ctx.writeAndFlush(new TftpErrorPacket(packet.getRemoteAddress(), TftpErrorCode.ILLEGAL_OPERATION), ctx.voidPromise());
                    break;
                }
                case ERROR: {
                    LOG.error("Received TFTP error packet: {}", packet);
                    break;
                }
                default: {
                    LOG.error("Received unknown TFTP packet: {}", packet);
                    break;
                }
            }

        } catch (Exception e) {
            ctx.fireExceptionCaught(e);
            throw e;
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("Error on channel: " + cause, cause);
    }
}
