package com.original.component.automated.admin.dto.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.original.framework.clientobject.AbstractClientObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MenuCheckReportCO extends AbstractClientObject<String> {

    @Size(max = 500)
    private String url;

    @Size(max = 50)
    private String loginid;

    @Size(max = 50)
    private String password;

    @Size(max = 50)
    private String hostAddress;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Instant createTime;

    private List<MenuCheckResultCO> menuCheckResultCOS;
}
