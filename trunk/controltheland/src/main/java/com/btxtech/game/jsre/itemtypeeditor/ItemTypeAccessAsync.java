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

package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.mapeditor.TerrainInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.Collection;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 8:18:09 PM
 */
@RemoteServiceRelativePath("gwtrpc/ItemTypeAccess")
public interface ItemTypeAccessAsync extends RemoteService {
    void getBoundingBox(int itemTypeId, AsyncCallback<BoundingBox> async);

    void saveBoundingBox(int itemTypeId, BoundingBox boundingBox, AsyncCallback<Void> async);
}