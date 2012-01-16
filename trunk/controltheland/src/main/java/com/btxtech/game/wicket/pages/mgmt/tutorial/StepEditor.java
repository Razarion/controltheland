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
import com.btxtech.game.services.tutorial.DbStepConfig;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.pages.mgmt.condition.ConditionConfigPanel;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 27.07.2010
 * Time: 23:29:54
 */
public class StepEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbStepConfig> ruStepServiceHelper;

    public StepEditor(DbStepConfig dbStepConfig) {
        add(new FeedbackPanel("msgs"));

        final Form<DbStepConfig> form = new Form<DbStepConfig>("stepForm", new CompoundPropertyModel<DbStepConfig>(new RuModel<DbStepConfig>(dbStepConfig, DbStepConfig.class) {
            @Override
            protected RuServiceHelper<DbStepConfig> getRuServiceHelper() {
                return ruStepServiceHelper;
            }
        }));
        add(form);

        form.add(new ConditionConfigPanel("conditionConfig"));

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruStepServiceHelper.updateDbEntity((DbStepConfig) form.getDefaultModelObject());
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