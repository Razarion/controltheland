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

import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImageBackground;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.mapeditor.TerrainInfo;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.terrain.DbSurfaceImage;
import com.btxtech.game.services.terrain.DbSurfaceRect;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.DbTerrainImageGroup;
import com.btxtech.game.services.terrain.DbTerrainImagePosition;
import com.btxtech.game.services.terrain.DbTerrainSetting;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: May 22, 2009
 * Time: 11:56:20 AM
 */
@Component
public class TerrainServiceImpl extends AbstractTerrainServiceImpl implements TerrainService {
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private CollisionService collisionService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private CrudRootServiceHelper<DbTerrainSetting> dbTerrainSettingCrudServiceHelper;
    @Autowired
    private CrudRootServiceHelper<DbTerrainImageGroup> dbTerrainImageGroupCrudRootServiceHelper;
    @Autowired
    private CrudRootServiceHelper<DbSurfaceImage> dbSurfaceImageCrudServiceHelper;
    @Autowired
    private MgmtService mgmtService;
    @Autowired
    private SessionFactory sessionFactory;

    private HashMap<Integer, DbTerrainImage> dbTerrainImages = new HashMap<Integer, DbTerrainImage>();
    private HashMap<Integer, DbSurfaceImage> dbSurfaceImages = new HashMap<Integer, DbSurfaceImage>();
    private Log log = LogFactory.getLog(TerrainServiceImpl.class);
    private TerrainImageBackground terrainImageBackground;

    @PostConstruct
    public void init() {
        if (mgmtService.isNoGameEngine()) {
            return;
        }
        dbTerrainImageGroupCrudRootServiceHelper.init(DbTerrainImageGroup.class);
        dbSurfaceImageCrudServiceHelper.init(DbSurfaceImage.class);
        dbTerrainSettingCrudServiceHelper.init(DbTerrainSetting.class);
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            activateTerrain();
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
    }

    public CrudRootServiceHelper<DbTerrainSetting> getDbTerrainSettingCrudServiceHelper() {
        return dbTerrainSettingCrudServiceHelper;
    }

    @Override
    public CrudRootServiceHelper<DbTerrainImageGroup> getDbTerrainImageGroupCrudServiceHelper() {
        return dbTerrainImageGroupCrudRootServiceHelper;
    }

    @Override
    public CrudRootServiceHelper<DbSurfaceImage> getDbSurfaceImageCrudServiceHelper() {
        return dbSurfaceImageCrudServiceHelper;
    }

    @Override
    public void activateTerrain() {
        // Terrain settings
        DbTerrainSetting dbTerrainSetting = getDbTerrainSetting4RealGame();
        if (dbTerrainSetting == null) {
            log.error("No terrain settings for real game.");
            return;
        }
        setTerrainSettings(dbTerrainSetting.createTerrainSettings());

        // Terrain image position
        setTerrainImagePositions(new ArrayList<TerrainImagePosition>());
        for (DbTerrainImagePosition dbTerrainImagePosition : dbTerrainSetting.getDbTerrainImagePositionCrudServiceHelper().readDbChildren()) {
            addTerrainImagePosition(dbTerrainImagePosition.createTerrainImagePosition());
        }

        // Surface rectangles
        setSurfaceRects(new ArrayList<SurfaceRect>());
        for (DbSurfaceRect dbSurfaceRect : dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().readDbChildren()) {
            addSurfaceRect(dbSurfaceRect.createSurfaceRect());
        }

        // Terrain images
        terrainImageBackground = new TerrainImageBackground();
        Collection<DbTerrainImageGroup> imageGroupList = dbTerrainImageGroupCrudRootServiceHelper.readDbChildren();
        clearTerrainImages();
        dbTerrainImages = new HashMap<Integer, DbTerrainImage>();
        for (DbTerrainImageGroup dbTerrainImageGroup : imageGroupList) {
            Collection<DbTerrainImage> imageList = dbTerrainImageGroup.getTerrainImageCrud().readDbChildren();
            for (DbTerrainImage dbTerrainImage : imageList) {
                dbTerrainImages.put(dbTerrainImage.getId(), dbTerrainImage);
                putTerrainImage(dbTerrainImage.createTerrainImage());
                terrainImageBackground.put(dbTerrainImage.getId(), dbTerrainImageGroup.getHtmlBackgroundColor());
            }
        }

        // Surface images
        Collection<DbSurfaceImage> surfaceList = dbSurfaceImageCrudServiceHelper.readDbChildren();
        clearSurfaceImages();
        dbSurfaceImages = new HashMap<Integer, DbSurfaceImage>();
        for (DbSurfaceImage dbSurfaceImage : surfaceList) {
            dbSurfaceImages.put(dbSurfaceImage.getId(), dbSurfaceImage);
            putSurfaceImage(dbSurfaceImage.createSurfaceImage());
        }
        fireTerrainChanged();
    }

