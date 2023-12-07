package com.tpa.userservice.service;

import com.tpa.userservice.dto.NewGameRequest;
import com.tpa.userservice.dto.SignUpRequest;
import com.tpa.userservice.dto.UserResponse;
import com.tpa.userservice.exception.UserRequestException;
import com.tpa.userservice.model.User;
import com.tpa.userservice.repostiory.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.pow;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    @Transactional
    public void createUser(SignUpRequest request) {
        try {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new UserRequestException("User already exist");
            }

            double defaultEloRating = 800;

            User user = User.builder()
                    .email(request.getEmail())
                    .username(request.getUsername())
                    .eloRating(defaultEloRating)
                    .build();

            userRepository.save(user);

            log.info("User with email {} added", request.getEmail());

        } catch (Exception e) {
            log.warn("Failed to save user to database, {}", e.getMessage());
            throw new UserRequestException("Failed during database operation");
        }
    }

    public UserResponse getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> UserResponse.builder()
                        .username(user.getUsername())
                        .build())
                .orElseThrow(() -> new UserRequestException("User does not exist"));
    }

    public void addGame(NewGameRequest request) {
        String gameId = request.getGameId();

        Optional<User> optionalFirstPlayer = userRepository.findByUsername(request.getFirstPlayerUsername());
        Optional<User> optionalSecondPlayer = userRepository.findByUsername(request.getSecondPlayerUsername());

        if (optionalFirstPlayer.isPresent() && optionalSecondPlayer.isPresent()) {

            User firstPlayer = optionalFirstPlayer.get();
            User secondPlayer = optionalSecondPlayer.get();

            firstPlayer.addGame(gameId);
            secondPlayer.addGame(gameId);

            log.info("Game with id: {} assigned to users: {}, {}",
                    request.getGameId(),
                    firstPlayer.getUsername(),
                    secondPlayer.getUsername());

            userRepository.saveAll(List.of(firstPlayer, secondPlayer));

        } else {
           throw new UserRequestException("User does not exist");
        }
    }

    public void tradeEloPoints(String winningUsername, String losingUsername){
        Optional<User> optionalWinningPlayer = userRepository.findByUsername(winningUsername);
        Optional<User> optionalLosingPlayer = userRepository.findByUsername(losingUsername);

        if (optionalWinningPlayer.isPresent() && optionalLosingPlayer.isPresent()) {

            User winningPlayer = optionalWinningPlayer.get();
            User losingPlayer = optionalLosingPlayer.get();

            double winningPlayerEloRating = winningPlayer.getEloRating();
            double losingPlayerEloRating = losingPlayer.getEloRating();

            double winningPlayerProb = playerWinningProbability(losingPlayerEloRating, winningPlayerEloRating);
            double losingPlayerProb = playerWinningProbability(winningPlayerEloRating, losingPlayerEloRating);

            // constant value to calculate elo rating
            //todo change value of k based on elo rating
            int k = 30;

            winningPlayerEloRating = winningPlayerEloRating + k * (1 - winningPlayerProb);
            losingPlayerEloRating = losingPlayerEloRating + k * (0 - losingPlayerProb);

            winningPlayer.setEloRating(winningPlayerEloRating);
            losingPlayer.setEloRating(losingPlayerEloRating);

            userRepository.saveAll(List.of(winningPlayer, losingPlayer));
        }
    }

    /**
     * Calculates probability of winning based on players elo rating
     * @param firstPlayerEloRating
     * @param secondPlayerEloRating
     * @return winning probability as double
     */
    private double playerWinningProbability(double firstPlayerEloRating, double secondPlayerEloRating){
        return 1.0f
                / (1
                + (float) (Math.pow(
                10, 1.0f * (firstPlayerEloRating - secondPlayerEloRating)
                        / 400)));
    }
}
