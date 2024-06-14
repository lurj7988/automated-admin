package com.original.component.automated.netty.handler;

import com.alibaba.fastjson.JSON;
import com.original.component.automated.netty.domain.FilePacket;
import com.original.component.automated.netty.utils.FileUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class ChunkedStreamHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final ByteBuf accumulation = Unpooled.buffer();
    private static final int JSON_OFFSET_SIZE = 0x8;
    private static final int JSON_SIZE_SIZE = 0x4;
    private static final int FILE_OFFSET_SIZE = 0x8;
    private static final int FILE_SIZE_SIZE = 0x4;
    private static final int HEADER_SIZE = JSON_OFFSET_SIZE + JSON_SIZE_SIZE + FILE_OFFSET_SIZE + FILE_SIZE_SIZE;
    private boolean canWrite = true;
    private final Path path;
    private final List<FileTransferListener> listeners = new CopyOnWriteArrayList<>();
    private FilePacket filePacket;

    public ChunkedStreamHandler() {
        this.path = Paths.get("D:\\RanorexStudio Projects\\Reports\\receive");
    }

    public ChunkedStreamHandler(Path path) {
        //客户端每次连接都会生成一个新的path
        this.path = path.resolve(DateTimeFormatter.ofPattern("yyyyMM").format(LocalDateTime.now()));
        log.info("current path is {}", this.path);
    }

    public void addListener(FileTransferListener listener) {
        listeners.add(listener);
    }

    public void addListeners(FileTransferListener... listeners) {
        Collections.addAll(this.listeners, listeners);
    }

    public void addListeners(List<FileTransferListener> listeners) {
        this.listeners.addAll(listeners);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        if (canWrite) {
            accumulation.writeBytes(msg);
            if (accumulation.readableBytes() >= HEADER_SIZE) {
                long jsonOffset = accumulation.getLong(0x00);
                int jsonSize = accumulation.getInt(0x08);
                long fileOffset = accumulation.getLong(0x0C);
                int fileSize = accumulation.getInt(0x14);
                if (accumulation.readableBytes() >= HEADER_SIZE + jsonSize) {
                    byte[] jsonBytes = new byte[jsonSize];
                    accumulation.getBytes((int) jsonOffset, jsonBytes);
                    String json = new String(jsonBytes);
                    filePacket = JSON.parseObject(json, FilePacket.class);
                    log.info("fileSize:{}({}),json:{}", fileSize, filePacket.getFileSize(), json);
                    canWrite = false;
                    //计算除了head和json剩余的数据
                    byte[] data = new byte[accumulation.readableBytes() - HEADER_SIZE - jsonSize];
                    accumulation.getBytes((int) fileOffset, data);
                    //等价
                    //accumulation.readBytes(data);
                    log.info("readerIndex:{},fileOffset:{},fileSize:{},readableBytes:{},lastdata:{}", accumulation.readerIndex(), fileOffset, fileSize, accumulation.readableBytes(), accumulation.readableBytes() - HEADER_SIZE - jsonSize);
                    writeFile(data);
                    accumulation.clear();
                }
            }
        } else {
            byte[] byteArray = new byte[msg.readableBytes()];
            msg.readBytes(byteArray);
            appendToFile(byteArray);
        }
    }

    private void writeFile(byte[] data) throws IOException, NoSuchAlgorithmException {
        Path filePath = path.resolve(filePacket.getFileName());
        if (!Files.exists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath.toFile(), "rw")) {
            randomAccessFile.seek(0);
            randomAccessFile.setLength(0);
            randomAccessFile.write(data);
            complete(randomAccessFile);
        }
    }

    private void appendToFile(byte[] data) throws IOException, NoSuchAlgorithmException {
        Path filePath = path.resolve(filePacket.getFileName());
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath.toFile(), "rw")) {
            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.write(data);
            complete(randomAccessFile);
        }
    }

    private void complete(RandomAccessFile randomAccessFile) throws IOException, NoSuchAlgorithmException {
        // complete
        if (randomAccessFile.length() >= filePacket.getFileSize()) {
            Path filePath = path.resolve(filePacket.getFileName());
            String SHA256 = FileUtils.calculateFileHash(filePath.toString(), "SHA-256");
            if (SHA256.equals(filePacket.getFileHash())) {
                log.info("Success: Received file {}", filePacket.getFileName());
                listeners.forEach(listener -> listener.onSuccess(filePath));
            } else {
                log.error("Failed: Received file {}, SHA256 is different: {} {}", filePacket.getFileName(), SHA256, filePacket.getFileHash());
                listeners.forEach(listener -> listener.onFailure(filePath));
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }

    /**
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        log.info("客户端建立连接，channelId:{},IP:{},Port:{}", channel.id(), channel.localAddress().getHostString(), channel.localAddress().getPort());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端断开链接{}", ctx.channel().localAddress().toString());
        listeners.forEach(listener -> listener.onFinished(path.resolve(filePacket.getFileName())));
        super.channelInactive(ctx);
    }
}