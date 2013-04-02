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

import com.btxtech.game.services.connection.ServerGlobalConnectionService;
import com.btxtech.game.services.planet.PlanetSystemService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 03.03.2013
 * Time: 23:03:10
 */
public class OnlineUtils extends MgmtWebPage {
    @SpringBean
    private ServerGlobalConnectionService connectionService;
    @SpringBean
    private PlanetSystemService planetSystemService;

    public OnlineUtils() {
        FeedbackPanel feedbackPanel = new FeedbackPanel("msgs");
        add(feedbackPanel);

        setupOnlineUserPanel();
        setupRebootMessage();
    }

    private void setupOnlineUserPanel() {
        add(new Label("planetUsers", new AbstractReadOnlyModel<Integer>() {
            @Override
            public Integer getObject() {
                return planetSystemService.getAllOnlineBases().size();
            }
        }));
        add(new Label("missionUsers", new AbstractReadOnlyModel<Integer>() {
            @Override
            public Integer getObject() {
                return connectionService.getAllOnlineMissionUserState().size();
            }
        }));
        Form form = new Form("onlineUserForm");
        add(form);
        form.add(new Button("reload"));
        form.add(new Button("details") {

            @Override
            public void onSubmit() {
                setResponsePage(new OnlineUserDetails());
            }
        });
    }

    private void setupRebootMessage() {
        Form form = new Form("rebootMessageForm");
        add(form);

        final Model<Integer> rebootModel = new Model<>(60);
        final Model<Integer> downTimeModel = new Model<>(5);
        form.add(new TextField<>("rebootInSeconds", rebootModel, Integer.class));
        form.add(new TextField<>("downTimInMinutes", downTimeModel, Integer.class));


        form.add(new Button("send") {
            @Override
            public void onSubmit() {
                if (rebootModel.getObject() == null || downTimeModel.getObject() == null) {
                    error("Enter a valid number for 'Reboot in seconds' and 'Downtime in minutes'");
                }
                connectionService.sendServerRebootMessage(rebootModel.getObject(), downTimeModel.getObject());
            }
        });
    }
}
