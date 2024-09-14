package com.original.component.automated.admin.report;

import com.original.component.automated.admin.api.WeChatService;
import com.original.component.automated.admin.dto.data.WeChatMessageCO;
import com.original.component.automated.admin.report.wechat.WeChatClient;
import com.original.component.automated.admin.report.wechat.WeChatResponse;
import com.original.framework.dto.Response;
import com.original.framework.dto.SingleResponse;
import org.springframework.stereotype.Component;

@Component
public class WeChatServiceImpl implements WeChatService {

    private final WeChatClient weChatClient;

    public WeChatServiceImpl(WeChatClient weChatClient) {
        this.weChatClient = weChatClient;
    }

    @Override
    public Response sendMessage(WeChatMessageCO message) {
        WeChatResponse response = weChatClient.sendMessage(message.getToUser(), message.getContent());
        return SingleResponse.of(response);
    }
}
