package com.original.component.automated.admin.report.server;

import com.original.netty.handler.FileTransferListener;
import com.original.netty.server.ReportServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;
import java.util.List;

@Configuration
public class NettyConfiguration {

    @Bean
    public ReportServer reportServer(List<FileTransferListener> listeners) {
        return new ReportServer(Paths.get(NettyConstants.REPORT_PATH), listeners);
    }

    @Bean
    public FileTransferListener fileTransferListener() {
        return new FileAcceptListener();
    }

    @Bean
    public ApplicationRunner applicationRunner(ReportServer reportServer, @Value("${netty.port:7397}") int port) {
        return args -> reportServer.start(port);
    }
}
