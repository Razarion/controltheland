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

package com.btxtech.game.jsre.client.effects;

import com.btxtech.game.jsre.client.ClientClipHandler;
import com.btxtech.game.jsre.client.NoSuchClipException;
import com.btxtech.game.jsre.client.NoSuchImageSpriteMapInfoException;
import com.btxtech.game.jsre.client.common.info.PreloadedImageSpriteMapInfo;
import com.btxtech.game.jsre.client.renderer.ClipRendererModel;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

/**
 * User: beat
 * Date: Jul 1, 2009
 * Time: 2:28:42 PM
 */
public class Explosion extends ClipRendererModel {
    private static final int REMOVE_ITEM = 3;
    private SyncItem syncItem;

    public Explosion(long timeStamp, SyncItem syncItem) throws NoSuchClipException, NoSuchImageSpriteMapInfoException {
        initAndPlaySound(timeStamp, ClientClipHandler.getInstance().getClipInfo(syncItem.getItemType().getExplosionClipId()), syncItem.getSyncItemArea().getPosition(), 0.0, false);
        setPreLoadedSpriteMapInfo(PreloadedImageSpriteMapInfo.Type.EXPLOSION);
        this.syncItem = syncItem;
    }

    public SyncItem getSyncItem() {
        return syncItem;
    }

    public boolean isItemVisible() {
        return getFrame() <= REMOVE_ITEM;
    }
}
