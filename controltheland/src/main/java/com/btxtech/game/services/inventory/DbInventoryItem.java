package com.btxtech.game.services.inventory;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
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
    // Gold
    private String goldImageContentType;
    @Column(length = 500000)
    @Basic(fetch = FetchType.LAZY)
    private byte[] goldImageData;
    private int goldAmount;
    @OneToOne(fetch = FetchType.LAZY)
    private DbLevel goldLevel;
    // Item type
    @OneToOne(fetch = FetchType.LAZY)
    private DbBaseItemType dbBaseItemType;
    private int baseItemTypeCount;
    // Artifact
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "dbInventoryItem", nullable = false)
    private Collection<DbInventoryArtifactCount> artifactCounts;

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

    public String getGoldImageContentType() {
        return goldImageContentType;
    }

    public void setGoldImageContentType(String goldImageContentType) {
        this.goldImageContentType = goldImageContentType;
    }

    public byte[] getGoldImageData() {
        return goldImageData;
    }

    public void setGoldImageData(byte[] goldImageData) {
        this.goldImageData = goldImageData;
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

    public CrudChildServiceHelper<DbInventoryArtifactCount> getArtifactCountCrud() {
        if (artifactCountCrud == null) {
            artifactCountCrud = new CrudChildServiceHelper<>(artifactCounts, DbInventoryArtifactCount.class, this);
        }
        return artifactCountCrud;
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
