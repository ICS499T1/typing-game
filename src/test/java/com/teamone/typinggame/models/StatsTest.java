package com.teamone.typinggame.models;

import com.teamone.typinggame.services.StatsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class StatsTest {
    private Stats mockStats;
    private User mockUser;

    @BeforeEach
    void setUp() {
        System.out.println("-----Setting up stats tests-----");
        mockUser = new User ("testuser");
        mockStats = new Stats(mockUser, 12.5, 2, 3, 4, 1, 2);
    }

    @AfterEach
    void tearDown() {
        System.out.println("-----Terminating stats tests-----");
    }

    @Test
    void getUser() {
        assertEquals(mockUser.getUsername(),("testuser"));
    }

    @Test
    void setUser() {
        mockUser.setUsername("funrun");
        assertEquals(mockUser.getUsername(),("funrun"));
    }

    @Test
    void getAverageSpeed() {
        assertEquals(mockStats.getAverageSpeed(),(12.5));
    }

    @Test
    void setAverageSpeed() {
        mockStats.setAverageSpeed(1.5);
        assertEquals(mockStats.getAverageSpeed(),(1.5));
    }

    @Test
    void getNumSingleGamesCompleted() {
        assertEquals(mockStats.getNumSingleGamesCompleted(),(2));
    }

    @Test
    void setNumSingleGamesCompleted() {
        mockStats.setNumSingleGamesCompleted(1);
        assertEquals(mockStats.getNumSingleGamesCompleted(),(1));
    }

    @Test
    void getNumMultiGamesCompleted() {
        assertEquals(mockStats.getNumMultiGamesCompleted(),(3));
    }

    @Test
    void setNumMultiGamesCompleted() {
        mockStats.setNumMultiGamesCompleted(6);
        assertEquals(mockStats.getNumMultiGamesCompleted(),(6));
    }

    @Test
    void updateNumSingleGamesCompleted() throws Exception {
    }

    @Test
    void updateNumMultiGamesCompleted() throws Exception {
    }

    @Test
    void updateRacesWon() throws Exception {
    }

    @Test
    void getRacesWon() {
        assertEquals(mockStats.getRacesWon(),(4));
    }

    @Test
    void setRacesWon() {
        mockStats.setRacesWon(2);
        assertEquals(mockStats.getRacesWon(),(2));
    }

    @Test
    void getLastRaceSpeed() {
        assertEquals(mockStats.getLastRaceSpeed(),(1));
    }

    @Test
    void setLastRaceSpeed() {
        mockStats.setLastRaceSpeed(2);
        assertEquals(mockStats.getLastRaceSpeed(),(2));
    }

    @Test
    void getBestRaceSpeed() {
        assertEquals(mockStats.getBestRaceSpeed(),(2));
    }

    @Test
    void setBestRaceSpeed() {
        mockStats.setBestRaceSpeed(3);
        assertEquals(mockStats.getBestRaceSpeed(),(3));
    }
}