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

package com.btxtech.game.services.planet;

import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.services.action.CommonActionService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;

import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: Jun 1, 2009
 * Time: 12:47:37 PM
 */
public interface ActionService extends CommonActionService, SyncItemListener {
    void executeCommand(BaseCommand baseCommand, boolean supressUserCheck) throws ItemDoesNotExistException, NotYourBaseException;

    void executeCommands(List<BaseCommand> baseCommands);

    void reload();

    void pause(boolean pause);

    boolean isBusy();

    void activate();

    void deactivate();

    void onGuildChanged(Collection<SyncBaseItem> idleAttackItems);
}
