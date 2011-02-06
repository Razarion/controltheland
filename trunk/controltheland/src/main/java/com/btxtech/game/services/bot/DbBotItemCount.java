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

package com.btxtech.game.services.bot;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * User: beat
 * Date: 04.04.2010
 * Time: 20:41:25
 */
@Entity(name = "BOT_ITEM_COUNT")
public class DbBotItemCount implements CrudChild<DbBotConfig>, Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    private DbBaseItemType baseItemType;
    private int count;
    private int type;
    @ManyToOne
    private DbBotConfig parent;

    /**
     * Used by Hibernate
     */
    public DbBotItemCount() {
    }

    public Integer getId() {
        return id;
    }

    public DbBaseItemType getBaseItemType() {
        return baseItemType;
    }

    public void setBaseItemType(DbBaseItemType baseItemType) {
        this.baseItemType = baseItemType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String getName() {
        throw new NotImplementedException();
    }

    @Override
    public void setName(String name) {
        throw new NotImplementedException();
    }

    @Override
    public void init() {
        type = 1;
    }

    @Override
    public void setParent(DbBotConfig parent) {
        this.parent = parent;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbBotItemCount)) return false;

        DbBotItemCount that = (DbBotItemCount) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
