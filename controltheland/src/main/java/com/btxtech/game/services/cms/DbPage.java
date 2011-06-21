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

    /*   static {
       DbBeanTable dbBeanTable = new DbBeanTable();
       dbBeanTable.setRowsPerPage(5);
       dbBeanTable.setSpringBeanName("cmsService");
       dbBeanTable.setContentProviderGetter("getBlogEntryCrudRootServiceHelper");
       List<DbProperty> dbPropertyColumns = new ArrayList<DbProperty>();
       dbBeanTable.setDbPropertyColumns(dbPropertyColumns);

       DbPropertyContainer dbPropertyContainer = new DbPropertyContainer();
       dbPropertyColumns.add(dbPropertyContainer);
       dbPropertyContainer.setParentContentDataProviderInfo(dbBeanTable);

       List<DbProperty> dbProperties = new ArrayList<DbProperty>();
       dbPropertyContainer.setDbProperties(dbProperties);
       DbExpressionProperty title = new DbExpressionProperty();
       title.setExpression("name");
       dbProperties.add(title);
       DbExpressionProperty date = new DbExpressionProperty();
       date.setExpression("timeStamp");
       dbProperties.add(date);
       DbExpressionProperty html = new DbExpressionProperty();
       html.setExpression("html");
       html.setEscapeMarkup(false);
       dbProperties.add(html);

       content = dbBeanTable;
   } */

    /*
    static {
        // Columns
        DbBeanTable dbBeanTable = new DbBeanTable();
        dbBeanTable.setRowsPerPage(5);
        dbBeanTable.setSpringBeanName("userGuidanceService");
        dbBeanTable.setContentProviderGetter("getDbLevelCrudServiceHelper");
        List<DbProperty> dbPropertyColumns = new ArrayList<DbProperty>();
        dbBeanTable.setDbPropertyColumns(dbPropertyColumns);
        DbExpressionProperty column0 = new DbExpressionProperty();
        column0.setExpression("name");
        dbPropertyColumns.add(column0);
        DbExpressionProperty column1 = new DbExpressionProperty();
        column1.setExpression("internalDescription");
        dbPropertyColumns.add(column1);
        DbContentLink column3 = new DbContentLink();
        column3.setLabel("Details");
        column3.setPage(new DbPage(4));
        dbPropertyColumns.add(column3);

        List<DbContentBook> dbPropertyBooks = new ArrayList<DbContentBook>();

        DbContentBook dbPropertyBook0 = new DbContentBook();
        dbPropertyBooks.add(dbPropertyBook0);
        dbPropertyBook0.setParentSpringBeanProvider(dbBeanTable);
        dbPropertyBook0.setClassName("com.btxtech.game.services.utg.DbSimulationLevel");
        List<DbContentRow> rows = new ArrayList<DbContentRow>();
        dbPropertyBook0.setDbPropertyRows(rows);

        DbContentRow dbPropertyRow0 = new DbContentRow();
        rows.add(dbPropertyRow0);
        dbPropertyRow0.setName("Name");
        DbExpressionProperty row0p1 = new DbExpressionProperty();
        row0p1.setExpression("name");
        dbPropertyRow0.setDbProperty(row0p1);

        DbContentRow dbPropertyRow1 = new DbContentRow();
        dbPropertyRow1.setName("Description");
        DbExpressionProperty row1p1 = new DbExpressionProperty();
        row1p1.setExpression("html");
        row1p1.setEscapeMarkup(false);
        dbPropertyRow1.setDbProperty(row1p1);
        rows.add(dbPropertyRow1);

        DbContentBook dbPropertyBook1 = new DbContentBook();
        dbPropertyBook1.setParentSpringBeanProvider(dbBeanTable);
        dbPropertyBooks.add(dbPropertyBook1);
        dbPropertyBook1.setClassName("com.btxtech.game.services.utg.DbRealGameLevel");
        rows = new ArrayList<DbContentRow>();
        dbPropertyBook1.setDbPropertyRows(rows);

        DbContentRow dbProperty1Row0 = new DbContentRow();
        dbProperty1Row0.setName("Name");
        DbExpressionProperty row0p2 = new DbExpressionProperty();
        row0p2.setExpression("name");
        dbProperty1Row0.setDbProperty(row0p1);
        rows.add(dbProperty1Row0);

        DbContentRow dbProperty1Row1 = new DbContentRow();
        dbProperty1Row1.setName("Description");
        DbExpressionProperty row1p2 = new DbExpressionProperty();
        row1p2.setExpression("html");
        row1p2.setEscapeMarkup(false);
        dbProperty1Row1.setDbProperty(row1p1);
        rows.add(dbProperty1Row1);

        dbBeanTable.setDbPropertyBooks(dbPropertyBooks);


        DbContentRow dbProperty1Row2 = new DbContentRow();
        dbProperty1Row2.setName("Allowed Items");
        DbBeanTable dbBeanTable1 = new DbBeanTable();
        dbBeanTable1.setParentSpringBeanProvider(dbPropertyBook1);
        dbBeanTable1.setContentProviderGetter("getDbItemTypeLimitationCrudServiceHelper");
        List<DbProperty> itemLimitations = new ArrayList<DbProperty>();
        dbBeanTable1.setDbPropertyColumns(itemLimitations);
        DbExpressionProperty count = new DbExpressionProperty();
        count.setExpression("count");
        itemLimitations.add(count);
        DbExpressionProperty img = new DbExpressionProperty();
        img.setExpression("dbBaseItemType");
        itemLimitations.add(img);
        dbProperty1Row2.setDbProperty(dbBeanTable1);
        rows.add(dbProperty1Row2);

        content = dbBeanTable;
    }
    */
}
