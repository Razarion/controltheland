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

package com.btxtech.game.jsre.common.gameengine.services.connection;

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

/**
 * User: beat
 * Date: 06.12.2009
 * Time: 18:49:44
 */
public interface ConnectionService {
    void sendSyncInfo(SyncItem syncItem);

    GameEngineMode getGameEngineMode();
}