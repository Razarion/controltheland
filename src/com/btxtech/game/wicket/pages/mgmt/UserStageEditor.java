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

package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.utg.DbUserStage;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.settings.TinyMCESettings;

/**
 * User: beat
 * Date: 19.12.2010
 * Time: 15:23:18
 */
public class UserStageEditor extends WebPage {
    @SpringBean
    private UserGuidanceService userGuidanceService;

    public UserStageEditor(final DbUserStage dbUserStage) {
        add(new FeedbackPanel("msgs"));

        // Form
        Form<DbUserStage> form = new Form<DbUserStage>("form", new CompoundPropertyModel<DbUserStage>(dbUserStage));
        add(form);

        // Text editor
        TextArea<String> contentArea = new TextArea<String>("html");
        TinyMCESettings tinyMCESettings = new TinyMCESettings(TinyMCESettings.Theme.advanced);
        tinyMCESettings.add(wicket.contrib.tinymce.settings.Button.link, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
        tinyMCESettings.add(wicket.contrib.tinymce.settings.Button.unlink, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
        tinyMCESettings.add(wicket.contrib.tinymce.settings.Button.fontselect, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
        tinyMCESettings.add(wicket.contrib.tinymce.settings.Button.fontsizeselect, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
        tinyMCESettings.add(wicket.contrib.tinymce.settings.Button.forecolor, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
        tinyMCESettings.add(wicket.contrib.tinymce.settings.Button.backcolor, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
        contentArea.add(new TinyMceBehavior(tinyMCESettings));
        form.add(contentArea);

        // Save button
        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                // TODO userGuidanceService.getUserStageCrudServiceHelper().updateDbChild(dbUserStage);
            }
        });

        // Back button
        form.add(new Button("back") {

            @Override
            public void onSubmit() {
                setResponsePage(UserStageTable.class);
            }
        });

    }
}
