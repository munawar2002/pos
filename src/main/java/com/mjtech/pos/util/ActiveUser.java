package com.mjtech.pos.util;


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
    }

    public static String  getActiveUsername() {
        return activeUser.getUsername();
    }
}
