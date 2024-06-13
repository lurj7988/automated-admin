package com.original.component.automated.admin.web;

import com.original.boot.core.utils.classpath.ClassPathUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * 访问外部文件配置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("file:" + ClassPathUtils.getDeployWarPath() + "data" + File.separator, "classpath:/static/");
    }
}
