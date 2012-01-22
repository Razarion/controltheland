package com.btxtech.game.services.statistics;

import com.btxtech.game.services.common.SimpleCrudChild;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.utg.DbLevel;

import java.io.Serializable;

/**
 * User: beat
 * Date: 20.09.2011
 * Time: 14:54:23
 */
public class CurrentStatisticEntry extends SimpleCrudChild {
    private DbLevel level;
    private User user;
    private String baseName;
    private Long baseUpTime;
    private Integer itemCount;
    private Integer money;

    public CurrentStatisticEntry(DbLevel level, User user, String baseName, Long baseUpTime, Integer itemCount, Integer money) {
        this.level = level;
        this.user = user;
        this.baseName = baseName;
        this.baseUpTime = baseUpTime;
        this.itemCount = itemCount;
        this.money = money;
    }

    public DbLevel getLevel() {
        return level;
    }

    public User getUser() {
        return user;
    }

    public String getBaseName() {
        return baseName;
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

    @Override
    public Serializable getId() {
        return System.identityHashCode(this);
    }
}
