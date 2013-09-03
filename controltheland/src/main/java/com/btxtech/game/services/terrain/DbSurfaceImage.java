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

import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.media.DbImageSpriteMap;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;


/**
 * User: beat
 * Date: 19.04.2010
 * Time: 22:09:11
 */
@Entity(name = "TERRAIN_SURFACE_IMAGE")
public class DbSurfaceImage implements CrudChild, Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(length = 500000)
    private byte[] imageData;
    private String contentType;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbImageSpriteMap imageSpriteMap;
    @Column(nullable = false)
    private SurfaceType surfaceType;
    private String htmlBackgroundColor;

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init(UserService userService) {
        surfaceType = SurfaceType.LAND;
        htmlBackgroundColor = "#FFFFFF";
    }

    @Override
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParent() {
        return null;
    }

    public DbImageSpriteMap getImageSpriteMap() {
        return imageSpriteMap;
    }

    public void setImageSpriteMap(DbImageSpriteMap imageSpriteMap) {
        this.imageSpriteMap = imageSpriteMap;
    }

    public Integer getId() {
        return id;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public SurfaceType getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(SurfaceType surfaceType) {
        this.surfaceType = surfaceType;
    }

    public SurfaceImage createSurfaceImage() {
        ImageSpriteMapInfo imageSpriteMapInfo = null;
        if (imageSpriteMap != null) {
            imageSpriteMapInfo = imageSpriteMap.createImageSpriteMapInfo();
        }
        return new SurfaceImage(surfaceType, id, imageSpriteMapInfo, htmlBackgroundColor);
    }

    public String getHtmlBackgroundColor() {
        return htmlBackgroundColor;
    }

    public void setHtmlBackgroundColor(String htmlBackgroundColor) {
        this.htmlBackgroundColor = htmlBackgroundColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbSurfaceImage that = (DbSurfaceImage) o;

        return id != null && id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}