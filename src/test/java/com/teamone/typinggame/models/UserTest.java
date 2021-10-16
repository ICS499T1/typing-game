package com.teamone.typinggame.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserTest {

    private User mockUser;

    @BeforeEach
    void setUp() {
        System.out.println("-----Setting up user tests-----");
        mockUser = new User("test");
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