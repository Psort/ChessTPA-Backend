package com.tpa.gameservice.repository;

import com.tpa.gameservice.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepository extends MongoRepository<Game, String> {
}
