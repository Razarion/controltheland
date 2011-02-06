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

package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import java.io.Serializable;
import java.util.Collection;

/**
 * User: beat
 * Date: 26.05.2010
 * Time: 11:35:30
 */
public class Territory implements Serializable {
    private String name;
    private Collection<Rectangle> territoryTileRegions;
    private Collection<Integer> allowedItemTypes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Rectangle> getTerritoryTileRegions() {
        return territoryTileRegions;
    }

    public void setTerritoryTileRegions(Collection<Rectangle> territoryTileRegions) {
        this.territoryTileRegions = territoryTileRegions;
    }

    public boolean contains(Index tile) {
        for (Rectangle rectangle : territoryTileRegions) {
            if(rectangle.containsExclusive(tile)) {
                return true;
            }
        }
        return false;
    }

    public void setAllowedItemTypes(Collection<Integer> allowedItemTypes) {
        this.allowedItemTypes = allowedItemTypes;
    }

    public boolean isItemAllowed(int itemTypeId) {
        return allowedItemTypes.contains(itemTypeId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Territory)) return false;

        Territory territory = (Territory) o;

        if (name != null ? !name.equals(territory.name) : territory.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
