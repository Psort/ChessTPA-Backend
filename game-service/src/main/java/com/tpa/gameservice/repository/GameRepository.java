package com.tpa.gameservice.repository;

import com.tpa.gameservice.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends MongoRepository<Game, String> {

    List<Game> findByPlayersUsernameAndHistoryStatusIsNotIn(String username, List<String> statuses);


}
