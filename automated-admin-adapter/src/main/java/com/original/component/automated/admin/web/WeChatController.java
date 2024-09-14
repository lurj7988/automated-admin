package com.original.component.automated.admin.web;

import com.original.component.automated.admin.api.WeChatService;
import com.original.component.automated.admin.dto.data.WeChatMessageCO;
import com.original.framework.dto.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "企业微信 - 发送消息")
@RestController
@RequestMapping("/automated/wechat")
@Validated
public class WeChatController {

    @Resource
    private WeChatService weChatService;


    @PostMapping("/send")
    @Operation(summary = "发送消息", description = "用于自动化测试")
    public Response sendMessage(@RequestBody WeChatMessageCO weChatMessageCO) {
        return weChatService.sendMessage(weChatMessageCO);
    }
}
