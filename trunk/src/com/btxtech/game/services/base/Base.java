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

import com.btxtech.game.jsre.client.common.Level;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbRealGameLevel;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
    private BaseColor baseColor;
    private Date startTime;
    private int kills;
    private int created;
    private int lost;
    private double totalSpent;
    private double totalEarned;
    private boolean abandoned = false;
    private int baseId;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "base", fetch = FetchType.LAZY)
    private UserState userState;
    @Transient
    private final Object syncObject = new Object();
    @Transient
    private final HashSet<SyncBaseItem> items = new HashSet<SyncBaseItem>();
    @Transient
    private SimpleBase simpleBase;
    @Transient
    private int houseSpace = 0;

    /**
     * Used by hibernate
     */
    public Base() {
    }

    public Base(BaseColor baseColor, UserState userState, int baseId) {
        this.baseColor = baseColor;
        this.userState = userState;
        userState.setBase(this);
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
        if (userState != null) {
            userState.clearId();
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

    public void setAbandoned() {
        abandoned = true;
        userState = null;
    }

    @Override
    public String toString() {
        return getSimpleBase().toString();
    }

    public int getBaseId() {
        return baseId;
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

    public int getHouseSpace() {
        return houseSpace;
    }

    public UserState getUserState() {
        return userState;
    }

    public Date getStartTime() {
        return startTime;
    }

    public int getCreated() {
        return created;
    }

    public int getLost() {
        return lost;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public double getTotalEarned() {
        return totalEarned;
    }
}
