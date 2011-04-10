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

import com.btxtech.game.services.history.DisplayHistoryElement;

import java.io.Serializable;
import java.util.Date;

/**
 * User: beat
 * Date: 20.01.2010
 * Time: 14:32:31
 */
public class UserCommandHistoryElement implements Comparable, Serializable {
    private DbUserCommand dbUserCommand;
    private DisplayHistoryElement historyElement;

    public UserCommandHistoryElement(DbUserCommand dbUserCommand) {
        this.dbUserCommand = dbUserCommand;
    }

    public UserCommandHistoryElement(DisplayHistoryElement historyElement) {
        this.historyElement = historyElement;
    }

    @Override
    public int compareTo(Object o) {
        UserCommandHistoryElement other = (UserCommandHistoryElement) o;

        Date timeStamp;
        if (dbUserCommand != null) {
            timeStamp = dbUserCommand.getTimeStamp();
        } else {
            timeStamp = historyElement.getTimeStamp();
        }

        Date otherTimeStamp = null;
        if (other.dbUserCommand != null) {
            otherTimeStamp = other.dbUserCommand.getTimeStamp();
        } else {
            otherTimeStamp = other.historyElement.getTimeStamp();
        }

        if (timeStamp.after(otherTimeStamp)) {
            return 1;
        }
        if (timeStamp.before(otherTimeStamp)) {
            return -1;
        }
        return 0;
    }

    public Date getClientTimeStamp() {
        if (dbUserCommand != null) {
            return dbUserCommand.getClientTimeStamp();
        }
        return null;
    }

    public Date getTimeStamp() {
        if (dbUserCommand != null) {
            return dbUserCommand.getTimeStamp();
        } else {
            return historyElement.getTimeStamp();
        }
    }

    public String getInfo1() {
        if (dbUserCommand != null) {
            return dbUserCommand.getInteractionClass();
        } else {
            return historyElement.getMessage();
        }
    }

    public String getInfo2() {
        if (dbUserCommand != null) {
            return dbUserCommand.getInteraction();
        } else {
            return null;
        }
    }
}
