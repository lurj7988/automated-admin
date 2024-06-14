package com.original.component.automated.netty.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;

import com.original.component.automated.netty.domain.ReportData;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
public class RecursiveDirectoryWatcher {

    private final WatchService watchService;
    private final Map<WatchKey, Path> keys;
    private final ExecutorService executorService;
    private final static int THREADS = 10;
    private final Path reportDir;
    private Properties properties;

    public RecursiveDirectoryWatcher(Path reportDir) throws IOException {
        initProperties();
        this.watchService = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        //this.client = client;
        this.executorService = Executors.newFixedThreadPool(THREADS);
        this.reportDir = reportDir;
        Path waiting = reportDir.resolve("waiting");
        if (!Files.exists(waiting)) {
            Files.createDirectory(waiting);
        }
        log.info("start watching:{}", waiting);
        registerAll(waiting);
    }

    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
        keys.put(key, dir);
    }

    private void initProperties() {
        properties = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(in);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void registerAll(final Path startDir) throws IOException {
        // register directory and subdirectories
        Files.walkFileTree(startDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                Objects.requireNonNull(file);
                Objects.requireNonNull(attrs);
                handleCreateEvent(file);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void processEvents() {
        try {
            while (true) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException ex) {
                    return;
                }

                Path dir = keys.get(key);
                if (dir == null) {
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    // Context for directory entry event is the file name of entry
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path name = ev.context();
                    Path child = dir.resolve(name);

                    log.info("{}: {}", kind.name(), child);

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        // if directory is created, register it and its subdirectories
                        if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                            try {
                                registerAll(child);
                            } catch (IOException e) {
                                log.info(e.getMessage(), e);
                            }
                        }
                        // if file is created, send it
                        executorService.submit(() -> handleCreateEvent(child));
                    }
                }

                // reset key and remove from set if directory no longer accessible
                boolean valid = key.reset();
                if (!valid) {
                    keys.remove(key);

                    // all directories are inaccessible
                    if (keys.isEmpty()) {
                        break;
                    }
                }
            }
        } finally {
            shutdownAndAwaitTermination(executorService);
        }
    }

    private void handleCreateEvent(Path child) {
        try {
            if (Files.isRegularFile(child)) {
                if (validFile(child)) {
                    log.info("文件传输开始：{}", child);
                    FileTransferClient client = new FileTransferClient(properties.getProperty("report.server.host"), Integer.parseInt(properties.getProperty("report.server.port")));
                    client.start();
                    client.sendFile(child, future -> {
                        Path destinationDir;
                        if (future.isSuccess()) {
                            log.info("文件传输成功：{}", child);
                            destinationDir = reportDir.resolve("success").resolve(getCurrentMonthDir());
                        } else {
                            log.error("文件传输失败：{}", child);
                            destinationDir = reportDir.resolve("failure").resolve(getCurrentMonthDir());
                        }
                        moveFileToDirectory(child, destinationDir);
                    });
                    client.stop();
                } else {
                    log.info("文件不合法：{}", child);
                    Path abandonDir = reportDir.resolve("abandon").resolve(getCurrentMonthDir());
                    moveFileToDirectory(child, abandonDir);
                }
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("处理文件出错：{}", child, e);
        }
    }


    private String getCurrentMonthDir() {
        return DateTimeFormatter.ofPattern("yyyyMM").format(LocalDateTime.now());
    }

    private void moveFileToDirectory(Path file, Path directory) {
        try {
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            Files.move(file, directory.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("移动文件出错：{}", file, e);
        }
    }

    private boolean validFile(Path path) {
        if (isZipFile(path.toFile())) {
            try (ZipFile zipFile = new ZipFile(path.toFile())) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    // 如果是目录，跳过
                    if (entry.isDirectory()) {
                        continue;
                    }
                    if ("ReportData.json".equals(entry.getName())) {
                        try (InputStream stream = zipFile.getInputStream(entry)) {
                            ReportData reportData = JSON.parseObject(stream, ReportData.class, Feature.AutoCloseSource);
                            if (reportData.getReportGuid().equals(removeExtension(path.toFile().getName()))) {
                                return true;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return false;
    }

    /**
     * Removes the file extension from a file name.
     *
     * @param fileName the file name from which the extension should be removed
     * @return the file name without its extension
     */
    public static String removeExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return fileName;
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName; // No extension found
        }

        return fileName.substring(0, lastDotIndex);
    }


    public static boolean isZipFile(File file) {
        if (!file.isFile() || !file.canRead()) {
            return false;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[4];
            int readBytes = fis.read(header, 0, 4);

            // ZIP file's header (magic number) is "PK\x03\x04"
            return readBytes == 4 &&
                    header[0] == 0x50 &&
                    header[1] == 0x4B &&
                    header[2] == 0x03 &&
                    header[3] == 0x04;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return false;
    }

    private void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.error("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }


    public static void main(String[] args) throws IOException {
        Path userHome = Paths.get(System.getProperty("user.home"));
        Path reportDir = userHome.resolve("report-collectd");
        if (!Files.exists(reportDir)) {
            Files.createDirectory(reportDir);
        }
        new RecursiveDirectoryWatcher(reportDir).processEvents();
    }
}
