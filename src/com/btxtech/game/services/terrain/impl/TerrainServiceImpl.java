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

package com.btxtech.game.services.terrain.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.mapeditor.TerrainInfo;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.common.CrudServiceHelper;
import com.btxtech.game.services.common.CrudServiceHelperHibernateImpl;
import com.btxtech.game.services.terrain.DbSurfaceImage;
import com.btxtech.game.services.terrain.DbSurfaceRect;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.DbTerrainImagePosition;
import com.btxtech.game.services.terrain.DbTerrainSetting;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.utg.DbUserStage;
import com.btxtech.game.services.utg.UserGuidanceService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: beat
 * Date: May 22, 2009
 * Time: 11:56:20 AM
 */
public class TerrainServiceImpl extends AbstractTerrainServiceImpl implements TerrainService {
    private HibernateTemplate hibernateTemplate;
    private HashMap<Integer, DbTerrainImage> dbTerrainImages = new HashMap<Integer, DbTerrainImage>();
    private HashMap<Integer, DbSurfaceImage> dbSurfaceImages = new HashMap<Integer, DbSurfaceImage>();
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private CollisionService collisionService;
    @Autowired
    private ItemService itemService;
    private CrudServiceHelper<DbTerrainSetting> dbTerrainSettingCrudServiceHelper;
    private Log lgo = LogFactory.getLog(TerrainServiceImpl.class);

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @PostConstruct
    public void init() {
        dbTerrainSettingCrudServiceHelper = new CrudServiceHelperHibernateImpl<DbTerrainSetting>(hibernateTemplate, DbTerrainSetting.class);
        loadTerrain();
    }

    public CrudServiceHelper<DbTerrainSetting> getDbTerrainSettingCrudServiceHelper() {
        return dbTerrainSettingCrudServiceHelper;
    }

    private void loadTerrain() {
        SessionFactoryUtils.initDeferredClose(hibernateTemplate.getSessionFactory());
        try {
            // Terrain settings
            DbUserStage dbUserStage = userGuidanceService.getDbUserStage4RealGame();
            if (dbUserStage.getDbTerrainSetting() == null) {
                lgo.error("No DbTerrainSetting for real game");
                return;
            }
            setTerrainSettings(dbUserStage.getDbTerrainSetting().createTerrainSettings());

            // Terrain image position
            setTerrainImagePositions(new ArrayList<TerrainImagePosition>());
            for (DbTerrainImagePosition dbTerrainImagePosition : dbUserStage.getDbTerrainSetting().getDbTerrainImagePositionCrudServiceHelper().readDbChildren()) {
                addTerrainImagePosition(dbTerrainImagePosition.createTerrainImagePosition());
            }

            // Surface rectangles
            setSurfaceRects(new ArrayList<SurfaceRect>());
            for (DbSurfaceRect dbSurfaceRect : dbUserStage.getDbTerrainSetting().getDbSurfaceRectCrudServiceHelper().readDbChildren()) {
                addSurfaceRect(dbSurfaceRect.createSurfaceRect());
            }

            // Terrain images
            @SuppressWarnings("unchecked")
            List<DbTerrainImage> imageList = hibernateTemplate.loadAll(DbTerrainImage.class);
            clearTerrainImages();
            dbTerrainImages = new HashMap<Integer, DbTerrainImage>();
            for (DbTerrainImage dbTerrainImage : imageList) {
                dbTerrainImages.put(dbTerrainImage.getId(), dbTerrainImage);
                putTerrainImage(dbTerrainImage.createTerrainImage());
            }

            // Surface images
            @SuppressWarnings("unchecked")
            List<DbSurfaceImage> surfaceList = hibernateTemplate.loadAll(DbSurfaceImage.class);
            clearSurfaceImages();
            dbSurfaceImages = new HashMap<Integer, DbSurfaceImage>();
            for (DbSurfaceImage dbSurfaceImage : surfaceList) {
                dbSurfaceImages.put(dbSurfaceImage.getId(), dbSurfaceImage);
                putSurfaceImage(dbSurfaceImage.createSurfaceImage());
            }
            fireTerrainChanged();
        } finally {
            SessionFactoryUtils.processDeferredClose(hibernateTemplate.getSessionFactory());
        }
    }

