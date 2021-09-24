package com.gameplay.logic;

import com.user.logic.User;

public class PlayArea {
    private User user;
    private GameText gameText;
    private PositionMonitor monitor;
    private Timer timer;

    public PlayArea(User user, String gameText) {
        user.setInAnActiveSession(true);
        this.user = user;
        this.gameText = new GameText(gameText);
        this.monitor = new PositionMonitor(this.gameText.getWords());
        this.timer = new Timer();
    }

    // method for returning cursor position
    // think of better way to monitor cursor position


}
