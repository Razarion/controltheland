package com.btxtech.game.services.inventory;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.io.Serializable;

/**
 * User: beat
 * Date: 15.05.12
 * Time: 11:34
 */
@Entity(name = "INVENTORY_ARTIFACT_COUNT")
public class DbInventoryArtifactCount implements CrudChild<DbInventoryItem> {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbInventoryItem dbInventoryItem;
    @Column(name = "theCount")
    private int count;
    @OneToOne(fetch = FetchType.LAZY)
    private DbInventoryArtifact dbInventoryArtifact;

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
    public void setParent(DbInventoryItem dbInventoryItem) {
        this.dbInventoryItem = dbInventoryItem;
    }

    @Override
    public DbInventoryItem getParent() {
        return dbInventoryItem;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public DbInventoryArtifact getDbInventoryArtifact() {
        return dbInventoryArtifact;
    }

    public void setDbInventoryArtifact(DbInventoryArtifact dbInventoryArtifact) {
        this.dbInventoryArtifact = dbInventoryArtifact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbInventoryArtifactCount that = (DbInventoryArtifactCount) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
