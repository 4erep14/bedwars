package com.test.bedwars.stats;

import org.bukkit.entity.Player;

public class BedwarsPlayer {
    Player player;
    private int kills = 0;



    private int woolBlocksDestroyed = 0;
    private int bedsDestroyed = 0;
    private int deathsInGame = 0;
    private double damageDealt = 0;
    private double damageReceived = 0;

    public BedwarsPlayer(Player player) { this.player = player; }

    public Player getPlayer() { return player; }

    public int getKills() { return kills; }

    public int getWoolBlocksDestroyed() { return woolBlocksDestroyed; }

    public int getBedsDestroyed() { return bedsDestroyed; }

    public int getDeathsInGame() { return deathsInGame; }

    public double getDamageDealt() { return damageDealt; }

    public double getDamageReceived() { return damageReceived; }

    public void setPlayer(Player player) { this.player = player; }

    public void setKills(int kills) { this.kills = kills; }

    public void incrementWoolBlocksDestroyed() { this.woolBlocksDestroyed++; }

    public void incrementBedsDestroyed() { this.bedsDestroyed++; }

    public void incrementDeathsInGame() { this.deathsInGame++; }

    public void addToDamageDealt(double damageDealt) { this.damageDealt += damageDealt; }

    public void addToDamageReceived(double damageReceived) { this.damageReceived += damageReceived; }
}
