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

package com.btxtech.game.jsre.client.cockpit.radar;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.ColorConstants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.google.gwt.user.client.Timer;

/**
 * User: beat
 * Date: 06.04.2010
 * Time: 21:23:04
 */
public class RadarItemView extends MiniMap {
    public static final int BASE_ITEM_SIZE_SMALL_MAP = 4;
    public static final int OWN_BASE_ITEM_SIZE_SMALL_MAP = 6;
    public static final int RESOURCE_ITEM_SIZE_SMALL_MAP = 2;
    public static final int BASE_ITEM_SIZE = 2;
    public static final int OWN_BASE_ITEM_SIZE = 3;
    public static final int RESOURCE_ITEM_SIZE = 1;

    public RadarItemView(int width, int height) {
        super(width, height, false);
    }

    @Override
    public void onTerrainSettings(TerrainSettings terrainSettings) {
        super.onTerrainSettings(terrainSettings);
        Timer timer = new Timer() {

            @Override
            public void run() {
                refreshItems();
            }
        };
        timer.scheduleRepeating(1000);
    }

    private void refreshItems() {
        clear();

        double ownSize;
        double baseItemSize;
        double resourceItemSize;
        if (getScale() > 0.01) {
            ownSize = OWN_BASE_ITEM_SIZE_SMALL_MAP / getScale();
            baseItemSize = BASE_ITEM_SIZE_SMALL_MAP / getScale();
            resourceItemSize = RESOURCE_ITEM_SIZE_SMALL_MAP / getScale();
        } else {
            ownSize = OWN_BASE_ITEM_SIZE / getScale();
            baseItemSize = BASE_ITEM_SIZE / getScale();
            resourceItemSize = RESOURCE_ITEM_SIZE / getScale();
        }

        for (ClientSyncItem clientSyncItem : ItemContainer.getInstance().getItems()) {
            if (clientSyncItem.isSyncBaseItem()) {
                Index pos = clientSyncItem.getSyncItem().getSyncItemArea().getPosition();
                if (pos == null) {
                    continue;
                }
                getContext2d().setFillStyle(ClientBase.getInstance().getBaseHtmlColor(clientSyncItem.getSyncBaseItem().getBase()));
                if (clientSyncItem.isMyOwnProperty()) {
                    getContext2d().fillRect(pos.getX(), pos.getY(), ownSize, ownSize);
                } else {
                    getContext2d().fillRect(pos.getX(), pos.getY(), baseItemSize, baseItemSize);
                }
            } else if (clientSyncItem.isSyncResourceItem()) {
                Index pos = clientSyncItem.getSyncItem().getSyncItemArea().getPosition();
                getContext2d().setFillStyle(ColorConstants.WHITE);
                getContext2d().fillRect(pos.getX(), pos.getY(), resourceItemSize, resourceItemSize);
            }
        }
    }
}
