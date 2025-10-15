package org.tung.springbootlab3.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tung.springbootlab3.model.User;
import org.tung.springbootlab3.repository.UserRepository;
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(String userName, String password) {
        User user = new User();
        user.setUsername(userName);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public User validateUser(String username, String password) {
        return userRepository.findByUsername(username).filter(u->passwordEncoder.matches(password, u.getPassword())).orElse(null);
    }
}
