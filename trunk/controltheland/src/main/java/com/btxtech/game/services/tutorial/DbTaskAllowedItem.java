package com.btxtech.game.services.tutorial;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * User: beat
 * Date: 18.03.2011
 * Time: 13:09:38
 */
@Entity(name = "TUTORIAL_TASK_ALLOWED_ITEMS")
public class DbTaskAllowedItem implements CrudChild<DbTaskConfig> {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbBaseItemType dbBaseItemType;
    @Column(name = "theCount")
    private int count;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "dbTaskConfig", insertable = false, updatable = false, nullable = false)
    private DbTaskConfig dbTaskConfig;

    @Override
    public Serializable getId() {
        return id;
    }

    public DbBaseItemType getDbBaseItemType() {
        return dbBaseItemType;
    }

    public void setDbBaseItemType(DbBaseItemType dbBaseItemType) {
        this.dbBaseItemType = dbBaseItemType;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public void init(UserService userService) {
    }

    @Override
    public void setParent(DbTaskConfig dbTaskConfig) {
        this.dbTaskConfig = dbTaskConfig;
    }

    @Override
    public DbTaskConfig getParent() {
        return dbTaskConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbTaskAllowedItem)) return false;

        DbTaskAllowedItem that = (DbTaskAllowedItem) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
