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

import com.btxtech.game.jsre.common.gameengine.services.utg.UserAction;
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
    private int attackCommands;
    private int moveCommands;
    private int builderCommands;
    private int factoryCommands;
    private int moneyCollectCommands;
    private GameStartup serverGameStartup;
    private GameStartup clientStartGameStartup;
    private GameStartup clientRunningGameStartup;
    private List<DbUserAction> userActions = new ArrayList<DbUserAction>();
    private List<UserCommand> userCommands;

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
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
        start = clientRunningGameStartup.getClientTimeStamp();
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

    public void setUserAction(List<DbUserAction> userActions) {
        this.userActions = userActions;
    }

    public void calculateEnd(GameTrackingInfo next) {
        DbUserAction last = null;
        for (DbUserAction dbUserAction : userActions) {
            if (dbUserAction.getType().equals(UserAction.CLOSE_WINDOW)) {
                end = dbUserAction.getClientTimeStamp();
                return;
            }
            last = dbUserAction;
        }
        if (last != null) {
            end = last.getClientTimeStamp();
            return;
        }
        if (next != null && next.getClientRunningGameStartup() != null) {
            end = next.getClientRunningGameStartup().getClientTimeStamp();
        }
    }

    public void setUserCommands(List<UserCommand> userCommands) {
        this.userCommands = userCommands;
    }

    public void setAttackCommands(int attackCommands) {
        this.attackCommands = attackCommands;
    }

    public void setMoveCommands(int moveCommands) {
        this.moveCommands = moveCommands;
    }

    public void setBuilderCommands(int builderCommands) {
        this.builderCommands = builderCommands;
    }

    public void setFactoryCommands(int factoryCommands) {
        this.factoryCommands = factoryCommands;
    }

    public void setMoneyCollectCommands(int moneyCollectCommands) {
        this.moneyCollectCommands = moneyCollectCommands;
    }

    public int getAttackCommands() {
        return attackCommands;
    }

    public int getMoveCommands() {
        return moveCommands;
    }

    public int getBuilderCommands() {
        return builderCommands;
    }

    public int getFactoryCommands() {
        return factoryCommands;
    }

    public int getMoneyCollectCommands() {
        return moneyCollectCommands;
    }

    public List<UserActionCommand> getUserActionCommand() {
        ArrayList<UserActionCommand> userActionCommands = new ArrayList<UserActionCommand>();
        for (DbUserAction userAction : userActions) {
            userActionCommands.add(new UserActionCommand(userAction));
        }
        for (UserCommand userCommand : userCommands) {
            userActionCommands.add(new UserActionCommand(userCommand));
        }

        Collections.sort(userActionCommands);

        return userActionCommands;
    }
}
