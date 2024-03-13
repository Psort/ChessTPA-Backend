package com.tpa.userservice.service;

import com.tpa.userservice.dto.NewGameRequest;
import com.tpa.userservice.dto.SignUpRequest;
import com.tpa.userservice.dto.UserResponse;
import com.tpa.userservice.exception.UserRequestException;
import com.tpa.userservice.model.User;
import com.tpa.userservice.repostiory.UserRepository;
import com.tpa.userservice.type.LogType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final LogService logService;
    @Transactional
    public UserResponse createUser(SignUpRequest request) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                logService.send(LogType.ERROR, "User with email {} already exist", request.getEmail());
                throw new UserRequestException("User already exists");
            }

            User user = buildUser(request);

            userRepository.save(user);

            log.info("User with email {} added", request.getEmail());
            logService.sendInfo( "User with email {} added", request.getEmail());

            return buildUserResponse(user);
    }


    public UserResponse getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> UserResponse.builder()
                        .username(user.getUsername())
                        .eloRating(user.getEloRating())
                        .build())
                .orElseThrow(() -> new UserRequestException("User does not exists"));
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
           throw new UserRequestException("User does not exists");
        }
    }

    @Transactional
    public void tradeEloPoints(String winningUsername, String losingUsername, boolean isWin){
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

            if(isWin) {
                winningPlayerEloRating = winningPlayerEloRating + k * (1 - winningPlayerProb);
                winningPlayer.setEloRating(winningPlayerEloRating);
                userRepository.save(winningPlayer);
            }
            else {
                losingPlayerEloRating = losingPlayerEloRating + k * (0 - losingPlayerProb);
                losingPlayer.setEloRating(losingPlayerEloRating);
                userRepository.save(losingPlayer);
            }

            log.info("ELO FOR WINNING PLAYER {}, ELO FOR LOSING PLAYER {}", winningPlayerEloRating, losingPlayerEloRating);

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

    private UserResponse buildUserResponse(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    private User buildUser(SignUpRequest request) {
        double defaultEloRating = 800;

        return User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .eloRating(defaultEloRating)
                .build();
    }
}
