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
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.resource.DbRegionResource;
import com.btxtech.game.services.resource.ResourceService;
import com.btxtech.game.services.terrain.TerrainService;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

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
    private HibernateTemplate hibernateTemplate;
    private ArrayList<RegionResource> regionResources = new ArrayList<RegionResource>();
    private Log log = LogFactory.getLog(ResourceServiceImpl.class);

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @PostConstruct
    public void start() {
        loadDbRegionResources();
        collisionService.addCollisionServiceChangedListener(this);
    }

    @Override
    public void collisionServiceChanged() {
        for (RegionResource regionResource : regionResources) {
            regionResource.resolveCollision();
        }
    }

    @Override
    public void resetAllResources() {
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
        List<DbRegionResource> dbRegionResources = getDbRegionResources();
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

    @Override
    public List<DbRegionResource> getDbRegionResources() {
        @SuppressWarnings("unchecked")
        List<DbRegionResource> dbRegionResources = hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbRegionResource.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                return criteria.list();
            }
        });
        return dbRegionResources;
    }

    private void setupResources() {
        for (RegionResource regionResource : regionResources) {
            List<SyncResourceItem> resourceItems = (List<SyncResourceItem>) itemService.getItems(regionResource.getResourceItemType(), null);
            regionResource.adjust(resourceItems);
        }
    }

    @Override
    public void addDbRegionResource() {
        hibernateTemplate.save(new DbRegionResource());
    }

    @Override
    public void saveDbRegionResource(List<DbRegionResource> dbRegionResource) {
        hibernateTemplate.saveOrUpdateAll(dbRegionResource);
    }

    @Override
    public void deleteDbRegionResource(DbRegionResource dbRegionResource) {
        hibernateTemplate.delete(dbRegionResource);
    }
}
