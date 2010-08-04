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

import com.btxtech.game.jsre.client.StartupTask;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: 17.01.2010
 * Time: 13:15:17
 */
public class GameTrackingInfo implements Serializable {
    private Date start;
    private Long duration;
    private int attackCommandCount;
    private int moveCommandCount;
    private int builderCommandCount;
    private int factoryCommandCount;
    private int moneyCollectCommandCount;
    private List<GameStartup> gameStartups = new ArrayList<GameStartup>();
    private List<DbUserAction> userActions = new ArrayList<DbUserAction>();
    private List<UserCommand> userCommands = new ArrayList<UserCommand>();
    private List<DbMissionAction> missionActions = new ArrayList<DbMissionAction>();
    private Date mapBgLoaded;
    private Date mapImagesLoaded;
    private Long startupDuration;

    public GameTrackingInfo(GameStartup gameStartup) {
        gameStartups.add(gameStartup);
        if (!StartupTask.isFirstTask(gameStartup.getState())) {
            throw new IllegalArgumentException("gameStartup must be first task");
        }
        start = gameStartup.getClientTimeStamp();
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }


    public List<GameStartup> getGameStartups() {
        return gameStartups;
    }

    public void setUserActions(List<DbUserAction> userActions) {
        this.userActions = userActions;
    }

    public void setUserCommands(List<UserCommand> userCommands) {
        this.userCommands = userCommands;
    }

    public void setMissionActions(List<DbMissionAction> missionActions) {
        this.missionActions = missionActions;
    }

    public void setAttackCommandCount(int attackCommandCount) {
        this.attackCommandCount = attackCommandCount;
    }

    public void setMoveCommandCount(int moveCommandCount) {
        this.moveCommandCount = moveCommandCount;
    }

    public void setBuilderCommandCount(int builderCommandCount) {
        this.builderCommandCount = builderCommandCount;
    }

    public void setFactoryCommandCount(int factoryCommandCount) {
        this.factoryCommandCount = factoryCommandCount;
    }

    public void setMoneyCollectCommandCount(int moneyCollectCommandCount) {
        this.moneyCollectCommandCount = moneyCollectCommandCount;
    }

    public int getAttackCommandCount() {
        return attackCommandCount;
    }

    public int getMoveCommandCount() {
        return moveCommandCount;
    }

    public int getBuilderCommandCount() {
        return builderCommandCount;
    }

    public int getFactoryCommandCount() {
        return factoryCommandCount;
    }

    public int getMoneyCollectCommandCount() {
        return moneyCollectCommandCount;
    }

    public List<UserActionCommandMissions> getUserActionCommand() {
        ArrayList<UserActionCommandMissions> userActionCommands = new ArrayList<UserActionCommandMissions>();
        for (DbUserAction userAction : userActions) {
            userActionCommands.add(new UserActionCommandMissions(userAction));
        }
        for (UserCommand userCommand : userCommands) {
            userActionCommands.add(new UserActionCommandMissions(userCommand));
        }
        for (DbMissionAction missionAction : missionActions) {
            userActionCommands.add(new UserActionCommandMissions(missionAction));
        }

        Collections.sort(userActionCommands);

        return userActionCommands;
    }

    public Date getMapBgLoaded() {
        return mapBgLoaded;
    }

    public void setMapBgLoaded(Date mapBgLoaded) {
        this.mapBgLoaded = mapBgLoaded;
    }

    public Date getMapImagesLoaded() {
        return mapImagesLoaded;
    }

    public void setMapImagesLoaded(Date mapImagesLoaded) {
        this.mapImagesLoaded = mapImagesLoaded;
    }

    public String getBaseName() {
        if (gameStartups.isEmpty()) {
            return "???";
        } else {
            return gameStartups.get(0).getBaseName();
        }
    }

    public String getUserName() {
        if (gameStartups.isEmpty()) {
            return "???";
        } else {
            if (gameStartups.get(0).getUserName() != null) {
                return gameStartups.get(0).getUserName();
            } else {
                return "not registered";
            }
        }
    }

    public boolean hasDuration() {
        return duration != null;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public boolean hasTotalStartup() {
        return startupDuration != null;
    }

    public void setTotalStartup(long startupDuration) {
        this.startupDuration = startupDuration;
    }

    public long getStartupDuration() {
        return startupDuration;
    }
}
