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

import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.bot.DbBotItemCount;
import com.btxtech.game.services.common.CrudServiceHelper;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.uiservices.CrudTableHelper;
import com.btxtech.game.wicket.uiservices.RectanglePanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 25.09.2010
 * Time: 14:04:26
 */
public class BotEditor extends WebPage {
    @SpringBean
    private BotService botService;
    @SpringBean
    private UserService userService;
    @SpringBean
    private ItemService itemService;
    private Log log = LogFactory.getLog(BotEditor.class);

    public BotEditor(final DbBotConfig dbBotConfig) {
        add(new FeedbackPanel("msgs"));

        Form<DbBotConfig> form = new Form<DbBotConfig>("from", new CompoundPropertyModel<DbBotConfig>(dbBotConfig));
        add(form);
        form.add(new TextField("actionDelay"));
        form.add(new RectanglePanel("core"));
        form.add(new TextField("coreSuperiority"));
        form.add(new RectanglePanel("realm"));
        form.add(new TextField("realmSuperiority"));

        new CrudTableHelper<DbBotItemCount>("baseFundamental", null, "createBaseFundamentalItem", false, form, false) {

            @Override
            protected CrudServiceHelper<DbBotItemCount> getCrudServiceHelper() {
                return dbBotConfig.getBaseFundamentalCrudServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbBotItemCount> item) {
                item.add(new TextField<Integer>("itemTypeId", new IModel<Integer>() {

                    @Override
                    public Integer getObject() {
                        DbBaseItemType itemType = item.getModelObject().getBaseItemType();
                        if (itemType != null) {
                            return itemType.getId();
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public void setObject(Integer id) {
                        if (id != null) {
                            try {
                                item.getModelObject().setBaseItemType((DbBaseItemType) itemService.getDbItemType(id));
                            } catch (Throwable t) {
                                log.error("", t);
                                error(t.getMessage());
                            }
                        } else {
                            item.getModelObject().setBaseItemType(null);
                        }
                    }

                    @Override
                    public void detach() {
                        //Ignore
                    }
                }, Integer.class));
                item.add(new TextField("count"));
            }
        };

        new CrudTableHelper<DbBotItemCount>("baseBuildup", null, "createBaseBuildupItem", false, form, false) {

            @Override
            protected CrudServiceHelper<DbBotItemCount> getCrudServiceHelper() {
                return dbBotConfig.getBaseBuildupCrudServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbBotItemCount> item) {
                item.add(new TextField<Integer>("itemTypeId", new IModel<Integer>() {

                    @Override
                    public Integer getObject() {
                        DbBaseItemType itemType = item.getModelObject().getBaseItemType();
                        if (itemType != null) {
                            return itemType.getId();
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public void setObject(Integer id) {
                        if (id != null) {
                            try {
                                item.getModelObject().setBaseItemType((DbBaseItemType) itemService.getDbItemType(id));
                            } catch (Throwable t) {
                                log.error("", t);
                                error(t.getMessage());
                            }
                        } else {
                            item.getModelObject().setBaseItemType(null);
                        }
                    }

                    @Override
                    public void detach() {
                        //Ignore
                    }
                }, Integer.class));
                item.add(new TextField("count"));
            }
        };

        new CrudTableHelper<DbBotItemCount>("defence", null, "createDefenceItem", false, form, false) {

            @Override
            protected CrudServiceHelper<DbBotItemCount> getCrudServiceHelper() {
                return dbBotConfig.getDefenceCrudServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbBotItemCount> item) {
                item.add(new TextField<Integer>("itemTypeId", new IModel<Integer>() {

                    @Override
                    public Integer getObject() {
                        DbBaseItemType itemType = item.getModelObject().getBaseItemType();
                        if (itemType != null) {
                            return itemType.getId();
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public void setObject(Integer id) {
                        if (id != null) {
                            try {
                                item.getModelObject().setBaseItemType((DbBaseItemType) itemService.getDbItemType(id));
                            } catch (Throwable t) {
                                log.error("", t);
                                error(t.getMessage());
                            }
                        } else {
                            item.getModelObject().setBaseItemType(null);
                        }
                    }

                    @Override
                    public void detach() {
                        //Ignore
                    }
                }, Integer.class));
                item.add(new TextField("count"));
            }
        };

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                botService.getDbBotConfigCrudServiceHelper().updateDbChild(dbBotConfig);
            }
        });
        form.add(new Button("back") {

            @Override
            public void onSubmit() {
                setResponsePage(BotTable.class);
            }
        });

    }
}
