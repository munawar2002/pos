package com.mjtech.pos;

import com.mjtech.pos.entity.User;
import com.mjtech.pos.util.ActiveUser;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {

    @BeforeEach
    public void init() {
        User user = new User();
        user.setId(1);
        user.setFirstName("test");
        user.setFirstName("last");
        user.setUsername("test");
        user.setPassword("test");

        ActiveUser.setActiveUser(user);

    }
}
