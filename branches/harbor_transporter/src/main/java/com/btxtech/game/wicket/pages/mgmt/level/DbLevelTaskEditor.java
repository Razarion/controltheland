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

import com.btxtech.game.jsre.client.dialogs.quest.QuestTypeEnum;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.pages.mgmt.condition.ConditionConfigPanel;
import com.btxtech.game.wicket.uiservices.I18nStringEditor;
import com.btxtech.game.wicket.uiservices.I18nStringWYSIWYGEditor;
import com.btxtech.game.wicket.uiservices.RuModel;
import com.btxtech.game.wicket.uiservices.TerrainLinkHelper;
import com.btxtech.game.wicket.uiservices.TutorialPanel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;

/**
 * User: beat
 * Date: 14.05.2010
 * Time: 14:53:19
 */
public class DbLevelTaskEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbLevelTask> ruServiceHelper;

    public DbLevelTaskEditor(DbLevelTask dbLevelTask, TerrainLinkHelper terrainLinkHelper) {
        add(new FeedbackPanel("msgs"));

        final Form<DbLevelTask> form = new Form<>("form", new CompoundPropertyModel<DbLevelTask>(new RuModel<DbLevelTask>(dbLevelTask, DbLevelTask.class) {

            @Override
            protected RuServiceHelper<DbLevelTask> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new I18nStringEditor("i18nTitle"));
        form.add(new I18nStringWYSIWYGEditor("i18nDescription"));
        form.add(new DropDownChoice<>("questTypeEnum", Arrays.asList(QuestTypeEnum.values())));
        form.add(new TutorialPanel("dbTutorialConfig"));
        form.add(new ConditionConfigPanel("dbConditionConfig", terrainLinkHelper));
        form.add(new TextField("unlockCrystals"));
        // Reward
        form.add(new TextField("money"));
        form.add(new TextField("xp"));

        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });
        form.add(new Button("back") {
            @Override
            public void onSubmit() {
                setResponsePage(LevelTable.class);
            }
        });
    }
}
