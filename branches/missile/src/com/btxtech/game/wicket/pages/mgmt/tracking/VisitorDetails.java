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

import com.btxtech.game.services.utg.GameTrackingInfo;
import com.btxtech.game.services.utg.PageAccess;
import com.btxtech.game.services.utg.BrowserDetails;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.services.utg.VisitorDetailInfo;
import com.btxtech.game.wicket.WebCommon;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: Aug 4, 2009
 * Time: 10:31:43 PM
 */
public class VisitorDetails extends WebPage {
    @SpringBean
    private UserTrackingService userTrackingService;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(WebCommon.DATE_TIME_FORMAT_STRING);

    public VisitorDetails(String sessionId) {
        VisitorDetailInfo visitorDetailInfo = userTrackingService.getVisitorDetails(sessionId);

        userInfo(sessionId, visitorDetailInfo);

        gameOverview(visitorDetailInfo);

        pageHostory(visitorDetailInfo);

        gameInfo(visitorDetailInfo);
    }

    private void pageHostory(final VisitorDetailInfo visitorDetailInfo) {
        ListView<PageAccess> pageAccessHistory = new ListView<PageAccess>("pageAccessHistory", visitorDetailInfo.getPageAccessHistory()) {
            private Date previous;

            @Override
            protected void populateItem(ListItem<PageAccess> listItem) {
                listItem.add(new Label("time", simpleDateFormat.format(listItem.getModelObject().getTimeStamp())));
                if (previous != null) {
                    listItem.add(new Label("delta", WebCommon.getTimeDiff(previous, listItem.getModelObject().getTimeStamp())));
                } else {
                    listItem.add(new Label("delta", ""));
                }
                previous = listItem.getModelObject().getTimeStamp();
                listItem.add(new Label("page", listItem.getModelObject().getPage()));
                listItem.add(new Label("additional", listItem.getModelObject().getAdditional()));
            }
        };
        add(pageAccessHistory);
    }

    private void gameInfo(final VisitorDetailInfo visitorDetailInfo) {
        ListView<GameTrackingInfo> gameTrackingInfoList = new ListView<GameTrackingInfo>("gameTrackings", visitorDetailInfo.getGameTrackingInfos()) {
            @Override
            protected void populateItem(ListItem<GameTrackingInfo> listItem) {
                listItem.add(new GameTracking("gameTracking", listItem.getModelObject()));
            }
        };
        add(gameTrackingInfoList);
    }

    private void userInfo(String sessionId, VisitorDetailInfo visitorDetailInfo) {
        BrowserDetails browserDetails = visitorDetailInfo.getUserDetails();
        add(new Label("sessionId", sessionId));
        add(new Label("timeStamp", simpleDateFormat.format(browserDetails.getTimeStamp())));
        add(new Label("userAgent", browserDetails.getUserAgent()));
        add(new Label("language", browserDetails.getLanguage()));
        add(new Label("remoteHost", browserDetails.getRemoteHost()));
        add(new Label("remoteAddr", browserDetails.getRemoteAddr()));
        add(new Label("cookieId", browserDetails.getCookieId()));
        add(new Label("crawler", browserDetails.isCrawler() ? "Yes" : "No"));
        add(new Label("referer", browserDetails.getReferer()));
    }

    private void gameOverview(VisitorDetailInfo visitorDetailInfo) {
        add(new Label("totalTime", WebCommon.formatDuration(visitorDetailInfo.getTotalTime())));
        add(new Label("gameAttempts", Integer.toString(visitorDetailInfo.getGameAttemps())));
        add(new Label("moveCommands", Integer.toString(visitorDetailInfo.getMoveCommands())));
        add(new Label("builderCommands", Integer.toString(visitorDetailInfo.getBuilderCommands())));
        add(new Label("factoryCommands", Integer.toString(visitorDetailInfo.getFactoryCommands())));
        add(new Label("collectCommands", Integer.toString(visitorDetailInfo.getMoneyCollectCommands())));
        add(new Label("attackCommands", Integer.toString(visitorDetailInfo.getAttackCommands())));
        add(new Label("completedMissions", Integer.toString(visitorDetailInfo.getCompletedMissionCount())));
    }
}