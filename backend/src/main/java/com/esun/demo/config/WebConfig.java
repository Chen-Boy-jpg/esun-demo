package com.esun.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 處理 URL 請求以 /uploads/ 開頭的情況
        registry.addResourceHandler("/uploads/**")
                // 2. 指向容器內的實體檔案路徑 (使用 file: 前綴)
                // 注意：根據你的 Log，檔案在 /app/src/main/resources/static/uploads/
                .addResourceLocations("file:/app/src/main/resources/static/uploads/");
    }
}