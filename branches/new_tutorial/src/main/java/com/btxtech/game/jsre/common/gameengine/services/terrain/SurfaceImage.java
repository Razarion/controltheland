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

package com.btxtech.game.jsre.common.gameengine.services.terrain;

import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;

import java.io.Serializable;

/**
 * User: beat
 * Date: 14.04.2010
 * Time: 11:29:22
 */
public class SurfaceImage implements Serializable {
    private SurfaceType surfaceType;
    private int id;
    private String htmlBackgroundColor;
    private ImageSpriteMapInfo imageSpriteMapInfo;

    /**
     * Used by GWT
     */
    public SurfaceImage() {
    }

    public SurfaceImage(SurfaceType surfaceType, int id, ImageSpriteMapInfo imageSpriteMapInfo, String htmlBackgroundColor) {
        this.surfaceType = surfaceType;
        this.id = id;
        this.imageSpriteMapInfo = imageSpriteMapInfo;
        if (htmlBackgroundColor != null) {
            this.htmlBackgroundColor = htmlBackgroundColor;
        } else {
            this.htmlBackgroundColor = "#FFFFFF";
        }
    }

    public SurfaceType getSurfaceType() {
        return surfaceType;
    }

    public int getImageId() {
        return id;
    }

    public String getHtmlBackgroundColor() {
        return htmlBackgroundColor;
    }

    public ImageSpriteMapInfo getImageSpriteMapInfo() {
        return imageSpriteMapInfo;
    }
}
