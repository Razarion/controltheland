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

import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.mgmt.StartupData;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 30.03.2010
 * Time: 23:03:10
 */
public class Startup extends MgmtWebPage {
    @SpringBean
    private MgmtService mgmtService;

    public Startup() {
        FeedbackPanel feedbackPanel = new FeedbackPanel("msgs");
        add(feedbackPanel);

        final StartupData startupData = mgmtService.getStartupData();
        Form<StartupData> form = new Form<StartupData>("form", new CompoundPropertyModel<StartupData>(startupData)) {

            @Override
            protected void onSubmit() {
                mgmtService.saveStartupData(startupData);
            }
        };

        form.add(new TextField<String>("registerDialogDelay"));

        add(form);
    }
}
