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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: beat
 * Date: 17.01.2010
 * Time: 13:15:17
 */
public class RealGameTrackingInfo implements Serializable {
    private List<DbUserCommand> dbUserCommands = new ArrayList<DbUserCommand>();
    private List<DisplayHistoryElement> historyElements;

    public void setUserCommands(List<DbUserCommand> dbUserCommands) {
        this.dbUserCommands = dbUserCommands;
    }

    public void setHistoryElements(List<DisplayHistoryElement> historyElements) {
        this.historyElements = historyElements;
    }

    public List<UserCommandHistoryElement> getUserCommandHistoryElements() {
        ArrayList<UserCommandHistoryElement> userCommands = new ArrayList<UserCommandHistoryElement>();
        for (DbUserCommand dbUserCommand : dbUserCommands) {
            userCommands.add(new UserCommandHistoryElement(dbUserCommand));
        }
        for (DisplayHistoryElement historyElement : historyElements) {
            userCommands.add(new UserCommandHistoryElement(historyElement));            
        }
        Collections.sort(userCommands);
        return userCommands;
    }
}
