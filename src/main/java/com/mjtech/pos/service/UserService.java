package com.mjtech.pos.service;

import com.mjtech.pos.entity.Role;
import com.mjtech.pos.entity.User;
import com.mjtech.pos.entity.UserRole;
import com.mjtech.pos.repository.RoleRepository;
import com.mjtech.pos.repository.UserRepository;
import com.mjtech.pos.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    public User findUserWithRoles(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password)
                .orElse(null);
    }

}
