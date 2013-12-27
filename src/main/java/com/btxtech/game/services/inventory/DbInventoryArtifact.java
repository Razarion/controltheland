package com.btxtech.game.services.inventory;

import com.btxtech.game.jsre.client.dialogs.inventory.InventoryArtifactInfo;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemTypePossibility;
import com.btxtech.game.services.planet.db.DbBoxRegion;
import com.btxtech.game.services.planet.db.DbBoxRegionCount;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.HashSet;

/**
 * User: beat
 * Date: 15.05.12
 * Time: 11:33
 */
@Entity(name = "INVENTORY_ARTIFACT")
public class DbInventoryArtifact implements CrudChild {
    public enum Rareness {
        COMMON("#d6f6ff"),
        UN_COMMON("#70d460"),
        RARE("#1273d2"),
        EPIC("#a042cc"),
        LEGENDARY("#f07d4e");
        private String htmlColor;

        Rareness(String htmlColor) {
            this.htmlColor = htmlColor;
        }

        public String getHtmlColor() {
            return htmlColor;
        }
    }

    @Id
    @GeneratedValue
    private Integer id;
    private Rareness rareness;
    private String name;
    private String imageContentType;
    @Column(length = 500000)
    @Basic(fetch = FetchType.LAZY)
    private byte[] imageData;
    private Integer crystalCost;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dbInventoryArtifact")
    private Collection<DbBoxItemTypePossibility> dbBoxItemTypePossibilities;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void init(UserService userService) {
        rareness = Rareness.COMMON;
    }

    @Override
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParent() {
        throw new UnsupportedOperationException();
    }

    public Rareness getRareness() {
        return rareness;
    }

    public void setRareness(Rareness rareness) {
        this.rareness = rareness;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public InventoryArtifactInfo generateInventoryArtifactInfo() {
        return new InventoryArtifactInfo(name, id, rareness.getHtmlColor(), crystalCost);
    }

    public Integer getCrystalCost() {
        return crystalCost;
    }

    public void setCrystalCost(Integer crystalCost) {
        this.crystalCost = crystalCost;
    }

    public Collection<DbPlanet> getPlanets() {
        Collection<DbPlanet> dbPlanets = new HashSet<>();
        if (dbBoxItemTypePossibilities == null) {
            return dbPlanets;
        }
        for (DbBoxItemTypePossibility dbBoxItemTypePossibility : dbBoxItemTypePossibilities) {
            DbBoxItemType dbBoxItemType = dbBoxItemTypePossibility.getParent();
            if (dbBoxItemType != null) {
                Collection<DbBoxRegionCount> dbBoxRegionCounts = dbBoxItemType.getDbBoxRegionCounts();
                if (dbBoxRegionCounts == null) {
                    continue;
                }
                for (DbBoxRegionCount dbBoxRegionCount : dbBoxRegionCounts) {
                    if (dbBoxRegionCount == null) {
                        continue;
                    }
                    DbBoxRegion dbBoxRegion = dbBoxRegionCount.getParent();
                    if (dbBoxRegion != null) {
                        DbPlanet dbPlanet = dbBoxRegion.getParent();
                        if (dbPlanet != null) {
                            dbPlanets.add(dbPlanet);
                        }
                    }
                }
            }
        }
        return dbPlanets;
    }

    public Collection<DbBaseItemType> getBaseItemTypes() {
        Collection<DbBaseItemType> dbBaseItemTypes = new HashSet<>();
        if (dbBoxItemTypePossibilities == null) {
            return dbBaseItemTypes;
        }
        for (DbBoxItemTypePossibility dbBoxItemTypePossibility : dbBoxItemTypePossibilities) {
            DbBoxItemType dbBoxItemType = dbBoxItemTypePossibility.getParent();
            if (dbBoxItemType != null) {
                Collection<DbBaseItemType> baseItemTypes = dbBoxItemType.getDbBaseItemTypes();
                if (baseItemTypes == null) {
                    continue;
                }
                for (DbBaseItemType dbBaseItemType : baseItemTypes) {
                    if (dbBaseItemType == null) {
                        continue;
                    }
                    dbBaseItemTypes.add(dbBaseItemType);
                }
            }
        }
        return dbBaseItemTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbInventoryArtifact)) return false;

        DbInventoryArtifact that = (DbInventoryArtifact) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "DbInventoryArtifact{id=" + id + ", name='" + name + "\'}";
    }
}