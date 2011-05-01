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

package com.btxtech.game.services.utg;

import com.btxtech.game.services.utg.tracker.DbPageAccess;
import com.btxtech.game.services.utg.tracker.DbSessionDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 17.01.2010
 * Time: 12:01:19
 */
public class SessionDetailDto {
    private DbSessionDetail dbSessionDetail;
    private List<DbPageAccess> dbPageAccessHistory;
    private int attackCommands;
    private int moveCommands;
    private int builderCommands;
    private int factoryCommands;
    private int moneyCollectCommands;
    private int gameAttempts;
    private List<LifecycleTrackingInfo> lifecycleTrackingInfos = new ArrayList<LifecycleTrackingInfo>();

    public SessionDetailDto(DbSessionDetail dbSessionDetail) {
        this.dbSessionDetail = dbSessionDetail;
    }

    public DbSessionDetail getUserDetails() {
        return dbSessionDetail;
    }

    public void setGameAttempts(int gameAttempts) {
        this.gameAttempts = gameAttempts;
    }

    public int getGameAttempts() {
        return gameAttempts;
    }

    public void setPageAccessHistory(List<DbPageAccess> dbPageAccessHistory) {
        this.dbPageAccessHistory = dbPageAccessHistory;
    }

    public List<DbPageAccess> getPageAccessHistory() {
        return dbPageAccessHistory;
    }

    public int getAttackCommands() {
        return attackCommands;
    }

    public void setAttackCommands(int attackCommands) {
        this.attackCommands = attackCommands;
    }

    public int getMoveCommands() {
        return moveCommands;
    }

    public void setMoveCommands(int moveCommands) {
        this.moveCommands = moveCommands;
    }

    public int getBuilderCommands() {
        return builderCommands;
    }

    public void setBuilderCommands(int builderCommands) {
        this.builderCommands = builderCommands;
    }

    public int getFactoryCommands() {
        return factoryCommands;
    }

    public void setFactoryCommands(int factoryCommands) {
        this.factoryCommands = factoryCommands;
    }

    public int getMoneyCollectCommands() {
        return moneyCollectCommands;
    }

    public void setMoneyCollectCommands(int moneyCollectCommands) {
        this.moneyCollectCommands = moneyCollectCommands;
    }

    public void setLifecycleTrackingInfos(List<LifecycleTrackingInfo> lifecycleTrackingInfos) {
        this.lifecycleTrackingInfos = lifecycleTrackingInfos;
    }

    public List<LifecycleTrackingInfo> getLifecycleTrackingInfos() {
        return lifecycleTrackingInfos;
    }
}
