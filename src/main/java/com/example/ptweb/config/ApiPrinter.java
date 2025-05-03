package com.example.ptweb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Component
public class ApiPrinter implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println("=== 项目启动，已注册的 API 接口如下： ===");
        handlerMapping.getHandlerMethods().forEach((mapping, method) -> {
            System.out.println("接口路径: " + mapping + " -> 方法: " + method.getMethod().getDeclaringClass().getSimpleName()
                    + "." + method.getMethod().getName());
        });
        System.out.println("========================================");
    }
}