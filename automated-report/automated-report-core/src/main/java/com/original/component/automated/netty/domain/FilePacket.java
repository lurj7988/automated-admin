package com.original.component.automated.netty.domain;

import lombok.Data;

@Data
public class FilePacket {
    private String fileName;
    private String filePath;
    private String fileHash;
    private long fileSize;
}
