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

import com.btxtech.game.services.common.DateUtil;
import com.btxtech.game.services.utg.LifecycleTrackingInfo;
import com.btxtech.game.services.utg.tracker.DbStartupTask;
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
        add(new Label("userName", lifecycleTrackingInfo.getUserName()));
        add(new Label("level", lifecycleTrackingInfo.getLevel()));
        add(new Label("levelTaskName", lifecycleTrackingInfo.getLevelTaskName()));
        add(new Label("startTime", DateUtil.formatDateTime(new Date(lifecycleTrackingInfo.getStartServer()))));
        add(new Label("startUuid", lifecycleTrackingInfo.getStartUuid()));
        add(new ListView<DbStartupTask>("startup", lifecycleTrackingInfo.getGameStartups()) {
            @Override
            protected void populateItem(ListItem<DbStartupTask> gameStartupListItem) {
                gameStartupListItem.add(new Label("task", gameStartupListItem.getModelObject().getTask()));
                gameStartupListItem.add(new Label("time", DateUtil.formatDurationMilis(gameStartupListItem.getModelObject().getDuration())));
                gameStartupListItem.add(new Label("failureText", gameStartupListItem.getModelObject().getFailureText()));
            }
        });
        add(new Label("totalStartup", DateUtil.formatDurationMilis(lifecycleTrackingInfo.getStartupDuration())));
    }
}
