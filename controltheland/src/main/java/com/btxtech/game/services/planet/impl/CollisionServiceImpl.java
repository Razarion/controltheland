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

package com.btxtech.game.services.planet.impl;

import com.btxtech.game.jsre.client.terrain.TerrainListener;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.collision.Path;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.CommonCollisionServiceImpl;
import com.btxtech.game.services.planet.CollisionService;
import com.btxtech.game.services.planet.CollisionServiceChangedListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

/**
 * User: beat
 * Date: May 24, 2009
 * Time: 6:44:01 PM
 */
public class CollisionServiceImpl extends CommonCollisionServiceImpl implements CollisionService, TerrainListener {
    private PlanetServices planetServices;
    private Log log = LogFactory.getLog(CollisionServiceImpl.class);
    private ArrayList<CollisionServiceChangedListener> collisionServiceChangedListeners = new ArrayList<>();

    @PostConstruct
    public void init(PlanetServices planetServices) {
        this.planetServices = planetServices;
        try {
            planetServices.getTerrainService().addTerrainListener(this);
            if (planetServices.getTerrainService().getTerrainSettings() != null) {
                fireCollisionChangeEvent();
            } else {
                log.error("No terrain settings for real game");
            }
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    private void fireCollisionChangeEvent() {
        for (CollisionServiceChangedListener collisionServiceChangedListener : collisionServiceChangedListeners) {
            collisionServiceChangedListener.collisionServiceChanged();
        }
    }

/*    @Override
    public Index getFreeRandomPosition(ItemType itemType, Territory territory, int itemFreeRange, boolean botFree) {
        if (!territory.isItemAllowed(itemType.getBaseId())) {
            throw new IllegalArgumentException("Item Type '" + itemType + "' not allowed on territory: " + territory);
        }

        Random random = new Random();
        List<Rectangle> territoryRectangles = new ArrayList<>(territory.getTerritoryTileRegions());

        for (int i = 0; i < MAX_TRIES; i++) {
            int territoryRectIndex = random.nextInt(territoryRectangles.size());
            Rectangle tileRectangle = territoryRectangles.get(territoryRectIndex);
            Rectangle absoluteRectangle = terrainService.convertToAbsolutePosition(tileRectangle);
            int x = random.nextInt(absoluteRectangle.getWidth()) + absoluteRectangle.getX();
            int y = random.nextInt(absoluteRectangle.getHeight()) + absoluteRectangle.getY();
            Index point = new Index(x, y);
            if (botFree && botService.isInRealm(point)) {
                continue;
            }

            if (!terrainService.isFree(point, itemType)) {
                continue;
            }
            Index start = point.sub(new Index(itemFreeRange / 2, itemFreeRange / 2));
            Rectangle rectangle = new Rectangle(start.getX(), start.getY(), itemFreeRange, itemFreeRange);
            if (itemService.hasStandingItemsInRect(rectangle, null)) {
                continue;
            }
            return point;
        }
        throw new IllegalStateException("Can not find free position. itemType: " + itemType + " territory: " + territory + " itemFreeRange: " + itemFreeRange);
    }*/

    @Override
    public void onTerrainChanged() {
        fireCollisionChangeEvent();
    }

    @Override
    public void addCollisionServiceChangedListener(CollisionServiceChangedListener collisionServiceChangedListener) {
        collisionServiceChangedListeners.add(collisionServiceChangedListener);
        //if (!getPassableRectangles().isEmpty()) {
            collisionServiceChangedListener.collisionServiceChanged();
        //}
    }

    @Override
    public void removeCollisionServiceChangedListener(CollisionServiceChangedListener collisionServiceChangedListener) {
        collisionServiceChangedListeners.remove(collisionServiceChangedListener);
    }

    @Override
    protected PlanetServices getServices() {
        return planetServices;
    }

    @Override
    public boolean checkIfPathValid(Path path) {
        return true;
    }
}
