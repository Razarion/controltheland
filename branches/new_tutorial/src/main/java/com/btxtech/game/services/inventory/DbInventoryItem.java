package com.btxtech.game.services.inventory;

import com.btxtech.game.jsre.client.dialogs.inventory.InventoryArtifactInfo;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryItemInfo;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemTypePossibility;
import com.btxtech.game.services.planet.db.DbBoxRegion;
import com.btxtech.game.services.planet.db.DbBoxRegionCount;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.DbLevel;
import org.hibernate.annotations.Cascade;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * User: beat
 * Date: 15.05.12
 * Time: 11:33
 */
@Entity(name = "INVENTORY_ITEM")
public class DbInventoryItem implements CrudChild, CrudParent {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String imageContentType;
    @Column(length = 500000)
    @Basic(fetch = FetchType.LAZY)
    private byte[] imageData;
    // Gold
    private int goldAmount;
    @OneToOne(fetch = FetchType.LAZY)
    private DbLevel goldLevel;
    // Item type
    @OneToOne(fetch = FetchType.LAZY)
    private DbBaseItemType dbBaseItemType;
    private int baseItemTypeCount;
    private int itemFreeRange;
    // Artifact
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "dbInventoryItem", nullable = false)
    private Collection<DbInventoryArtifactCount> artifactCounts;
    private Integer crystalCost;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dbInventoryItem")
    private Collection<DbBoxItemTypePossibility> dbBoxItemTypePossibilities;

    @Transient
    private CrudChildServiceHelper<DbInventoryArtifactCount> artifactCountCrud;

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
        artifactCounts = new ArrayList<>();
    }

    @Override
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParent() {
        throw new UnsupportedOperationException();
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

    public int getGoldAmount() {
        return goldAmount;
    }

    public void setGoldAmount(int goldAmount) {
        this.goldAmount = goldAmount;
    }

    public DbLevel getGoldLevel() {
        return goldLevel;
    }

    public void setGoldLevel(DbLevel goldLevel) {
        this.goldLevel = goldLevel;
    }

    public DbBaseItemType getDbBaseItemType() {
        return dbBaseItemType;
    }

    public void setDbBaseItemType(DbBaseItemType dbBaseItemType) {
        this.dbBaseItemType = dbBaseItemType;
    }

    public int getBaseItemTypeCount() {
        return baseItemTypeCount;
    }

    public void setBaseItemTypeCount(int baseItemTypeCount) {
        this.baseItemTypeCount = baseItemTypeCount;
    }

    public int getItemFreeRange() {
        return itemFreeRange;
    }

    public void setItemFreeRange(int itemFreeRange) {
        this.itemFreeRange = itemFreeRange;
    }

    public Integer getCrystalCost() {
        return crystalCost;
    }

    public void setCrystalCost(Integer crystalCost) {
        this.crystalCost = crystalCost;
    }

    public CrudChildServiceHelper<DbInventoryArtifactCount> getArtifactCountCrud() {
        if (artifactCountCrud == null) {
            artifactCountCrud = new CrudChildServiceHelper<>(artifactCounts, DbInventoryArtifactCount.class, this);
        }
        return artifactCountCrud;
    }

    public Integer getCrystalCostViaArtifacts() {
        Integer cost = null;
        for (DbInventoryArtifactCount dbInventoryArtifactCount : getArtifactCountCrud().readDbChildren()) {
            DbInventoryArtifact dbInventoryArtifact = dbInventoryArtifactCount.getDbInventoryArtifact();
            if (dbInventoryArtifact != null && dbInventoryArtifact.getCrystalCost() != null && dbInventoryArtifactCount.getCount() > 0) {
                if (cost == null) {
                    cost = 0;
                }
                cost += (dbInventoryArtifact.getCrystalCost() * dbInventoryArtifactCount.getCount());
            }
        }
        return cost;
    }

    public Collection<DbPlanet> getPlanetsViaArtifact() {
        Collection<DbPlanet> dbPlanets = new HashSet<>();
        for (DbInventoryArtifactCount dbInventoryArtifactCount : getArtifactCountCrud().readDbChildren()) {
            DbInventoryArtifact dbInventoryArtifact = dbInventoryArtifactCount.getDbInventoryArtifact();
            if (dbInventoryArtifact != null) {
                Collection<DbPlanet> planets = dbInventoryArtifact.getPlanets();
                if (planets != null) {
                    dbPlanets.addAll(planets);
                }
            }
        }
        return dbPlanets;
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

    public InventoryItemInfo generateInventoryItemInfo(Map<Integer, InventoryArtifactInfo> allArtifacts) {
        Map<InventoryArtifactInfo, Integer> artifacts = new HashMap<>();
        if (allArtifacts != null) {
            for (DbInventoryArtifactCount dbInventoryArtifactCount : getArtifactCountCrud().readDbChildren()) {
                InventoryArtifactInfo inventoryArtifactInfo = allArtifacts.get(dbInventoryArtifactCount.getDbInventoryArtifact().getId());
                if (inventoryArtifactInfo == null) {
                    throw new IllegalStateException("InventoryArtifactInfo does not exist: " + dbInventoryArtifactCount.getDbInventoryArtifact().getId());
                }
                artifacts.put(inventoryArtifactInfo, dbInventoryArtifactCount.getCount());
            }
        }
        return new InventoryItemInfo(name,
                id,
                artifacts,
                dbBaseItemType != null ? dbBaseItemType.getId() : null,
                baseItemTypeCount,
                itemFreeRange,
                goldAmount,
                crystalCost);
    }

    public Collection<DbBaseItemType> getBaseItemTypesViaArtifact() {
        Collection<DbBaseItemType> dbBaseItemTypes = new HashSet<>();
        for (DbInventoryArtifactCount dbInventoryArtifactCount : getArtifactCountCrud().readDbChildren()) {
            DbInventoryArtifact dbInventoryArtifact = dbInventoryArtifactCount.getDbInventoryArtifact();
            if (dbInventoryArtifact != null) {
                Collection<DbBaseItemType> types = dbInventoryArtifact.getBaseItemTypes();
                if (types != null) {
                    dbBaseItemTypes.addAll(types);
                }
            }
        }
        return dbBaseItemTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbInventoryItem that = (DbInventoryItem) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "DbInventoryItem{id=" + id + ", name='" + name + '\'' + '}';
    }
}
