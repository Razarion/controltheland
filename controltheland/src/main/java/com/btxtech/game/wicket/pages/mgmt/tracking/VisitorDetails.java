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

import com.btxtech.game.services.utg.BrowserDetails;
import com.btxtech.game.services.utg.LifecycleTrackingInfo;
import com.btxtech.game.services.utg.PageAccess;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.services.utg.VisitorDetailInfo;
import com.btxtech.game.wicket.WebCommon;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Date;

/**
 * User: beat
 * Date: Aug 4, 2009
 * Time: 10:31:43 PM
 */
public class VisitorDetails extends MgmtWebPage {
    @SpringBean
    private UserTrackingService userTrackingService;

    public VisitorDetails(String sessionId) {
        VisitorDetailInfo visitorDetailInfo = userTrackingService.getVisitorDetails(sessionId);

        userInfo(sessionId, visitorDetailInfo);

        gameOverview(visitorDetailInfo);

        pageHostory(visitorDetailInfo);

        detailTrackingInfo(visitorDetailInfo);
    }

    private void pageHostory(final VisitorDetailInfo visitorDetailInfo) {
        ListView<PageAccess> pageAccessHistory = new ListView<PageAccess>("pageAccessHistory", visitorDetailInfo.getPageAccessHistory()) {
            private Date previous;

            @Override
            protected void populateItem(ListItem<PageAccess> listItem) {
                listItem.add(new Label("time", WebCommon.formatDateTime(listItem.getModelObject().getTimeStamp())));
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

    private void detailTrackingInfo(final VisitorDetailInfo visitorDetailInfo) {
        ListView<LifecycleTrackingInfo> gameTrackingInfoList = new ListView<LifecycleTrackingInfo>("detailTrackings", visitorDetailInfo.getLifecycleTrackingInfos()) {
            @Override
            protected void populateItem(ListItem<LifecycleTrackingInfo> listItem) {
                if(listItem.getModelObject().isRealGame()) {
                    listItem.add(new RealGameTracking("detailTracking", listItem.getModelObject()));
                } else {
                    listItem.add(new TutorialTracking("detailTracking", listItem.getModelObject()));
                }
            }
        };
        add(gameTrackingInfoList);
    }

    private void userInfo(String sessionId, VisitorDetailInfo visitorDetailInfo) {
        BrowserDetails browserDetails = visitorDetailInfo.getUserDetails();
        add(new Label("sessionId", sessionId));
        add(new Label("timeStamp", WebCommon.formatDateTime(browserDetails.getTimeStamp())));
        add(new Label("userAgent", browserDetails.getUserAgent()));
        add(new Label("language", browserDetails.getLanguage()));
        add(new Label("remoteHost", browserDetails.getRemoteHost()));
        add(new Label("remoteAddr", browserDetails.getRemoteAddr()));
        add(new Label("cookieId", browserDetails.getCookieId()));
        add(new Label("javaScriptDetected", browserDetails.isJavaScriptDetected() ? "Yes" : "No"));
        add(new Label("referer", browserDetails.getReferer()));
    }

    private void gameOverview(VisitorDetailInfo visitorDetailInfo) {
        add(new Label("gameAttempts", Integer.toString(visitorDetailInfo.getGameAttempts())));
        add(new Label("moveCommands", Integer.toString(visitorDetailInfo.getMoveCommands())));
        add(new Label("builderCommands", Integer.toString(visitorDetailInfo.getBuilderCommands())));
        add(new Label("factoryCommands", Integer.toString(visitorDetailInfo.getFactoryCommands())));
        add(new Label("collectCommands", Integer.toString(visitorDetailInfo.getMoneyCollectCommands())));
        add(new Label("attackCommands", Integer.toString(visitorDetailInfo.getAttackCommands())));
    }
}