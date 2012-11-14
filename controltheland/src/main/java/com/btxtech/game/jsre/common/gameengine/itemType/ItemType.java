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

import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;

import java.io.Serializable;

/**
 * User: beat
 * Date: 17.11.2009
 * Time: 22:50:01
 */
public abstract class ItemType implements Serializable {
    private int id;
    private String name;
    private String description;
    private TerrainType terrainType;
    private BoundingBox boundingBox;
    private ItemTypeSpriteMap itemTypeSpriteMap;
    private Integer selectionSound;
    private Integer buildupSound;
    private Integer commandSound;
    private Integer explosionClipId;

    public int getId() {
        return id;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
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

    public void setTerrainType(TerrainType terrainType) {
        this.terrainType = terrainType;
    }

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public ItemTypeSpriteMap getItemTypeSpriteMap() {
        return itemTypeSpriteMap;
    }

    public void setItemTypeSpriteMap(ItemTypeSpriteMap itemTypeSpriteMap) {
        this.itemTypeSpriteMap = itemTypeSpriteMap;
    }

    public Integer getSelectionSound() {
        return selectionSound;
    }

    public void setSelectionSound(Integer selectionSound) {
        this.selectionSound = selectionSound;
    }

    public Integer getBuildupSound() {
        return buildupSound;
    }

    public void setBuildupSound(Integer buildupSound) {
        this.buildupSound = buildupSound;
    }

    public Integer getCommandSound() {
        return commandSound;
    }

    public void setCommandSound(Integer commandSound) {
        this.commandSound = commandSound;
    }

    public Integer getExplosionClipId() {
        return explosionClipId;
    }

    public void setExplosionClipId(Integer explosionClipId) {
        this.explosionClipId = explosionClipId;
    }

    public void changeTo(ItemType itemType) {
        if (id != itemType.id) {
            throw new IllegalArgumentException("Id must be the same: " + id + ":" + itemType.id);
        }
        boundingBox = itemType.boundingBox;
        name = itemType.name;
        description = itemType.description;
        terrainType = itemType.terrainType;
        itemTypeSpriteMap = itemType.itemTypeSpriteMap;
        selectionSound = itemType.selectionSound;
        buildupSound = itemType.buildupSound;
        commandSound = itemType.commandSound;
        explosionClipId = itemType.explosionClipId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemType itemType = (ItemType) o;

        return id == itemType.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "ItemType: " + name;
    }
}
