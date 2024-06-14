package com.original.component.automated.netty.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import java.nio.file.Path;
import java.util.List;

public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final Path path;
    private final List<FileTransferListener> listeners;

    public ServerChannelInitializer(Path path, List<FileTransferListener> listeners) {
        this.path = path;
        this.listeners = listeners;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        //每个 Channel 一个 Handler 实例
        ChunkedStreamHandler chunkedStreamHandler = new ChunkedStreamHandler(path);
        chunkedStreamHandler.addListeners(listeners);
        channel.pipeline().addLast(chunkedStreamHandler);
    }
}
