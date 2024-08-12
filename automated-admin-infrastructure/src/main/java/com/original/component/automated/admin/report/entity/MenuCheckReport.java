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
@Table(name = "menu_check_report")
public class MenuCheckReport extends IDEntity {
    @Size(max = 50)
    @Column(name = "url", length = 50)
    private String url;

    @Size(max = 50)
    @Column(name = "loginid", length = 50)
    private String loginid;

    @Size(max = 50)
    @Column(name = "password", length = 50)
    private String password;

    @Size(max = 50)
    @Column(name = "host_address", length = 50)
    private String hostAddress;

    @Column(name = "create_time")
    private Instant createTime;
}
