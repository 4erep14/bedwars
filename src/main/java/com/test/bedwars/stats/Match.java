package com.test.bedwars.stats;

import com.test.bedwars.gamemanager.GameManager;
import com.test.bedwars.worlds.Island;

import java.util.ArrayList;
import java.util.List;

public class Match {

    GameManager gameManager;

    private int matchId;
    private List<Island> teams;
    private Island winner;
    private List<BedwarsPlayer> players;

    public Match(GameManager gameManager) {
        this.gameManager = gameManager;
        this.teams = new ArrayList<>();
        this.players = new ArrayList<>();
    }

    public int getMatchId() {
        return matchId;
    }

    public List<Island> getTeams() {
        return teams;
    }

    public void addTeam(Island team) {
        teams.add(team);
    }

    public Island getWinner() {
        return winner;
    }

    public void setWinner(Island winner) {
        this.winner = winner;
    }

    public List<BedwarsPlayer> getPlayers() {
        return players;
    }

    public void addPlayer(BedwarsPlayer player) {
        players.add(player);
    }
}
