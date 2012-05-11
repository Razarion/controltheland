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

package com.btxtech.game.wicket.pages.mgmt.level;

import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.utg.DbItemTypeLimitation;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.BaseItemTypePanel;
import com.btxtech.game.wicket.uiservices.CrudChildTableHelper;
import com.btxtech.game.wicket.uiservices.RuModel;
import com.btxtech.game.wicket.uiservices.WysiwygEditor;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 14.05.2010
 * Time: 14:53:19
 */
public class DbLevelEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbLevel> ruServiceHelper;

    public DbLevelEditor(DbLevel dbLevel) {
        add(new FeedbackPanel("msgs"));

        final Form<DbLevel> form = new Form<DbLevel>("form", new CompoundPropertyModel<DbLevel>(new RuModel<DbLevel>(dbLevel, DbLevel.class) {

            @Override
            protected RuServiceHelper<DbLevel> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new WysiwygEditor("html"));
        form.add(new TextField("xp"));
        // Scope
        form.add(new TextField("houseSpace"));
        form.add(new TextField("itemSellFactor"));
        form.add(new TextField("maxMoney"));
        form.add(new DropDownChoice<RadarMode>("radarMode", RadarMode.getList()));

        new CrudChildTableHelper<DbLevel, DbItemTypeLimitation>("itemTypeLimitation", null, "createItemTypeLimitation", false, form, false) {

            @Override
            protected void extendedPopulateItem(Item<DbItemTypeLimitation> dbItemTypeLimitationItem) {
                dbItemTypeLimitationItem.add(new BaseItemTypePanel("dbBaseItemType"));
                dbItemTypeLimitationItem.add(new TextField("count"));
            }

            @Override
            protected RuServiceHelper<DbLevel> getRuServiceHelper() {
                return ruServiceHelper;
            }

            @Override
            protected DbLevel getParent() {
                return form.getModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbItemTypeLimitation> getCrudChildServiceHelperImpl() {
                return getParent().getItemTypeLimitationCrud();
            }
        };

        new CrudChildTableHelper<DbLevel, DbLevelTask>("levelTasks", null, "createLevelTask", true, form, false) {

            @Override
            protected void extendedPopulateItem(Item<DbLevelTask> dbLevelTaskItem) {
                displayId(dbLevelTaskItem);
                super.extendedPopulateItem(dbLevelTaskItem);
            }

            @Override
            protected RuServiceHelper<DbLevel> getRuServiceHelper() {
                return ruServiceHelper;
            }

            @Override
            protected DbLevel getParent() {
                return form.getModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbLevelTask> getCrudChildServiceHelperImpl() {
                return getParent().getLevelTaskCrud();
            }

            @Override
            protected void onEditSubmit(DbLevelTask dbLevelTask) {
                setResponsePage(new DbLevelTaskEditor(dbLevelTask));
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
                setResponsePage(DbQuestHubTable.class);
            }
        });
    }

    public RuServiceHelper<DbLevel> getRuServiceHelper() {
        return ruServiceHelper;
    }
}
