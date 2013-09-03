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

import com.btxtech.game.jsre.client.common.Index;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class DbKey implements Serializable{
    @Column(nullable = false)
    private int indexX;
    @Column(nullable = false)
    private int indexY;

    public DbKey(int indexX, int indexY) {
        this.indexX = indexX;
        this.indexY = indexY;
    }

    /**
     * Used by hibernate
     */
    public DbKey() {
    }

    public int getIndexX() {
        return indexX;
    }

    public int getIndexY() {
        return indexY;
    }

    public Index getIndex() {
        return new Index(indexX, indexY);
    }

    public void setIndexX(int indexX) {
        this.indexX = indexX;
    }

    public void setIndexY(int indexY) {
        this.indexY = indexY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbKey dbKey = (DbKey) o;

        return indexX == dbKey.indexX && indexY == dbKey.indexY;

    }

    @Override
    public int hashCode() {
        int result = indexX;
        result = 31 * result + indexY;
        return result;
    }

    @Override
    public String toString() {
        return getClass() + " indexX: " + indexX + " indexY: " + indexY;
    }
}
