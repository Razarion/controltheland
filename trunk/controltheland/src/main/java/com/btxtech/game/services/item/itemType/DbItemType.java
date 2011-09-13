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

package com.btxtech.game.services.item.itemType;

import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.UserService;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: beat
 * Date: 09.12.2009
 * Time: 14:51:59
 */
@Entity(name = "ITEM_TYPE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class DbItemType implements Serializable, DbItemTypeI, CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private String proDescription;
    private String contraDescription;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "itemType", orphanRemoval = true)
    private Set<DbItemTypeImage> itemTypeImages;
    private TerrainType terrainType;
    private int imageWidth;
    private int imageHeight;
    private int boundingBoxWidth;
    private int boundingBoxHeight;
    private int imageCount;


    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Set<DbItemTypeImage> getItemTypeImages() {
        if (itemTypeImages == null) {
            itemTypeImages = new HashSet<DbItemTypeImage>();
        }
        return itemTypeImages;
    }

    @Override
    public void setItemTypeImages(Collection<DbItemTypeImage> itemTypeImages) {
        getItemTypeImages().clear();
        for (DbItemTypeImage itemTypeImage : itemTypeImages) {
            getItemTypeImages().add(itemTypeImage);
        }
    }

    @Override
    public String getProDescription() {
        return proDescription;
    }

    @Override
    public void setProDescription(String proDescription) {
        this.proDescription = proDescription;
    }

    @Override
    public String getContraDescription() {
        return contraDescription;
    }

    @Override
    public void setContraDescription(String contraDescription) {
        this.contraDescription = contraDescription;
    }

    @Override
    public TerrainType getTerrainType() {
        return terrainType;
    }

    @Override
    public void setTerrainType(TerrainType terrainType) {
        this.terrainType = terrainType;
    }

    @Override
    public int getImageWidth() {
        return imageWidth;
    }

    @Override
    public int getImageHeight() {
        return imageHeight;
    }

    @Override
    public int getBoundingBoxWidth() {
        return boundingBoxWidth;
    }

    @Override
    public int getBoundingBoxHeight() {
        return boundingBoxHeight;
    }

    @Override
    public int getImageCount() {
        return imageCount;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    public BoundingBox getBoundingBox() {
        return new BoundingBox(imageWidth, imageHeight, boundingBoxWidth, boundingBoxHeight, imageCount);
    }

    public void setBounding(BoundingBox boundingBox) {
        imageWidth = boundingBox.getImageWidth();
        imageHeight = boundingBox.getImageHeight();
        boundingBoxWidth = boundingBox.getWidth();
        boundingBoxHeight = boundingBox.getHeight();
        imageCount = boundingBox.getImageCount();
    }

    public abstract ItemType createItemType();

    protected Collection<Integer> toInt(Collection<DbBaseItemType> items) {
        ArrayList<Integer> ints = new ArrayList<Integer>();
        if (items == null) {
            return ints;
        }
        for (DbBaseItemType item : items) {
            ints.add(item.getId());
        }
        return ints;
    }

    protected void setupItemType(ItemType itemType) {
        itemType.setId(id);
        itemType.setName(getName());
        itemType.setDescription(getDescription());
        itemType.setBoundingBox(getBoundingBox());
        itemType.setTerrainType(terrainType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbItemType that = (DbItemType) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    @Override
    public void init(UserService userService) {
    }

    @Override
    public void setParent(Object o) {
    }
}
