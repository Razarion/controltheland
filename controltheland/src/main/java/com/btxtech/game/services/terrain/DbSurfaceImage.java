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

import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.services.common.CrudChild;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


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
    @Column(nullable = false)
    private SurfaceType surfaceType;

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init() {
        surfaceType = SurfaceType.LAND;
    }

    @Override
    public void setParent(Object o) {
        // Ignore
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
        return new SurfaceImage(surfaceType, id);
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
        int hash = id != null ? id.hashCode() : System.identityHashCode(this);
        System.out.println("***DbSurfaceImage Hash: " + hash + "--" + System.identityHashCode(this));
        return hash;

    }
}