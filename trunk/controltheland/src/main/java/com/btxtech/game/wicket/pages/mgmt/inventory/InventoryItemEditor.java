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

package com.btxtech.game.wicket.pages.mgmt.inventory;

import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.inventory.DbInventoryArtifactCount;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.BaseItemTypePanel;
import com.btxtech.game.wicket.uiservices.CrudChildTableHelper;
import com.btxtech.game.wicket.uiservices.InventoryArtifactPanel;
import com.btxtech.game.wicket.uiservices.LevelPanel;
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
 * Date: 23.07.2010
 * Time: 23:29:54
 */
public class InventoryItemEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbInventoryItem> ruTaskServiceHelper;

    public InventoryItemEditor(DbInventoryItem dbInventoryItem) {
        add(new FeedbackPanel("msgs"));

        final Form<DbInventoryItem> form = new Form<>("inventoryItemForm", new CompoundPropertyModel<DbInventoryItem>(new RuModel<DbInventoryItem>(dbInventoryItem, DbInventoryItem.class) {
            @Override
            protected RuServiceHelper<DbInventoryItem> getRuServiceHelper() {
                return ruTaskServiceHelper;
            }
        }));
        add(form);
        form.add(new TextField("goldAmount"));
        form.add(new LevelPanel("goldLevel"));
        form.add(new BaseItemTypePanel("dbBaseItemType"));
        form.add(new TextField("baseItemTypeCount"));
        form.add(new TextField("itemFreeRange"));

        new CrudChildTableHelper<DbInventoryItem, DbInventoryArtifactCount>("artifacts", null, "createArtifact", false, form, false) {
            @Override
            protected RuServiceHelper<DbInventoryItem> getRuServiceHelper() {
                return ruTaskServiceHelper;
            }

            @Override
            protected DbInventoryItem getParent() {
                return (DbInventoryItem) form.getDefaultModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbInventoryArtifactCount> getCrudChildServiceHelperImpl() {
                return ((DbInventoryItem) form.getDefaultModelObject()).getArtifactCountCrud();
            }

            @Override
            protected void extendedPopulateItem(Item<DbInventoryArtifactCount> dbTaskAllowedItemItem) {
                dbTaskAllowedItemItem.add(new TextField("count"));
                dbTaskAllowedItemItem.add(new InventoryArtifactPanel("dbInventoryArtifact"));
            }
        };

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruTaskServiceHelper.updateDbEntity((DbInventoryItem) form.getDefaultModelObject());
            }
        });
        form.add(new Button("back") {

            @Override
            public void onSubmit() {
                setResponsePage(InventoryEditor.class);
            }
        });


    }

}