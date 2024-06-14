package com.original.component.automated.netty.domain;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
public class ReportData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reportGuid;

    private String testSuite;

    private String reportType;

    private String hostAddress;

    private Instant uploadTime;

    private String productName;

    private String productVersion;

    private String dataBaseType;

    private String browserType;
}
