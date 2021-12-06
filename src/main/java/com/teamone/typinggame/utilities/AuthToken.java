package com.teamone.typinggame.utilities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamone.typinggame.configuration.GameConfig;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.services.user.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
public class AuthToken {
    @Autowired
    private GameConfig gameConfig;

    private final UserServiceImpl userServiceImpl;

    @Autowired
    public AuthToken(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    /**
     * Creates access token for the user.
     *
     * @param request - http request
     * @param user    - user associated with the token
     * @return String - new access token
     */
    public String createAccessToken(HttpServletRequest request, User user) {
        Algorithm algorithm = Algorithm.HMAC256(gameConfig.getSecret());
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1 * 3600 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
        return accessToken;
    }

    /**
     * Creates refresh token for the user.
     *
     * @param request - http request
     * @param user    - user associated with the token
     * @return String - new refresh token
     */
    public String createRefreshToken(HttpServletRequest request, User user) {
        Algorithm algorithm = Algorithm.HMAC256(gameConfig.getSecret());
        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 12 * 3600 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
        return refreshToken;
    }

    /**
     * Refreshes access token for the user.
     *
     * @param request  - http request
     * @param response - http response
     * @throws IOException when the input or output is incorrect
     */
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Algorithm algorithm = Algorithm.HMAC256(gameConfig.getSecret());

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String username = decodedJWT.getSubject();
                User user = (User) userServiceImpl.loadUserByUsername(username);
                String accessToken = createAccessToken(request, user);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("accessToken", accessToken);
                tokens.put("refreshToken", refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception exception) {
                log.error("Error logging in: {}", exception.getMessage());
                response.setHeader("error", exception.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error", exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("refreshToken -> Refresh token is missing");
        }
    }

}
