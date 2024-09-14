package com.original.component.automated.admin.report.wechat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WeChatConfiguration {

    @Bean
    public WeChatClient webChatClient(WeChatProperties properties) {
        return new WeChatClient(properties.getCorpid(), properties.getCorpsecret());
    }
}
