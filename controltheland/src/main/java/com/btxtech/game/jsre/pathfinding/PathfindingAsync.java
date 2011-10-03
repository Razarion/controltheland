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

import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.mapeditor.TerrainInfo;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 13.02.2010
 * Time: 12:16:13
 */
public interface PathfindingAsync {
    void getPassableRectangles(AsyncCallback<Map<TerrainType, List<Rectangle>>> async);

    void getTerrainInfo(AsyncCallback<TerrainInfo> async);

    void findPath(Index start, Index destination, TerrainType terrainType, AsyncCallback<List<Index>> async);
}
