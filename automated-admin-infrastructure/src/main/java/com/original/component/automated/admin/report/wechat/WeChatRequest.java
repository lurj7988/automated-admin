package com.original.component.automated.admin.report.wechat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class WeChatRequest {
    private String touser;
    private String msgtype;
    private int agentid;
    private WeChatText text;
    private int safe;

    @Data
    @AllArgsConstructor
    static class WeChatText {
        private String content;
    }
}
