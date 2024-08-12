package com.original.component.automated.admin.report.entity;

import com.original.framework.data.entity.IDEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "menu_check_result")
public class MenuCheckResult extends IDEntity {

    @Size(max = 50)
    @Column(name = "report_guid", length = 50)
    private String reportGuid;

    @Size(max = 50)
    @Column(name = "code", length = 50)
    private String code;

    @Size(max = 50)
    @Column(name = "name", length = 50)
    private String name;

    @Size(max = 50)
    @Column(name = "url", length = 50)
    private String url;

    @Size(max = 50)
    @Column(name = "error", length = 50)
    private String error;
}
