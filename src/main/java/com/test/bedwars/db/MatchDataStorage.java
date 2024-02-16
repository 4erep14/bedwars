package com.test.bedwars.db;

import com.test.bedwars.stats.BedwarsPlayer;
import com.test.bedwars.stats.Match;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MatchDataStorage {

    public static void storeMatchData(Match match) {
        String insertMatchSql = "INSERT INTO matches (winner_team_color) VALUES (?)";
        String insertPlayerStatsSql = "INSERT INTO match_players (match_id, player_uuid, kills, wool_blocks_destroyed, beds_destroyed, deaths_in_game, damage_dealt, damage_received) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement insertMatchStmt = conn.prepareStatement(insertMatchSql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            insertMatchStmt.setString(1, match.getWinner().getColor().toString());
            insertMatchStmt.executeUpdate();

            // Get the generated match ID
            try (ResultSet generatedKeys = insertMatchStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int matchId = generatedKeys.getInt(1);

                    // Store player stats for the match
                    try (PreparedStatement insertPlayerStatsStmt = conn.prepareStatement(insertPlayerStatsSql)) {
                        for (BedwarsPlayer player : match.getPlayers()) {
                            insertPlayerStatsStmt.setInt(1, matchId);
                            insertPlayerStatsStmt.setString(2, player.getPlayer().getUniqueId().toString());
                            insertPlayerStatsStmt.setInt(3, player.getKills());
                            insertPlayerStatsStmt.setInt(4, player.getWoolBlocksDestroyed());
                            insertPlayerStatsStmt.setInt(5, player.getBedsDestroyed());
                            insertPlayerStatsStmt.setInt(6, player.getDeathsInGame());
                            insertPlayerStatsStmt.setDouble(7, player.getDamageDealt());
                            insertPlayerStatsStmt.setDouble(8, player.getDamageReceived());
                            insertPlayerStatsStmt.addBatch();
                        }
                        insertPlayerStatsStmt.executeBatch();
                    }
                } else {
                    throw new SQLException("Creating match failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}