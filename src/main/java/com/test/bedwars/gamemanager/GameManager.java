package com.test.bedwars.managers;

import com.test.bedwars.BedwarsPlugin;

public class GameManager {
    private BedwarsPlugin plugin;

    private GameState state;

    public GameManager(BedwarsPlugin plugin) { this.plugin = plugin; }

    public void setState(GameState state) {
        this.state = state;

        switch(state) {
            case PRELOBBY:
                //
                break;
            case LOBBY:
                //
                break;
            case STARTING:
                //
                break;
            case ACTIVE:
                //
                break;
            case ENDGAME:
                //
                break;
        }
    }

    public GameState getState() { return state; }
}
