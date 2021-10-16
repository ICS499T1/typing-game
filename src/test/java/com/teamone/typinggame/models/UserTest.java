package com.teamone.typinggame.models;

import com.teamone.typinggame.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User mockUser;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        System.out.println("-----Setting up user tests-----");
        mockUser = new User("testuser");
    }

    @AfterEach
    void tearDown() {
        System.out.println("-----Terminating user tests-----");
    }

    @Test
    void getUserID() {
    }

    @Test
    void getUsername() {
    }

    @Test
    void setUsername() {
    }

    @Test
    void setUserStats() {
    }

    @Test
    void getUserStats() {
    }

    @Test
    void getAllKeys() {
    }

    @Test
    void setAllKeys() {
    }

    @Test
    void testToString() {
    }
}