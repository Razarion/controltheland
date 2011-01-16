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

import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.pages.mgmt.condition.ConditionConfigPanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * Date: 14.05.2010
 * Time: 14:53:19
 */
public class DbLevelEditor extends WebPage {
    @SpringBean
    private UserGuidanceService userGuidanceService;
    @SpringBean
    private ItemService itemService;
    private Log log = LogFactory.getLog(DbLevelEditor.class);

    public DbLevelEditor(final DbLevel dbLevel) {
        add(new FeedbackPanel("msgs"));

        Form<DbLevel> form = new Form<DbLevel>("form", new CompoundPropertyModel<DbLevel>(dbLevel));
        add(form);

        TextArea<String> contentArea = new TextArea<String>("html");
        TinyMCESettings tinyMCESettings = new TinyMCESettings();
        contentArea.add(new TinyMceBehavior(tinyMCESettings));
        form.add(contentArea);

        form.add(new ConditionConfigPanel("dbConditionConfig"));        

        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                userGuidanceService.saveDbLevel(dbLevel);
            }
        });

    }
}
