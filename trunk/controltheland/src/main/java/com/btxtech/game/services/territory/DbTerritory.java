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
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import org.hibernate.annotations.Cascade;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: beat
 * Date: 23.05.2010
 * Time: 14:41:45
 */
@Entity(name = "TERRITORY_TERRITORY")
public class DbTerritory implements CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(unique = true)
    private String name;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "dbTerritory")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private Set<DbTerritoryRegion> dbTerritoryRegions;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "TERRITORY_TERRITORY_ALLOWED_ITEM_TYPES",
            joinColumns = @JoinColumn(name = "territoryId"),
            inverseJoinColumns = @JoinColumn(name = "itemTypeId")
    )
    private Set<DbBaseItemType> allowedItemTypes;

    @Override
    public Integer getId() {
        return id;
    }

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

        return id != null && id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    public Territory createTerritory() {
        ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
        for (DbTerritoryRegion dbTerritoryRegion : dbTerritoryRegions) {
            rectangles.add(dbTerritoryRegion.getTileRectangle());
        }
        HashSet<Integer> allowedItemTypes = new HashSet<Integer>();
        for (DbBaseItemType dbBaseItemType : this.allowedItemTypes) {
            allowedItemTypes.add(dbBaseItemType.getId());
        }
        return new Territory(id, name, rectangles, allowedItemTypes);
    }

    public void setDbTerritoryRegion(Collection<Rectangle> territoryRegions) {
        if (dbTerritoryRegions == null) {
            dbTerritoryRegions = new HashSet<DbTerritoryRegion>();
        } else {
            dbTerritoryRegions.clear();
        }
        for (Rectangle rectangle : territoryRegions) {
            // TODO use CrudHelper
            DbTerritoryRegion dbTerritoryRegion = new DbTerritoryRegion();
            dbTerritoryRegion.setDbTerritory(this);
            dbTerritoryRegion.setTileRectangle(rectangle);
            dbTerritoryRegions.add(dbTerritoryRegion);
        }
    }

    public Set<DbTerritoryRegion> getDbTerritoryRegions() {
        return dbTerritoryRegions;
    }

    /**
     * Only call within a valid hibernate session
     *
     * @param dbBaseItemType base item type
     * @return if allowed on territory
     */
    public Boolean isItemAllowed(DbBaseItemType dbBaseItemType) {
        return allowedItemTypes != null && allowedItemTypes.contains(dbBaseItemType);
    }

    /**
     * Only call within a valid hibernate session
     *
     * @param dbBaseItemType base item type
     * @param allowed allowed on territory
     */
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

    @Override
    public void init() {
        allowedItemTypes = new HashSet<DbBaseItemType>();
        dbTerritoryRegions = new HashSet<DbTerritoryRegion>();
    }

    @Override
    public void setParent(Object o) {
        // No parent
    }
}
