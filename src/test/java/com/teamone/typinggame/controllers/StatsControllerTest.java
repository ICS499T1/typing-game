package com.teamone.typinggame.controllers;

import com.teamone.typinggame.models.Stats;
import com.teamone.typinggame.services.user.StatsServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class StatsControllerTest {

    @MockBean
    private StatsServiceImpl statsServiceImpl;

    @Autowired
    private MockMvc mockMvc;

    private Stats mockStats;

    @BeforeEach
    void setUp() {
        System.out.println("-----Setting up stats controller tests-----");
        mockStats = new Stats();
        mockStats.setLastRaceSpeed(61);
        mockStats.updateRacesWon();
        mockStats.setAverageSpeed(57.5);
        mockStats.setBestRaceSpeed(70);
        mockStats.setRacesWon(3);
        mockStats.setNumMultiGamesCompleted(3);
        mockStats.setNumSingleGamesCompleted(2);
    }

    @AfterEach
    void tearDown() {
        System.out.println("-----Terminating stats controller tests-----");
    }

//    @Test
//    void updateStatsMultiplayerSuccessful() throws Exception {
//        when(statsServiceImpl.updateStatsMultiplayer(1L, 25.0, true)).thenReturn(mockStats);
//
//        MockHttpServletRequestBuilder builder =
//                MockMvcRequestBuilders.put("/stats/multi?id="+1L+"&speed="+25.0+"&victory="+true)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON);
//
//        mockMvc.perform(builder)
//                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
//                .andDo(MockMvcResultHandlers.print());
//    }

//    @Test
//    void updateStatsMultiplayerUnsuccessful() throws Exception {
//        when(statsServiceImpl.updateStatsMultiplayer(1L, 25.0, true)).thenReturn(mockStats);
//
//        MockHttpServletRequestBuilder builder =
//                MockMvcRequestBuilders.put("/stats/multiplayer?id="+1L+"&speed="+25.0+"&victory="+true)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON);
//
//        mockMvc.perform(builder)
//                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
//                .andDo(MockMvcResultHandlers.print());
//    }

    @Test
    void updateStatsSinglePlayerSuccessful() throws Exception {
        when(statsServiceImpl.updateStatsSinglePlayer(2L, 30.0)).thenReturn(mockStats);

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/stats/single?id="+2L+"&speed="+30.0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void updateStatsSinglePlayerUnsuccessful() throws Exception {
        when(statsServiceImpl.updateStatsSinglePlayer(2L, 30.0)).thenReturn(mockStats);

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/stats/sin?id="+2L+"&speed="+30.0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andDo(MockMvcResultHandlers.print());
    }
}