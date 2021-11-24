package com.teamone.typinggame.services.game;

import com.teamone.typinggame.repositories.UserRepository;
import com.teamone.typinggame.services.user.UserServiceImpl;
import com.teamone.typinggame.storage.ActiveUserStorage;
import com.teamone.typinggame.storage.GameStorage;
import com.teamone.typinggame.storage.PlayerStorage;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public abstract class AbstractGameService implements GameService {

    protected final GameStorage gameStorage = GameStorage.getInstance();
    protected final ActiveUserStorage activeUserStorage = ActiveUserStorage.getInstance();
    protected final PlayerStorage playerStorage = PlayerStorage.getInstance();
    protected final UserServiceImpl userService;
    protected final UserRepository userRepository;
    protected final Object lockObject = new Object();

    @Override
    public String generateGameId() {
        return UUID.randomUUID().toString();
    }

}
