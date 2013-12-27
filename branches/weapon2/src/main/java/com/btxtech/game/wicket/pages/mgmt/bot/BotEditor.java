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

import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.bot.DbBotEnragementStateConfig;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CrudListChildTableHelper;
import com.btxtech.game.wicket.uiservices.RegionPanel;
import com.btxtech.game.wicket.uiservices.RuModel;
import com.btxtech.game.wicket.uiservices.TerrainLinkHelper;
import com.btxtech.game.wicket.uiservices.TimeSelector;
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
public class BotEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbBotConfig> dbBotConfigRuServiceHelper;

    public BotEditor(DbBotConfig dbBotConfig, final TerrainLinkHelper terrainLinkHelper) {

        add(new FeedbackPanel("msgs"));

        final Form<DbBotConfig> form = new Form<>("from", new CompoundPropertyModel<DbBotConfig>(new RuModel<DbBotConfig>(dbBotConfig, DbBotConfig.class) {
            @Override
            protected RuServiceHelper<DbBotConfig> getRuServiceHelper() {
                return dbBotConfigRuServiceHelper;
            }
        }));
        add(form);
        form.add(new TextField("actionDelay"));
        form.add(new RegionPanel("realm", terrainLinkHelper) {
            @Override
            protected void updateDependentModel() {
                dbBotConfigRuServiceHelper.updateDbEntity(form.getModelObject());
            }
        });
        form.add(new CheckBox("attacksOtherBots"));


        new CrudListChildTableHelper<DbBotConfig, DbBotEnragementStateConfig>("enragement", null, "createEnragementConfig", true, form, true) {
            @Override
            protected RuServiceHelper<DbBotConfig> getRuServiceHelper() {
                return dbBotConfigRuServiceHelper;
            }

            @Override
            protected DbBotConfig getParent() {
                return form.getModelObject();
            }

            @Override
            protected CrudListChildServiceHelper<DbBotEnragementStateConfig> getCrudListChildServiceHelperImpl() {
                return getParent().getEnrageStateCrud();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbBotEnragementStateConfig> item) {
                super.extendedPopulateItem(item);
                item.add(new TextField("enrageUpKills"));
            }

            @Override
            protected void onEditSubmit(DbBotEnragementStateConfig dbBotEnragementStateConfig) {
                setResponsePage(new BotItemEditor(dbBotEnragementStateConfig, terrainLinkHelper));
            }
        };

        form.add(new TimeSelector("minInactiveMs", TimeSelector.Converter.MINUTES_TO_MS));
        form.add(new TimeSelector("maxInactiveMs", TimeSelector.Converter.MINUTES_TO_MS));
        form.add(new TimeSelector("minActiveMs", TimeSelector.Converter.MINUTES_TO_MS));
        form.add(new TimeSelector("maxActiveMs", TimeSelector.Converter.MINUTES_TO_MS));


        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                dbBotConfigRuServiceHelper.updateDbEntity(form.getModelObject());
            }
        });

    }
}
