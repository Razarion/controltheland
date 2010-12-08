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

import com.btxtech.game.services.utg.GameStartup;
import com.btxtech.game.services.utg.LifecycleTrackingInfo;
import com.btxtech.game.wicket.WebCommon;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * User: beat
 * Date: 08.08.2010
 * Time: 14:01:23
 */
public class LifecyclePanel extends Panel {
    public LifecyclePanel(String id, LifecycleTrackingInfo lifecycleTrackingInfo) {
        super(id);
        if (lifecycleTrackingInfo.hasDuration()) {
            add(new Label("timeInGame", WebCommon.formatDuration(lifecycleTrackingInfo.getDuration())));
        } else {
            add(new Label("timeInGame", "???"));
        }
        add(new Label("userName", lifecycleTrackingInfo.getUserName()));
        add(new Label("baseName", lifecycleTrackingInfo.getBaseName()));

        add(new ListView<GameStartup>("startup", lifecycleTrackingInfo.getGameStartups()) {
            @Override
            protected void populateItem(ListItem<GameStartup> gameStartupListItem) {
                // TODO startup                
                /*
                gameStartupListItem.add(new Label("task", gameStartupListItem.getModelObject().getState().getNiceText()));
                */
                gameStartupListItem.add(new Label("time", WebCommon.formatDurationMilis(gameStartupListItem.getModelObject().getDuration())));
            }
        });
        if (lifecycleTrackingInfo.hasTotalStartupDurtaion()) {
            add(new Label("totalStartup", WebCommon.formatDurationMilis(lifecycleTrackingInfo.getStartupDuration())));
        } else {
            add(new Label("totalStartup", "???"));
        }
        add(new Label("userStageName", lifecycleTrackingInfo.getUserStageName()));

    }
}
