package com.nhnacademy.shoppingmallservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients  // 유레카 클라이언트 활성화
public class ShoppingmallServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingmallServiceApplication.class, args);
    }

}
