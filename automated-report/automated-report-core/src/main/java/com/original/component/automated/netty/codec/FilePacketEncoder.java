package com.original.component.automated.netty.codec;

import com.alibaba.fastjson.JSON;
import com.original.component.automated.netty.domain.FilePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.stream.ChunkedStream;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class FilePacketEncoder extends MessageToMessageEncoder<FilePacket> {
    @Override
    protected void encode(ChannelHandlerContext ctx, FilePacket msg, List<Object> out) throws IOException {
        String jsonString = JSON.toJSONString(msg);
        byte[] jsonContent = jsonString.getBytes();
        byte[] fileContent = Files.readAllBytes(Paths.get(msg.getFilePath()));

        // 计算偏移量和大小
        int jsonSize = jsonContent.length;
        int fileSize = fileContent.length;

        // 计算偏移量
        long jsonOffset = 0x18L; // JSON 的偏移量，从 0x18 开始
        long fileOffset = jsonOffset + jsonSize; // 文件的偏移量，紧跟在 JSON 数据后

        // 创建包含偏移量和大小信息的字节数组
        byte[] header = ByteBuffer.allocate(2 * Long.BYTES + 2 * Integer.BYTES)
                .putLong(jsonOffset) // JSON 偏移量
                .putInt(jsonSize)    // JSON 大小
                .putLong(fileOffset) // 文件偏移量
                .putInt(fileSize)    // 文件大小
                .array();

        // 将所有数据合并为一个输入流
        InputStream combinedStream = new SequenceInputStream(
                new SequenceInputStream(new ByteArrayInputStream(header), new ByteArrayInputStream(jsonContent)),
                new ByteArrayInputStream(fileContent));

        ChunkedStream chunkedStream = new ChunkedStream(combinedStream);
        out.add(chunkedStream);
    }

}
