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

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.db.DbI18nString;
import com.btxtech.game.services.user.UserService;

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
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private DbI18nString dbI18nName = new DbI18nString();
    private CmsUtil.CmsPredefinedPage predefinedType;
    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private DbContent content;
    private boolean accessRestricted;
    private boolean headerVisible;
    private boolean footerVisible;
    private boolean adsVisible;

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

    public DbI18nString getDbI18nName() {
        return dbI18nName;
    }

    @Override
    public void init(UserService userService) {
        accessRestricted = false;
        headerVisible = true;
        footerVisible = true;
        adsVisible = true;
    }

    @Override
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParent() {
        return null;
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

    public CmsUtil.CmsPredefinedPage getPredefinedType() {
        return predefinedType;
    }

    public void setPredefinedType(CmsUtil.CmsPredefinedPage predefinedType) {
        this.predefinedType = predefinedType;
    }

    public DbContent getContent() {
        return HibernateUtil.deproxy(content, DbContent.class);
    }

    public void setContent(DbContent content) {
        this.content = content;
    }

    public void setContentAndAccessWrites(DbContent content) {
        setContent(content);
        if(content == null) {
            return;
        }
        content.setReadRestricted(DbContent.Access.ALLOWED);
        content.setWriteRestricted(DbContent.Access.DENIED);
        content.setCreateRestricted(DbContent.Access.DENIED);
        content.setDeleteRestricted(DbContent.Access.DENIED);
    }

    public boolean isAccessRestricted() {
        return accessRestricted;
    }

    public void setAccessRestricted(boolean accessRestricted) {
        this.accessRestricted = accessRestricted;
    }

    public boolean isFooterVisible() {
        return footerVisible;
    }

    public void setFooterVisible(boolean footerVisible) {
        this.footerVisible = footerVisible;
    }

    public boolean isHeaderVisible() {
        return headerVisible;
    }

    public void setHeaderVisible(boolean headerVisible) {
        this.headerVisible = headerVisible;
    }

    public boolean isAdsVisible() {
        return adsVisible;
    }

    public void setAdsVisible(boolean adsVisible) {
        this.adsVisible = adsVisible;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbPage)) return false;

        DbPage dbPage = (DbPage) o;

        // Use getBaseId() due to lazy loading
        return id != null && id.equals(dbPage.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "DbPage: " + id;
    }
}
