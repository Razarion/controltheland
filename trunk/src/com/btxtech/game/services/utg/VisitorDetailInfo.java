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

import java.util.List;
import java.util.ArrayList;

/**
 * User: beat
 * Date: 17.01.2010
 * Time: 12:01:19
 */
public class VisitorDetailInfo {
    private UserDetails userDetails;
    private List<GameTrackingInfo> gameTrackingInfos = new ArrayList<GameTrackingInfo>();
    private List<PageAccess> pageAccessHistory;
    private long totalTime;
    private int attackCommands;
    private int moveCommands;
    private int builderCommands;
    private int factoryCommands;
    private int moneyCollectCommands;

    public VisitorDetailInfo(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public List<GameTrackingInfo> getGameTrackingInfos() {
        return gameTrackingInfos;
    }

    public void setGameTrackingInfos(List<GameTrackingInfo> gameTrackingInfos) {
        this.gameTrackingInfos = gameTrackingInfos;
        totalTime = 0;
        for (GameTrackingInfo gameTrackingInfo : gameTrackingInfos) {
            totalTime += gameTrackingInfo.getInGameMilliS();
        }
    }

    public int getGameAttemps() {
        return gameTrackingInfos.size();
    }

    public void setPageAccessHistory(List<PageAccess> pageAccessHistory) {
        this.pageAccessHistory = pageAccessHistory;
    }

    public List<PageAccess> getPageAccessHistory() {
        return pageAccessHistory;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
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
}
