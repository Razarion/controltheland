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

package com.btxtech.game.jsre.client.common;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
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
public class GameInfo implements Serializable{
    private SimpleBase base;
    private double accountBalance;
    private int xp;
    private Collection<Integer> allowedItemTypes;
    private int energyGenerating;
    private int energyConsuming;
    private TerrainSettings terrainSettings;
    private Collection<TerrainImagePosition> terrainImagePositions;
    private Collection<SurfaceImage> surfaceImages;
    private Collection<TerrainImage> terrainImages;
    private boolean registered;
    private OnlineBaseUpdate onlineBaseUpdate;
    private int tutorialTimeout;
    private int registerDialogDelay;
    private int userActionCollectionTime;

    public SimpleBase getBase() {
        return base;
    }

    public void setBase(SimpleBase base) {
        this.base = base;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Collection<Integer> getAllowedItemTypes() {
        return allowedItemTypes;
    }

    public void setAllowedItemTypes(Collection<Integer> allowedItemTypes) {
        this.allowedItemTypes = allowedItemTypes;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getEnergyGenerating() {
        return energyGenerating;
    }

    public void setEnergyGenerating(int energyGenerating) {
        this.energyGenerating = energyGenerating;
    }

    public int getEnergyConsuming() {
        return energyConsuming;
    }

    public void setEnergyConsuming(int energyConsuming) {
        this.energyConsuming = energyConsuming;
    }

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

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public OnlineBaseUpdate getOnlineBaseUpdate() {
        return onlineBaseUpdate;
    }

    public void setOnlineBaseUpdate(OnlineBaseUpdate onlineBaseUpdate) {
        this.onlineBaseUpdate = onlineBaseUpdate;
    }

    public int getTutorialTimeout() {
        return tutorialTimeout;
    }

    public void setTutorialTimeout(int tutorialTimeout) {
        this.tutorialTimeout = tutorialTimeout;
    }

    public int getRegisterDialogDelay() {
        return registerDialogDelay;
    }

    public void setRegisterDialogDelay(int registerDialogDelay) {
        this.registerDialogDelay = registerDialogDelay;
    }

    public int getUserActionCollectionTime() {
        return userActionCollectionTime;
    }

    public void setUserActionCollectionTime(int userActionCollectionTime) {
        this.userActionCollectionTime = userActionCollectionTime;
    }
}
