/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.logging.LoggingHandler;

import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class TftpPipelineInitializer extends ChannelInitializer<Channel> {

    public static class SharedHandlers {
        // These are all singleton instances.

        private final TftpCodec codec = new TftpCodec();
        private final LoggingHandler packetLogger = new LoggingHandler("tftp-packet");

        private boolean debug = false;

        public void setDebug(boolean debug) {
            this.debug = debug;
        }
    }
    private final SharedHandlers sharedHandlers;
    private final ChannelHandler[] handlers;

    public TftpPipelineInitializer(@Nonnull SharedHandlers sharedHandlers, @Nonnull ChannelHandler... handlers) {
        this.sharedHandlers = sharedHandlers;
        this.handlers = handlers;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(sharedHandlers.codec);
        if (sharedHandlers.debug)
            pipeline.addLast(sharedHandlers.packetLogger);

        for (ChannelHandler handler : handlers) {
            pipeline.addLast(handler);
        }
    }
}