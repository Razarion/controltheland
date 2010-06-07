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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
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
    private Log log = LogFactory.getLog(BaseEditor.class);

    public BaseEditor(final SimpleBase simpleBase) {
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

        // Items
        final DataView<SyncBaseItem> itemDataView = new DataView<SyncBaseItem>("itemTypes", new SyncBaseItemProvider(simpleBase)) {

            @Override
            protected void populateItem(final Item<SyncBaseItem> item) {
                if (item.getModelObject() != null) {
                    item.add(new Label("id", item.getModelObject().getId().toString()));
                    item.add(new Label("itemType", item.getModelObject().getItemType().getName()));
                } else {
                    item.add(new Label("id", "?"));
                    item.add(new Label("itemType", "?"));
                }

                item.add(new TextField<String>("health", new IModel<String>() {
                    @Override
                    public String getObject() {
                        return Integer.toString((int) item.getModelObject().getHealth());
                    }

                    @Override
                    public void setObject(String health) {
                        if (item.getModelObject() != null) {
                            item.getModelObject().setHealth(Integer.parseInt(health));
                        }
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
                            item.getModelObject().setPosition(new Index(Integer.parseInt(xPos), item.getModelObject().getPosition().getY()));
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
                            item.getModelObject().setPosition(new Index(item.getModelObject().getPosition().getX(), Integer.parseInt(yPos)));
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
                        if (item.getModelObject() != null) {
                            itemService.killBaseSyncObject(item.getModelObject(), null, true);
                        }
                    }
                };
                item.add(killButton);
                // alternating row color
                item.add(new AttributeModifier("class", true, new Model<String>(item.getIndex() % 2 == 0 ? "even" : "odd")));

            }
        };
        form.add(itemDataView);
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
                        log.error("", e);
                        return null;
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