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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.energy.ServerEnergyService;
import com.btxtech.game.services.item.ItemService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 16.02.2010
 * Time: 21:35:44
 */
public class BaseEditor extends WebPage {
    public static final String NO_POS = "-";
    @SpringBean
    private ItemService itemService;
    @SpringBean
    private BaseService baseService;
    @SpringBean
    private ConnectionService connectionService;
    @SpringBean
    private ServerEnergyService energyService;
    private HashSet<Id> itemsToKill = new HashSet<Id>();
    private Rectangle selection = new Rectangle(0, 0, 0, 0);
    private SimpleBase simpleBase;

    public static TextField<String> createReadonlyTextFiled(String id) {
        TextField<String> field = new TextField<String>(id);
        field.setEnabled(false);
        return field;
    }

    public BaseEditor(final SimpleBase simpleBase) {
        this.simpleBase = simpleBase;
        createBaseItems();
        createSelectionHelper();
    }

    private void createSelectionHelper() {
        Form<Rectangle> form = new Form<Rectangle>("selection", new CompoundPropertyModel<Rectangle>(selection)) {
            @Override
            protected void onSubmit() {
                itemsToKill.clear();
                for (SyncBaseItem syncBaseItem : baseService.getBase(simpleBase).getItems()) {
                    if (selection.contains(syncBaseItem.getPosition())) {
                        itemsToKill.add(syncBaseItem.getId());
                    }
                }
            }
        };
        form.add(new TextField<Integer>("x"));
        form.add(new TextField<Integer>("y"));
        form.add(new TextField<Integer>("width"));
        form.add(new TextField<Integer>("height"));
        form.add(new Label("count", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                return itemsToKill.size();
            }

            @Override
            public void setObject(Integer object) {
                // Ignore
            }

            @Override
            public void detach() {
                // Ignore
            }
        }));
        add(form);

    }


    private void createBaseItems() {
        Form form = new Form("base");

        // General
        form.add(new Label("name", new IModel<String>() {
            @Override
            public String getObject() {
                return baseService.getBase(simpleBase).getName();
            }

            @Override
            public void setObject(String s) {
            }

            @Override
            public void detach() {
            }
        }));
        form.add(new TextField<String>("accountBalance", new IModel<String>() {
            @Override
            public String getObject() {
                return Double.toString(baseService.getBase(simpleBase).getAccountBalance());
            }

            @Override
            public void setObject(String s) {
                baseService.getBase(simpleBase).setAccountBalance(Double.parseDouble(s));
            }

            @Override
            public void detach() {
            }
        }));
        form.add(new Label("energy", new IModel<String>() {
            @Override
            public String getObject() {
                return energyService.getConsuming(simpleBase) + "/" + energyService.getGenerating(simpleBase);
            }

            @Override
            public void setObject(String o) {
                // Ignore
            }

            @Override
            public void detach() {
                // Ignore
            }
        }));
        form.add(new Label("itemCount", new IModel<Integer>() {
            @Override
            public Integer getObject() {
                return baseService.getBase(simpleBase).getItems().size();
            }

            @Override
            public void setObject(Integer object) {
                // Ignore
            }

            @Override
            public void detach() {
                // Ignore
            }
        }));


        // Items
        final DataView<SyncBaseItem> itemDataView = new DataView<SyncBaseItem>("itemTypes", new SyncBaseItemProvider(simpleBase)) {

            @Override
            protected void populateItem(final Item<SyncBaseItem> item) {
                try {
                    item.add(new Label("id", item.getModelObject().getId().toString()));
                    item.add(new Label("itemType", item.getModelObject().getItemType().getName()));

                    item.add(new TextField<String>("health", new IModel<String>() {
                        @Override
                        public String getObject() {
                            return Integer.toString((int) item.getModelObject().getHealth());
                        }

                        @Override
                        public void setObject(String health) {
                            item.getModelObject().setHealth(Integer.parseInt(health));
                        }

                        @Override
                        public void detach() {
                        }
                    }));

                    item.add(new TextField<String>("xPos", new IModel<String>() {
                        @Override
                        public String getObject() {
                            Index pos = item.getModelObject().getPosition();
                            if (pos != null) {
                                return Integer.toString(pos.getX());
                            } else {
                                return NO_POS;
                            }
                        }

                        @Override
                        public void setObject(String xPos) {
                            if (!NO_POS.equals(xPos)) {
                                if (item.getModelObject().getPosition() != null) {
                                    item.getModelObject().setPosition(new Index(Integer.parseInt(xPos), item.getModelObject().getPosition().getY()));
                                } else {
                                    item.getModelObject().setPosition(new Index(Integer.parseInt(xPos), 0));
                                }
                            }
                        }

                        @Override
                        public void detach() {
                        }
                    }));
                    item.add(new TextField<String>("yPos", new IModel<String>() {
                        @Override
                        public String getObject() {
                            Index pos = item.getModelObject().getPosition();
                            if (pos != null) {
                                return Integer.toString(pos.getY());
                            } else {
                                return NO_POS;
                            }
                        }

                        @Override
                        public void setObject(String yPos) {
                            if (!NO_POS.equals(yPos)) {
                                if (item.getModelObject().getPosition() != null) {
                                    item.getModelObject().setPosition(new Index(item.getModelObject().getPosition().getX(), Integer.parseInt(yPos)));
                                } else {
                                    item.getModelObject().setPosition(new Index(0, Integer.parseInt(yPos)));
                                }
                            }
                        }

                        @Override
                        public void detach() {
                        }
                    }));
                    // Kill button
                    Button killButton = new Button("kill") {
                        @Override
                        public void onSubmit() {
                            itemService.killSyncItem(item.getModelObject(), null, true);
                            if (baseService.getBase(simpleBase) == null) {
                                setResponsePage(BasesTable.class);
                            }

                        }
                    };
                    item.add(killButton);
                    item.add(new CheckBox("select", new IModel<Boolean>() {

                        @Override
                        public Boolean getObject() {
                            return itemsToKill.contains(item.getModelObject().getId());
                        }

                        @Override
                        public void setObject(Boolean allowed) {
                            itemsToKill.add(item.getModelObject().getId());
                        }

                        @Override
                        public void detach() {
                            // Ignore
                        }
                    }));

                    // alternating row color
                    item.add(new AttributeModifier("class", true, new Model<String>(item.getIndex() % 2 == 0 ? "even" : "odd")));

                } catch (RuntimeException e) {
                    if (e.getCause() instanceof ItemDoesNotExistException) {
                        item.add(new Label("id", ((ItemDoesNotExistException) e.getCause()).getId().toString()));
                        item.add(new Label("itemType", "Dead"));
                        item.add(createReadonlyTextFiled("health"));
                        item.add(createReadonlyTextFiled("xPos"));
                        item.add(createReadonlyTextFiled("yPos"));
                        Button killButton = new Button("kill") {
                            @Override
                            public void onSubmit() {
                            }
                        };
                        killButton.setEnabled(false);
                        item.add(killButton);
                        CheckBox checkBox = new CheckBox("select", new Model<Boolean>(false));
                        item.add(checkBox);
                        // alternating row color
                        item.add(new AttributeModifier("class", true, new Model<String>(item.getIndex() % 2 == 0 ? "even" : "odd")));
                    } else {
                        throw e;
                    }
                }
            }
        };
        form.add(itemDataView);
        form.add(new Button("killSelected") {
            @Override
            public void onSubmit() {
                for (Id id : itemsToKill) {
                    try {
                        itemService.killSyncItem(itemService.getItem(id), null, true);

                    } catch (Exception e) {
                        // Ignore
                    }
                }
                if (baseService.getBase(simpleBase) == null) {
                    setResponsePage(BasesTable.class);
                }
            }
        });

        form.add(new Button("apply") {
            @Override
            public void onSubmit() {
                HashSet<SyncBaseItem> syncBaseItems = baseService.getBase(simpleBase).getItems();
                if (!syncBaseItems.isEmpty()) {
                    baseService.sendAccountBaseUpdate(syncBaseItems.iterator().next());
                    connectionService.sendSyncInfos(syncBaseItems);
                }
            }
        });

        add(form);
    }

    class SyncBaseItemProvider implements IDataProvider<SyncBaseItem> {
        private SimpleBase simpleBase;
        private List<SyncBaseItem> items;

        SyncBaseItemProvider(SimpleBase simpleBase) {
            this.simpleBase = simpleBase;
        }

        private List<SyncBaseItem> getSyncBaseItem() {
            if (items == null) {
                items = new ArrayList<SyncBaseItem>(baseService.getBase(simpleBase).getItems());
            }
            return items;
        }

        @Override
        public Iterator<SyncBaseItem> iterator(int first, int count) {
            return getSyncBaseItem().subList(first, first + count).iterator();
        }

        @Override
        public int size() {
            return getSyncBaseItem().size();
        }

        @Override
        public IModel<SyncBaseItem> model(SyncBaseItem syncBaseItem) {
            final Id id = syncBaseItem.getId();
            return new LoadableDetachableModel<SyncBaseItem>() {
                @Override
                protected SyncBaseItem load() {
                    try {
                        return (SyncBaseItem) itemService.getItem(id);
                    } catch (ItemDoesNotExistException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }

        @Override
        public void detach() {
            items = null;
        }
    }
}