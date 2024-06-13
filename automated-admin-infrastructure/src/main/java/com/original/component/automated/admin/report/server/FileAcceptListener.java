package com.original.component.automated.admin.report.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.original.component.automated.admin.api.AutomatedReportCQRS;
import com.original.component.automated.admin.dto.data.AutomatedReportCO;
import com.original.component.automated.admin.dto.data.AutomatedReportSaveCmd;
import com.original.netty.domain.ReportData;
import com.original.netty.handler.FileTransferListener;
import com.original.netty.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileAcceptListener implements FileTransferListener {

    @Resource
    AutomatedReportCQRS automatedReportCQRS;

    @Override
    public void onSuccess(Path path) {
        try {
            File file = path.toFile();
            //解压文件
            String destDirectory = file.getParent() + File.separator + file.getName().substring(0, file.getName().lastIndexOf("."));
            FileUtils.unzip(file.getPath(), destDirectory);
            //读取json文件
            try (InputStream stream = Files.newInputStream(Paths.get(destDirectory + File.separator + "ReportData.json"))) {
                ReportData reportData = JSON.parseObject(stream, ReportData.class, Feature.AutoCloseSource);
                //TODO: 保存数据
                log.info(reportData.getHostAddress());
                AutomatedReportCO automatedReportCO = new AutomatedReportCO();
                automatedReportCO.setId(reportData.getReportGuid());
                AutomatedReportSaveCmd cmd = new AutomatedReportSaveCmd(automatedReportCO);
                automatedReportCQRS.save(cmd);
            }
        } catch (IOException e) {
            log.error("Error while unzipping file: {}", path, e);
        }
    }

    @Override
    public void onFailure(Path path) {

    }

}
