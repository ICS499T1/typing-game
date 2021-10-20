package com.teamone.typinggame.controllers;

import com.teamone.typinggame.models.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import com.teamone.typinggame.services.UserService;
import org.json.JSONArray;
import org.json.JSONObject;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.test.web.servlet.MvcResult;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private User mockUser;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        System.out.println("-----Setting up user controller test-----");
        mockUser = new User("testuser");
    }

    @Test
    void postRequestSuccessfulTest() throws Exception {
        when(userService.newUser(any(User.class))).thenReturn(mockUser);

        MvcResult mvcResult = mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"testuser\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        assertEquals(mockUser.getUsername(), jsonObject.get("username"));
    }

    @Test
    void postRequestNotFoundTest() throws Exception {
        mockMvc.perform(post("/users/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"username\": \"testuser\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRequestSuccessfulTest() throws Exception{
        mockMvc.perform(get("/user/getusers")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
//        JSONArray jsonArray = new JSONArray(mvcResult.getResponse().getContentAsString());
//        assertEquals(mockUser.getUsername(), jsonObject.get("username"));
    }

    @Test
    void getRequestUnSuccessfulTest() throws Exception{
        mockMvc.perform(get("/user/getusers1212")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @AfterEach
    void tearDown() {
        System.out.println("-----Terminating user controller test-----");
    }
}