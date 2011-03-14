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
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.uiservices.BaseItemTypePanel;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import com.btxtech.game.wicket.uiservices.RectanglePanel;
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
public class BotEditor extends MgmtWebPage {
    @SpringBean
    private BotService botService;
    @SpringBean
    private UserService userService;
    @SpringBean
    private ItemService itemService;

    public BotEditor(DbBotConfig dbBotConfig) {
        final int dbBotConfigId = dbBotConfig.getId();

        add(new FeedbackPanel("msgs"));

        final Form<DbBotConfig> form = new Form<DbBotConfig>("from", new CompoundPropertyModel<DbBotConfig>(new IModel<DbBotConfig>() {
            private DbBotConfig dbBotConfig;

            @Override
            public DbBotConfig getObject() {
                if (dbBotConfig == null) {
                    dbBotConfig = botService.getDbBotConfigCrudServiceHelper().readDbChild(dbBotConfigId);
                }
                return dbBotConfig;
            }

            @Override
            public void setObject(DbBotConfig object) {
                // Ignore
            }

            @Override
            public void detach() {
                dbBotConfig = null;
            }
        }));
        add(form);
        form.add(new TextField("actionDelay"));
        form.add(new RectanglePanel("core"));
        form.add(new TextField("coreSuperiority"));
        form.add(new RectanglePanel("realm"));
        form.add(new TextField("realmSuperiority"));

        new CrudRootTableHelper<DbBotItemCount>("baseFundamental", null, "createBaseFundamentalItem", false, form, false) {

            @Override
            protected CrudServiceHelper<DbBotItemCount> getCrudRootServiceHelperImpl() {
                return ((DbBotConfig) form.getDefaultModelObject()).getBaseFundamentalCrudServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbBotItemCount> item) {
                item.add(new BaseItemTypePanel("baseItemType"));
                item.add(new TextField("count"));
            }
        };

        new CrudRootTableHelper<DbBotItemCount>("baseBuildup", null, "createBaseBuildupItem", false, form, false) {

            @Override
            protected CrudServiceHelper<DbBotItemCount> getCrudRootServiceHelperImpl() {
                return ((DbBotConfig) form.getDefaultModelObject()).getBaseFundamentalCrudServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbBotItemCount> item) {
                item.add(new BaseItemTypePanel("baseItemType"));
                item.add(new TextField("count"));
            }
        };

        new CrudRootTableHelper<DbBotItemCount>("defence", null, "createDefenceItem", false, form, false) {

            @Override
            protected CrudServiceHelper<DbBotItemCount> getCrudRootServiceHelperImpl() {
                return ((DbBotConfig) form.getDefaultModelObject()).getBaseFundamentalCrudServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbBotItemCount> item) {
                item.add(new BaseItemTypePanel("baseItemType"));
                item.add(new TextField("count"));
            }
        };

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                // TODO botService.getDbBotConfigCrudServiceHelper().updateDbChild((DbBotConfig) form.getDefaultModelObject());
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
