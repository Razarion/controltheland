package com.btxtech.game.services.statistics;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * User: beat
 * Date: 16.09.2011
 * Time: 20:58:42
 */
@Entity(name = "STAT_ENTRY")
public class DbStatisticsEntry implements CrudChild {

    public enum Type {
        DAY,
        WEEK,
        ALL_TIME
    }

    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private Type type;
    private Date date;
    private int killedStructureBot;
    private int killedUnitsBot;
    private int killedStructurePlayer;
    private int killedUnitsPlayer;
    private int builtStructures;
    private int builtUnits;
    private int basesDestroyedBot;
    private int basesDestroyedPlayer;
    private int ownBaseLost;
    private double moneyEarned;
    private double moneySpent;
    private int levelCompleted;

    @Override
    public Integer getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getKilledStructureBot() {
        return killedStructureBot;
    }

    public void setKilledStructureBot(int killedStructureBot) {
        this.killedStructureBot = killedStructureBot;
    }

    public void increaseKilledStructureBot() {
        killedStructureBot++;
    }

    public int getKilledUnitsBot() {
        return killedUnitsBot;
    }

    public void setKilledUnitsBot(int killedUnitsBot) {
        this.killedUnitsBot = killedUnitsBot;
    }

    public void increaseKilledUnitsBot() {
        killedUnitsBot++;
    }

    public int getKilledStructurePlayer() {
        return killedStructurePlayer;
    }

    public void setKilledStructurePlayer(int killedStructurePlayer) {
        this.killedStructurePlayer = killedStructurePlayer;
    }

    public void increaseKilledStructurePlayer() {
        killedStructurePlayer++;
    }

    public int getKilledUnitsPlayer() {
        return killedUnitsPlayer;
    }

    public void setKilledUnitsPlayer(int killedUnitsPlayer) {
        this.killedUnitsPlayer = killedUnitsPlayer;
    }

    public void increaseKilledUnitsPlayer() {
        killedUnitsPlayer++;
    }

    public double getMoneyEarned() {
        return moneyEarned;
    }

    public void setMoneyEarned(double moneyEarned) {
        this.moneyEarned = moneyEarned;
    }

    public void increaseMoneyEarned(double moneyEarned) {
        this.moneyEarned += moneyEarned;
    }

    public double getMoneySpent() {
        return moneySpent;
    }

    public void setMoneySpent(double moneySpent) {
        this.moneySpent = moneySpent;
    }

    public void increaseMoneySpent(double moneySpent) {
        this.moneySpent += moneySpent;
    }

    public int getBuiltStructures() {
        return builtStructures;
    }

    public void setBuiltStructures(int builtStructures) {
        this.builtStructures = builtStructures;
    }

    public void increaseBuiltStructures() {
        builtStructures++;
    }

    public int getBuiltUnits() {
        return builtUnits;
    }

    public void setBuiltUnits(int builtUnits) {
        this.builtUnits = builtUnits;
    }

    public void increaseBuiltUnits() {
        builtUnits++;
    }

    public int getBasesDestroyedBot() {
        return basesDestroyedBot;
    }

    public void setBasesDestroyedBot(int basesDestroyedBot) {
        this.basesDestroyedBot = basesDestroyedBot;
    }

    public void increaseBasesDestroyedBot() {
        basesDestroyedBot++;
    }

    public int getBasesDestroyedPlayer() {
        return basesDestroyedPlayer;
    }

    public void setBasesDestroyedPlayer(int basesDestroyedPlayer) {
        this.basesDestroyedPlayer = basesDestroyedPlayer;
    }

    public void increaseBasesDestroyedPlayer() {
        basesDestroyedPlayer++;
    }

    public int getOwnBaseLost() {
        return ownBaseLost;
    }

    public void setOwnBaseLost(int ownBaseLost) {
        this.ownBaseLost = ownBaseLost;
    }

    public void increaseOwnBaseLost() {
        ownBaseLost++;
    }

    public int getLevelCompleted() {
        return levelCompleted;
    }

    public void setLevelCompleted(int levelCompleted) {
        this.levelCompleted = levelCompleted;
    }

    public void increaseLevelCompleted() {
        levelCompleted++;
    }

    public void sumUp(DbStatisticsEntry dbStatisticsEntry) {
        killedUnitsBot += dbStatisticsEntry.killedUnitsBot;
        killedStructurePlayer += dbStatisticsEntry.killedStructurePlayer;
        killedUnitsPlayer += dbStatisticsEntry.killedUnitsPlayer;
        builtStructures += dbStatisticsEntry.builtStructures;
        builtUnits += dbStatisticsEntry.builtUnits;
        basesDestroyedBot += dbStatisticsEntry.basesDestroyedBot;
        basesDestroyedPlayer += dbStatisticsEntry.basesDestroyedPlayer;
        ownBaseLost += dbStatisticsEntry.ownBaseLost;
        moneyEarned += dbStatisticsEntry.moneyEarned;
        moneySpent += dbStatisticsEntry.moneySpent;
        levelCompleted += dbStatisticsEntry.levelCompleted;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {
    }

    @Override
    public void init(UserService userService) {
    }

    @Override
    public void setParent(Object o) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbStatisticsEntry)) return false;

        DbStatisticsEntry that = (DbStatisticsEntry) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
