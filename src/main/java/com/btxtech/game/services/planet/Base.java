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

package com.btxtech.game.services.planet;

import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.user.UserState;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 8:16:46 PM
 */
public class Base {
    private double accountBalance;
    private Date startTime;
    private boolean abandoned = false;
    private int baseId;
    private UserState userState;
    private Planet planet;
    private final Object syncObject = new Object();
    private final HashSet<SyncBaseItem> items = new HashSet<>();
    private SimpleBase simpleBase;
    private int houseSpace = 0;
    private int usedHouseSpace = 0;

    public Base(Planet planet, int baseId) {
        startTime = new Date();
        abandoned = false;
        this.baseId = baseId;
        this.planet = planet;
    }

    public Base(UserState userState, Planet planet, int baseId) {
        this(planet, baseId);
        userState.setBase(this);
        this.userState = userState;
    }

    public Base(double accountBalance,
                Date startTime,
                boolean abandoned,
                int baseId,
                UserState userState,
                Planet planet) {
        this.accountBalance = accountBalance;
        this.startTime = startTime;
        this.abandoned = abandoned;
        this.baseId = baseId;
        this.userState = userState;
        this.planet = planet;
    }

    public void removeItem(SyncBaseItem syncItem) {
        synchronized (items) {
            if (!items.remove(syncItem)) {
                throw new IllegalArgumentException("Item (" + syncItem + ") does not exist in base: " + getSimpleBase());
            }
        }
    }

    public void addItem(SyncBaseItem syncItem) {
        synchronized (items) {
            items.add(syncItem);
        }
    }

    public boolean hasItems() {
        return !items.isEmpty();
    }

    public Set<SyncBaseItem> getItems() {
        synchronized (items) {
            return new HashSet<>(items);
        }
    }

    public SimpleBase getSimpleBase() {
        if (simpleBase == null) {
            simpleBase = new SimpleBase(baseId, planet.getPlanetServices().getPlanetInfo().getPlanetId());
        }
        return simpleBase;
    }

    public void depositMoney(double amount) {
        synchronized (syncObject) {
            accountBalance += amount;
        }
    }

    public void withdrawalMoney(double amount) throws InsufficientFundsException {
        synchronized (syncObject) {
            if (amount > Math.round(accountBalance)) {
                throw new InsufficientFundsException();
            } else {
                accountBalance -= amount;
            }
        }
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public long getUpTime() {
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
        updateUsedHouseSpace();
        return oldSpace != houseSpace;
    }

    private void updateUsedHouseSpace() {
        int newUsedHouseSpace = 0;
        synchronized (items) {
            for (SyncBaseItem syncBaseItem : items) {
                newUsedHouseSpace += syncBaseItem.getBaseItemType().getConsumingHouseSpace();
            }
        }
        usedHouseSpace = newUsedHouseSpace;
    }

    public int getHouseSpace() {
        return houseSpace;
    }

    public int getUsedHouseSpace() {
        return usedHouseSpace;
    }

    public UserState getUserState() {
        return userState;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Planet getPlanet() {
        return planet;
    }
}
