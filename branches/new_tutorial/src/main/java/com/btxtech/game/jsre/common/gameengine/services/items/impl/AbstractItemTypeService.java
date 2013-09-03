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

package com.btxtech.game.jsre.common.gameengine.services.items.impl;

import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemTypeService;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * User: beat
 * Date: 28.11.2009
 * Time: 13:04:56
 */
abstract public class AbstractItemTypeService implements ItemTypeService {
    private final HashMap<Integer, ItemType> itemTypes = new HashMap<Integer, ItemType>();
    private int maxItemRadius;
    private int maxItemDiameter;

    @Override
    public ItemType getItemType(int itemTypeId) throws NoSuchItemTypeException {
        ItemType itemType = itemTypes.get(itemTypeId);
        if (itemType == null) {
            throw new NoSuchItemTypeException(itemTypeId);
        }
        return itemType;
    }

    @Override
    public List<ItemType> getItemTypes() {
        return new ArrayList<ItemType>(itemTypes.values());
    }

    public void setItemTypes(Collection<ItemType> itemTypes) {
        this.itemTypes.clear();
        for (ItemType itemType : itemTypes) {
            if (this.itemTypes.containsKey(itemType.getId())) {
                throw new IllegalArgumentException(this + " ID already exists in items: " + itemType.getId());
            }
            this.itemTypes.put(itemType.getId(), itemType);
        }
        calculateMaxItemDimension();
    }

    public void addDeltaItemTypes(Collection<ItemType> itemTypes) {
        for (ItemType itemType : itemTypes) {
            if (!this.itemTypes.containsKey(itemType.getId())) {
                this.itemTypes.put(itemType.getId(), itemType);
            }
        }
        calculateMaxItemDimension();
    }

    protected void removeAll(ArrayList<ItemType> newItems) {
        for (ItemType newItem : newItems) {
            this.itemTypes.put(newItem.getId(), newItem);
        }
        calculateMaxItemDimension();
    }

    protected void changeAll(ArrayList<ItemType> changingItems) {
        for (ItemType changingItem : changingItems) {
            ItemType itemType = itemTypes.get(changingItem.getId());
            itemType.changeTo(changingItem);
        }
        calculateMaxItemDimension();
    }

    protected void putAll(ArrayList<ItemType> newItems) {
        for (ItemType newItem : newItems) {
            if (this.itemTypes.containsKey(newItem.getId())) {
                throw new IllegalArgumentException(this + " ID already exists in items: " + newItem.getId());
            }
            this.itemTypes.put(newItem.getId(), newItem);
        }
        calculateMaxItemDimension();
    }


    @Override
    public boolean areItemTypesLoaded() {
        return !itemTypes.isEmpty();
    }

    @Override
    public List<BaseItemType> ableToBuild(BaseItemType toBeBuilt) {
        ArrayList<BaseItemType> result = new ArrayList<BaseItemType>();
        for (ItemType itemType : itemTypes.values()) {
            if (!(itemType instanceof BaseItemType)) {
                continue;
            }
            BaseItemType baseItemType = (BaseItemType) itemType;
            if (baseItemType.getBuilderType() != null && baseItemType.getBuilderType().getAbleToBuild().contains(toBeBuilt.getId())) {
                result.add(baseItemType);
            }
            if (baseItemType.getFactoryType() != null && baseItemType.getFactoryType().getAbleToBuild().contains(toBeBuilt.getId())) {
                result.add(baseItemType);
            }
        }
        return result;
    }

    private void calculateMaxItemDimension() {
        maxItemRadius = 0;
        for (ItemType itemType : itemTypes.values()) {
            if (itemType.getBoundingBox().getRadius() > maxItemRadius) {
                maxItemRadius = itemType.getBoundingBox().getRadius();
            }
        }
        maxItemDiameter = maxItemRadius * 2;
    }

    @Override
    public int getMaxItemRadius() {
        return maxItemRadius;
    }

    @Override
    public int getMaxItemDiameter() {
        return maxItemDiameter;
    }
}
