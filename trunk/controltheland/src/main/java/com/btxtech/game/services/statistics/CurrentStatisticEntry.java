package com.btxtech.game.services.statistics;

import com.btxtech.game.services.common.SimpleCrudChild;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.utg.DbAbstractLevel;

import java.io.Serializable;

/**
 * User: beat
 * Date: 20.09.2011
 * Time: 14:54:23
 */
public class CurrentStatisticEntry extends SimpleCrudChild {
    private DbAbstractLevel level;
    private String user;
    private String baseName;
    private String baseUpTime;
    private int itemCount;
    private int money;

    public CurrentStatisticEntry(DbAbstractLevel level, String user, String baseName, String baseUpTime, int itemCount, int money) {
        this.level = level;
        this.user = user;
        this.baseName = baseName;
        this.baseUpTime = baseUpTime;
        this.itemCount = itemCount;
        this.money = money;
    }

    public DbAbstractLevel getLevel() {
        return level;
    }

    public String getUser() {
        return user;
    }

    public String getBaseName() {
        return baseName;
    }

    public String getBaseUpTime() {
        return baseUpTime;
    }

    public int getItemCount() {
        return itemCount;
    }

    public int getMoney() {
        return money;
    }

    @Override
    public Serializable getId() {
        return System.identityHashCode(this);
    }
}
