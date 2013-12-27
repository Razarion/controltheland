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

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Entity(name = "TERRAIN_SCATTER_SURFACE_IMAGE")
public class DbScatterSurfaceImage implements CrudChild<DbSurfaceImage>, Serializable, Comparable<DbScatterSurfaceImage> {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(length = 500000)
    private byte[] imageData;
    private String contentType;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbSurfaceImage parent;
    @Enumerated(EnumType.STRING)
    private Frequency frequency;

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
        frequency = Frequency.COMMON;
    }

    @Override
    public void setParent(DbSurfaceImage parent) {
        this.parent = parent;
    }

    @Override
    public DbSurfaceImage getParent() {
        return parent;
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

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    @Override
    public int compareTo(DbScatterSurfaceImage o) {
        return frequency.compareTo(o.getFrequency());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DbScatterSurfaceImage that = (DbScatterSurfaceImage) o;
        return id != null && id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }

    public enum Frequency {
        COMMON,
        UNCOMMON,
        RARE
    }
}