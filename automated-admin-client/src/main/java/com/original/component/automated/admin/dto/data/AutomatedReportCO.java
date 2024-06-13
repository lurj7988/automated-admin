package com.original.component.automated.admin.dto.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.original.framework.clientobject.AbstractClientObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
public class AutomatedReportCO extends AbstractClientObject<String> {

    @Size(max = 50)
    private String testSuite;

    @Size(max = 50)
    private String reportType;

    @Size(max = 50)
    private String reportPath;

    @Size(max = 50)
    private String hostAddress;

    @JsonFormat(pattern = " yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Instant createTime;

    @Size(max = 50)
    private String productName;

    @Size(max = 50)
    private String productVersion;

    @Size(max = 50)
    private String databaseType;

    @Size(max = 50)
    private String browserType;
}
