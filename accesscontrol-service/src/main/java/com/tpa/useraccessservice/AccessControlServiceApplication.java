package com.tpa.useraccessservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AccessControlServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccessControlServiceApplication.class, args);
    }
}
