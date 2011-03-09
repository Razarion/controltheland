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
import com.btxtech.game.services.market.MarketCategory;
import com.btxtech.game.services.market.MarketEntry;
import com.btxtech.game.services.market.MarketFunction;
import com.btxtech.game.services.market.ServerMarketService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
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
public class MarketEntryEditor extends MgmtWebPage {
    @SpringBean
    private ServerMarketService serverMarketService;
    @SpringBean
    private ItemService itemService;

    public MarketEntryEditor() {
        showCategoryList();
        showFunctionList();
        showMarketList();
    }

    private void showCategoryList() {
        Form form = new Form("categoryForm");
        add(form);

        final DataView<MarketCategory> dataView = new DataView<MarketCategory>("categories", new CategoryProvider()) {
            @Override
            protected void populateItem(final Item<MarketCategory> categoryItem) {
                categoryItem.add(new TextField<String>("name", new IModel<String>() {

                    @Override
                    public String getObject() {
                        return categoryItem.getModelObject().getName();
                    }

                    @Override
                    public void setObject(String s) {
                        categoryItem.getModelObject().setName(s);
                    }

                    @Override
                    public void detach() {
                        // Ignore
                    }
                }));
                categoryItem.add(new Button("delete") {
                    @Override
                    public void onSubmit() {
                        serverMarketService.deleteMarketCategory(categoryItem.getModelObject());
                    }
                });
            }
        };
        form.add(dataView);
        form.add(new Button("add") {
            @Override
            public void onSubmit() {
                serverMarketService.addMarketCategory();
            }
        });
        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                ArrayList<MarketCategory> marketCategories = new ArrayList<MarketCategory>();
                Iterator<Item<MarketCategory>> iterator = dataView.getItems();
                while (iterator.hasNext()) {
                    marketCategories.add(iterator.next().getModelObject());
                }
                serverMarketService.saveMarketCategories(marketCategories);
            }
        });
    }

    private void showFunctionList() {
        Form form = new Form("functionForm");
        add(form);

        final DataView<MarketFunction> dataView = new DataView<MarketFunction>("functions", new FunctionProvider()) {
            @Override
            protected void populateItem(final Item<MarketFunction> functionItem) {
                functionItem.add(new TextField<String>("name", new IModel<String>() {

                    @Override
                    public String getObject() {
                        return functionItem.getModelObject().getName();
                    }

                    @Override
                    public void setObject(String s) {
                        functionItem.getModelObject().setName(s);
                    }

                    @Override
                    public void detach() {
                        // Ignore
                    }
                }));
                functionItem.add(new Button("delete") {
                    @Override
                    public void onSubmit() {
                        serverMarketService.deleteMarketFunction(functionItem.getModelObject());
                    }
                });
            }
        };
        form.add(dataView);
        form.add(new Button("add") {
            @Override
            public void onSubmit() {
                serverMarketService.addMarketFunction();
            }
        });
        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                ArrayList<MarketFunction> marketCategories = new ArrayList<MarketFunction>();
                Iterator<Item<MarketFunction>> iterator = dataView.getItems();
                while (iterator.hasNext()) {
                    marketCategories.add(iterator.next().getModelObject());
                }
                serverMarketService.saveMarketFunctions(marketCategories);
            }
        });
    }

    private void showMarketList() {
        Form form = new Form("itemTypeAccessForm");
        add(form);

        final DataView<MarketEntry> entries = new DataView<MarketEntry>("itemTypeAccessEntries", new ItemTypeAccessEntryProvider()) {
            protected void populateItem(final Item<MarketEntry> item) {
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
                // Category
                IModel<MarketCategory> categoryModel = new IModel<MarketCategory>() {
                    @Override
                    public MarketCategory getObject() {
                        return item.getModelObject().getMarketCategory();
                    }

                    @Override
                    public void setObject(MarketCategory category) {
                        item.getModelObject().setMarketCategory(category);
                    }

                    @Override
                    public void detach() {
                        //Ignore
                    }
                };
                item.add(new DropDownChoice<MarketCategory>("category", categoryModel, serverMarketService.getMarketCategories()));
                // Category
                IModel<MarketFunction> functionModel = new IModel<MarketFunction>() {
                    @Override
                    public MarketFunction getObject() {
                        return item.getModelObject().getMarketFunction();
                    }

                    @Override
                    public void setObject(MarketFunction function) {
                        item.getModelObject().setMarketFunction(function);
                    }

                    @Override
                    public void detach() {
                        //Ignore
                    }
                };
                item.add(new DropDownChoice<MarketFunction>("function", functionModel, serverMarketService.getMarketFunctions()));
                // Delete
                item.add(new Button("delete") {

                    @Override
                    public void onSubmit() {
                        serverMarketService.deleteItemTypeAccessEntry(item.getModelObject());
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
                ArrayList<MarketEntry> list = new ArrayList<MarketEntry>();
                Iterator<Item<MarketEntry>> iterator = entries.getItems();
                while (iterator.hasNext()) {
                    list.add(iterator.next().getModelObject());
                }
                serverMarketService.saveItemTypeAccessEntries(list);
            }
        });

        form.add(new Button("add") {
            @Override
            public void onSubmit() {
                serverMarketService.createNewItemTypeAccessEntry();
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

    class ItemTypeAccessEntryProvider implements IDataProvider<MarketEntry> {
        private List<MarketEntry> marketEntries;

        @Override
        public Iterator<MarketEntry> iterator(int first, int count) {
            setupList();
            return marketEntries.subList(first, first + count).iterator();
        }

        @Override
        public int size() {
            setupList();
            return marketEntries.size();
        }

        private void setupList() {
            if (marketEntries == null) {
                marketEntries = serverMarketService.getItemTypeAccessEntries();
            }
        }

        @Override
        public IModel<MarketEntry> model(MarketEntry marketEntry) {
            return new Model<MarketEntry>(marketEntry);
        }

        @Override
        public void detach() {
            marketEntries = null;
        }
    }

    class CategoryProvider implements IDataProvider<MarketCategory> {
        private List<MarketCategory> marketCategories;

        @Override
        public Iterator<MarketCategory> iterator(int first, int count) {
            setupList();
            return marketCategories.subList(first, first + count).iterator();
        }

        private void setupList() {
            if (marketCategories == null) {
                marketCategories = serverMarketService.getMarketCategories();
            }
        }

        @Override
        public int size() {
            setupList();
            return marketCategories.size();
        }

        @Override
        public IModel<MarketCategory> model(MarketCategory category) {
            return new Model<MarketCategory>(category);
        }

        @Override
        public void detach() {
            marketCategories = null;
        }
    }

    class FunctionProvider implements IDataProvider<MarketFunction> {
        private List<MarketFunction> marketFunctions;

        @Override
        public Iterator<MarketFunction> iterator(int first, int count) {
            setupList();
            return marketFunctions.subList(first, first + count).iterator();
        }

        private void setupList() {
            if (marketFunctions == null) {
                marketFunctions = serverMarketService.getMarketFunctions();
            }
        }

        @Override
        public int size() {
            setupList();
            return marketFunctions.size();
        }

        @Override
        public IModel<MarketFunction> model(MarketFunction function) {
            return new Model<MarketFunction>(function);
        }

        @Override
        public void detach() {
            marketFunctions = null;
        }
    }
}