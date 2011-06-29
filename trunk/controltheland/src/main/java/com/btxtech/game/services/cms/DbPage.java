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

package com.btxtech.game.services.cms;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.HibernateUtil;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * User: beat
 * Date: 04.06.2011
 * Time: 01:14:29
 */
@Entity(name = "CMS_PAGE")
public class DbPage implements CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbPageStyle style;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbMenu menu;
    private String name;
    private boolean home;
    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private DbContent content;
    private boolean accessRestricted;

    public DbPage() {
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
    public void init() {
        accessRestricted = false;
    }

    @Override
    public void setParent(Object o) {
        // Ignore
    }

    public DbPageStyle getStyle() {
        return style;
    }

    public void setStyle(DbPageStyle style) {
        this.style = style;
    }

    public Integer getId() {
        return id;
    }

    public DbMenu getMenu() {
        return menu;
    }

    public void setMenu(DbMenu menu) {
        this.menu = menu;
    }

    public boolean isHome() {
        return home;
    }

    public void setHome(boolean home) {
        this.home = home;
    }

    public DbContent getContent() {
        return HibernateUtil.deproxy(content, DbContent.class);
    }

    public void setContent(DbContent content) {
        this.content = content;
    }

    public boolean isAccessRestricted() {
        return accessRestricted;
    }

    public void setAccessRestricted(boolean accessRestricted) {
        this.accessRestricted = accessRestricted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbPage)) return false;

        DbPage dbPage = (DbPage) o;

        return id != null && id.equals(dbPage.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
