package com.btxtech.game.services.planet.db;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
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
 * Time: 22:11
 */
@Entity(name = "BOX_REGION_COUNT")
public class DbBoxRegionCount implements CrudChild<DbBoxRegion> {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbBoxRegion dbBoxRegion;
    @Column(name = "theCount")
    private int count;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbBoxItemType dbBoxItemType;

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
    public void setParent(DbBoxRegion dbBoxRegion) {
        this.dbBoxRegion = dbBoxRegion;
    }

    @Override
    public DbBoxRegion getParent() {
        return dbBoxRegion;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public DbBoxItemType getDbBoxItemType() {
        return dbBoxItemType;
    }

    public void setDbBoxItemType(DbBoxItemType dbBoxItemType) {
        this.dbBoxItemType = dbBoxItemType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbBoxRegionCount)) return false;

        DbBoxRegionCount that = (DbBoxRegionCount) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
