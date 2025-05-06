package com.assignment.task.service;

import com.assignment.task.model.User;
import com.assignment.task.repository.UserRepository;
import com.assignment.task.util.JwtUtil;
import com.assignment.task.util.KeyWords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    public String addUser(User user) {
        if(checkUser(user.getUsername())) {
            return KeyWords.EXIST.name();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return KeyWords.SUCCESSFUL.name();
    }

    public String authenticateUser(User user) {

        if(!checkUser(user.getUsername())) {
            return KeyWords.NON_EXIST.name();
        }
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
        if (auth.isAuthenticated()) {
            return jwtUtil.generateToken((UserDetails) auth.getPrincipal());
        } else {
            return KeyWords.INVALID_CREDENTIALS.name();
        }
    }


    public Boolean checkUser(String username){
        return userRepository.findByUsername(username).isPresent();
    }

    public User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
