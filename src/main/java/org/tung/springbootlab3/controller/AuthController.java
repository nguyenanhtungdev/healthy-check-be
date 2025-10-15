package org.tung.springbootlab3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tung.springbootlab3.model.User;
import org.tung.springbootlab3.services.UserService;
import org.tung.springbootlab3.util.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService  userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        userService.registerUser(user.getUsername(), user.getPassword());
        return "success";
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        User validUser = userService.validateUser(user.getUsername(), user.getPassword());
        if (validUser != null) {
            return jwtUtil.generateToken(validUser.getUsername());
        }
        return "username or password is incorrect";
    }
}
