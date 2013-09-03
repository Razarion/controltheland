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

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.jsre.common.TerrainInfo;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImageBackground;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.packets.UserAttentionPacket;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * User: beat
 * Date: Jun 5, 2009
 * Time: 8:19:05 PM
 */
public abstract class GameInfo implements TerrainInfo, Serializable {
    private TerrainSettings terrainSettings;
    private Collection<TerrainImagePosition> terrainImagePositions;
    private Collection<SurfaceRect> surfaceRects;
    private Collection<SurfaceImage> surfaceImages;
    private Collection<TerrainImage> terrainImages;
    private TerrainImageBackground terrainImageBackground;
    private SimpleUser simpleUser;
    private int registerDialogDelay;
    private Collection<ItemType> itemTypes;
    private Map<CmsUtil.CmsPredefinedPage, String> predefinedUrls;
    private CommonSoundInfo commonSoundInfo;
    private Collection<ImageSpriteMapInfo> imageSpriteMapLibrary;
    private Collection<ClipInfo> clipLibrary;
    private PreloadedImageSpriteMapInfo preloadedImageSpriteMapInfo;
    private UserAttentionPacket userAttentionPacket;

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

    public void freePositionAndRects() {
        surfaceRects.clear();
        terrainImagePositions.clear();
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

    public TerrainImageBackground getTerrainImageBackground() {
        return terrainImageBackground;
    }

    public void setTerrainImageBackground(TerrainImageBackground terrainImageBackground) {
        this.terrainImageBackground = terrainImageBackground;
    }

    public SimpleUser getSimpleUser() {
        return simpleUser;
    }

    public void setSimpleUser(SimpleUser simpleUser) {
        this.simpleUser = simpleUser;
    }

    public int getRegisterDialogDelay() {
        return registerDialogDelay;
    }

    public void setRegisterDialogDelay(int registerDialogDelay) {
        this.registerDialogDelay = registerDialogDelay;
    }

    public Collection<ItemType> getItemTypes() {
        return itemTypes;
    }

    public void setItemTypes(Collection<ItemType> itemTypes) {
        this.itemTypes = itemTypes;
    }

    public Map<CmsUtil.CmsPredefinedPage, String> getPredefinedUrls() {
        return predefinedUrls;
    }

    public void setPredefinedUrls(Map<CmsUtil.CmsPredefinedPage, String> predefinedUrls) {
        this.predefinedUrls = predefinedUrls;
    }

    public CommonSoundInfo getCommonSoundInfo() {
        return commonSoundInfo;
    }

    public void setCommonSoundInfo(CommonSoundInfo commonSoundInfo) {
        this.commonSoundInfo = commonSoundInfo;
    }

    public Collection<ClipInfo> getClipLibrary() {
        return clipLibrary;
    }

    public void setClipLibrary(Collection<ClipInfo> clipLibrary) {
        this.clipLibrary = clipLibrary;
    }

    public PreloadedImageSpriteMapInfo getPreloadedImageSpriteMapInfo() {
        return preloadedImageSpriteMapInfo;
    }

    public void setPreloadedImageSpriteMapInfo(PreloadedImageSpriteMapInfo preloadedImageSpriteMapInfo) {
        this.preloadedImageSpriteMapInfo = preloadedImageSpriteMapInfo;
    }

    public Collection<ImageSpriteMapInfo> getImageSpriteMapLibrary() {
        return imageSpriteMapLibrary;
    }

    public void setImageSpriteMapLibrary(Collection<ImageSpriteMapInfo> imageSpriteMapLibrary) {
        this.imageSpriteMapLibrary = imageSpriteMapLibrary;
    }

    public UserAttentionPacket getUserAttentionPacket() {
        return userAttentionPacket;
    }

    public void setUserAttentionPacket(UserAttentionPacket userAttentionPacket) {
        this.userAttentionPacket = userAttentionPacket;
    }

    public abstract boolean isSellAllowed();
}
