package com.original.component.automated.netty.handler;

import com.original.component.automated.netty.codec.FilePacketEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) {
        channel.pipeline().addLast(new FilePacketEncoder());
        channel.pipeline().addFirst(new ChunkedWriteHandler());
    }
}
