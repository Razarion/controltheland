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

import com.btxtech.game.jsre.common.TerrainInfo;
import com.btxtech.game.jsre.common.gameengine.services.terrain.CommonTerrainImageServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImageBackground;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.Utils;
import com.btxtech.game.services.terrain.DbSurfaceImage;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.DbTerrainImageGroup;
import com.btxtech.game.services.terrain.TerrainImageService;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;

/**
 * User: beat
 * Date: May 22, 2009
 * Time: 11:56:20 AM
 */
@Component
public class TerrainImageServiceImpl extends CommonTerrainImageServiceImpl implements TerrainImageService {
    @Autowired
    private CrudRootServiceHelper<DbTerrainImageGroup> dbTerrainImageGroupCrudRootServiceHelper;
    @Autowired
    private CrudRootServiceHelper<DbSurfaceImage> dbSurfaceImageCrudServiceHelper;
    @Autowired
    private SessionFactory sessionFactory;

    private HashMap<Integer, DbTerrainImage> dbTerrainImages = new HashMap<>();
    private HashMap<Integer, DbSurfaceImage> dbSurfaceImages = new HashMap<>();


    @PostConstruct
    public void init() {
        if (Utils.NO_GAME_ENGINE) {
            return;
        }
        dbTerrainImageGroupCrudRootServiceHelper.init(DbTerrainImageGroup.class);
        dbSurfaceImageCrudServiceHelper.init(DbSurfaceImage.class);
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            activate();
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
    }

    @Override
    public CrudRootServiceHelper<DbTerrainImageGroup> getDbTerrainImageGroupCrudServiceHelper() {
        return dbTerrainImageGroupCrudRootServiceHelper;
    }

    @Override
    public CrudRootServiceHelper<DbSurfaceImage> getDbSurfaceImageCrudServiceHelper() {
        return dbSurfaceImageCrudServiceHelper;
    }

    public void activate() {
        // Terrain images
        TerrainImageBackground terrainImageBackground = new TerrainImageBackground();
        setTerrainImageBackground(terrainImageBackground);
        Collection<DbTerrainImageGroup> imageGroupList = dbTerrainImageGroupCrudRootServiceHelper.readDbChildren();
        clearTerrainImages();
        dbTerrainImages = new HashMap<>();
        for (DbTerrainImageGroup dbTerrainImageGroup : imageGroupList) {
            Collection<DbTerrainImage> imageList = dbTerrainImageGroup.getTerrainImageCrud().readDbChildren();
            for (DbTerrainImage dbTerrainImage : imageList) {
                dbTerrainImages.put(dbTerrainImage.getId(), dbTerrainImage);
                putTerrainImage(dbTerrainImage.createTerrainImage());
                terrainImageBackground.put(dbTerrainImage.getId(),
                        dbTerrainImageGroup.getId(),
                        dbTerrainImageGroup.getHtmlBackgroundColorNone(),
                        dbTerrainImageGroup.getHtmlBackgroundColorWater(),
                        dbTerrainImageGroup.getHtmlBackgroundColorLand(),
                        dbTerrainImageGroup.getHtmlBackgroundColorWaterCoast(),
                        dbTerrainImageGroup.getHtmlBackgroundColorLandCoast()
                );
            }
        }

        // Surface images
        Collection<DbSurfaceImage> surfaceList = dbSurfaceImageCrudServiceHelper.readDbChildren();
        clearSurfaceImages();
        dbSurfaceImages = new HashMap<>();
        for (DbSurfaceImage dbSurfaceImage : surfaceList) {
            dbSurfaceImages.put(dbSurfaceImage.getId(), dbSurfaceImage);
            putSurfaceImage(dbSurfaceImage.createSurfaceImage());
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
    public int getDbTerrainImagesSizeInBytes() {
        int size = 0;
        for (DbTerrainImage dbTerrainImage : dbTerrainImages.values()) {
            if (dbTerrainImage.getImageData() != null) {
                size += dbTerrainImage.getImageData().length;
            }
        }
        return size;
    }

    @Override
    public void setupTerrainImages(TerrainInfo terrainInfo) {
        terrainInfo.setTerrainImageBackground(getTerrainImageBackground());
        terrainInfo.setTerrainImages(getTerrainImages());
        terrainInfo.setSurfaceImages(getSurfaceImages());
    }
}
