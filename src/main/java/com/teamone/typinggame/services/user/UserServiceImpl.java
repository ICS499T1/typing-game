package com.teamone.typinggame.services.user;

import com.teamone.typinggame.exceptions.InvalidSignUpInfoException;
import com.teamone.typinggame.exceptions.UserAlreadyExistsException;
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
    public User newUser(User user) throws UserAlreadyExistsException, InvalidSignUpInfoException {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new UserAlreadyExistsException(user.getUsername() + " already exists.");
        }
        if (user.getUsername().length() < 4 || user.getUsername().length() > 16) {
            throw new InvalidSignUpInfoException(user.getUsername() + " is not a valid username.");
        } else if (!user.getUsername().matches("^([A-Za-z0-9]+)$")) {
            throw new InvalidSignUpInfoException(user.getUsername() + " is not a valid username.");
        } else if (user.getPassword().length() < 1) {
            throw new InvalidSignUpInfoException("Password must be at least one character.");
        }
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        Stats userStats = new Stats(user, 0.0, 0, 0, 0, 0, 0);
        user.setUserStats(userStats);
        userStats.setUser(user);
        return userRepository.saveAndFlush(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username + " not found.");
        }
        return user;
    }

    public User updateUserInfo(User user) {
        User pulledUser = userRepository.findByUsername(user.getUsername());
        pulledUser.setAllKeys(user.getAllKeys());
        pulledUser.setUserStats(user.getUserStats());
        return userRepository.saveAndFlush(pulledUser);
    }
}
