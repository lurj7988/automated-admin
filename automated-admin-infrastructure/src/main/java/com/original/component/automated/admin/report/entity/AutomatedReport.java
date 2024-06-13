package com.original.component.automated.admin.report.entity;

import com.original.framework.data.entity.IDEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "automated_report")
public class AutomatedReport extends IDEntity {

    @Size(max = 50)
    @Column(name = "report_guid", length = 50)
    private String reportGuid;

    @Size(max = 50)
    @Column(name = "test_suite", length = 50)
    private String testSuite;

    @Size(max = 50)
    @Column(name = "report_type", length = 50)
    private String reportType;

    @Size(max = 50)
    @Column(name = "report_path", length = 50)
    private String reportPath;

    @Size(max = 50)
    @Column(name = "host_address", length = 50)
    private String hostAddress;

    @Column(name = "create_time")
    private Instant createTime;

    @Size(max = 50)
    @Column(name = "product_name", length = 50)
    private String productName;

    @Size(max = 50)
    @Column(name = "product_version", length = 50)
    private String productVersion;

    @Size(max = 50)
    @Column(name = "database_type", length = 50)
    private String databaseType;

    @Size(max = 50)
    @Column(name = "browser_type", length = 50)
    private String browserType;

}
