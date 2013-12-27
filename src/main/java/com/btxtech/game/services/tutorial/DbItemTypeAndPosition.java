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

package com.btxtech.game.services.tutorial;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.tutorial.ItemTypeAndPosition;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.db.IndexUserType;
import com.btxtech.game.services.item.itemType.DbItemType;
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


/**
 * User: beat
 * Date: 25.07.2010
 * Time: 17:02:51
 */
@Entity(name = "TUTORIAL_ITEM_TYPE_POSITION")
@TypeDef(name = "index", typeClass = IndexUserType.class)
public class DbItemTypeAndPosition implements CrudChild<DbAbstractTaskConfig> {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbItemType itemType;
    @Type(type = "index")
    @Columns(columns = {@Column(name = "xPos"), @Column(name = "yPos")})
    private Index position;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private DbAbstractTaskConfig dbTaskConfig;
    private Integer angel;

    public DbItemType getItemType() {
        return itemType;
    }

    public void setItemType(DbItemType itemType) {
        this.itemType = itemType;
    }

    public Index getPosition() {
        return position;
    }

    public void setPosition(Index position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbItemTypeAndPosition)) return false;

        DbItemTypeAndPosition that = (DbItemTypeAndPosition) o;

        return that.id != null && id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
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
        position = new Index(0, 0);
    }

    @Override
    public void setParent(DbAbstractTaskConfig crudParent) {
        dbTaskConfig = crudParent;
    }

    @Override
    public DbAbstractTaskConfig getParent() {
        return dbTaskConfig;
    }

    public Integer getId() {
        return id;
    }

    public Integer getAngel() {
        return angel;
    }

    public void setAngel(Integer angel) {
        this.angel = angel;
    }

    public ItemTypeAndPosition createItemTypeAndPosition() {
        if (itemType == null || position == null) {
            return null;
        }
        double radiant = 0;
        if (angel != null) {
            radiant = (double) angel / 180.0 * Math.PI;
        }
        return new ItemTypeAndPosition(itemType.getId(), position, radiant);
    }
}
