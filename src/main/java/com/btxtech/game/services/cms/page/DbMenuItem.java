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

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.db.DbI18nString;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * User: beat
 * Date: 04.06.2011
 * Time: 01:21:54
 */
@Entity(name = "CMS_MENU_ITEM")
public class DbMenuItem implements CrudChild<DbMenu> {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbPage page;
    private String name;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private DbI18nString dbI18nName = new DbI18nString();
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "dbMenu_id", insertable = false, updatable = false, nullable = false)
    private DbMenu dbMenu;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private DbMenu subMenu;
    private String cssClass;
    private String selectedCssClass;
    private String cssLinkClass;
    private String selectedCssLinkClass;
    private String cssTrClass;
    private String selectedCssTrClass;

    public DbPage getPage() {
        return page;
    }

    public void setPage(DbPage page) {
        this.page = page;
    }

    public String getName() {
        return name;
    }

    public DbI18nString getDbI18nName() {
        return dbI18nName;
    }

    public DbMenu getSubMenu() {
        return subMenu;
    }

    public void setSubMenu(DbMenu subMenu) {
        this.subMenu = subMenu;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getSelectedCssClass() {
        return selectedCssClass;
    }

    public void setSelectedCssClass(String selectedCssClass) {
        this.selectedCssClass = selectedCssClass;
    }

    public String getCssLinkClass() {
        return cssLinkClass;
    }

    public void setCssLinkClass(String cssLinkClass) {
        this.cssLinkClass = cssLinkClass;
    }

    public String getSelectedCssLinkClass() {
        return selectedCssLinkClass;
    }

    public void setSelectedCssLinkClass(String selectedCssLinkClass) {
        this.selectedCssLinkClass = selectedCssLinkClass;
    }

    public String getCssTrClass() {
        return cssTrClass;
    }

    public void setCssTrClass(String cssTrClass) {
        this.cssTrClass = cssTrClass;
    }

    public String getSelectedCssTrClass() {
        return selectedCssTrClass;
    }

    public void setSelectedCssTrClass(String selectedCssTrClass) {
        this.selectedCssTrClass = selectedCssTrClass;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void init(UserService userService) {
    }

    @Override
    public void setParent(DbMenu parent) {
        dbMenu = parent;
    }

    @Override
    public DbMenu getParent() {
        return dbMenu;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbMenuItem)) return false;

        DbMenuItem that = (DbMenuItem) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
