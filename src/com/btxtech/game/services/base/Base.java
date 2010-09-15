/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.services.base;

import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.market.impl.UserItemTypeAccess;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.utg.BaseLevelStatus;
import com.btxtech.game.services.utg.DbLevel;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 8:16:46 PM
 */
@Entity(name = "BACKUP_BASE")
public class Base implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    private double accountBalance;
    @OneToOne
    private User user;
    @OneToOne
    private BaseColor baseColor;
    private Date startTime;
    @Column(name = "kills", nullable = false, columnDefinition = "INT default '0'")
    private int kills;
    @Column(name = "created", nullable = false, columnDefinition = "INT default '0'")
    private int created;
    @Column(name = "lost", nullable = false, columnDefinition = "INT default '0'")
    private int lost;
    @Column(name = "totalSpent", nullable = false, columnDefinition = "INT default '0'")
    private double totalSpent;
    @Column(name = "totalEarned", nullable = false, columnDefinition = "INT default '0'")
    private double totalEarned;
    @Column(name = "abandoned", nullable = false, columnDefinition = "bit default b'1'")
    private boolean abandoned = false;
    @Column(nullable = false, columnDefinition = "bit default b'0'")
    private boolean bot = false;
    @OneToOne(cascade = CascadeType.ALL)
    private BaseLevelStatus baseLevelStatus;
    private int baseId;
    @Transient
    private final Object syncObject = new Object();
    @Transient
    private final HashSet<SyncBaseItem> items = new HashSet<SyncBaseItem>();
    @Transient
    private UserItemTypeAccess userItemTypeAccess;
    @Transient
    private SimpleBase simpleBase;
    @Transient
    private int houseSpace = 0;

    /**
     * Used by hibernate
     */
    public Base() {
    }

    public Base(BaseColor baseColor, User user, int baseId) {
        this.baseColor = baseColor;
        this.user = user;
        startTime = new Date();
        abandoned = false;
        this.baseId = baseId;
    }

    public void removeItem(SyncBaseItem syncItem) {
        synchronized (items) {
            if (!items.remove(syncItem)) {
                throw new IllegalArgumentException("Item (" + syncItem + ") does not exist in base: " + getSimpleBase());
            }
            lost++;
        }
    }

    public void addItem(SyncBaseItem syncItem) {
        synchronized (items) {
            items.add(syncItem);
            created++;
        }
    }

    public boolean hasItems() {
        return !items.isEmpty();
    }

    public HashSet<SyncBaseItem> getItems() {
        return items;
    }

    public BaseColor getBaseColor() {
        return baseColor;
    }

    public void setBaseColor(BaseColor baseColor) {
        this.baseColor = baseColor;
    }

    public SimpleBase getSimpleBase() {
        if (simpleBase == null) {
            simpleBase = new SimpleBase(baseId);
        }
        return simpleBase;
    }

    public void depositMoney(double amount) {
        synchronized (syncObject) {
            accountBalance += amount;
            totalEarned += amount;
        }
    }

    public void withdrawalMoney(double amount) throws InsufficientFundsException {
        synchronized (syncObject) {
            if (amount > Math.round(accountBalance)) {
                throw new InsufficientFundsException();
            } else {
                accountBalance -= amount;
                totalSpent += amount;
            }
        }
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public long getUptime() {
        return System.currentTimeMillis() - startTime.getTime();
    }

    public int getItemCount(ItemType itemType) {
        int count = 0;
        synchronized (items) {
            for (SyncItem item : items) {
                if (item.getItemType().equals(itemType)) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getItemCount() {
        return items.size();
    }

    public void clearId() {
        id = null;
        if (baseLevelStatus != null) {
            baseLevelStatus.clearId();
        }
    }

    public void increaseKills() {
        kills++;
    }

    public int getKills() {
        return kills;
    }

    public boolean isAbandoned() {
        return abandoned;
    }

    public void setAbandoned(boolean abandoned) {
        this.abandoned = abandoned;
    }

    public void connectionClosed() {
        if (user == null) {
            abandoned = true;
            userItemTypeAccess = null;
        }
    }

    public UserItemTypeAccess getUserItemTypeAccess() {
        return userItemTypeAccess;
    }

    public void setUserItemTypeAccess(UserItemTypeAccess userItemTypeAccess) {
        this.userItemTypeAccess = userItemTypeAccess;
    }

    public boolean isBot() {
        return bot;
    }

    public void setBot(boolean bot) {
        this.bot = bot;
    }

    @Override
    public String toString() {
        return getSimpleBase().toString();
    }

    public BaseLevelStatus getBaseLevelStatus() {
        return baseLevelStatus;
    }

    public void setBaseLevelStatus(BaseLevelStatus baseLevelStatus) {
        this.baseLevelStatus = baseLevelStatus;
    }

    public int getBaseId() {
        return baseId;
    }


    public void checkItemLimit4ItemAdding() throws ItemLimitExceededException, HouseSpaceExceededException {
        DbLevel dbLevel = baseLevelStatus.getCurrentLevel();
        if (getItemCount() >= dbLevel.getItemLimit()) {
            throw new ItemLimitExceededException();
        }
        if (getItemCount() >= houseSpace + dbLevel.getHouseSpace()) {
            throw new HouseSpaceExceededException();
        }
    }

    public boolean updateHouseSpace() {
        int oldSpace = houseSpace;
        houseSpace = 0;
        synchronized (items) {
            for (SyncBaseItem item : items) {
                if (item.hasSyncHouse() && item.isReady()) {
                    houseSpace += item.getSyncHouse().getSpace();
                }
            }
        }
        return oldSpace != houseSpace;
    }

    public int getTotalHouseSpace() {
        return houseSpace + baseLevelStatus.getCurrentLevel().getHouseSpace();
    }

    public int getItemLimit() {
        return baseLevelStatus.getCurrentLevel().getItemLimit();
    }

}
