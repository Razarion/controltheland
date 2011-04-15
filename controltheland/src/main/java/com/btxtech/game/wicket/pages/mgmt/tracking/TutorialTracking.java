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
import com.btxtech.game.services.utg.DbEventTrackingStart;
import com.btxtech.game.services.utg.DbTutorialProgress;
import com.btxtech.game.services.utg.LifecycleTrackingInfo;
import com.btxtech.game.services.utg.TutorialTrackingInfo;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.WebCommon;
import com.btxtech.game.wicket.pages.mgmt.PlaybackPage;
import javax.servlet.http.HttpSession;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;

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

        overview(tutorialTrackingInfo, lifecycleTrackingInfo.getLevel());
        tutorialProgress(tutorialTrackingInfo);
    }

    private void tutorialProgress(TutorialTrackingInfo tutorialTrackingInfo) {
        add(new ListView<DbTutorialProgress>("tutorialProgress", tutorialTrackingInfo.getDbTutorialProgresss()) {
            @Override
            protected void populateItem(ListItem<DbTutorialProgress> gameStartupListItem) {
                gameStartupListItem.add(new Label("type", gameStartupListItem.getModelObject().getType()));
                gameStartupListItem.add(new Label("name", gameStartupListItem.getModelObject().getName()));
                gameStartupListItem.add(new Label("parent", gameStartupListItem.getModelObject().getParent()));
                gameStartupListItem.add(new Label("duration", WebCommon.formatDurationMilis(gameStartupListItem.getModelObject().getDuration())));
            }
        });
    }

    private void overview(TutorialTrackingInfo tutorialTrackingInfo, final String level) {
        final DbEventTrackingStart dbEventTrackingStart = tutorialTrackingInfo.getDbEventTrackingStart();
        if (dbEventTrackingStart != null) {
            Link link = new Link("link") {

                @Override
                public void onClick() {
                    HttpSession httpSession = ((WebRequest) getRequest()).getHttpServletRequest().getSession();
                    httpSession.setAttribute(PlaybackEntry.SESSION_ID, dbEventTrackingStart.getSessionId());
                    httpSession.setAttribute(PlaybackEntry.START_TIME, Long.toString(dbEventTrackingStart.getClientTimeStamp()));
                    httpSession.setAttribute(PlaybackEntry.LAVAL_NAME, level);
                    setResponsePage(new PlaybackPage());
                }
            };
            add(link);
            add(new Label("resolution", dbEventTrackingStart.getClientWidth() + " x " + dbEventTrackingStart.getClientHeight()));
        } else {
            BookmarkablePageLink<PlaybackPage> pageLink = new BookmarkablePageLink<PlaybackPage>("link", PlaybackPage.class, null);
            pageLink.setEnabled(false);
            add(pageLink);
            add(new Label("resolution", "-"));
        }
    }
}