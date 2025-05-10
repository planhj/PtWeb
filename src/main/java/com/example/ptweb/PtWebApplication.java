package com.example.ptweb;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.example.ptweb.mapper")
@EnableCaching
@Slf4j
public class PtWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(PtWebApplication.class, args);
    }

}
