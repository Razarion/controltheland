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

import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.utg.DbAbstractLevel;
import com.btxtech.game.services.utg.DbRealGameLevel;
import com.btxtech.game.services.utg.DbSimulationLevel;
import com.btxtech.game.wicket.uiservices.RuModel;
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
 * Date: 14.05.2010
 * Time: 14:53:19
 */
public class DbLevelEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbAbstractLevel> ruServiceHelper;

    public DbLevelEditor(DbAbstractLevel dbAbstractLevel) {
        add(new FeedbackPanel("msgs"));

        final Form<DbAbstractLevel> form = new Form<DbAbstractLevel>("form", new CompoundPropertyModel<DbAbstractLevel>(new RuModel<DbAbstractLevel>(dbAbstractLevel, DbAbstractLevel.class) {

            @Override
            protected RuServiceHelper<DbAbstractLevel> getRuServiceHelper() {
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

        if (dbAbstractLevel instanceof DbRealGameLevel) {
            form.add(new RealGameLevelEditor("levelDetail"));
        } else if (dbAbstractLevel instanceof DbSimulationLevel) {
            form.add(new SimulationLevelEditor("levelDetail"));
        } else {
            throw new IllegalArgumentException("Unknown level: " + dbAbstractLevel);
        }
        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });
        form.add(new Button("back") {
            @Override
            public void onSubmit() {
                setResponsePage(DbLevelTable.class);
            }
        });
    }

    public RuServiceHelper<DbAbstractLevel> getRuServiceHelper() {
        return ruServiceHelper;
    }
}
