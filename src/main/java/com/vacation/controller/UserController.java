package com.vacation.controller;

import com.vacation.config.JwtTokenProvider;
import com.vacation.domain.LoginForm;
import com.vacation.domain.UserInfo;
import com.vacation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginForm login){
        UserInfo user = userRepository.findById(login.getId()).orElseThrow(()->
                new IllegalArgumentException("가입되지 않은 ID"));
        if(!passwordEncoder.matches(login.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 맞지 않음.");
        }
        log.info(user.getId() + " login success");

        Map<String, String> response = new HashMap<>();
        response.put("token", jwtTokenProvider.createToken(user.getId()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
