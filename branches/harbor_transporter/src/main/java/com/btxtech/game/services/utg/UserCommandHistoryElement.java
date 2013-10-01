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
import com.btxtech.game.services.utg.tracker.DbUserCommand;

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

        Long timeStamp;
        if (dbUserCommand != null) {
            timeStamp = dbUserCommand.getTimeStampMs();
        } else {
            timeStamp = historyElement.getTimeStamp();
        }

        Long otherTimeStamp;
        if (other.dbUserCommand != null) {
            otherTimeStamp = other.dbUserCommand.getTimeStampMs();
        } else {
            otherTimeStamp = other.historyElement.getTimeStamp();
        }

        return timeStamp.compareTo(otherTimeStamp);
    }

    public Date getClientTimeStamp() {
        if (dbUserCommand != null) {
            return new Date(dbUserCommand.getClientTimeStamp());
        }
        return null;
    }

    public long getTimeStamp() {
        if (dbUserCommand != null) {
            return dbUserCommand.getTimeStampMs();
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
