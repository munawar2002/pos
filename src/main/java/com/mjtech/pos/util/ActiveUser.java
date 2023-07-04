package com.mjtech.pos.util;


import com.mjtech.pos.constant.Constants;
import com.mjtech.pos.entity.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActiveUser {

    private static User activeUser;

    private ActiveUser() {

    }

    public static User getActiveUser() {
        return activeUser;
    }

    public static void setActiveUser(User user) {
        activeUser = user;
        log.info("Current Active User is : "+ activeUser.getUsername());
        user.getRoles().forEach(r -> {
            log.info("User has role "+ r.getName());
        });

    }

    public static boolean isSuperAdmin() {
        return getActiveUser().getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase(Constants.SUPER_ADMIN));
    }

    public static String  getActiveUsername() {
        return activeUser.getUsername();
    }
}
