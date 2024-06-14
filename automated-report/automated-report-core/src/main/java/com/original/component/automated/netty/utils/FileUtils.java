package com.original.component.automated.netty.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {

    /**
     * 计算文件的哈希值
     *
     * @param filePath  文件路径
     * @param algorithm 哈希算法（如 "MD5", "SHA-1", "SHA-256"）
     * @return 文件的哈希值
     * @throws IOException              读取文件时发生错误
     * @throws NoSuchAlgorithmException 无效的哈希算法
     */
    public static String calculateFileHash(String filePath, String algorithm) throws IOException, NoSuchAlgorithmException {
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        return calculateByteHash(fileBytes, algorithm);
    }

    /**
     * @param fileBytes 字节
     * @param algorithm 哈希算法（如 "MD5", "SHA-1", "SHA-256"）
     * @return 文件的哈希值
     * @throws NoSuchAlgorithmException 无效的哈希算法
     */
    public static String calculateByteHash(byte[] fileBytes, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] hashBytes = digest.digest(fileBytes);
        // 将 hash 字节数组转换为十六进制字符串
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 解压 ZIP 文件到指定目录
     *
     * @param zipFilePath   ZIP 文件路径
     * @param destDirectory 目标目录
     * @throws IOException 如果发生 I/O 错误
     */
    public static void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists() && !destDir.mkdirs()) {
            throw new IOException("Failed to create destination directory: " + destDirectory);
        }
        try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(Paths.get(zipFilePath)))) {
            ZipEntry entry = zipIn.getNextEntry();
            // 遍历每个条目
            while (entry != null) {
                String filePath = destDirectory + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    // 如果是文件，提取文件
                    extractFile(zipIn, filePath);
                } else {
                    // 如果是目录，创建目录
                    File dir = new File(filePath);
                    if (!dir.exists() && !dir.mkdirs()) {
                        throw new IOException("Failed to create directory: " + dir.getAbsolutePath());
                    }
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }

    /**
     * 提取文件
     *
     * @param zipIn    ZipInputStream 对象
     * @param filePath 提取文件的路径
     * @throws IOException 如果发生 I/O 错误
     */
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        File destFile = new File(filePath);
        // 创建父目录
        File parent = destFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("Failed to create parent directory: " + parent.getAbsolutePath());
        }
        try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(Paths.get(filePath)))) {
            byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }
}
