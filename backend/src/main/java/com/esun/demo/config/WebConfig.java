package com.esun.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 處理 URL 請求以 /uploads/ 開頭的情況
        registry.addResourceHandler("/uploads/**")
                // 指向容器內的實體檔案路徑

                .addResourceLocations("file:/app/src/main/resources/static/uploads/");
    }
}