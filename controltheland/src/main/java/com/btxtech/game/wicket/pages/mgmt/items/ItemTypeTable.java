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

import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbProjectileItemType;
import com.btxtech.game.services.item.itemType.DbResourceItemType;
import com.btxtech.game.services.planet.ServerEnergyService;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Iterator;

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
        DataView<DbItemType> tileList = new DataView<DbItemType>("itemTypes", new ItemTypeProvider()) {
            protected void populateItem(final Item<DbItemType> item) {
                // Name
                item.add(new Label("name", item.getModelObject().getName()));
                // Id
                item.add(new Label("id", Integer.toString(item.getModelObject().getId())));
                // alternating row color
                item.add(new AttributeModifier("class", true, new Model<String>(item.getIndex() % 2 == 0 ? "even" : "odd")));
                // Delete
                Button edit = new Button("edit") {
                    @Override
                    public void onSubmit() {
                        if (item.getModelObject() instanceof DbBaseItemType) {
                            setResponsePage(new BaseItemTypeEditor((DbBaseItemType) item.getModelObject()));
                        } else if (item.getModelObject() instanceof DbResourceItemType) {
                            setResponsePage(new ResourceItemTypeEditor((DbResourceItemType) item.getModelObject()));
                        } else if (item.getModelObject() instanceof DbBoxItemType) {
                            setResponsePage(new BoxItemTypeEditor((DbBoxItemType) item.getModelObject()));
                        } else if (item.getModelObject() instanceof DbProjectileItemType) {
                            setResponsePage(new ProjectileItemTypeEditor((DbProjectileItemType) item.getModelObject()));
                        }
                    }
                };
                item.add(edit);
                // Delete
                Button delete = new Button("delete") {
                    @Override
                    public void onSubmit() {
                        serverItemTypeService.deleteItemType(item.getModelObject());
                    }
                };
                item.add(delete);
            }
        };
        form.add(tileList);
        form.add(new Button("activate") {
            @Override
            public void onSubmit() {
                // TODO put to a service -> MGMT
                serverItemTypeService.activate();
                // TODO energyService.recalculateEnergy();
            }
        });
        form.add(new Button("addBaseItemType") {
            @Override
            public void onSubmit() {
                DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
                serverItemTypeService.saveDbItemType(dbBaseItemType);
                setResponsePage(new BaseItemTypeEditor(dbBaseItemType));
            }
        });
        form.add(new Button("addResourceItemType") {
            @Override
            public void onSubmit() {
                DbResourceItemType dbResourceItemType = (DbResourceItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbResourceItemType.class);
                serverItemTypeService.saveDbItemType(dbResourceItemType);
                setResponsePage(new ResourceItemTypeEditor(dbResourceItemType));
            }
        });
        form.add(new Button("addBoxItemType") {
            @Override
            public void onSubmit() {
                DbBoxItemType dbBoxItemType = (DbBoxItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBoxItemType.class);
                serverItemTypeService.saveDbItemType(dbBoxItemType);
                setResponsePage(new BoxItemTypeEditor(dbBoxItemType));
            }
        });
        form.add(new Button("addProjectileItemType") {
            @Override
            public void onSubmit() {
                DbProjectileItemType dbProjectileItemType = (DbProjectileItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbProjectileItemType.class);
                serverItemTypeService.saveDbItemType(dbProjectileItemType);
                setResponsePage(new ProjectileItemTypeEditor(dbProjectileItemType));
            }
        });
    }

    class ItemTypeProvider implements IDataProvider<DbItemType> {
        @Override
        public Iterator<DbItemType> iterator(int first, int count) {
            if (first != 0 && count != serverItemTypeService.getDbItemTypes().size()) {
                throw new IllegalArgumentException("first: " + first + " count: " + count + " | " + serverItemTypeService.getDbItemTypes().size());
            }
            return serverItemTypeService.getDbItemTypes().iterator();
        }

        @Override
        public int size() {
            return serverItemTypeService.getDbItemTypes().size();
        }

        @Override
        public IModel<DbItemType> model(DbItemType tile) {
            return new Model<DbItemType>(tile);
        }

        @Override
        public void detach() {
        }
    }

}