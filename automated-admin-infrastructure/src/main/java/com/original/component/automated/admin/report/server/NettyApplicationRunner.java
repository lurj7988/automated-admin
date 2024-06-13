package com.original.component.automated.admin.report.server;

import com.original.netty.server.ReportServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class NettyApplicationRunner implements ApplicationRunner {

    @Value("${netty.port:7397}")
    private int port;

    private final ReportServer reportServer;

    public NettyApplicationRunner(ReportServer reportServer) {
        this.reportServer = reportServer;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        reportServer.start(port);
    }
}
