package com.original.component.automated.admin.report.wechat;

import lombok.Data;

@Data
public class AccessToken {
    private int errcode;
    private String errmsg;
    private String access_token;
    private int expires_in;
}
