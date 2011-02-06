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

package com.btxtech.game.services.utg.impl;

import com.btxtech.game.services.utg.DbAbstractLevel;

/**
 * User: beat
 * Date: 16.05.2010
 * Time: 17:48:30
 */
public class PendingPromotion {
    public static final String INTERIM_PROMOTION_TUTORIAL = "TUTORIAL";
    public static final String INTERIM_PROMOTION_XP = "XP";
    public static final String INTERIM_PROMOTION_ITEMS = "ITEMS";
    public static final String INTERIM_PROMOTION_MIN_MONEY = "MIN MONEY";
    public static final String INTERIM_PROMOTION_DELTA_MONEY = "DELTA MONEY";
    public static final String INTERIM_PROMOTION_DELTA_KILLS = "DELTA KILLS";
    private DbAbstractLevel dbAbstractLevel;
    private boolean xpAchieved = false;
    private boolean itemCountAchieved = false;
    private boolean minMoneyAchieved = false;
    private boolean deltaMoneyAchieved = false;
    private boolean deltaKillsAchieved = false;

    public PendingPromotion(DbAbstractLevel dbAbstractLevel) {
        this.dbAbstractLevel = dbAbstractLevel;
    }

    public DbAbstractLevel getDbLevel() {
        return dbAbstractLevel;
    }

    public void setXpAchieved() {
        xpAchieved = true;
    }

    public void setItemCountAchieved() {
        itemCountAchieved = true;
    }

    public void setMinMoneyAchieved() {
        minMoneyAchieved = true;
    }

    public void setDeltaMoneyAchieved() {
        deltaMoneyAchieved = true;
    }

    public void setDeltaKillsAchieved() {
        deltaKillsAchieved = true;
    }

   /* public boolean achieved() {
        if (dbAbstractLevel.getMinXp() != null) {
            if (!xpAchieved) {
                return false;
            }
        }

        if (dbAbstractLevel.getDbItemCounts() != null && !dbAbstractLevel.getDbItemCounts().isEmpty()) {
            if (!itemCountAchieved) {
                return false;
            }
        }

        if (dbAbstractLevel.getMinMoney() != null) {
            if (!minMoneyAchieved) {
                return false;
            }
        }

        if (dbAbstractLevel.getDeltaMoney() != null) {
            if (!deltaMoneyAchieved) {
                return false;
            }
        }

        if (dbAbstractLevel.getDeltaKills() != null) {
            if (!deltaKillsAchieved) {
                return false;
            }
        }

        return true;
    } */
}
