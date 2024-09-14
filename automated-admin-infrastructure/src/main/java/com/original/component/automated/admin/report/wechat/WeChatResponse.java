package com.original.component.automated.admin.report.wechat;

import lombok.Data;

@Data
public class WeChatResponse {
    private int errcode;
    private String errmsg;
    private String msgid;
}
