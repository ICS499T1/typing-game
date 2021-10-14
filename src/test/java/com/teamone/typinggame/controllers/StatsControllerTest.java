package com.teamone.typinggame.controllers;

import com.teamone.typinggame.models.Stats;
import com.teamone.typinggame.services.StatsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.put;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class StatsControllerTest {

    @MockBean
    private StatsService statsService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void updateStatsMultiplayer() {
        Stats mockStats = new Stats();
        when(statsService.updateStatsMultiplayer(1L, 25.0, true)).thenReturn(mockStats);

        //mockMvc.perform(put("/")).andExpect();
    }

    @Test
    void updateStatsSinglePlayer() {
    }
}