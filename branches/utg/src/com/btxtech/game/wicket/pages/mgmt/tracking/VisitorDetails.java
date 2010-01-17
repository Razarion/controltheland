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
import com.btxtech.game.services.utg.UserDetails;
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
        // User Info
        UserDetails userDetails = visitorDetailInfo.getUserDetails();
        add(new Label("sessionId", sessionId));
        add(new Label("timeStamp", simpleDateFormat.format(userDetails.getTimeStamp())));
        add(new Label("userAgent", userDetails.getUserAgent()));
        add(new Label("language", userDetails.getLanguage()));
        add(new Label("remoteHost", userDetails.getRemoteHost()));
        add(new Label("remoteAddr", userDetails.getRemoteAddr()));
        add(new Label("crawler", userDetails.isCrawler() ? "Yes" : "No"));

        // Game
        ListView<GameTrackingInfo> gameTrackingInfoList = new ListView<GameTrackingInfo>("gameTrackings", visitorDetailInfo.getGameTrackingInfos()) {
            @Override
            protected void populateItem(ListItem<GameTrackingInfo> listItem) {
                listItem.add(new GameTracking("gameTracking", listItem.getModelObject()));
            }
        };
        add(gameTrackingInfoList);

        // Page access history
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

}