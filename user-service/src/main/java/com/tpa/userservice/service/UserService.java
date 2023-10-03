package com.tpa.userservice.service;

import com.tpa.userservice.dto.SignUpRequest;
import com.tpa.userservice.model.User;
import com.tpa.userservice.repostiory.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    public void createUser(SignUpRequest request){
        User user = User.builder()
                .email(request.getEmail())
                .build();

        userRepository.save(user);

        log.info("User with email {} added", request.getEmail());
    }
}
