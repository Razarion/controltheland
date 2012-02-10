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

import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbQuestHub;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.BaseItemTypePanel;
import com.btxtech.game.wicket.uiservices.CrudListChildTableHelper;
import com.btxtech.game.wicket.uiservices.RuModel;
import com.btxtech.game.wicket.uiservices.TerritoryPanel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.settings.TinyMCESettings;

/**
 * User: beat
 * Date: 14.05.2010
 * Time: 14:53:19
 */
public class DbQuestHubEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbQuestHub> ruServiceHelper;

    public DbQuestHubEditor(DbQuestHub dbQuestHub) {
        add(new FeedbackPanel("msgs"));

        final Form<DbQuestHub> form = new Form<DbQuestHub>("form", new CompoundPropertyModel<DbQuestHub>(new RuModel<DbQuestHub>(dbQuestHub, DbQuestHub.class) {

            @Override
            protected RuServiceHelper<DbQuestHub> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        TextArea<String> contentArea = new TextArea<String>("html");
        TinyMCESettings tinyMCESettings = new TinyMCESettings(TinyMCESettings.Theme.advanced);
        tinyMCESettings.add(wicket.contrib.tinymce.settings.Button.link, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
        tinyMCESettings.add(wicket.contrib.tinymce.settings.Button.unlink, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
        contentArea.add(new TinyMceBehavior(tinyMCESettings));
        form.add(contentArea);
        
        form.add(new CheckBox("realBaseRequired"));
        form.add(new BaseItemTypePanel("startItemType"));
        form.add(new TextField("startItemFreeRange"));
        form.add(new TerritoryPanel("startTerritory"));
        form.add(new TextField("startMoney"));

        new CrudListChildTableHelper<DbQuestHub, DbLevel>("levels", null, "createLevel", true, form, true) {
            @Override
            protected RuServiceHelper<DbQuestHub> getRuServiceHelper() {
                return ruServiceHelper;
            }

            @Override
            protected DbQuestHub getParent() {
                return form.getModelObject();
            }

            @Override
            protected CrudListChildServiceHelper<DbLevel> getCrudListChildServiceHelperImpl() {
                return getParent().getLevelCrud();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbLevel> item) {
                displayId(item);
                super.extendedPopulateItem(item);
            }

            @Override
            protected void onEditSubmit(DbLevel dbLevel) {
                setResponsePage(new DbLevelEditor(dbLevel));
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
}
