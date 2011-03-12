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

package com.btxtech.game.services.resource.impl;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.collision.CollisionServiceChangedListener;
import com.btxtech.game.services.common.CrudServiceHelper;
import com.btxtech.game.services.common.CrudServiceHelperSpringTransactionImpl;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.resource.DbRegionResource;
import com.btxtech.game.services.resource.ResourceService;
import com.btxtech.game.services.terrain.TerrainService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 08.05.2010
 * Time: 21:57:48
 */
@Component("resourceService")
public class ResourceServiceImpl implements ResourceService, CollisionServiceChangedListener {
    @Autowired
    private CollisionService collisionService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private TerrainService terrainService;
    @Autowired
    private ApplicationContext applicationContext;
    private HibernateTemplate hibernateTemplate;
    private ArrayList<RegionResource> regionResources = new ArrayList<RegionResource>();
    private Log log = LogFactory.getLog(ResourceServiceImpl.class);
    private CrudServiceHelper<DbRegionResource> dbRegionResourceCrudServiceHelper;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @PostConstruct
    public void start() {
        dbRegionResourceCrudServiceHelper = CrudServiceHelperSpringTransactionImpl.create(applicationContext, DbRegionResource.class);
        collisionService.addCollisionServiceChangedListener(this);
    }

    @Override
    public CrudServiceHelper<DbRegionResource> getDbRegionResourceCrudServiceHelper() {
        return dbRegionResourceCrudServiceHelper;
    }

    @Override
    public void collisionServiceChanged() {
        for (RegionResource regionResource : regionResources) {
            regionResource.resolveCollision();
        }
    }

    @Override
    public void activate() {
        loadDbRegionResources();
        setupResources();
    }

    @Override
    public void resourceItemDeleted(SyncResourceItem syncResourceItem) {
        for (RegionResource regionResource : regionResources) {
            if (regionResource.resourceItemDeleted(syncResourceItem)) {
                break;
            }
        }
    }

    private void loadDbRegionResources() {
        regionResources.clear();
        @SuppressWarnings("unchecked")
        Collection<DbRegionResource> dbRegionResources = dbRegionResourceCrudServiceHelper.readDbChildren();
        if (dbRegionResources.isEmpty()) {
            log.info("No DbRegionResource specified");
            return;
        }
        for (DbRegionResource dbRegionResource : dbRegionResources) {
            if (dbRegionResource.getResourceItemType() == null) {
                continue;
            }
            if (checkRegion(dbRegionResource.getRegion())) {
                regionResources.add(new RegionResource(dbRegionResource, itemService, collisionService, terrainService));
            } else {
                log.info("Region resource '" + dbRegionResource.getName() + "' can not be started, region do overlap");
            }
        }
    }

    private boolean checkRegion(Rectangle region) {
        for (RegionResource regionResource : regionResources) {
            if (regionResource.getDbRegionResource().getRegion().adjoins(region)) {
                return false;
            }
        }
        return true;
    }

    private void setupResources() {
        for (RegionResource regionResource : regionResources) {
            List<SyncResourceItem> resourceItems = (List<SyncResourceItem>) itemService.getItems(regionResource.getResourceItemType(), null);
            regionResource.adjust(resourceItems);
        }
    }
}