    @Override
    public DbTerrainImage getDbTerrainImage(int id) {
        DbTerrainImage dbTerrainImage = dbTerrainImages.get(id);
        if (dbTerrainImage == null) {
            throw new IllegalArgumentException("No terrain image for id: " + id);
        }
        return dbTerrainImage;
    }

    @Override
    public DbSurfaceImage getDbSurfaceImage(int id) {
        DbSurfaceImage dbSurfaceImage = dbSurfaceImages.get(id);
        if (dbSurfaceImage == null) {
            throw new IllegalArgumentException("No terrain surface image for id: " + id);
        }
        return dbSurfaceImage;

    }

    @Override
    public List<DbTerrainImage> getDbTerrainImagesCopy() {
        return new ArrayList<DbTerrainImage>(dbTerrainImages.values());
    }

    @Override
    public int getDbTerrainImagesBitSize() {
        int size = 0;
        for (DbTerrainImage dbTerrainImage : dbTerrainImages.values()) {
            if (dbTerrainImage.getImageData() != null) {
                size += dbTerrainImage.getImageData().length;
            }
        }
        return size;
    }

    @Override
    public List<DbSurfaceImage> getDbSurfaceImagesCopy() {
        return new ArrayList<DbSurfaceImage>(dbSurfaceImages.values());
    }

    @Override
    public void saveAndActivateTerrainImages(List<DbTerrainImage> dbTerrainImages, List<DbSurfaceImage> dbSurfaceImages) {
        // DbTerrainImage
        hibernateTemplate.saveOrUpdateAll(dbTerrainImages);
        ArrayList<DbTerrainImage> doBeDeleted = new ArrayList<DbTerrainImage>(this.dbTerrainImages.values());
        doBeDeleted.removeAll(dbTerrainImages);
        if (!doBeDeleted.isEmpty()) {
            hibernateTemplate.deleteAll(doBeDeleted);
        }
        // DbSurfaceImage
        hibernateTemplate.saveOrUpdateAll(dbSurfaceImages);
        ArrayList<DbSurfaceImage> doBeDeletedSurface = new ArrayList<DbSurfaceImage>(this.dbSurfaceImages.values());
        doBeDeletedSurface.removeAll(dbSurfaceImages);
        if (!doBeDeletedSurface.isEmpty()) {
            hibernateTemplate.deleteAll(doBeDeletedSurface);
        }

        loadTerrain();
    }

    @Transactional
    @Override
    public void saveAndActivateTerrain(Collection<TerrainImagePosition> terrainImagePositions, Collection<SurfaceRect> surfaceRects, int terrainId) {
        DbTerrainSetting dbTerrainSetting = dbTerrainSettingCrudServiceHelper.readDbChild(terrainId);

        // Terrain Image Position
        dbTerrainSetting.getDbTerrainImagePositionCrudServiceHelper().deleteAllChildren();
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            DbTerrainImage dbTerrainImage = getDbTerrainImage(terrainImagePosition.getImageId());
            dbTerrainSetting.getDbTerrainImagePositionCrudServiceHelper().addChild(new DbTerrainImagePosition(terrainImagePosition.getTileIndex(), dbTerrainImage));
        }

        // Surface Rects
        dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().deleteAllChildren();
        for (SurfaceRect surfaceRect : surfaceRects) {
            DbSurfaceImage dbSurfaceImage = getDbSurfaceImage(surfaceRect.getSurfaceImageId());
            dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(new DbSurfaceRect(surfaceRect.getTileRectangle(), dbSurfaceImage));
        }

