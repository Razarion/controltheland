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

package com.btxtech.game.services.terrain;

import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * User: beat
 * Date: 08.01.2010
 * Time: 21:01:14
 */
@Entity(name = "TERRAIN_SETTINGS")
public class DbTerrainSetting implements CrudParent, CrudChild, Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    private int tileXCount;
    private int tileYCount;
    private int tileHeight;
    private int tileWidth;
    @OneToMany(mappedBy = "dbTerrainSetting", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Set<DbTerrainImagePosition> dbTerrainImagePositions;
    @OneToMany(mappedBy = "dbTerrainSetting", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private Set<DbSurfaceRect> dbSurfaceRects;
    private String name;
    private boolean isRealGame;
    @Transient
    private CrudChildServiceHelper<DbTerrainImagePosition> dbTerrainImagePositionCrudServiceHelper;
    @Transient
    private CrudChildServiceHelper<DbSurfaceRect> dbSurfaceRectCrudServiceHelper;

    public Integer getId() {
        return id;
    }

    public int getTileXCount() {
        return tileXCount;
    }

    public void setTileXCount(int tileXCount) {
        this.tileXCount = tileXCount;
    }

    public int getTileYCount() {
        return tileYCount;
    }

    public void setTileYCount(int tileYCount) {
        this.tileYCount = tileYCount;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public void setTileHeight(int tileHeight) {
        this.tileHeight = tileHeight;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public void setTileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
    }

    public int getPlayFieldXSize() {
        return tileXCount * tileWidth;
    }

    public int getPlayFieldYSize() {
        return tileYCount * tileHeight;
    }

    public TerrainSettings createTerrainSettings() {
        return new TerrainSettings(tileXCount, tileYCount, tileHeight, tileWidth);
    }

    public boolean isRealGame() {
        return isRealGame;
    }

    public void setRealGame(boolean realGame) {
        isRealGame = realGame;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbTerrainSetting that = (DbTerrainSetting) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public CrudChildServiceHelper<DbTerrainImagePosition> getDbTerrainImagePositionCrudServiceHelper() {
        if (dbTerrainImagePositionCrudServiceHelper == null) {
            dbTerrainImagePositionCrudServiceHelper = new CrudChildServiceHelper<DbTerrainImagePosition>(dbTerrainImagePositions, DbTerrainImagePosition.class, this);
        }
        return dbTerrainImagePositionCrudServiceHelper;
    }

    public CrudChildServiceHelper<DbSurfaceRect> getDbSurfaceRectCrudServiceHelper() {
        if (dbSurfaceRectCrudServiceHelper == null) {
            dbSurfaceRectCrudServiceHelper = new CrudChildServiceHelper<DbSurfaceRect>(dbSurfaceRects, DbSurfaceRect.class, this);
        }
        return dbSurfaceRectCrudServiceHelper;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void init(UserService userService) {
        tileXCount = 50;
        tileYCount = 50;
        tileHeight = 100;
        tileWidth = 100;
        name = "Unnamed";
        dbTerrainImagePositions = new HashSet<DbTerrainImagePosition>();
        dbSurfaceRects = new HashSet<DbSurfaceRect>();
    }

    @Override
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }
}