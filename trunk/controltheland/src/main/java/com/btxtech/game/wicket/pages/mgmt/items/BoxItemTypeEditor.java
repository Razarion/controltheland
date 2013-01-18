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

package com.btxtech.game.wicket.pages.mgmt.items;

import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemTypePossibility;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CrudChildTableHelper;
import com.btxtech.game.wicket.uiservices.I18nStringEditor;
import com.btxtech.game.wicket.uiservices.I18nStringWYSIWYGEditor;
import com.btxtech.game.wicket.uiservices.InventoryArtifactPanel;
import com.btxtech.game.wicket.uiservices.InventoryItemPanel;
import com.btxtech.game.wicket.uiservices.MinutePanel;
import com.btxtech.game.wicket.uiservices.PercentPanel;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;

/**
 * User: beat
 * Date: Sep 4, 2009
 * Time: 10:35:35 PM
 */
public class BoxItemTypeEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbBoxItemType> ruServiceHelper;

    public BoxItemTypeEditor(DbBoxItemType dbBoxItemType) {
        add(new FeedbackPanel("msgs"));

        final Form<DbBoxItemType> form = new Form<>("boxItemTypeForm", new CompoundPropertyModel<DbBoxItemType>(new RuModel<DbBoxItemType>(dbBoxItemType, DbBoxItemType.class) {
            @Override
            protected RuServiceHelper<DbBoxItemType> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);
        form.add(new ItemTypeImagePanel("itemTypeImagePanel", dbBoxItemType.getId()));
        form.add(new I18nStringEditor("dbI18nName"));
        form.add(new I18nStringWYSIWYGEditor("dbI18nDescription"));
        form.add(new DropDownChoice<>("terrainType", Arrays.asList(TerrainType.values())));
        form.add(new MinutePanel("ttl"));

        new CrudChildTableHelper<DbBoxItemType, DbBoxItemTypePossibility>("possibilities", null, "createPossibility", false, form, false) {

            @Override
            protected RuServiceHelper<DbBoxItemType> getRuServiceHelper() {
                return ruServiceHelper;
            }

            @Override
            protected DbBoxItemType getParent() {
                return (DbBoxItemType) form.getDefaultModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbBoxItemTypePossibility> getCrudChildServiceHelperImpl() {
                return ((DbBoxItemType) form.getDefaultModelObject()).getBoxPossibilityCrud();
            }

            @Override
            protected void extendedPopulateItem(Item<DbBoxItemTypePossibility> dbBoxItemTypePossibilityItem) {
                dbBoxItemTypePossibilityItem.add(new PercentPanel("possibility"));
                dbBoxItemTypePossibilityItem.add(new InventoryItemPanel("dbInventoryItem"));
                dbBoxItemTypePossibilityItem.add(new InventoryArtifactPanel("dbInventoryArtifact"));
                dbBoxItemTypePossibilityItem.add(new TextField<Integer>("razarion"));

            }
        };
        form.add(new Button("editSounds") {
            @Override
            public void onSubmit() {
                setResponsePage(new ItemTypeSoundEditor(form.getModelObject()));
            }
        });
        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
                setResponsePage(ItemTypeTable.class);
            }
        });
        form.add(new Button("cancel") {
            @Override
            public void onSubmit() {
                setResponsePage(ItemTypeTable.class);
            }
        });
        add(form);
    }
}