        hibernateTemplate.saveOrUpdate(dbTerrainSetting);
        if (userGuidanceService.getDbUserStage4RealGame().getDbTerrainSetting().equals(dbTerrainSetting)) {
            loadTerrain();
        }
    }


    @Override
    public List<Index> setupPathToDestination(SyncBaseItem syncBaseItem, Index absoluteDestination, int minRadius, int delta) {
        Index start = syncBaseItem.getPosition();
        if (start.isInRadius(absoluteDestination, delta + minRadius) && !itemService.hasStandingItemsInRect(syncBaseItem.getRectangle(), syncBaseItem)) {
            ArrayList<Index> singleIndex = new ArrayList<Index>();
            singleIndex.add(start);
            return singleIndex;
        }

        SurfaceType destSurfaceType = getSurfaceTypeAbsolute(absoluteDestination);
        if (!syncBaseItem.getTerrainType().allowSurfaceType(destSurfaceType)) {
            // Destination is has a different surface type
            return setupPathToDestinationDifferentTerrainType(syncBaseItem, start, absoluteDestination, minRadius, delta);
        } else {
            return setupPathToDestinationSameTerrainType(syncBaseItem, start, absoluteDestination, minRadius, delta);
        }
    }

    private List<Index> setupPathToDestinationDifferentTerrainType(SyncBaseItem syncBaseItem, Index start, Index absoluteDestination, int minRadius, int delta) {
        Index reachableDestination = getNearestFreePoint(syncBaseItem, syncBaseItem.getTerrainType(), absoluteDestination, minRadius + delta);
        return setupPathToDestination(start, reachableDestination, syncBaseItem.getTerrainType());
    }

    private Index getNearestFreePoint(SyncBaseItem syncBaseItem, TerrainType allowedTerrainType, Index absoluteDestination, int maxRadius) {
        SurfaceType destSurfaceType = getSurfaceTypeAbsolute(absoluteDestination);
        if (allowedTerrainType.allowSurfaceType(destSurfaceType)) {
            throw new IllegalArgumentException("getNearestFreePoint: same surface type. absoluteDestination: " + absoluteDestination + " ");
        }
        int CIRCLE_PARTS = 36;
        int RADIUS_STEPS = 10;
        for (int radius = maxRadius; radius > 0; radius -= maxRadius / RADIUS_STEPS) {
            for (double angel = 0; angel < (2 * Math.PI); angel += 2 * Math.PI / CIRCLE_PARTS) {
                Index newDestination = absoluteDestination.getPointFromAngelToNord(angel, radius);
                Rectangle newRectangle = syncBaseItem.getItemType().getRectangle(newDestination);
                if (!isFree(new Index(newDestination.getX(), newDestination.getY()), newRectangle.getWidth(), newRectangle.getHeight(), allowedTerrainType.getSurfaceTypes())) {
                    continue;
                }
                if (itemService.hasStandingItemsInRect(newRectangle, syncBaseItem)) {
                    continue;
                }
                return newDestination;
            }
        }
        throw new IllegalArgumentException(this + "getNearestFreePoint: can not find position on surface. allowedTerrainType: " + allowedTerrainType + " absoluteDestination: " + absoluteDestination);
    }


    private List<Index> setupPathToDestinationSameTerrainType(SyncBaseItem syncBaseItem, Index start, Index absoluteDestination, int minRadius, int delta) {
        List<Index> path = setupPathToDestination(start, absoluteDestination, syncBaseItem.getTerrainType());
        path.remove(path.size() - 1); // This will be replace
        Index secondLastPoint;
        if (path.isEmpty()) {
            // Start and destination are in the same passable rectangle
            secondLastPoint = start;
        } else {
            secondLastPoint = path.get(path.size() - 1);
        }
        double startAngle = 0;
        if (!absoluteDestination.equals(secondLastPoint)) {
            startAngle = absoluteDestination.getAngleToNord(secondLastPoint);
        }
        for (double testAngle = 0; testAngle < startAngle + Math.PI; testAngle += Math.PI / 50) {
            for (int radius = delta + minRadius; radius >= minRadius; radius -= getTerrainSettings().getTileHeight() / 10) {
                Index newDestination = correctDestination(absoluteDestination, startAngle + testAngle, radius, syncBaseItem);
                if (newDestination != null) {
                    path.add(newDestination);
                    return path;
                }
                newDestination = correctDestination(absoluteDestination, startAngle - testAngle, radius, syncBaseItem);
                if (newDestination != null) {
                    path.add(newDestination);
                    return path;
                }
            }
        }
        throw new IllegalStateException("Can not find position. Pos: " + start + " dest: " + absoluteDestination + " maxRadius: " + delta + " syncBaseItem:" + syncBaseItem);
    }

    private Index correctDestination(Index absoluteDestination, double angle, int radius, SyncBaseItem syncBaseItem) {
        Index newDestination = absoluteDestination.getPointFromAngelToNord(angle, radius);
        Rectangle newRectangle = syncBaseItem.getItemType().getRectangle(newDestination);

        if (!isFree(new Index(newRectangle.getX(), newRectangle.getY()), newRectangle.getWidth(), newRectangle.getHeight(), syncBaseItem.getTerrainType().getSurfaceTypes())) {
            return null;

        }
        if (itemService.hasStandingItemsInRect(newRectangle, syncBaseItem)) {
            return null;
        }
        return newDestination;
    }

    @Override
    public List<Index> setupPathToDestination(Index start, Index destination, TerrainType terrainType) {
        return collisionService.setupPathToDestination(start, destination, terrainType);
    }

    @Override
    public List<Index> setupPathToSyncMovableRandomPositionIfTaken(SyncItem syncItem) {
        Index position = collisionService.getFreeSyncMovableRandomPositionIfTaken(syncItem, 500);
        if (position == null) {
            return null;
        } else {
            return setupPathToDestination(syncItem.getPosition(), position, syncItem.getTerrainType());
        }
    }

    @Override
    public void setupTerrain(SimulationInfo simulationInfo, DbUserStage dbUserStage) {
        SessionFactoryUtils.initDeferredClose(hibernateTemplate.getSessionFactory());
        try {
            DbTerrainSetting reattached = (DbTerrainSetting) hibernateTemplate.get(DbTerrainSetting.class, dbUserStage.getDbTerrainSetting().getId());
            simulationInfo.setTerrainSettings(reattached.createTerrainSettings());// TODO cache
            simulationInfo.setTerrainImagePositions(getTerrainImagePositions(reattached)); // TODO cache
            simulationInfo.setTerrainImages(getTerrainImages());
            simulationInfo.setSurfaceRects(getSurfaceRects(reattached));// TODO cache
            simulationInfo.setSurfaceImages(getSurfaceImages());
        } finally {
            SessionFactoryUtils.processDeferredClose(hibernateTemplate.getSessionFactory());
        }
    }

    @Override
    public void setupTerrain(TerrainInfo terrainInfo, int terrainId) {
        SessionFactoryUtils.initDeferredClose(hibernateTemplate.getSessionFactory());
        try {
            DbTerrainSetting dbTerrainSetting = dbTerrainSettingCrudServiceHelper.readDbChild(terrainId);
            terrainInfo.setTerrainSettings(dbTerrainSetting.createTerrainSettings());
            terrainInfo.setTerrainImagePositions(getTerrainImagePositions(dbTerrainSetting));
            terrainInfo.setTerrainImages(getTerrainImages());
            terrainInfo.setSurfaceRects(getSurfaceRects(dbTerrainSetting));
            terrainInfo.setSurfaceImages(getSurfaceImages());
        } finally {
            SessionFactoryUtils.processDeferredClose(hibernateTemplate.getSessionFactory());
        }
    }

    private Collection<TerrainImagePosition> getTerrainImagePositions(DbTerrainSetting dbTerrainSetting) {
        ArrayList<TerrainImagePosition> result = new ArrayList<TerrainImagePosition>();
        for (DbTerrainImagePosition dbTerrainImagePosition : dbTerrainSetting.getDbTerrainImagePositionCrudServiceHelper().readDbChildren()) {
            result.add(dbTerrainImagePosition.createTerrainImagePosition());
        }
        return result;
    }

    private Collection<SurfaceRect> getSurfaceRects(DbTerrainSetting dbTerrainSetting) {
        ArrayList<SurfaceRect> result = new ArrayList<SurfaceRect>();
        for (DbSurfaceRect dbSurfaceRect : dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().readDbChildren()) {
            result.add(dbSurfaceRect.createSurfaceRect());
        }
        return result;
    }

}
