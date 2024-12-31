package com.nhnacademy.shoppingmallservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
@ConfigurationPropertiesScan
public class BookstoreAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookstoreAuthApplication.class, args);
    }

}
