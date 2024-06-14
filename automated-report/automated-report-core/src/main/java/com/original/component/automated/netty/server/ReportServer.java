package com.original.component.automated.netty.server;


import com.original.component.automated.netty.handler.FileTransferListener;
import com.original.component.automated.netty.handler.ServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.List;

@Slf4j
public class ReportServer {

    private final EventLoopGroup parentGroup = new NioEventLoopGroup();
    private final EventLoopGroup childGroup = new NioEventLoopGroup();
    private Channel channel;
    private final Path path;
    private final List<FileTransferListener> listeners;

    public ReportServer(Path path, List<FileTransferListener> listeners) {
        this.path = path;
        this.listeners = listeners;
    }

    public void start(int port) {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO)) // Add logging handler for better debugging
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new ServerChannelInitializer(path, listeners));

            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channel = channelFuture.channel();

            log.info("Server started and listening on {}", channel.localAddress());

            // Add shutdown hook to gracefully shut down the server
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Shutdown hook invoked, shutting down gracefully...");
                shutdown();
            }));

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Server interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            shutdown();
        }
    }

    private void shutdown() {
        log.info("Shutting down event loops...");
        if (channel != null) {
            channel.close();
        }
        parentGroup.shutdownGracefully().addListener(future -> {
            if (future.isSuccess()) {
                log.info("Parent group shut down successfully.");
            } else {
                log.error("Error shutting down parent group.", future.cause());
            }
        });
        childGroup.shutdownGracefully().addListener(future -> {
            if (future.isSuccess()) {
                log.info("Child group shut down successfully.");
            } else {
                log.error("Error shutting down child group.", future.cause());
            }
        });
    }

}

