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

import com.btxtech.game.services.utg.DbLevel;

/**
 * User: beat
 * Date: 16.05.2010
 * Time: 17:48:30
 */
public class PendingPromotion {
    private DbLevel dbLevel;
    private boolean xpAchieved = false;
    private boolean itemCountAchieved = false;
    private boolean tutorialAchieved = false;

    public PendingPromotion(DbLevel dbLevel) {
        this.dbLevel = dbLevel;
    }

    public DbLevel getDbLevel() {
        return dbLevel;
    }

    public void setXpAchieved() {
        xpAchieved = true;
    }

    public void setItemCountAchieved() {
        itemCountAchieved = true;
    }

    public void setTutorialAchieved() {
        tutorialAchieved = true;
    }

    public boolean achieved() {
        if (dbLevel.isTutorialTermination()) {
            if (!tutorialAchieved) {
                return false;
            }
        }

        if (dbLevel.getMinXp() != null) {
            if (!xpAchieved) {
                return false;
            }
        }

        if (dbLevel.getDbItemCounts() != null && !dbLevel.getDbItemCounts().isEmpty()) {
            if (!itemCountAchieved) {
                return false;
            }
        }

        return true;
    }
}
