package com.assignment.task.controller;

import com.assignment.task.model.User;
import com.assignment.task.model.dto.AuthResponseDTO;
import com.assignment.task.service.UserService;
import com.assignment.task.util.KeyWords;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {



    @Autowired
    private UserService userService;



    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        log.info("Registering user: " + user.getUsername());
        try {
            String res = userService.addUser(user);
            if(res.equals(KeyWords.EXIST.name())){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("The given username already exists");
            }
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }catch (Exception e) {
            log.error("An error occurred while registering user: {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred at the server side.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        log.info("Logging user: " + user.getUsername());
        try {
            String res = userService.authenticateUser(user);
            if (res.equals(KeyWords.NON_EXIST.name())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The given username does not exist");
            }
            if(res.equals(KeyWords.INVALID_CREDENTIALS.name())){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
            return ResponseEntity.ok(new AuthResponseDTO(res));
        }catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Password");
        }catch (Exception e) {
            log.error("An error occurred while logging user: {}" , e.getMessage());
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred at the server side.");

        }
    }
}
