package com.teamone.typinggame.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void getUsername() {
        assertEquals(mockUser.getUsername(),("test"));
    }

    @Test
    void setUsername() {
        mockUser.setUsername("funrun");
        assertEquals(mockUser.getUsername(),("funrun"));
    }

    @Test
    void setUserStats() {
        Stats stats = new Stats(mockUser,2, 3, 4, 5, 6, 7);
        mockUser.setUserStats(stats);
        assertEquals(mockUser.getUserStats(),(stats));
    }

    @Test
    void getUserStats() {
        assertEquals(mockUser.getUserStats(), (null));
    }

    @Test
    void getAllKeys() {
        assertEquals(mockUser.getUserStats(), (null));
    }

    @Test
    void setAllKeys() {
        KeyStats keystats = new KeyStats('a', 2, 3, mockUser);
        List<KeyStats> keyStatsList = new ArrayList<>();
        keyStatsList.add(keystats);
        mockUser.setAllKeys(keyStatsList);
        assertEquals(mockUser.getAllKeys(), (keyStatsList));
    }
}