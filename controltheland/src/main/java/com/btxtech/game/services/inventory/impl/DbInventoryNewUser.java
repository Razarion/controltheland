package com.btxtech.game.services.inventory.impl;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * User: beat
 * Date: 01.06.12
 * Time: 13:12
 */
@Entity(name = "INVENTORY_NEW_USER")
public class DbInventoryNewUser implements CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    private Integer razarion;
    @Column(name = "theCount")
    private int count;
    @OneToOne(fetch = FetchType.LAZY)
    private DbInventoryArtifact dbInventoryArtifact;
    @OneToOne(fetch = FetchType.LAZY)
    private DbInventoryItem dbInventoryItem;

    public Integer getId() {
        return id;
    }

    public Integer getRazarion() {
        return razarion;
    }

    public void setRazarion(Integer razarion) {
        this.razarion = razarion;
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

    public DbInventoryItem getDbInventoryItem() {
        return dbInventoryItem;
    }

    public void setDbInventoryItem(DbInventoryItem dbInventoryItem) {
        this.dbInventoryItem = dbInventoryItem;
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
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof DbInventoryNewUser)) return false;

        DbInventoryNewUser that = (DbInventoryNewUser) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
