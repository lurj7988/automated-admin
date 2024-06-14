package com.original.component.automated.netty.client;


import com.original.component.automated.netty.domain.FilePacket;
import com.original.component.automated.netty.handler.ClientChannelInitializer;
import com.original.component.automated.netty.utils.FileUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class FileTransferClient {
    private final String host;
    private final int port;
    private Channel channel;
    private EventLoopGroup group;

    public FileTransferClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.AUTO_READ, true)
                    .handler(new ClientChannelInitializer());
            ChannelFuture f = b.connect(host, port).sync();
            channel = f.channel();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void sendFile(Path filePath, GenericFutureListener<? extends Future<? super Void>> listener) throws IOException, NoSuchAlgorithmException {
        File file = filePath.toFile();
        if (channel != null && channel.isActive()) {
            FilePacket filePacket = new FilePacket();
            filePacket.setFileName(file.getName());
            filePacket.setFilePath(file.getAbsolutePath());
            filePacket.setFileSize(file.length());
            filePacket.setFileHash(FileUtils.calculateFileHash(filePacket.getFilePath(), "SHA-256"));
            ChannelFuture future = channel.writeAndFlush(filePacket);
            future.addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()) {
                    log.info("File sent successfully: {}", filePath);
                } else {
                    log.error("Failed to send file: {}", filePath, future.cause());
                }
                channelFuture.channel().close();
            });
            future.addListener(listener);
        } else {
            log.error("Channel is not active. Cannot send file: {}", filePath);
        }
    }

    public void sendFile(Path filePath) throws IOException, NoSuchAlgorithmException {
        sendFile(filePath, null);
    }

    public void stop() {
        if (channel != null) {
            channel.closeFuture().syncUninterruptibly();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
    }

}

