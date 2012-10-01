package com.btxtech.game.jsre.client.dialogs.highscore;

import java.io.Serializable;

/**
 * User: beat
 * Date: 20.09.2011
 * Time: 14:54:23
 */
public class CurrentStatisticEntryInfo implements Serializable {
    private int rank;
    private int score;
    private String userName;
    private Integer itemCount;
    private Integer money;
    private int killed;
    private int killedPve;
    private int killedPvp;
    private int basesKilled;
    private int basesLost;
    private int created;
    private boolean isMy;
    private String planet;

    /**
     * Used by GWT
     */
    CurrentStatisticEntryInfo() {
    }

    public CurrentStatisticEntryInfo(int score, String userName, String planet, Integer itemCount, Integer money, int killed, int killedPve, int killedPvp, int basesKilled, int basesLost, int created, boolean isMy) {
        this.score = score;
        this.userName = userName;
        this.itemCount = itemCount;
        this.planet = planet;
        this.money = money;
        this.killed = killed;
        this.killedPve = killedPve;
        this.killedPvp = killedPvp;
        this.basesKilled = basesKilled;
        this.basesLost = basesLost;
        this.created = created;
        this.isMy = isMy;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public int getScore() {
        return score;
    }

    public String getUserName() {
        return userName;
    }

    public String getPlanet() {
        return planet;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public Integer getMoney() {
        return money;
    }

    public int getKilled() {
        return killed;
    }

    public int getKilledPve() {
        return killedPve;
    }

    public int getKilledPvp() {
        return killedPvp;
    }

    public int getBasesKilled() {
        return basesKilled;
    }

    public int getBasesLost() {
        return basesLost;
    }

    public int getCreated() {
        return created;
    }

    public boolean isMy() {
        return isMy;
    }
}