package com.tpa.chessengine.config;


import com.chesstpa.communication.ChessEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChessEngineConfig {
    @Bean
    public ChessEngine chessEngine(){
        return new ChessEngine();
    }
}
