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
import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ColorConstants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
import com.google.gwt.user.client.Timer;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 06.04.2010
 * Time: 21:23:04
 */
public class RadarItemView extends MiniMap {
    private static final int BASE_ITEM_SIZE_SMALL_MAP = 4;
    private static final int OWN_BASE_ITEM_SIZE_SMALL_MAP = 6;
    private static final int RESOURCE_ITEM_SIZE_SMALL_MAP = 2;
    private static final int BASE_ITEM_SIZE = 2;
    private static final int OWN_BASE_ITEM_SIZE = 3;
    private static final int RESOURCE_ITEM_SIZE = 1;
    private Logger log = Logger.getLogger(RadarItemView.class.getName());
    private Timer timer;

    public RadarItemView(int width, int height) {
        super(width, height);
    }

    @Override
    public void onTerrainSettings(TerrainSettings terrainSettings) {
        super.onTerrainSettings(terrainSettings);
        if (timer == null) {
            timer = new TimerPerfmon(PerfmonEnum.RADAR_ITEM_VIEW) {

                @Override
                public void runPerfmon() {
                    try {
                        draw();
                    } catch (Throwable t) {
                        log.log(Level.SEVERE, "Exception in RadarItemView Timer", t);
                    }
                }
            };
            timer.scheduleRepeating(1000);
        }
    }

    @Override
    protected void render() {
        double ownSize;
        double baseItemSize;
        double resourceItemSize;

        MiniMapRenderDetails miniMapRenderDetails = new MiniMapRenderDetails(getTileViewRectangle());
        if (miniMapRenderDetails.isDrawImages()) {
            ownSize = OWN_BASE_ITEM_SIZE_SMALL_MAP;
            baseItemSize = BASE_ITEM_SIZE_SMALL_MAP;
            resourceItemSize = RESOURCE_ITEM_SIZE_SMALL_MAP;
        } else {
            ownSize = OWN_BASE_ITEM_SIZE;
            baseItemSize = BASE_ITEM_SIZE;
            resourceItemSize = RESOURCE_ITEM_SIZE;
        }

        for (SyncItem syncItem : ItemContainer.getInstance().getItemsInRectangleFast(getAbsoluteViewRectangle())) {
            try {
                if (syncItem instanceof SyncBaseItem) {
                    SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                    Index pos = syncBaseItem.getSyncItemArea().getPosition();
                    if (pos == null) {
                        continue;
                    }
                    getContext2d().setFillStyle(ClientBase.getInstance().getBaseHtmlColor(syncBaseItem.getBase()));
                    if (ClientBase.getInstance().isMyOwnProperty(syncBaseItem)) {
                        getContext2d().fillRect(absolute2RadarPositionX(pos), absolute2RadarPositionY(pos), ownSize, ownSize);
                    } else if (ClientBase.getInstance().isEnemy(syncBaseItem)) {
                        getContext2d().fillRect(absolute2RadarPositionX(pos), absolute2RadarPositionY(pos), baseItemSize, baseItemSize);
                    } else {
                        getContext2d().fillRect(absolute2RadarPositionX(pos), absolute2RadarPositionY(pos), ownSize, ownSize);
                    }
                } else if (syncItem instanceof SyncResourceItem) {
                    Index pos = syncItem.getSyncItemArea().getPosition();
                    getContext2d().setFillStyle(ColorConstants.WHITE);
                    getContext2d().fillRect(absolute2RadarPositionX(pos), absolute2RadarPositionY(pos), resourceItemSize, resourceItemSize);
                }
            } catch (Exception e) {
                ClientExceptionHandler.handleExceptionOnlyOnce("RadarItemView.render() failed", e);
            }
        }
    }

    @Override
    public void cleanup() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
