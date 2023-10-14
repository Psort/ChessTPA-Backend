package com.tpa.chessengine.config;

import com.chesstpa.Game;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChessEngineConfig {
    @Bean
    public Game game(){
        return new Game();
    }
}
