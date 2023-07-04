package com.mjtech.pos.service;

import com.mjtech.pos.entity.User;
import com.mjtech.pos.repository.RoleRepository;
import com.mjtech.pos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public User findUserWithRoles(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password)
                .orElse(null);
    }

}
