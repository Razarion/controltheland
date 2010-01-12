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

package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import com.btxtech.game.services.itemTypeAccess.ItemTypeAccessEntry;
import com.btxtech.game.services.itemTypeAccess.ServerItemTypeAccessService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.resource.ByteArrayResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: Sep 4, 2009
 * Time: 10:35:35 PM
 */
public class ItemTypeAccessEntryEditor extends WebPage {
    @SpringBean
    private ServerItemTypeAccessService serverItemTypeAccessService;
    @SpringBean
    private ItemService itemService;

    public ItemTypeAccessEntryEditor() {
        Form form = new Form("itemTypeAccessForm");
        add(form);

        final DataView<ItemTypeAccessEntry> entries = new DataView<ItemTypeAccessEntry>("itemTypeAccessEntries", new ItemTypeAccessEntryProvider()) {
            protected void populateItem(final Item<ItemTypeAccessEntry> item) {
                DbItemType dbItemType = item.getModelObject().getItemType();
                // Id
                item.add(new Label("id", item.getModelObject().getId().toString()));
                // image
                if (dbItemType != null) {
                    Image image = new Image("image", new ByteArrayResource("", getImage(item.getModelObject().getItemType())));
                    item.add(image);
                } else {
                    item.add(new Label("image", "No image"));
                }
                // Item type
                item.add(new TextField<String>("itemType", new IModel<String>() {
                    @Override
                    public String getObject() {
                        DbItemType dbItemType = item.getModelObject().getItemType();
                        if (dbItemType != null) {
                            return Integer.toString(dbItemType.getId());
                        } else {
                            return "";
                        }
                    }

                    @Override
                    public void setObject(String string) {
                        if (string == null || string.length() == 0) {
                            item.getModelObject().setItemType(null);
                        } else {
                            int itemTypeId = Integer.parseInt(string);
                            item.getModelObject().setItemType(itemService.getDbItemType(itemTypeId));
                        }
                    }

                    @Override
                    public void detach() {
                        // Ignore
                    }
                }));
                // Price
                item.add(new TextField<String>("price", new IModel<String>() {
                    @Override
                    public String getObject() {
                        return Integer.toString(item.getModelObject().getPrice());
                    }

                    @Override
                    public void setObject(String string) {
                        if (string != null && string.length() != 0) {
                            int price = Integer.parseInt(string);
                            item.getModelObject().setPrice(price);
                        } 
                    }

                    @Override
                    public void detach() {
                        // Ignore
                    }
                }));
                // Always allowed
                item.add(new CheckBox("alwaysAllowed", new IModel<Boolean>() {
                    @Override
                    public Boolean getObject() {
                        return item.getModelObject().isAlwaysAllowed();
                    }

                    @Override
                    public void setObject(Boolean aBoolean) {
                        item.getModelObject().setAlwaysAllowed(aBoolean);
                    }

                    @Override
                    public void detach() {
                        // Ignore
                    }
                }));
                // Delete
                item.add(new Button("delete") {

                    @Override
                    public void onSubmit() {
                        serverItemTypeAccessService.delteItemTypeAccessEntry(item.getModelObject());
                    }
                });
                // alternating row color
                item.add(new AttributeModifier("class", true, new Model<String>(item.getIndex() % 2 == 0 ? "even" : "odd")));
            }
        };
        form.add(entries);

        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                ArrayList<ItemTypeAccessEntry> list = new ArrayList<ItemTypeAccessEntry>();
                Iterator<org.apache.wicket.markup.repeater.Item<ItemTypeAccessEntry>> iterator = entries.getItems();
                while (iterator.hasNext()) {
                    list.add(iterator.next().getModelObject());
                }
                serverItemTypeAccessService.saveItemTypeAccessEntries(list);
            }
        });

        form.add(new Button("add") {
            @Override
            public void onSubmit() {
                serverItemTypeAccessService.createNewItemTypeAccessEntry();
            }
        });
    }

    private byte[] getImage(DbItemType itemType) {
        Set<DbItemTypeImage> dbItemTypeImages = itemType.getItemTypeImages();
        if (dbItemTypeImages == null || dbItemTypeImages.isEmpty()) {
            return null;
        }
        return dbItemTypeImages.iterator().next().getData();
    }

    class ItemTypeAccessEntryProvider implements IDataProvider<ItemTypeAccessEntry> {
        @Override
        public Iterator<ItemTypeAccessEntry> iterator(int first, int count) {
            if (first != 0 && count != serverItemTypeAccessService.getItemTypeAccessEntries().size()) {
                throw new IllegalArgumentException("first: " + first + " count: " + count + " | " + serverItemTypeAccessService.getItemTypeAccessEntries().size());
            }
            return serverItemTypeAccessService.getItemTypeAccessEntries().iterator();
        }

        @Override
        public int size() {
            return serverItemTypeAccessService.getItemTypeAccessEntries().size();
        }

        @Override
        public IModel<ItemTypeAccessEntry> model(ItemTypeAccessEntry itemTypeAccessEntry) {
            return new Model<ItemTypeAccessEntry>(itemTypeAccessEntry);
        }

        @Override
        public void detach() {
        }
    }

}