    @Override
    public DbTerrainSetting getDbTerrainSetting4RealGame() {
        Collection<DbTerrainSetting> dbTerrainSettings = dbTerrainSettingCrudServiceHelper.readDbChildren();
        DbTerrainSetting realGame = null;
        for (DbTerrainSetting dbTerrainSetting : dbTerrainSettings) {
            if (dbTerrainSetting.isRealGame()) {
                if (realGame != null) {
                    log.warn("More than one terrain setting for real game detected.");
                } else {
                    realGame = dbTerrainSetting;
                }
            }
        }
        if (realGame == null) {
            log.warn("No terrain setting for real game detected.");
        }
        return realGame;
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
    public int getDbTerrainImagesBitSize() {
        int size = 0;
        for (DbTerrainImage dbTerrainImage : dbTerrainImages.values()) {
            if (dbTerrainImage.getImageData() != null) {
                size += dbTerrainImage.getImageData().length;
            }
        }
        return size;
    }

    @Transactional
    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveTerrain(Collection<TerrainImagePosition> terrainImagePositions, Collection<SurfaceRect> surfaceRects, int terrainId) {
        DbTerrainSetting dbTerrainSetting = dbTerrainSettingCrudServiceHelper.readDbChild(terrainId);

        // Terrain Image Position
        Map<ImagePositionKey, TerrainImagePosition> newImagePosition = new HashMap<ImagePositionKey, TerrainImagePosition>(terrainImagePositions.size());
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            newImagePosition.put(new ImagePositionKey(terrainImagePosition), terrainImagePosition);
        }
        Collection<DbTerrainImagePosition> dbTerrainImagePositions = dbTerrainSetting.getDbTerrainImagePositionCrudServiceHelper().readDbChildren();
        // Remove Same
        for (Iterator<DbTerrainImagePosition> iterator = dbTerrainImagePositions.iterator(); iterator.hasNext();) {
            DbTerrainImagePosition dbTerrainImagePosition = iterator.next();
            ImagePositionKey key = new ImagePositionKey(dbTerrainImagePosition);
            if (newImagePosition.containsKey(key)) {
                newImagePosition.remove(key);
            } else {
                iterator.remove();
            }
        }
        // Add new
        for (TerrainImagePosition terrainImagePosition : newImagePosition.values()) {
            DbTerrainImage dbTerrainImage = getDbTerrainImage(terrainImagePosition.getImageId());
            dbTerrainSetting.getDbTerrainImagePositionCrudServiceHelper().addChild(new DbTerrainImagePosition(terrainImagePosition.getTileIndex(), dbTerrainImage), null);
        }

        // Surface Rects
        Map<SurfaceRectKey, SurfaceRect> newSurfaceRect = new HashMap<SurfaceRectKey, SurfaceRect>(surfaceRects.size());
        for (SurfaceRect surfaceRect : surfaceRects) {
            newSurfaceRect.put(new SurfaceRectKey(surfaceRect), surfaceRect);
        }
        Collection<DbSurfaceRect> dbSurfaceRects = dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().readDbChildren();
        // Remove Same
        for (Iterator<DbSurfaceRect> iterator = dbSurfaceRects.iterator(); iterator.hasNext();) {
            DbSurfaceRect dbSurfaceRect = iterator.next();
            SurfaceRectKey key = new SurfaceRectKey(dbSurfaceRect);
            if (newSurfaceRect.containsKey(key)) {
                newSurfaceRect.remove(key);
            } else {
                iterator.remove();
            }
        }
        // Add new
        for (SurfaceRect surfaceRect : newSurfaceRect.values()) {
            DbSurfaceImage dbSurfaceImage = getDbSurfaceImage(surfaceRect.getSurfaceImageId());
            dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(new DbSurfaceRect(surfaceRect.getTileRectangle(), dbSurfaceImage), null);
        }

        sessionFactory.getCurrentSession().saveOrUpdate(dbTerrainSetting);
    }

    @Override
    public void setupTerrainRealGame(GameInfo gameInfo) {
        gameInfo.setTerrainImageBackground(terrainImageBackground);
        gameInfo.setTerrainSettings(getTerrainSettings());
        gameInfo.setTerrainImagePositions(getTerrainImagePositions());
        gameInfo.setTerrainImages(getTerrainImages());
        gameInfo.setSurfaceRects(getSurfaceRects());
        gameInfo.setSurfaceImages(getSurfaceImages());
    }

    @Override
    public void setupTerrainTutorial(GameInfo gameInfo, DbTutorialConfig dbTutorialConfig) {
        gameInfo.setTerrainImageBackground(terrainImageBackground);
        DbTerrainSetting terrainSetting = reattachDbTerrainSetting4Tutorial(dbTutorialConfig);
        gameInfo.setTerrainSettings(terrainSetting.createTerrainSettings());
        gameInfo.setTerrainImagePositions(getTerrainImagePositions(terrainSetting));
        gameInfo.setTerrainImages(getTerrainImages());
        gameInfo.setSurfaceRects(getSurfaceRects(terrainSetting));
        gameInfo.setSurfaceImages(getSurfaceImages());
    }

    private DbTerrainSetting reattachDbTerrainSetting4Tutorial(DbTutorialConfig dbTutorialConfig) {
        sessionFactory.getCurrentSession().load(dbTutorialConfig, dbTutorialConfig.getId());

        DbTerrainSetting dbTerrainSetting = dbTutorialConfig.getDbTerrainSetting();
        if (dbTerrainSetting == null) {
            throw new IllegalStateException("No terrain for tutorial: " + dbTutorialConfig);
        }
        return dbTerrainSetting;
    }

    @Override
    public void setupTerrain(TerrainInfo terrainInfo, int terrainId) {
        DbTerrainSetting dbTerrainSetting = dbTerrainSettingCrudServiceHelper.readDbChild(terrainId);
        terrainInfo.setTerrainSettings(dbTerrainSetting.createTerrainSettings());
        terrainInfo.setTerrainImagePositions(getTerrainImagePositions(dbTerrainSetting));
        terrainInfo.setTerrainImages(getTerrainImages());
        terrainInfo.setSurfaceRects(getSurfaceRects(dbTerrainSetting));
        terrainInfo.setSurfaceImages(getSurfaceImages());
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

    @Override
    @Transactional
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveDbTerrainSetting(List<DbTerrainSetting> dbTerrainSettings) {
        dbTerrainSettingCrudServiceHelper.updateDbChildren(dbTerrainSettings);
    }
}
