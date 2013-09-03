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

package com.btxtech.game.services.cms.page;

import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 04.06.2011
 * Time: 01:23:39
 */
@Entity(name = "CMS_MENU")
public class DbMenu implements CrudChild, CrudParent {
    @Id
    @GeneratedValue
    private Integer id;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @org.hibernate.annotations.IndexColumn(name = "orderIndex", nullable = false, base = 0)
    @JoinColumn(name = "dbMenu_id", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private List<DbMenuItem> menuItems;
    private String name;
    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private DbContent bottom;
    @Transient
    private CrudListChildServiceHelper<DbMenuItem> menuItemCrudChildServiceHelper;

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
        menuItems = new ArrayList<DbMenuItem>();
    }

    @Override
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParent() {
        return null;
    }

    public CrudListChildServiceHelper<DbMenuItem> getMenuItemCrudChildServiceHelper() {
        if (menuItemCrudChildServiceHelper == null) {
            menuItemCrudChildServiceHelper = new CrudListChildServiceHelper<DbMenuItem>(menuItems, DbMenuItem.class, this);
        }
        return menuItemCrudChildServiceHelper;
    }

    public DbContent getBottom() {
        return bottom;
    }

    public void setBottom(DbContent bottom) {
        this.bottom = bottom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbMenu)) return false;

        DbMenu that = (DbMenu) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

}
