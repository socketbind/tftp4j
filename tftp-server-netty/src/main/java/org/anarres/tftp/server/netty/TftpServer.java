/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.anarres.tftp.protocol.engine.AbstractTftpServer;
import org.anarres.tftp.protocol.resource.TftpDataProvider;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.concurrent.ThreadFactory;

/**
 *
 * @author shevek
 */
public class TftpServer extends AbstractTftpServer {

    private int timeoutSeconds = 5;
    private Channel channel;
    private final TftpPipelineInitializer.SharedHandlers sharedHandlers = new TftpPipelineInitializer.SharedHandlers();
    private TftpChannelType channelType = TftpChannelType.NIO;

    public TftpServer(@Nonnull TftpDataProvider dataProvider, @Nonnegative int port) {
        super(dataProvider, port);
    }

    public TftpServer(@Nonnull TftpDataProvider dataProvider) {
        super(dataProvider, DEFAULT_SERVER_PORT);
    }

    public void setDebug(boolean debug) {
        sharedHandlers.setDebug(debug);
    }

    @Nonnull
    public TftpChannelType getChannelType() {
        return channelType;
    }

    public void setChannelType(@Nonnull TftpChannelType channelType) {
        this.channelType = channelType;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public void start() throws IOException, InterruptedException {
        TftpChannelType mode = getChannelType();

        ThreadFactory factory = new DefaultThreadFactory("tftp-server");
        EventLoopGroup group = mode.newEventLoopGroup(factory);

        Bootstrap b = new Bootstrap();
        b.group(group);
        b.channel(mode.getChannelType());
        b.handler(new TftpPipelineInitializer(sharedHandlers, new TftpServerHandler(sharedHandlers, getDataProvider(), timeoutSeconds)));
        channel = b.bind(getPort()).sync().channel();
    }

    @Override
    public void stop() throws IOException, InterruptedException {
        EventLoop loop = channel.eventLoop();
        channel.close().sync();
        loop.shutdownGracefully();
    }
}
