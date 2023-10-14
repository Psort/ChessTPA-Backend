package com.tpa.chessengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ChessEngineServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChessEngineServiceApplication.class, args);
    }
}
