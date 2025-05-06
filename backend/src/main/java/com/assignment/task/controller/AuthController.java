package com.assignment.task.controller;

import com.assignment.task.model.User;
import com.assignment.task.model.dto.AuthResponseDTO;
import com.assignment.task.service.UserService;
import com.assignment.task.util.KeyWords;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Map;

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
    public ResponseEntity<?> login(@RequestBody User user, HttpServletResponse response) {
        log.info("Logging user: " + user.getUsername());
        try {
            String res = userService.authenticateUser(user);
            if (res.equals(KeyWords.NON_EXIST.name())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The given username does not exist");
            }
            if(res.equals(KeyWords.INVALID_CREDENTIALS.name())){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }

            Cookie cookie = new Cookie("authToken", res);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(86400);
            cookie.setDomain("localhost");

            response.addCookie(cookie);

            return ResponseEntity.ok().body(user.getUsername());
        }catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Password");
        }catch (Exception e) {
            log.error("An error occurred while logging user: {}" , e.getMessage());
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred at the server side.");

        }
    }


    @GetMapping("/check")
    public ResponseEntity<?> check(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return ResponseEntity.ok().body(Map.of(
                    "status", "authenticated",
                    "username", auth.getName()
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    HttpServletResponse response) {
        SecurityContextHolder.clearContext();

        Cookie cookie = new Cookie("authToken", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        if (request.isSecure()) {
            cookie.setSecure(true);
        }
        response.addCookie(cookie);

        return ResponseEntity.ok().body(Map.of(
                "message", "Logout successful",
                "timestamp", Instant.now()
        ));
    }

}
