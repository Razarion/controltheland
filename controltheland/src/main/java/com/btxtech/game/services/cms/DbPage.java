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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

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
    @Transient
    static private DbContent content;

    public DbPage() {
    }

    @Deprecated
    public DbPage(Integer id) {
        this.id = id;
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
        //DbTextContent dbTextContent = new DbTextContent();
        //dbTextContent.setContent("bla bla bla bla");
        //return dbTextContent;
        //return new DbGenericDetailTable();
        // return new DbBeanTable();
        return content;
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
        DbPropertyBookLink column3 = new DbPropertyBookLink();
        column3.setLabel("Details");
        column3.setPage(new DbPage(4));
        dbPropertyColumns.add(column3);

        List<DbPropertyBook> dbPropertyBooks = new ArrayList<DbPropertyBook>();

        DbPropertyBook dbPropertyBook0 = new DbPropertyBook();
        dbPropertyBooks.add(dbPropertyBook0);
        dbPropertyBook0.setParentSpringBeanProvider(dbBeanTable);
        dbPropertyBook0.setClassName("com.btxtech.game.services.utg.DbSimulationLevel");
        List<DbPropertyRow> rows = new ArrayList<DbPropertyRow>();
        dbPropertyBook0.setDbPropertyRows(rows);

        DbPropertyRow dbPropertyRow0 = new DbPropertyRow();
        rows.add(dbPropertyRow0);
        dbPropertyRow0.setName("Name");
        DbExpressionProperty row0p1 = new DbExpressionProperty();
        row0p1.setExpression("name");
        dbPropertyRow0.setDbProperty(row0p1);

        DbPropertyRow dbPropertyRow1 = new DbPropertyRow();
        dbPropertyRow1.setName("Description");
        DbExpressionProperty row1p1 = new DbExpressionProperty();
        row1p1.setExpression("html");
        row1p1.setEscapeMarkup(false);
        dbPropertyRow1.setDbProperty(row1p1);
        rows.add(dbPropertyRow1);

        DbPropertyBook dbPropertyBook1 = new DbPropertyBook();
        dbPropertyBook1.setParentSpringBeanProvider(dbBeanTable);
        dbPropertyBooks.add(dbPropertyBook1);
        dbPropertyBook1.setClassName("com.btxtech.game.services.utg.DbRealGameLevel");
        rows = new ArrayList<DbPropertyRow>();
        dbPropertyBook1.setDbPropertyRows(rows);

        DbPropertyRow dbProperty1Row0 = new DbPropertyRow();
        dbProperty1Row0.setName("Name");
        DbExpressionProperty row0p2 = new DbExpressionProperty();
        row0p2.setExpression("name");
        dbProperty1Row0.setDbProperty(row0p1);
        rows.add(dbProperty1Row0);

        DbPropertyRow dbProperty1Row1 = new DbPropertyRow();
        dbProperty1Row1.setName("Description");
        DbExpressionProperty row1p2 = new DbExpressionProperty();
        row1p2.setExpression("html");
        row1p2.setEscapeMarkup(false);
        dbProperty1Row1.setDbProperty(row1p1);
        rows.add(dbProperty1Row1);

        dbBeanTable.setDbPropertyBooks(dbPropertyBooks);


        DbPropertyRow dbProperty1Row2 = new DbPropertyRow();
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
}
