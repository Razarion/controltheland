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

package com.btxtech.game.jsre.common.gameengine.itemType;

import java.io.Serializable;

/**
 * User: beat
 * Date: 17.11.2009
 * Time: 22:50:01
 */
public abstract class ItemType implements Serializable {
    private int id;
    private int height;
    private int width;
    private String name;
    private String description;
    private String proDescription;
    private String contraDescription;

    public int getId() {
        return id;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProDescription() {
        return proDescription;
    }

    public void setProDescription(String proDescription) {
        this.proDescription = proDescription;
    }

    public String getContraDescription() {
        return contraDescription;
    }

    public void setContraDescription(String contraDescription) {
        this.contraDescription = contraDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemType itemType = (ItemType) o;

        if (id != itemType.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public void changeTo(ItemType itemType) {
        if (id != itemType.id) {
            throw new IllegalArgumentException("Id must be the same: " + id + ":" + itemType.id);
        }
        height = itemType.height;
        width = itemType.width;
        name = itemType.name;
        description = itemType.description;
    }

    @Override
    public String toString() {
        return "ItemType: " + name;
    }
}
