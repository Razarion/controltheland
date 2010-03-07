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
    private Date end;
    private int attackCommandCount;
    private int moveCommandCount;
    private int builderCommandCount;
    private int factoryCommandCount;
    private int moneyCollectCommandCount;
    private int completedMissionCount;
    private GameStartup serverGameStartup;
    private GameStartup clientStartGameStartup;
    private GameStartup clientRunningGameStartup;
    private List<DbUserAction> userActions = new ArrayList<DbUserAction>();
    private List<UserCommand> userCommands = new ArrayList<UserCommand>();
    private List<DbMissionAction> missionActions = new ArrayList<DbMissionAction>();
    private Date mapBgLoaded;
    private Date mapImagesLoaded;
    private String baseName;

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public long getInGameMilliS() {
        if (start == null) {
            return 0;
        }
        if (end == null) {
            return -1;
        }
        return end.getTime() - start.getTime();
    }

    public void setServerGameStartup(GameStartup serverGameStartup) {
        this.serverGameStartup = serverGameStartup;
    }

    public void setClientStartGameStartup(GameStartup clientStartGameStartup) {
        this.clientStartGameStartup = clientStartGameStartup;
    }

    public void setClientRunningGameStartup(GameStartup clientRunningGameStartup) {
        this.clientRunningGameStartup = clientRunningGameStartup;
    }

    public GameStartup getServerGameStartup() {
        return serverGameStartup;
    }

    public GameStartup getClientStartGameStartup() {
        return clientStartGameStartup;
    }

    public GameStartup getClientRunningGameStartup() {
        return clientRunningGameStartup;
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

    public void setCompletedMissionCount(int completedMissionCount) {
        this.completedMissionCount = completedMissionCount;
    }

    public int getCompletedMissionCount() {
        return completedMissionCount;
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
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }
}
