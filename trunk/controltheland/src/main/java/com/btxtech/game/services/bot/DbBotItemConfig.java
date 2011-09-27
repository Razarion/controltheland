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

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.db.RectangleUserType;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;


/**
 * User: beat
 * Date: 04.04.2010
 * Time: 20:41:25
 */
@Entity(name = "BOT_ITEM_CONFIG")
@TypeDef(name = "rectangle", typeClass = RectangleUserType.class)
public class DbBotItemConfig implements CrudChild<DbBotConfig>, Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbBaseItemType baseItemType;
    @Column(name = "theCount")
    private int count;
    private boolean createDirectly;
    @Type(type = "rectangle")
    @Columns(columns = {@Column(name = "regionX"), @Column(name = "regionY"), @Column(name = "regionWidth"), @Column(name = "regionHeight")})
    private Rectangle region;
    @ManyToOne
    private DbBotConfig parent;

    /**
     * Used by Hibernate
     */
    public DbBotItemConfig() {
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
    public void setParent(DbBotConfig parent) {
        this.parent = parent;
    }

    public boolean isCreateDirectly() {
        return createDirectly;
    }

    public void setCreateDirectly(boolean createDirectly) {
        this.createDirectly = createDirectly;
    }

    public Rectangle getRegion() {
        return region;
    }

    public void setRegion(Rectangle region) {
        this.region = region;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbBotItemConfig)) return false;

        DbBotItemConfig that = (DbBotItemConfig) o;

        return id != null && !id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}