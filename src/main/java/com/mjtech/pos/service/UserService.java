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
        Optional<User> optionalUser = userRepository.findByUsernameAndPassword(username, password);

        if(optionalUser.isEmpty()) {
            return  null;
        }
        User user = optionalUser.get();
        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        List<Integer> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        if(roleIds.isEmpty()) {
            user.setRoles(new ArrayList<>());
        } else {
            List<Role> allUserRoles = roleRepository.findByIdIn(roleIds);
            user.setRoles(allUserRoles);
        }
        return user;
    }

}
