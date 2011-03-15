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
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.uiservices.BaseItemTypePanel;
import com.btxtech.game.wicket.uiservices.CrudChildTableHelper;
import com.btxtech.game.wicket.uiservices.RectanglePanel;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
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
    @SpringBean
    private RuServiceHelper<DbBotConfig> dbBotConfigRuServiceHelper;


    public BotEditor(DbBotConfig dbBotConfig) {

        add(new FeedbackPanel("msgs"));

        final Form<DbBotConfig> form = new Form<DbBotConfig>("from", new CompoundPropertyModel<DbBotConfig>(new RuModel<DbBotConfig>(dbBotConfig, DbBotConfig.class) {
            @Override
            protected RuServiceHelper<DbBotConfig> getRuServiceHelper() {
                return dbBotConfigRuServiceHelper;
            }
        }));
        add(form);
        form.add(new TextField("actionDelay"));
        form.add(new RectanglePanel("core"));
        form.add(new TextField("coreSuperiority"));
        form.add(new RectanglePanel("realm"));
        form.add(new TextField("realmSuperiority"));

        new CrudChildTableHelper<DbBotConfig, DbBotItemCount>("baseFundamental", null, "createBaseFundamentalItem", false, form, false) {

            @Override
            protected void extendedPopulateItem(final Item<DbBotItemCount> item) {
                item.add(new BaseItemTypePanel("baseItemType"));
                item.add(new TextField("count"));
            }

            @Override
            protected RuServiceHelper<DbBotConfig> getRuServiceHelper() {
                return dbBotConfigRuServiceHelper;
            }

            @Override
            protected DbBotConfig getParent() {
                return form.getModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbBotItemCount> getCrudChildServiceHelperImpl() {
                return getParent().getBaseFundamentalCrudServiceHelper();
            }
        };

        new CrudChildTableHelper<DbBotConfig, DbBotItemCount>("baseBuildup", null, "createBaseBuildupItem", false, form, false) {
            @Override
            protected RuServiceHelper<DbBotConfig> getRuServiceHelper() {
                return dbBotConfigRuServiceHelper;
            }

            @Override
            protected DbBotConfig getParent() {
                return form.getModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbBotItemCount> getCrudChildServiceHelperImpl() {
                return getParent().getBaseBuildupCrudServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbBotItemCount> item) {
                item.add(new BaseItemTypePanel("baseItemType"));
                item.add(new TextField("count"));
            }
        };

        new CrudChildTableHelper<DbBotConfig, DbBotItemCount>("defence", null, "createDefenceItem", false, form, false) {
            @Override
            protected void extendedPopulateItem(final Item<DbBotItemCount> item) {
                item.add(new BaseItemTypePanel("baseItemType"));
                item.add(new TextField("count"));
            }

            @Override
            protected RuServiceHelper<DbBotConfig> getRuServiceHelper() {
                return dbBotConfigRuServiceHelper;
            }

            @Override
            protected DbBotConfig getParent() {
                return form.getModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbBotItemCount> getCrudChildServiceHelperImpl() {
                return getParent().getDefenceCrudServiceHelper();
            }
        };

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                dbBotConfigRuServiceHelper.updateDbEntity(form.getModelObject());
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
