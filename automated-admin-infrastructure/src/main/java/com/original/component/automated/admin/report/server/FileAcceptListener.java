package com.original.component.automated.admin.report.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.original.component.automated.admin.api.AutomatedReportCQRS;
import com.original.component.automated.admin.dto.data.AutomatedReportCO;
import com.original.component.automated.admin.dto.data.AutomatedReportSaveCmd;

import com.original.component.automated.netty.domain.ReportData;
import com.original.component.automated.netty.handler.FileTransferListener;
import com.original.component.automated.netty.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
public class FileAcceptListener implements FileTransferListener {

    @Resource
    AutomatedReportCQRS automatedReportCQRS;

    @Override
    public void onSuccess(Path path) {
        try {
            File file = path.toFile();
            //解压文件
            String reportPath = file.getParent() + File.separator + file.getName().substring(0, file.getName().lastIndexOf("."));
            FileUtils.unzip(file.getPath(), reportPath);
            //读取json文件
            try (InputStream stream = Files.newInputStream(Paths.get(reportPath + File.separator + "ReportData.json"))) {
                ReportData reportData = JSON.parseObject(stream, ReportData.class, Feature.AutoCloseSource);
                AutomatedReportCO automatedReportCO = convertReportData2CO(reportData, reportPath.replace("\\", "/").replace(NettyConstants.REPORT_PATH + "/", ""));
                AutomatedReportSaveCmd cmd = new AutomatedReportSaveCmd(automatedReportCO);
                automatedReportCQRS.save(cmd);
            }
        } catch (IOException e) {
            log.error("Error while unzipping file: {}", path, e);
        }
    }

    private static AutomatedReportCO convertReportData2CO(ReportData reportData, String reportPath) {
        AutomatedReportCO automatedReportCO = new AutomatedReportCO();
        automatedReportCO.setReportGuid(reportData.getReportGuid());
        automatedReportCO.setHostAddress(reportData.getHostAddress());
        automatedReportCO.setReportType(reportData.getReportType());
        automatedReportCO.setBrowserType(reportData.getBrowserType());
        automatedReportCO.setDatabaseType(reportData.getDataBaseType());
        automatedReportCO.setProductName(reportData.getProductName());
        automatedReportCO.setProductVersion(reportData.getProductVersion());
        automatedReportCO.setTestSuite(reportData.getTestSuite());
        automatedReportCO.setCreateTime(reportData.getUploadTime());
        automatedReportCO.setReportPath(reportPath);
        return automatedReportCO;
    }

    @Override
    public void onFailure(Path path) {

    }

    @Override
    public void onFinished(Path path) {
        try {
            File file = path.toFile();
            //解压文件
            String reportPath = file.getParent() + File.separator + file.getName().substring(0, file.getName().lastIndexOf("."));
            //移动到解压目录
            Files.move(path, Paths.get(reportPath + File.separator + file.getName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Error while moving file: {}", path, e);
        }
    }

}
