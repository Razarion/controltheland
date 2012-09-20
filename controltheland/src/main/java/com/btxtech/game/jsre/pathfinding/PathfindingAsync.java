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

package com.btxtech.game.jsre.pathfinding;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * User: beat
 * Date: 13.02.2010
 * Time: 12:16:13
 */
public interface PathfindingAsync {
    void getTerrainInfo(int terrainId, AsyncCallback<com.btxtech.game.jsre.common.TerrainInfo> async);
}
