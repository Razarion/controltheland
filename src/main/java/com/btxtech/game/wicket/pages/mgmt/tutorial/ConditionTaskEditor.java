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

import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.tutorial.DbConditionTaskConfig;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.pages.mgmt.condition.ConditionConfigPanel;
import com.btxtech.game.wicket.uiservices.RuModel;
import com.btxtech.game.wicket.uiservices.TerrainLinkHelper;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 23.07.2010
 * Time: 23:29:54
 */
public class ConditionTaskEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbConditionTaskConfig> ruTaskServiceHelper;

    public ConditionTaskEditor(DbConditionTaskConfig dbTaskConfig, final TerrainLinkHelper terrainLinkHelper) {
        add(new FeedbackPanel("msgs"));

        final Form<DbConditionTaskConfig> form = new Form<>("taskForm", new CompoundPropertyModel<>(new RuModel<DbConditionTaskConfig>(dbTaskConfig, DbConditionTaskConfig.class) {
            @Override
            protected RuServiceHelper<DbConditionTaskConfig> getRuServiceHelper() {
                return ruTaskServiceHelper;
            }
        }));
        add(form);
        form.add(new CommonTaskEditorPanel("commonTaskEditorPanel", terrainLinkHelper));
        form.add(new ConditionConfigPanel("conditionConfig", terrainLinkHelper));

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruTaskServiceHelper.updateDbEntity((DbConditionTaskConfig) form.getDefaultModelObject());
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