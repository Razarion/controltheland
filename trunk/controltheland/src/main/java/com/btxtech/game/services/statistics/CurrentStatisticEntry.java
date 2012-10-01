package com.btxtech.game.services.statistics;

import com.btxtech.game.services.common.SimpleCrudChild;
import com.btxtech.game.services.utg.DbLevel;

import java.io.Serializable;

/**
 * User: beat
 * Date: 20.09.2011
 * Time: 14:54:23
 */
public class CurrentStatisticEntry extends SimpleCrudChild {
    private int rank;
    private int score;
    private DbLevel level;
    private int xp;
    private String userName;
    private String planetName;
    private Long baseUpTime;
    private Integer itemCount;
    private Integer money;
    private int killedStructureBot;
    private int killedUnitsBot;
    private int killedStructurePlayer;
    private int killedUnitsPlayer;
    private int lostStructureBot;
    private int lostUnitsBot;
    private int lostStructurePlayer;
    private int lostUnitsPlayer;
    private int builtStructures;
    private int builtUnits;
    private int basesDestroyedBot;
    private int basesDestroyedPlayer;
    private int basesLostBot;
    private int basesLostPlayer;

    public CurrentStatisticEntry(int score, DbLevel level, int xp, String userName, String planetName, Long baseUpTime, Integer itemCount, Integer money, StatisticsEntry statisticsEntry) {
        this.level = level;
        this.xp = xp;
        this.score = score;
        this.userName = userName;
        this.planetName = planetName;
        this.baseUpTime = baseUpTime;
        this.itemCount = itemCount;
        this.money = money;
        killedStructureBot = statisticsEntry.getKilledStructureBot();
        killedUnitsBot = statisticsEntry.getKilledUnitsBot();
        killedStructurePlayer = statisticsEntry.getKilledStructurePlayer();
        killedUnitsPlayer = statisticsEntry.getKilledUnitsPlayer();
        lostStructureBot = statisticsEntry.getLostStructureBot();
        lostUnitsBot = statisticsEntry.getLostUnitsBot();
        lostStructurePlayer = statisticsEntry.getLostStructurePlayer();
        lostUnitsPlayer = statisticsEntry.getLostUnitsPlayer();
        builtStructures = statisticsEntry.getBuiltStructures();
        builtUnits = statisticsEntry.getBuiltUnits();
        basesDestroyedBot = statisticsEntry.getBasesDestroyedBot();
        basesDestroyedPlayer = statisticsEntry.getBasesDestroyedPlayer();
        basesLostBot = statisticsEntry.getBasesLostBot();
        basesLostPlayer = statisticsEntry.getBasesLostPlayer();
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public DbLevel getLevel() {
        return level;
    }

    public int getScore() {
        return score;
    }

    public int getXp() {
        return xp;
    }

    public String getUserName() {
        return userName;
    }

    public String getPlanetName() {
        return planetName;
    }

    public Long getBaseUpTime() {
        return baseUpTime;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public Integer getMoney() {
        return money;
    }

    public int getKilledStructureBot() {
        return killedStructureBot;
    }

    public int getKilledUnitsBot() {
        return killedUnitsBot;
    }

    public int getKilledStructurePlayer() {
        return killedStructurePlayer;
    }

    public int getKilledUnitsPlayer() {
        return killedUnitsPlayer;
    }

    public int getKilled() {
        return killedStructureBot + killedUnitsBot + killedStructurePlayer + killedUnitsPlayer;
    }

    public int getKilledStructures() {
        return killedStructureBot + killedStructurePlayer;
    }

    public int getKilledUnits() {
        return killedUnitsBot + killedUnitsPlayer;
    }

    public int getKilledBot() {
        return killedStructureBot + killedUnitsBot;
    }

    public int getKilledPlayer() {
        return killedStructurePlayer + killedUnitsPlayer;
    }

    public int getLostStructureBot() {
        return lostStructureBot;
    }

    public int getLostUnitsBot() {
        return lostUnitsBot;
    }

    public int getLostStructurePlayer() {
        return lostStructurePlayer;
    }

    public int getLostUnitsPlayer() {
        return lostUnitsPlayer;
    }

    public int getLost() {
        return lostStructureBot + lostUnitsBot + lostStructurePlayer + lostUnitsPlayer;
    }

    public int getLostStructures() {
        return lostStructureBot + lostStructurePlayer;
    }

    public int getLostUnits() {
        return lostUnitsBot + lostUnitsPlayer;
    }

    public int getLostBot() {
        return lostStructureBot + lostUnitsBot;
    }

    public int getLostPlayer() {
        return lostStructurePlayer + lostUnitsPlayer;
    }

    public int getBuiltStructures() {
        return builtStructures;
    }

    public int getBuiltUnits() {
        return builtUnits;
    }

    public int getBuilt() {
        return builtStructures + builtUnits;
    }

    public int getBasesDestroyedBot() {
        return basesDestroyedBot;
    }

    public int getBasesDestroyedPlayer() {
        return basesDestroyedPlayer;
    }

    public int getBasesDestroyed() {
        return basesDestroyedBot + basesDestroyedPlayer;
    }

    public int getBasesLostBot() {
        return basesLostBot;
    }

    public int getBasesLostPlayer() {
        return basesLostPlayer;
    }

    public int getBasesLost() {
        return basesLostBot + basesLostPlayer;
    }

    @Override
    public Serializable getId() {
        return System.identityHashCode(this);
    }
}