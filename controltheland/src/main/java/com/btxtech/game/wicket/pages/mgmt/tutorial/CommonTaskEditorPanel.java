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

package com.btxtech.game.wicket.pages.mgmt.tutorial;

import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.jsre.client.utg.tip.GameTipConfig;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.tutorial.DbAbstractTaskConfig;
import com.btxtech.game.services.tutorial.DbItemTypeAndPosition;
import com.btxtech.game.services.tutorial.DbTaskAllowedItem;
import com.btxtech.game.services.tutorial.DbTaskBotToStop;
import com.btxtech.game.wicket.pages.mgmt.bot.BotEditor;
import com.btxtech.game.wicket.uiservices.BaseItemTypePanel;
import com.btxtech.game.wicket.uiservices.BotPanel;
import com.btxtech.game.wicket.uiservices.CrudChildTableHelper;
import com.btxtech.game.wicket.uiservices.I18nStringEditor;
import com.btxtech.game.wicket.uiservices.IndexPanel;
import com.btxtech.game.wicket.uiservices.ItemTypePanel;
import com.btxtech.game.wicket.uiservices.ResourceItemTypePanel;
import com.btxtech.game.wicket.uiservices.TerrainLinkHelper;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 23.07.2010
 * Time: 23:29:54
 */
public class CommonTaskEditorPanel extends Panel {
    @SpringBean
    private RuServiceHelper<DbAbstractTaskConfig> ruTaskServiceHelper;

    public CommonTaskEditorPanel(String id, final TerrainLinkHelper terrainLinkHelper) {
        super(id);
        setDefaultModel(new CompoundPropertyModel<>(new AbstractReadOnlyModel<DbAbstractTaskConfig>() {

            @Override
            public DbAbstractTaskConfig getObject() {
                // TODO Why does not wicket do this?
                return (DbAbstractTaskConfig) getParent().getDefaultModelObject();
            }
        }));
        add(new I18nStringEditor("I18nTitle"));
        add(new CheckBox("clearGame"));
        add(new IndexPanel("scroll"));
        add(new TextField("money"));
        add(new TextField("maxMoney"));
        add(new TextField("houseCount"));
        add(new DropDownChoice<>("radarMode", RadarMode.getList()));

        new CrudChildTableHelper<DbAbstractTaskConfig, DbTaskAllowedItem>("allowedItemTable", null, "createAllowedItem", false, this, false) {
            @Override
            protected RuServiceHelper<DbAbstractTaskConfig> getRuServiceHelper() {
                return ruTaskServiceHelper;
            }

            @Override
            protected DbAbstractTaskConfig getParent() {
                return (DbAbstractTaskConfig) CommonTaskEditorPanel.this.getDefaultModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbTaskAllowedItem> getCrudChildServiceHelperImpl() {
                return ((DbAbstractTaskConfig) CommonTaskEditorPanel.this.getDefaultModelObject()).getAllowedItemHelper();
            }

            @Override
            protected void extendedPopulateItem(Item<DbTaskAllowedItem> dbTaskAllowedItemItem) {
                dbTaskAllowedItemItem.add(new BaseItemTypePanel("dbBaseItemType"));
                dbTaskAllowedItemItem.add(new TextField("count"));
            }
        };


        new CrudChildTableHelper<DbAbstractTaskConfig, DbItemTypeAndPosition>("itemTable", null, "createItem", false, this, false) {
            @Override
            protected RuServiceHelper<DbAbstractTaskConfig> getRuServiceHelper() {
                return ruTaskServiceHelper;
            }

            @Override
            protected DbAbstractTaskConfig getParent() {
                return (DbAbstractTaskConfig) CommonTaskEditorPanel.this.getDefaultModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbItemTypeAndPosition> getCrudChildServiceHelperImpl() {
                return ((DbAbstractTaskConfig) CommonTaskEditorPanel.this.getDefaultModelObject()).getItemCrudServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbItemTypeAndPosition> dbTaskConfigItem) {
                dbTaskConfigItem.add(new ItemTypePanel("itemType"));
                dbTaskConfigItem.add(new IndexPanel("position"));
                dbTaskConfigItem.add(new TextField("angel"));
            }
        };

        // Bot
        new CrudChildTableHelper<DbAbstractTaskConfig, DbBotConfig>("bots", null, "createBot", true, this, false) {

            @Override
            protected CrudChildServiceHelper<DbBotConfig> getCrudChildServiceHelperImpl() {
                return getParent().getBotCrud();
            }

            @Override
            protected void onEditSubmit(DbBotConfig dbBotConfig) {
                setResponsePage(new BotEditor(dbBotConfig, terrainLinkHelper));
            }

            @Override
            protected void extendedPopulateItem(Item<DbBotConfig> dbBotConfigItem) {
                displayId(dbBotConfigItem);
                super.extendedPopulateItem(dbBotConfigItem);
            }


            @Override
            protected RuServiceHelper<DbAbstractTaskConfig> getRuServiceHelper() {
                return ruTaskServiceHelper;
            }

            @Override
            protected DbAbstractTaskConfig getParent() {
                return (DbAbstractTaskConfig) CommonTaskEditorPanel.this.getDefaultModelObject();
            }
        };

        // Bot to stop
        new CrudChildTableHelper<DbAbstractTaskConfig, DbTaskBotToStop>("botsToStop", null, "createBotToStop", false, this, false) {

            @Override
            protected CrudChildServiceHelper<DbTaskBotToStop> getCrudChildServiceHelperImpl() {
                return getParent().getBotToStopCrud();
            }

            @Override
            protected void extendedPopulateItem(Item<DbTaskBotToStop> dbBotConfigItem) {
                displayId(dbBotConfigItem);
                dbBotConfigItem.add(new BotPanel("dbBotConfig"));
            }


            @Override
            protected RuServiceHelper<DbAbstractTaskConfig> getRuServiceHelper() {
                return ruTaskServiceHelper;
            }

            @Override
            protected DbAbstractTaskConfig getParent() {
                return (DbAbstractTaskConfig) CommonTaskEditorPanel.this.getDefaultModelObject();
            }
        };

        add(new I18nStringEditor("i18nStorySplashTitle"));
        add(new I18nStringEditor("i18nStorySplashText"));
        add(new I18nStringEditor("i18nPraiseSplashTitle"));
        add(new I18nStringEditor("i18nPraiseSplashText"));

        add(new DropDownChoice<>("tip", GameTipConfig.Tip.getValuesIncludingNull()));
        add(new BaseItemTypePanel("tipActor"));
        add(new BaseItemTypePanel("tipToBeBuilt"));
        add(new ResourceItemTypePanel("tipResource"));
        add(new IndexPanel("tipTerrainPositionHint"));
    }

}