package com.btxtech.game.services.item.itemType;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * User: beat
 * Date: 21.05.12
 * Time: 16:23
 */
@Entity(name = "ITEM_BOX_POSSIBILITY")
public class DbBoxItemTypePossibility implements CrudChild<DbBoxItemType> {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbBoxItemType dbBoxItemType;
    private double possibility;
    private Integer crystals;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbInventoryArtifact dbInventoryArtifact;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbInventoryItem dbInventoryItem;

    @Override
    public Serializable getId() {
        return id;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init(UserService userService) {
    }

    @Override
    public void setParent(DbBoxItemType dbBoxItemType) {
        this.dbBoxItemType = dbBoxItemType;
    }

    @Override
    public DbBoxItemType getParent() {
        return dbBoxItemType;
    }

    public double getPossibility() {
        return possibility;
    }

    public void setPossibility(double possibility) {
        this.possibility = possibility;
    }

    public Integer getCrystals() {
        return crystals;
    }

    public void setCrystals(Integer crystals) {
        this.crystals = crystals;
    }

    public DbInventoryArtifact getDbInventoryArtifact() {
        return dbInventoryArtifact;
    }

    public void setDbInventoryArtifact(DbInventoryArtifact dbInventoryArtifact) {
        this.dbInventoryArtifact = dbInventoryArtifact;
    }

    public DbInventoryItem getDbInventoryItem() {
        return dbInventoryItem;
    }

    public void setDbInventoryItem(DbInventoryItem dbInventoryItem) {
        this.dbInventoryItem = dbInventoryItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DbBoxItemTypePossibility)) {
            return false;
        }

        DbBoxItemTypePossibility that = (DbBoxItemTypePossibility) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "DbBoxItemTypePossibility{ id=" + id + '}';
    }
}
