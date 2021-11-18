package com.teamone.typinggame.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Data
@AllArgsConstructor
public class SignUp {
    private String username;
    private String password;
}
