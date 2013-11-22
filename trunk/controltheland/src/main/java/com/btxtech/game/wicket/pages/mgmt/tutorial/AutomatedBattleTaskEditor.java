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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.tutorial.DbAutomatedBattleTaskConfig;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.pages.mgmt.items.BaseItemTypeEditor;
import com.btxtech.game.wicket.uiservices.BaseItemTypePanel;
import com.btxtech.game.wicket.uiservices.IndexPanel;
import com.btxtech.game.wicket.uiservices.RuModel;
import com.btxtech.game.wicket.uiservices.TerrainLinkHelper;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 23.07.2010
 * Time: 23:29:54
 */
public class AutomatedBattleTaskEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbAutomatedBattleTaskConfig> ruTaskServiceHelper;

    public AutomatedBattleTaskEditor(DbAutomatedBattleTaskConfig dbTaskConfig, final TerrainLinkHelper terrainLinkHelper) {
        add(new FeedbackPanel("msgs"));

        final Form<DbAutomatedBattleTaskConfig> form = new Form<>("taskForm", new CompoundPropertyModel<>(new RuModel<DbAutomatedBattleTaskConfig>(dbTaskConfig, DbAutomatedBattleTaskConfig.class) {
            @Override
            protected RuServiceHelper<DbAutomatedBattleTaskConfig> getRuServiceHelper() {
                return ruTaskServiceHelper;
            }
        }));
        add(form);
        form.add(new CommonTaskEditorPanel("commonTaskEditorPanel", terrainLinkHelper));

        form.add(new TextField<String>("attackBotName"));
        form.add(new IndexPanel("attackBotItemPosition"));
        form.add(new BaseItemTypePanel("attackBotItemType"));
        form.add(new TextField("attackBotHealthFactor"));
        form.add(new BaseItemTypePanel("targetItemType"));

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruTaskServiceHelper.updateDbEntity((DbAutomatedBattleTaskConfig) form.getDefaultModelObject());
            }
        });
        form.add(new Button("back") {

            @Override
            public void onSubmit() {
                setResponsePage(TutorialTable.class);
            }
        });


    }

}