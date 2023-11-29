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
            User user = User.builder()
                    .email(request.getEmail())
                    .username(request.getUsername())
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
}
