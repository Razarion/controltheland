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

import java.util.Date;
import java.io.Serializable;

/**
 * User: beat
 * Date: 20.01.2010
 * Time: 14:32:31
 */
public class UserActionCommandMissions implements Comparable, Serializable {
    private DbUserAction userAction;
    private UserCommand userCommand;
    private DbMissionAction dbMissionAction;

    public UserActionCommandMissions(DbUserAction userAction) {
        this.userAction = userAction;
    }

    public UserActionCommandMissions(UserCommand userCommand) {
        this.userCommand = userCommand;
    }

    public UserActionCommandMissions(DbMissionAction dbMissionAction) {
        this.dbMissionAction = dbMissionAction;
    }

    @Override
    public int compareTo(Object o) {
        UserActionCommandMissions other = (UserActionCommandMissions) o;

        Date timeStamp;
        if (userAction != null) {
            timeStamp = userAction.getClientTimeStamp();
        } else if (userCommand != null) {
            timeStamp = userCommand.getClientTimeStamp();
        } else {
            timeStamp = dbMissionAction.getClientTimeStamp();
        }

        Date otherTimeStamp;
        if (other.userAction != null) {
            otherTimeStamp = other.userAction.getClientTimeStamp();
        } else if (other.userCommand != null) {
            otherTimeStamp = other.userCommand.getClientTimeStamp();
        } else {
            otherTimeStamp = other.dbMissionAction.getClientTimeStamp();
        }

        if (timeStamp.after(otherTimeStamp)) {
            return 1;
        }
        if (timeStamp.before(otherTimeStamp)) {
            return -1;
        }
        if (other.getClass().equals(getClass())) {
            return 0;
        }

        if (userAction != null) {
            return 1;
        } else {
            return -1;
        }
    }

    public Date getClientTimeStamp() {
        if (userAction != null) {
            return userAction.getClientTimeStamp();
        } else if (userCommand != null) {
            return userCommand.getClientTimeStamp();
        } else {
            return dbMissionAction.getClientTimeStamp();
        }
    }

    public DbUserAction getUserAction() {
        return userAction;
    }

    public UserCommand getUserCommand() {
        return userCommand;
    }

    public DbMissionAction getDbMissionAction() {
        return dbMissionAction;
    }
}
