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

import com.btxtech.game.services.common.CrudServiceHelper;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.tutorial.DbItemTypeAndPosition;
import com.btxtech.game.services.tutorial.DbStepConfig;
import com.btxtech.game.services.tutorial.DbTaskConfig;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.wicket.pages.mgmt.tutorial.condition.ConditionWrapperPanel;
import com.btxtech.game.wicket.uiservices.CrudTableHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 27.07.2010
 * Time: 23:29:54
 */
public class StepEditor extends WebPage {
    @SpringBean
    private TutorialService tutorialService;
    @SpringBean
    private ItemService itemService;
    private Log log = LogFactory.getLog(StepEditor.class);

    public StepEditor(final DbTutorialConfig dbTutorialConfig, final DbStepConfig dbStepConfig) {
        add(new FeedbackPanel("msgs"));

        Form<DbTaskConfig> form = new Form<DbTaskConfig>("stepForm", new CompoundPropertyModel<DbTaskConfig>(dbStepConfig));
        add(form);

        form.add(new ConditionWrapperPanel("abstractConditionConfig", dbStepConfig));
        form.add(new TextArea("description"));
        form.add(new ResourceHintPanel("resourceHint", dbStepConfig.getDbResourceHintConfig()));
        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                tutorialService.getDbTutorialCrudServiceHelper().updateDbChild(dbTutorialConfig);
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