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

package com.btxtech.game.wicket.pages.mgmt.bot;

import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.bot.DbBotEnragementStateConfig;
import com.btxtech.game.services.bot.DbBotItemConfig;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.BaseItemTypePanel;
import com.btxtech.game.wicket.uiservices.CrudChildTableHelper;
import com.btxtech.game.wicket.uiservices.RectanglePanel;
import com.btxtech.game.wicket.uiservices.RuModel;
import com.btxtech.game.wicket.uiservices.SecondPanel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
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
public class BotItemEditor extends MgmtWebPage {
    @SpringBean
    private BotService botService;
    @SpringBean
    private UserService userService;
    @SpringBean
    private ItemService itemService;
    @SpringBean
    private RuServiceHelper<DbBotEnragementStateConfig> ruServiceHelper;


    public BotItemEditor(DbBotEnragementStateConfig dbBotEnragementStateConfig) {

        add(new FeedbackPanel("msgs"));

        final Form<DbBotEnragementStateConfig> form = new Form<>("from", new CompoundPropertyModel<DbBotEnragementStateConfig>(new RuModel<DbBotEnragementStateConfig>(dbBotEnragementStateConfig, DbBotEnragementStateConfig.class) {
            @Override
            protected RuServiceHelper<DbBotEnragementStateConfig> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        new CrudChildTableHelper<DbBotEnragementStateConfig, DbBotItemConfig>("botItems", null, "createBaseBuildupItem", false, form, false) {
            @Override
            protected RuServiceHelper<DbBotEnragementStateConfig> getRuServiceHelper() {
                return ruServiceHelper;
            }

            @Override
            protected DbBotEnragementStateConfig getParent() {
                return form.getModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbBotItemConfig> getCrudChildServiceHelperImpl() {
                return getParent().getBotItemCrud();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbBotItemConfig> item) {
                item.add(new BaseItemTypePanel("baseItemType"));
                item.add(new TextField("count"));
                item.add(new CheckBox("createDirectly"));
                item.add(new RectanglePanel("region"));
                item.add(new CheckBox("moveRealmIfIdle"));
                item.add(new TextField("idleTtl"));
                item.add(new CheckBox("noRebuild"));
                item.add(new SecondPanel("rePopTime"));
            }
        };

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
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
