package com.example.ptweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.ptweb.mapper")
public class PtWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(PtWebApplication.class, args);
    }

}
