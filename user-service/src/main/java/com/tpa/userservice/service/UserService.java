package com.tpa.userservice.service;

import com.tpa.userservice.dto.SignUpRequest;
import com.tpa.userservice.dto.UserResponse;
import com.tpa.userservice.model.User;
import com.tpa.userservice.repostiory.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    @Transactional
    public void createUser(SignUpRequest request){
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .build();

        userRepository.save(user);

        log.info("User with email {} added", request.getEmail());
    }

    public ResponseEntity<UserResponse> getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {

            User user = userOptional.get();

            UserResponse userResponse = UserResponse.builder()
                    .username(user.getUsername())
                    .build();

            return ResponseEntity.ok(userResponse);

        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
