package com.tpa.userservice.service;

import com.tpa.userservice.dto.SignUpRequest;
import com.tpa.userservice.model.User;
import com.tpa.userservice.repostiory.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
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

    public ResponseEntity<String> getUsernameByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String username = user.getUsername();
            return ResponseEntity.ok(username);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
