package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@SpringBootApplication
public class JenkinsDockerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JenkinsDockerDemoApplication.class, args);
    }

    @GetMapping
    public String hello() {
        print();
        return "hello 妹妹";
    }

    private void print() {
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                log.info("打印数字：{}", i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
