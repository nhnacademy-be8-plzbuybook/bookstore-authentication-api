package com.nhnacademy.shoppingmallservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient // 유레카 클라이언트 활성화
public class ShoppingmallServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingmallServiceApplication.class, args);
    }

}
