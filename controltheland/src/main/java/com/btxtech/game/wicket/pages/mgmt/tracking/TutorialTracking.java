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

import com.btxtech.game.jsre.playback.PlaybackEntry;
import com.btxtech.game.services.common.DateUtil;
import com.btxtech.game.services.utg.LifecycleTrackingInfo;
import com.btxtech.game.services.utg.TutorialTrackingInfo;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.services.utg.tracker.DbEventTrackingStart;
import com.btxtech.game.services.utg.tracker.DbTutorialProgress;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.servlet.http.HttpSession;

/**
 * User: beat
 * Date: 08.08.2010
 * Time: 13:11:15
 */
public class TutorialTracking extends Panel {
    @SpringBean
    UserTrackingService userTrackingService;

    public TutorialTracking(String id, LifecycleTrackingInfo lifecycleTrackingInfo) {
        super(id);
        add(new LifecyclePanel("lifecycle", lifecycleTrackingInfo));
        TutorialTrackingInfo tutorialTrackingInfo = userTrackingService.getTutorialTrackingInfo(lifecycleTrackingInfo);

        overview(tutorialTrackingInfo, lifecycleTrackingInfo);
        tutorialProgress(tutorialTrackingInfo);
    }

    private void tutorialProgress(TutorialTrackingInfo tutorialTrackingInfo) {
        add(new ListView<DbTutorialProgress>("tutorialProgress", tutorialTrackingInfo.getDbTutorialProgresss()) {
            @Override
            protected void populateItem(ListItem<DbTutorialProgress> gameStartupListItem) {
                gameStartupListItem.add(new Label("type", gameStartupListItem.getModelObject().getType()));
                gameStartupListItem.add(new Label("levelTaskName", gameStartupListItem.getModelObject().getLevelTaskName()));
                gameStartupListItem.add(new Label("tutorialTaskName", gameStartupListItem.getModelObject().getTutorialTaskName()));
                gameStartupListItem.add(new Label("duration", DateUtil.formatDurationMilis(gameStartupListItem.getModelObject().getDuration())));
            }
        });
    }

    private void overview(final TutorialTrackingInfo tutorialTrackingInfo, final LifecycleTrackingInfo lifecycleTrackingInfo) {
        DbEventTrackingStart dbEventTrackingStart = tutorialTrackingInfo.getDbEventTrackingStart();
        Link link = new Link("link") {

            @Override
            public void onClick() {
                HttpSession httpSession = ((ServletWebRequest) getRequest()).getContainerRequest().getSession();
                httpSession.setAttribute(PlaybackEntry.START_UUID, lifecycleTrackingInfo.getStartUuid());
                setResponsePage(new PlaybackPage());
            }
        };
        add(link);
        if (dbEventTrackingStart != null) {
            add(new Label("resolution", dbEventTrackingStart.getClientWidth() + " x " + dbEventTrackingStart.getClientHeight()));
        } else {
            add(new Label("resolution", "-"));
        }
    }
}