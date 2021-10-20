package com.teamone.typinggame.controllers;

import com.teamone.typinggame.models.Stats;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.services.StatsService;
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
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class StatsControllerTest {

    private Stats mockStats;

    @MockBean
    private StatsService statsService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        System.out.println("-----Setting up stats controller test-----");
        User mockUser = new User("test");
        mockStats = new Stats(mockUser,2, 3, 4, 5, 6, 7);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void putRequestSuccessfulTest() throws Exception{
        Stats mockStats = new Stats();
        when(statsService.updateStatsMultiplayer(1L, 25.0, true)).thenReturn(mockStats);

        MvcResult mvcResult = mockMvc.perform(get("/stats/multi")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"statsId\" : \"1L\"}, {\"speed\" : \"2.0\"}, { \"victory\" : \"true\"} ")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    void updateStatsMultiplayer() throws Exception {
        Stats mockStats = new Stats();
        when(statsService.updateStatsMultiplayer(1L, 25.0, true)).thenReturn(mockStats);

        //mockMvc.perform(put("/")).andExpect();
    }

    @Test
    void updateStatsSinglePlayer() {
    }
}