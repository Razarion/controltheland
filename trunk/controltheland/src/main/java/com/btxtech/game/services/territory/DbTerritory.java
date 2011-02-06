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

package com.btxtech.game.services.territory;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Cascade;

/**
 * User: beat
 * Date: 23.05.2010
 * Time: 14:41:45
 */
@Entity(name = "TERRITORY_TERRITORY")
public class DbTerritory implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(unique = true)
    private String name;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "dbTerritory")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private Set<DbTerritoryRegion> dbTerritoryRegions;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "TERRITORY_TERRITORY_ALLOWED_ITEM_TYPES",
            joinColumns = @JoinColumn(name = "territoryId"),
            inverseJoinColumns = @JoinColumn(name = "itemTypeId")
    )
    private Set<DbBaseItemType> allowedItemTypes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbTerritory)) return false;

        DbTerritory that = (DbTerritory) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Territory createTerritory() {
        Territory territory = new Territory();
        territory.setName(name);
        ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
        for (DbTerritoryRegion dbTerritoryRegion : dbTerritoryRegions) {
            rectangles.add(dbTerritoryRegion.getTileRectangle());
        }
        territory.setTerritoryTileRegions(rectangles);
        HashSet<Integer> allowedItemTypes = new HashSet<Integer>();
        for (DbBaseItemType dbBaseItemType : this.allowedItemTypes) {
            allowedItemTypes.add(dbBaseItemType.getId());
        }
        territory.setAllowedItemTypes(allowedItemTypes);
        return territory;
    }

    public void addDbTerritoryRegion(Collection<Rectangle> territoryRegions) {
        if (dbTerritoryRegions == null) {
            dbTerritoryRegions = new HashSet<DbTerritoryRegion>();
        } else {
            dbTerritoryRegions.clear();
        }
        for (Rectangle rectangle : territoryRegions) {
            DbTerritoryRegion dbTerritoryRegion = new DbTerritoryRegion();
            dbTerritoryRegion.setDbTerritory(this);
            dbTerritoryRegion.setTileRectangle(rectangle);
            dbTerritoryRegions.add(dbTerritoryRegion);
        }
    }

    public Boolean isItemAllowed(DbBaseItemType dbBaseItemType) {
        return allowedItemTypes != null && allowedItemTypes.contains(dbBaseItemType);
    }

    public void setItemAllowed(DbBaseItemType dbBaseItemType, boolean allowed) {
        if (allowedItemTypes == null) {
            allowedItemTypes = new HashSet<DbBaseItemType>();
        }
        if (allowed) {
            allowedItemTypes.add(dbBaseItemType);
        } else {
            allowedItemTypes.remove(dbBaseItemType);
        }
    }
}
