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

package com.btxtech.game.jsre.client.common.info;

import com.btxtech.game.jsre.client.common.Level;
import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import java.io.Serializable;
import java.util.Collection;

/**
 * User: beat
 * Date: Jun 5, 2009
 * Time: 8:19:05 PM
 */
public abstract class GameInfo implements Serializable {
    private TerrainSettings terrainSettings;
    private Collection<TerrainImagePosition> terrainImagePositions;
    private Collection<SurfaceRect> surfaceRects;
    private Collection<SurfaceImage> surfaceImages;
    private Collection<TerrainImage> terrainImages;
    private boolean registered;
    @Deprecated
    private int tutorialTimeout;
    private int registerDialogDelay;
    @Deprecated
    private int userActionCollectionTime;
    private Collection<Territory> territories;
    @Deprecated
    private Level levelToRunMissionTarget;
    private Collection<ItemType> itemTypes;

    public TerrainSettings getTerrainSettings() {
        return terrainSettings;
    }

    public void setTerrainSettings(TerrainSettings terrainSettings) {
        this.terrainSettings = terrainSettings;
    }

    public Collection<TerrainImagePosition> getTerrainImagePositions() {
        return terrainImagePositions;
    }

    public void setTerrainImagePositions(Collection<TerrainImagePosition> terrainImagePositions) {
        this.terrainImagePositions = terrainImagePositions;
    }

    public Collection<SurfaceRect> getSurfaceRects() {
        return surfaceRects;
    }

    public void setSurfaceRects(Collection<SurfaceRect> surfaceRects) {
        this.surfaceRects = surfaceRects;
    }

    public Collection<SurfaceImage> getSurfaceImages() {
        return surfaceImages;
    }

    public void setSurfaceImages(Collection<SurfaceImage> surfaceImages) {
        this.surfaceImages = surfaceImages;
    }

    public Collection<TerrainImage> getTerrainImages() {
        return terrainImages;
    }

    public void setTerrainImages(Collection<TerrainImage> terrainImages) {
        this.terrainImages = terrainImages;
    }

    @Deprecated
    public int getTutorialTimeout() {
        return tutorialTimeout;
    }

    @Deprecated
    public void setTutorialTimeout(int tutorialTimeout) {
        this.tutorialTimeout = tutorialTimeout;
    }

    public int getRegisterDialogDelayInS() {
        return registerDialogDelay * 1000;
    }

    public void setRegisterDialogDelay(int registerDialogDelay) {
        this.registerDialogDelay = registerDialogDelay;
    }

    @Deprecated
    public int getUserActionCollectionTime() {
        return userActionCollectionTime;
    }

    @Deprecated
    public void setUserActionCollectionTime(int userActionCollectionTime) {
        this.userActionCollectionTime = userActionCollectionTime;
    }

    @Deprecated
    public Level getLevelToRunMissionTarget() {
        return levelToRunMissionTarget;
    }

    @Deprecated
    public void setLevelToRunMissionTarget(Level levelToRunMissionTarget) {
        this.levelToRunMissionTarget = levelToRunMissionTarget;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }


    public Collection<Territory> getTerritories() {
        return territories;
    }

    public void setTerritories(Collection<Territory> territories) {
        this.territories = territories;
    }

    public Collection<ItemType> getItemTypes() {
        return itemTypes;
    }

    public void setItemTypes(Collection<ItemType> itemTypes) {
        this.itemTypes = itemTypes;
    }

    public abstract boolean showMissionTargetDialog();

    public abstract boolean hasServerCommunication();
}
