package com.test.bedwars.scoreboards;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class ScoreboardUtils {
    private static final Map<Player, String> playerScoreboards = new HashMap<>();

    public static void createScoreboard(Player player, String title, Map<String, Boolean> teams) {
        try {
            String scoreboardName = "sb_" + player.getName();

            PacketPlayOutScoreboardObjective createPacket = createObjectivePacket(scoreboardName, title, 0);
            PacketPlayOutScoreboardDisplayObjective displayPacket = createDisplayObjectivePacket(scoreboardName);
            sendPacket(player, createPacket);
            sendPacket(player, displayPacket);

            // Add team entries to the scoreboard
            addTeamsToScoreboard(player, scoreboardName, teams);

            // Store the scoreboard name for future updates
            playerScoreboards.put(player, scoreboardName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateScoreboard(Player player, String title, Map<String, Boolean> teams) {
        try {
            String scoreboardName = playerScoreboards.get(player);
            if (scoreboardName == null) {
                createScoreboard(player, title, teams);
                return;
            }

            // Update scoreboard title
            PacketPlayOutScoreboardObjective updateTitlePacket = createObjectivePacket(scoreboardName, title, 2);
            sendPacket(player, updateTitlePacket);

            // Remove all existing scores
            PacketPlayOutScoreboardObjective removePacket = createObjectivePacket(scoreboardName, "", 1);
            sendPacket(player, removePacket);

            // Recreate scoreboard with updated title
            PacketPlayOutScoreboardObjective createPacket = createObjectivePacket(scoreboardName, title, 0);
            PacketPlayOutScoreboardDisplayObjective displayPacket = createDisplayObjectivePacket(scoreboardName);
            sendPacket(player, createPacket);
            sendPacket(player, displayPacket);

            // Add updated teams to scoreboard
            addTeamsToScoreboard(player, scoreboardName, teams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PacketPlayOutScoreboardObjective createObjectivePacket(String name, String title, int mode) throws Exception {
        Constructor<PacketPlayOutScoreboardObjective> constructor = PacketPlayOutScoreboardObjective.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        PacketPlayOutScoreboardObjective packet = constructor.newInstance();

        setField(packet, "a", name);
        setField(packet, "b", ChatColor.translateAlternateColorCodes('&', title));
        setField(packet, "c", IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
        setField(packet, "d", mode);

        return packet;
    }

    private static PacketPlayOutScoreboardDisplayObjective createDisplayObjectivePacket(String name) throws Exception {
        PacketPlayOutScoreboardDisplayObjective packet = new PacketPlayOutScoreboardDisplayObjective(1, new ScoreboardObjective(new Scoreboard(), name, IScoreboardCriteria.b));
        return packet;
    }

    private static PacketPlayOutScoreboardScore createScorePacket(String objectiveName, String entry, int score) throws Exception {
        PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(entry);
        setField(packet, "a", entry);
        setField(packet, "b", objectiveName);
        setField(packet, "c", score);
        setField(packet, "d", PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE);
        return packet;
    }

    private static void sendPacket(Player player, Packet<?> packet) throws Exception {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    private static void setField(Object object, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    private static void addTeamsToScoreboard(Player player, String scoreboardName, Map<String, Boolean> teams) throws Exception {
        int score = -1; // Set the same score for all entries
        for (Map.Entry<String, Boolean> entry : teams.entrySet()) {
            String teamName = entry.getKey();
            boolean isActive = entry.getValue();
            String entryText = (isActive ? ChatColor.GREEN + "✔ " : ChatColor.RED + "X ") + teamName;

            PacketPlayOutScoreboardScore scorePacket = createScorePacket(scoreboardName, entryText, score--);
            sendPacket(player, scorePacket);
        }
    }
}

