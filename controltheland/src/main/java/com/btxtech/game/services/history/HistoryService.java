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

package com.btxtech.game.services.history;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.user.User;
import java.util.List;

/**
 * User: beat
 * Date: Jul 5, 2009
 * Time: 7:27:16 PM
 */
public interface HistoryService {
    void addBaseStartEntry(SimpleBase simpleBase);

    void addBaseDefeatedEntry(SimpleBase actor, SimpleBase target);

    void addBaseSurrenderedEntry(SimpleBase simpleBase);

    void addItemCreatedEntry(SyncBaseItem syncBaseItem);

    void addItemDestroyedEntry(SimpleBase actor, SyncBaseItem target);

    List<DisplayHistoryElement> getNewestHistoryElements(User user, int count);
}
