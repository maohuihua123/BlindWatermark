package com.mao.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URL;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        // 加载动态库
        URL url = ClassLoader.getSystemResource("lib/opencv_java452.dll");
        System.load(url.getPath());
        SpringApplication.run(Application.class, args);
    }
}
