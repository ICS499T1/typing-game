package com.teamone.typinggame.services;

import com.teamone.typinggame.models.User;
import com.teamone.typinggame.repositories.UserRepository;
import com.teamone.typinggame.services.user.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserServiceImplTest {

    private User mockUser;
    private User result;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        System.out.println("-----Setting up user service test-----");
        mockUser = new User("kseniago");
    }

    @AfterEach
    void tearDown() {
        if (result != null) userRepository.delete(result);
        System.out.println("-----Terminating user service test-----");
    }

    @Test
    void newUserAdded() {
        result = userServiceImpl.newUser(mockUser);
        assertEquals(result.getUsername(), mockUser.getUsername());
    }

    @Test
    void existingUserAdded() {
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> userServiceImpl.newUser(new User("vtornik")));
    }
}