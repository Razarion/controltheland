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

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.DbCmsHomeText;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.settings.TinyMCESettings;

/**
 * User: beat
 * Date: 10.07.2010
 * Time: 13:11:59
 */
public class CmsHomeTextEditor extends MgmtWebPage {
    @SpringBean
    private CmsService cmsService;

    public CmsHomeTextEditor(final DbCmsHomeText dbCmsHomeText) {
        add(new FeedbackPanel("msgs"));
        Form<DbCmsHomeText> form = new Form<DbCmsHomeText>("form", new CompoundPropertyModel<DbCmsHomeText>(dbCmsHomeText)) {

            @Override
            protected void onSubmit() {
                cmsService.saveDbCmsHomeText(dbCmsHomeText);
                setResponsePage(CmsEditor.class);
            }
        };
        add(form);

        TextArea<String> contentArea = new TextArea<String>("text");
        TinyMCESettings tinyMCESettings = new TinyMCESettings(TinyMCESettings.Theme.advanced);
        tinyMCESettings.add(wicket.contrib.tinymce.settings.Button.link, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
        tinyMCESettings.add(wicket.contrib.tinymce.settings.Button.unlink, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
        contentArea.add(new TinyMceBehavior(tinyMCESettings));
        form.add(contentArea);
    }
}
