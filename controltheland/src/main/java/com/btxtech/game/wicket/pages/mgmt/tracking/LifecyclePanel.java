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

package com.btxtech.game.wicket.pages.mgmt.tracking;

import com.btxtech.game.services.utg.DbStartupTask;
import com.btxtech.game.services.utg.LifecycleTrackingInfo;
import com.btxtech.game.wicket.WebCommon;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.Date;

/**
 * User: beat
 * Date: 08.08.2010
 * Time: 14:01:23
 */
public class LifecyclePanel extends Panel {
    public LifecyclePanel(String id, LifecycleTrackingInfo lifecycleTrackingInfo) {
        super(id);
        add(new Label("level", lifecycleTrackingInfo.getLevel()));
        add(new Label("startTime", WebCommon.formatDateTime(new Date(lifecycleTrackingInfo.getStart()))));
        add(new Label("endTime", WebCommon.formatDateTime(lifecycleTrackingInfo.getEnd() != null ? new Date(lifecycleTrackingInfo.getEnd()) : null)));
        add(new ListView<DbStartupTask>("startup", lifecycleTrackingInfo.getGameStartups()) {
            @Override
            protected void populateItem(ListItem<DbStartupTask> gameStartupListItem) {
                gameStartupListItem.add(new Label("task", gameStartupListItem.getModelObject().getTask()));
                gameStartupListItem.add(new Label("time", WebCommon.formatDurationMilis(gameStartupListItem.getModelObject().getDuration())));
                gameStartupListItem.add(new Label("failureText", gameStartupListItem.getModelObject().getFailureText()));
            }
        });
        add(new Label("totalStartup", WebCommon.formatDurationMilis(lifecycleTrackingInfo.getStartupDuration())));
    }
}
