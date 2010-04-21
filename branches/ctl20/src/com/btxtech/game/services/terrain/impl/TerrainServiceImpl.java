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
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.services.terrain.DbSurfaceImage;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.terrain.DbTerrainImagePosition;
import com.btxtech.game.services.terrain.DbTerrainSetting;
import com.btxtech.game.services.terrain.TerrainService;
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

/**
 * User: beat
 * Date: May 22, 2009
 * Time: 11:56:20 AM
 */
public class TerrainServiceImpl extends AbstractTerrainServiceImpl implements TerrainService {
    private HibernateTemplate hibernateTemplate;
    private HashMap<Integer, DbTerrainImage> dbTerrainImages = new HashMap<Integer, DbTerrainImage>();
    private HashMap<Integer, DbSurfaceImage> dbSurfaceImages = new HashMap<Integer, DbSurfaceImage>();
    private Log log = LogFactory.getLog(TerrainServiceImpl.class);
    private DbTerrainSetting dbTerrainSettings;
    @Autowired
    private CollisionService collisionService;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @PostConstruct
    public void init() {
        loadTerrain();
    }


    private void loadTerrain() {
        List<DbTerrainSetting> dbTerrainSettings = hibernateTemplate.loadAll(DbTerrainSetting.class);
        if (dbTerrainSettings.isEmpty()) {
            this.dbTerrainSettings = createDefaultTerrainSettings();
        } else {
            if (dbTerrainSettings.size() > 1) {
                log.error("More than one terrain setting row found: " + dbTerrainSettings.size());
            }
            this.dbTerrainSettings = dbTerrainSettings.get(0);
        }
        setTerrainSettings(this.dbTerrainSettings.createTerrainSettings());

        setTerrainImagePositions(new ArrayList<TerrainImagePosition>());
        List<DbTerrainImagePosition> dbTerrainImagePositions = hibernateTemplate.loadAll(DbTerrainImagePosition.class);
        for (DbTerrainImagePosition dbTerrainImagePosition : dbTerrainImagePositions) {
            addTerrainImagePosition(dbTerrainImagePosition.createTerrainImagePosition());
        }


        List<DbTerrainImage> imageList = hibernateTemplate.loadAll(DbTerrainImage.class);
        clearTerrainImages();
        dbTerrainImages = new HashMap<Integer, DbTerrainImage>();
        for (DbTerrainImage dbTerrainImage : imageList) {
            dbTerrainImages.put(dbTerrainImage.getId(), dbTerrainImage);
            putTerrainImage(dbTerrainImage.createTerrainImage());
        }

        List<DbSurfaceImage> surfaceList = hibernateTemplate.loadAll(DbSurfaceImage.class);
        clearSurfaceImages();
        dbSurfaceImages = new HashMap<Integer, DbSurfaceImage>();
        for (DbSurfaceImage dbSurfaceImage : surfaceList) {
            dbSurfaceImages.put(dbSurfaceImage.getId(), dbSurfaceImage);
            putSurfaceImage(dbSurfaceImage.createSurfaceImage());
        }

        fireTerrainChanged();
    }

    private DbTerrainSetting createDefaultTerrainSettings() {
        DbTerrainSetting dbTerrainSetting = new DbTerrainSetting();
        dbTerrainSetting.setTileWidth(100);
        dbTerrainSetting.setTileHeight(100);
        dbTerrainSetting.setTileXCount(50);
        dbTerrainSetting.setTileYCount(50);
        hibernateTemplate.saveOrUpdate(dbTerrainSetting);
        return dbTerrainSetting;
    }

    @Override
    public DbTerrainSetting getDbTerrainSettings() {
        return dbTerrainSettings;
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

    @Override
    public void saveAndActivateTerrainImagePositions(Collection<TerrainImagePosition> terrainImagePositions) {
        List<DbTerrainImagePosition> dbTerrainImagePositions = hibernateTemplate.loadAll(DbTerrainImagePosition.class);
        hibernateTemplate.deleteAll(dbTerrainImagePositions);
        ArrayList<DbTerrainImagePosition> dbTerrainImagePositionsNew = new ArrayList<DbTerrainImagePosition>();
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            DbTerrainImagePosition dbTerrainImagePosition = new DbTerrainImagePosition(terrainImagePosition.getTileIndex());
            DbTerrainImage dbTerrainImage = getDbTerrainImage(terrainImagePosition.getImageId());
            dbTerrainImagePosition.setTerrainImage(dbTerrainImage);
            dbTerrainImagePositionsNew.add(dbTerrainImagePosition);
        }
        hibernateTemplate.saveOrUpdateAll(dbTerrainImagePositionsNew);
        loadTerrain();
    }

    @Override
    public List<Index> setupPathToDestination(Index absolutePosition, Index absoluteDestination, int maxRadius) {
        if (absolutePosition.isInRadius(absoluteDestination, maxRadius)) {
            ArrayList<Index> singleIndex = new ArrayList<Index>();
            singleIndex.add(absolutePosition);
            return singleIndex;
        }

        List<Index> path = setupPathToDestination(absolutePosition, absoluteDestination);
        path.remove(path.size() - 1); // This will be replace
        Index secondLastPoint;
        if (path.isEmpty()) {
            // Start and destination are in the same passable rectangle
            secondLastPoint = absolutePosition;
        } else {
            secondLastPoint = path.get(path.size() - 1);
        }
        double angle = 0;
        if (!absoluteDestination.equals(secondLastPoint)) {
            angle = absoluteDestination.getAngleToNord(secondLastPoint);
        }
        for (int radius = maxRadius; radius > 0; radius -= getTerrainSettings().getTileHeight() / 10) {
            for (double testAngle = angle; testAngle < angle + 2 * Math.PI; testAngle += Math.PI / 50) {
                Index newDestination = absoluteDestination.getPointFromAngelToNord(testAngle, maxRadius);
                if (getTerrainImagePosition(newDestination.getX(), newDestination.getY()) == null) {
                    path.add(newDestination);
                    return path;
                }
            }
        }
        throw new IllegalStateException("Can not find position. Pos: " + absolutePosition + " dest: " + absoluteDestination + " maxRadius: " + maxRadius);
    }

    @Override
    public List<Index> setupPathToDestination(Index start, Index destination) {
        return collisionService.setupPathToDestination(start, destination);
    }
}
