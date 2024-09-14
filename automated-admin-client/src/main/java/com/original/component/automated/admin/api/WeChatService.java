package com.original.component.automated.admin.api;

import com.original.component.automated.admin.dto.data.WeChatMessageCO;
import com.original.framework.dto.Response;

public interface WeChatService {
    Response sendMessage(WeChatMessageCO message);
}
