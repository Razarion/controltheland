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

import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.services.planet.CollisionService;
import com.btxtech.game.services.planet.CollisionServiceChangedListener;
import com.btxtech.game.services.planet.ResourceService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.planet.db.DbRegionResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 08.05.2010
 * Time: 21:57:48
 */
public class ResourceServiceImpl implements ResourceService, CollisionServiceChangedListener {
    private PlanetServices planetServices;
    private ArrayList<RegionResource> regionResources = new ArrayList<>();
    private Log log = LogFactory.getLog(ResourceServiceImpl.class);

    public void init(PlanetServices planetServices) {
        this.planetServices = planetServices;
        ((CollisionService) planetServices.getCollisionService()).addCollisionServiceChangedListener(this);
    }

    @Override
    public void collisionServiceChanged() {
        for (RegionResource regionResource : regionResources) {
            regionResource.resolveCollision();
        }
    }

    @Override
    public void activate(DbPlanet dbPlanet) {
        loadDbRegionResources(dbPlanet);
        setupResources();
    }

    @Override
    public void deactivate() {
        ArrayList<RegionResource> tmpRegionResources = new ArrayList<>(regionResources);
        regionResources.clear();
        for (RegionResource regionResource : tmpRegionResources) {
            regionResource.deleteAllResources();
        }
    }

    @Override
    public void reactivate(DbPlanet dbPlanet) {
        deactivate();
        activate(dbPlanet);
    }

    @Override
    public void resourceItemDeleted(SyncResourceItem syncResourceItem) {
        for (RegionResource regionResource : regionResources) {
            if (regionResource.resourceItemDeleted(syncResourceItem)) {
                break;
            }
        }
    }

    private void loadDbRegionResources(DbPlanet dbPlanet) {
        regionResources.clear();
        Collection<DbRegionResource> dbRegionResources = dbPlanet.getRegionResourceCrud().readDbChildren();
        if (dbRegionResources.isEmpty()) {
            log.info("No DbRegionResource specified");
            return;
        }
        for (DbRegionResource dbRegionResource : dbRegionResources) {
            if (dbRegionResource.getResourceItemType() == null) {
                continue;
            }
            regionResources.add(new RegionResource(dbRegionResource, planetServices));
        }
    }

    private void setupResources() {
        for (RegionResource regionResource : regionResources) {
            @SuppressWarnings("unchecked")
            Collection<SyncResourceItem> resourceItems = (Collection<SyncResourceItem>) planetServices.getItemService().getItems(regionResource.getResourceItemType(), null);
            regionResource.adjust(resourceItems);
        }
    }
}
