package com.teamone.typinggame.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeyStatsTest {
    private KeyStats mockKeyStats;
    private User mockuser;

    @BeforeEach
    void setUp() {
        System.out.println("-----Setting up keystats tests-----");
        mockuser = new User ("testuser");
        mockKeyStats = new KeyStats('a', 2, 3, mockuser);
    }

    @AfterEach
    void tearDown() {
        System.out.println("-----Terminating user tests-----");
    }

    //How we test this--?
//    @Test
//    void getKeyID() {
//        assertEquals(mockKeyStats.getKeyID(),(""));
//    }

    @Test
    void getCharacter() {
        assertEquals(mockKeyStats.getCharacter(),('a'));
    }

    @Test
    void setCharacter() {
        mockKeyStats.setCharacter('b');
        assertEquals(mockKeyStats.getCharacter(),('b'));
    }

    @Test
    void getNumFails() {
        assertEquals(mockKeyStats.getNumFails(),(2));
    }

    @Test
    void setNumFails() {
        mockKeyStats.setNumFails(1);
        assertEquals(mockKeyStats.getNumFails(),(1));
    }

    @Test
    void getNumSuccesses() {
        assertEquals(mockKeyStats.getNumSuccesses(), (3));
    }

    @Test
    void setNumSuccesses() {
        mockKeyStats.setNumSuccesses(4);
        assertEquals(mockKeyStats.getNumSuccesses(), (4));
    }

    @Test
    void getUser() {
        assertEquals(mockKeyStats.getUser(),(mockuser));
    }

    @Test
    void setUser() {
        User testSetUser = new User ("FunGuy");
        mockKeyStats.setUser(testSetUser);
        assertEquals(mockKeyStats.getUser(),(testSetUser));
    }
}