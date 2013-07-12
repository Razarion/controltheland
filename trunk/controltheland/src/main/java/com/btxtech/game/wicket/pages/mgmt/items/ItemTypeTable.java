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

package com.btxtech.game.wicket.pages.mgmt.items;

import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbProjectileItemType;
import com.btxtech.game.services.item.itemType.DbResourceItemType;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: Sep 4, 2009
 * Time: 10:35:35 PM
 */
public class ItemTypeTable extends MgmtWebPage {
    @SpringBean
    private ServerItemTypeService serverItemTypeService;

    public ItemTypeTable() {
        Form form = new Form("itemTypeForm");
        add(form);

        new CrudRootTableHelper<DbItemType>("itemTypes", "save", null, true, form, false) {

            @Override
            protected void extendedPopulateItem(Item<DbItemType> dbItemTypeItem) {
                displayId(dbItemTypeItem);
                // Name
                super.extendedPopulateItem(dbItemTypeItem);
                // alternating row color
                dbItemTypeItem.add(new AttributeModifier("class", new Model<>(dbItemTypeItem.getIndex() % 2 == 0 ? "even" : "odd")));
            }

            @Override
            protected CrudRootServiceHelper<DbItemType> getCrudRootServiceHelperImpl() {
                return serverItemTypeService.getDbItemTypeCrud();
            }

            @Override
            protected void onEditSubmit(DbItemType dbItemType) {
                if (dbItemType instanceof DbBaseItemType) {
                    setResponsePage(new BaseItemTypeEditor((DbBaseItemType) dbItemType));
                } else if (dbItemType instanceof DbResourceItemType) {
                    setResponsePage(new ResourceItemTypeEditor((DbResourceItemType) dbItemType));
                } else if (dbItemType instanceof DbBoxItemType) {
                    setResponsePage(new BoxItemTypeEditor((DbBoxItemType) dbItemType));
                } else if (dbItemType instanceof DbProjectileItemType) {
                    setResponsePage(new ProjectileItemTypeEditor((DbProjectileItemType) dbItemType));
                }
            }

            protected void setupCreate(WebMarkupContainer markupContainer, String createId) {
                markupContainer.add(new Button("addBaseItemType") {
                    @Override
                    public void onSubmit() {
                        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
                        serverItemTypeService.saveDbItemType(dbBaseItemType);
                        refresh();
                    }
                });
                markupContainer.add(new Button("addResourceItemType") {
                    @Override
                    public void onSubmit() {
                        DbResourceItemType dbResourceItemType = (DbResourceItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbResourceItemType.class);
                        serverItemTypeService.saveDbItemType(dbResourceItemType);
                        refresh();
                    }
                });
                markupContainer.add(new Button("addBoxItemType") {
                    @Override
                    public void onSubmit() {
                        DbBoxItemType dbBoxItemType = (DbBoxItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBoxItemType.class);
                        serverItemTypeService.saveDbItemType(dbBoxItemType);
                        refresh();
                    }
                });
                markupContainer.add(new Button("addProjectileItemType") {
                    @Override
                    public void onSubmit() {
                        DbProjectileItemType dbProjectileItemType = (DbProjectileItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbProjectileItemType.class);
                        serverItemTypeService.saveDbItemType(dbProjectileItemType);
                        refresh();
                    }
                });
            }
        };
        form.add(new Button("activate") {
            @Override
            public void onSubmit() {
                serverItemTypeService.activate();
            }
        });
    }
}