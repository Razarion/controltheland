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
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.PreloadedImageSpriteMapInfo;
import com.btxtech.game.jsre.client.renderer.ClipRendererModel;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

/**
 * User: beat
 * Date: 30.12.2009
 * Time: 12:48:08
 */
public class MuzzleFlash extends ClipRendererModel {
    private SyncBaseItem syncBaseItem;

    public MuzzleFlash(SyncBaseItem syncBaseItem, int muzzleFlashNr) throws ItemDoesNotExistException, NoSuchClipException, NoSuchImageSpriteMapInfoException {
        this.syncBaseItem = syncBaseItem;
        BaseItemType baseItemType = syncBaseItem.getBaseItemType();
        double angel = syncBaseItem.getSyncItemArea().getAngel();
        angel = baseItemType.getBoundingBox().getAllowedAngel(angel);
        int angelIndex = baseItemType.getBoundingBox().angelToAngelIndex(angel);
        Index muzzleStart = baseItemType.getWeaponType().getMuzzleFlashPosition(muzzleFlashNr, angelIndex);
        Index absoluteMuzzleStart = syncBaseItem.getSyncItemArea().getPosition().add(muzzleStart);
        initAndPlaySound(ClientClipHandler.getInstance().getMuzzleFireClipInfo(baseItemType), absoluteMuzzleStart, angel, false);
        setPreLoadedSpriteMapInfo(PreloadedImageSpriteMapInfo.Type.MUZZLE_FLASH);
    }

    public SyncBaseItem getSyncBaseItem() {
        return syncBaseItem;
    }
}
