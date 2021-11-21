package com.teamone.typinggame.services.user;

import com.teamone.typinggame.exceptions.IncorrectPasswordException;
import com.teamone.typinggame.exceptions.UserAlreadyExistsException;
import com.teamone.typinggame.exceptions.UserNotFoundException;
import com.teamone.typinggame.models.Stats;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class.getName());

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User newUser(User user) throws UserAlreadyExistsException {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new UserAlreadyExistsException(user.getUsername() + " already exists.");
        }
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        Stats userStats = new Stats(user, 0.0, 0, 0, 0, 0, 0);
        user.setUserStats(userStats);
        userStats.setUser(user);
        return userRepository.saveAndFlush(user);
    }

    public UserDetails authorizeUser(User user) {
        try {
            UserDetails loadedUser = loadUserByUsername(user.getUsername());
            if (bCryptPasswordEncoder.matches(user.getPassword(), loadedUser.getPassword())) return loadedUser;
            else throw new IncorrectPasswordException("The password is not correct.");
        } catch (UsernameNotFoundException ex) {
            logger.error("authorizeUser -> ", ex);
        } catch (Exception ex) {
            logger.error("authorizeUser -> ", ex);
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username + " not found.");
        }
        return user;
    }
}
