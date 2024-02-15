package com.test.bedwars.scoreboards;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class Scoreboards {

    private static final HashMap<String, Scoreboards> boards = new HashMap<>();
    private static final ScoreboardManager manager = Bukkit.getScoreboardManager();

    private Scoreboard board;
    private int teamCount;

    private Scoreboards() {

    }

    private Scoreboards(String name, String title) {
        board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective(name, "dummy");
        objective.setDisplayName(title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        teamCount = 0;
    }

    public static Scoreboards createBoard(String name, String title) {
        Scoreboards board = new Scoreboards(name, title);
        boards.put(name, board);
        return board;
    }

    public static Scoreboards getBoard(String name) {
        if (!boards.containsKey(name)) {
            Bukkit.broadcastMessage("§4No Scoreboard named §6" + name + " §4found");
        }
        return boards.get(name);
    }

    public static void addToPlayers(String name, List<Player> players) {
        for (Player player : players) {
            Scoreboards board = getBoard(name);
            if (board != null) {
                player.setScoreboard(board.board);
            }
        }
    }

    public static void addToPlayers(String name, Player... players) {
        addToPlayers(name, Arrays.asList(players));
    }

    @SuppressWarnings("unchecked")
    public static void addToAll(String name) {
        addToPlayers(name, (List<Player>) Bukkit.getOnlinePlayers());
    }

    public static void removeFromPlayers(String name, List<Player> players) {
        for (Player player : players) {
            Scoreboards board = getBoard(name);
            if (board != null && player.getScoreboard().equals(board.board)) {
                player.setScoreboard(manager.getMainScoreboard());
            }
        }
    }

    public static void removeFromPlayers(String name, Player... players) {
        removeFromPlayers(name, Arrays.asList(players));
    }

    @SuppressWarnings("unchecked")
    public static void removeFromAll(String name) {
        removeFromPlayers(name, (List<Player>) Bukkit.getOnlinePlayers());
    }

    public static void removeFromPlayers(List<Player> players) {
        for (Player player : players) {
            player.setScoreboard(manager.getMainScoreboard());
        }
    }

    public static void removeFromPlayers(Player... players) {
        removeFromPlayers(Arrays.asList(players));
    }

    @SuppressWarnings("unchecked")
    public static void removeFromAll() {
        removeFromPlayers((List<Player>) Bukkit.getOnlinePlayers());
    }

    public static void deleteBoards(String... names) {
        for (String name : names) {
            removeFromAll(name);
            boards.remove(name);
        }
    }

    public static void clearBoards() {
        for (String boardName : boards.keySet()) {
            removeFromAll(boardName);
        }
        boards.clear();
    }

    public static void updateTeam(String boardName, String teamName, String text) {
        Scoreboards board = getBoard(boardName);
        if (board != null) {
            Team team = board.board.getTeam(teamName);
            if (team != null) {
                team.setPrefix(text);
            }
        }
    }

    // ------------------
    // Scoreboard Builder
    // ------------------

    public Scoreboards addScore(String text, int slot) {
        board.getObjective(DisplaySlot.SIDEBAR).getScore(text).setScore(slot);
        return this;
    }

    public Scoreboards addScores(String[] texts, int... slots) {
        for (int i = 0; i < texts.length; i++) {
            addScore(texts[i], slots == null ? i : slots.length > i ? slots[i] : slots[slots.length - 1] + i);
        }
        return this;
    }

    public Scoreboards addScores(String... texts) {
        return addScores(texts, 0);
    }

    public Scoreboards addTeam(String teamName, String text, int slot) {
        Team team = board.registerNewTeam(teamName);
        String teamText = "";
        for (int i = 0; i < teamCount; i++) {
            teamText += "§r";
        }
        team.addEntry(teamText);
        team.setPrefix(text);
        teamCount++;
        return addScore(teamText, slot);
    }

}