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

package com.btxtech.game.jsre.mapeditor;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import java.util.List;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 8:18:09 PM
 */
@RemoteServiceRelativePath("gwtrpc/TerrainServiceService")
public interface GameEditorAsync extends RemoteService {
    //void getTerrainField(AsyncCallback<int[][]> async);

    //void getTiles(AsyncCallback<List<Integer>> async);

   // void setTerrainField(int[][] filed, AsyncCallback<Void> asyncCallback);

    void getTerrainSettings(AsyncCallback<TerrainSettings> asyncCallback);
}