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
import com.btxtech.game.services.utg.SessionDetailDto;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.services.utg.tracker.DbPageAccess;
import com.btxtech.game.services.utg.tracker.DbSessionDetail;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Date;

/**
 * User: beat
 * Date: Aug 4, 2009
 * Time: 10:31:43 PM
 */
public class SessionDetail extends MgmtWebPage {
    public static final String SESSION_KEY = "sessionId";
    @SpringBean
    private UserTrackingService userTrackingService;

    public SessionDetail(PageParameters parameters) {
        super(parameters);
        String sessionId = parameters.get(SESSION_KEY).toString();
        if (sessionId == null) {
            throw new IllegalArgumentException(SESSION_KEY + " must be available in the PageParameters");
        }
        SessionDetailDto sessionDetailDto = userTrackingService.getSessionDetailDto(sessionId);

        userInfo(sessionId, sessionDetailDto);

        gameOverview(sessionDetailDto);

        pageHistory(sessionDetailDto);

        detailTrackingInfo(sessionDetailDto);
    }

    private void pageHistory(final SessionDetailDto sessionDetailDto) {
        ListView<DbPageAccess> pageAccessHistory = new ListView<DbPageAccess>("pageAccessHistory", sessionDetailDto.getPageAccessHistory()) {
            private Date previous;

            @Override
            protected void populateItem(ListItem<DbPageAccess> listItem) {
                listItem.add(new Label("time", DateUtil.formatDateTime(listItem.getModelObject().getTimeStamp())));
                if (previous != null) {
                    listItem.add(new Label("delta", DateUtil.getTimeDiff(previous, listItem.getModelObject().getTimeStamp())));
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

    private void detailTrackingInfo(final SessionDetailDto sessionDetailDto) {
        ListView<LifecycleTrackingInfo> gameTrackingInfoList = new ListView<LifecycleTrackingInfo>("detailTrackings", sessionDetailDto.getLifecycleTrackingInfos()) {
            @Override
            protected void populateItem(ListItem<LifecycleTrackingInfo> listItem) {
                if (listItem.getModelObject().isSuccessFul()) {
                    if (listItem.getModelObject().isRealGame()) {
                        listItem.add(new RealGameTracking("detailTracking", listItem.getModelObject()));
                    } else {
                        listItem.add(new TutorialTracking("detailTracking", listItem.getModelObject()));
                    }
                } else {
                    listItem.add(new FailedTracking("detailTracking", listItem.getModelObject()));
                }
            }
        };
        add(gameTrackingInfoList);
    }

    private void userInfo(String sessionId, SessionDetailDto sessionDetailDto) {
        DbSessionDetail dbSessionDetail = sessionDetailDto.getUserDetails();
        add(new Label("sessionId", sessionId));
        add(new Label("timeStamp", DateUtil.formatDateTime(dbSessionDetail.getTimeStamp())));
        add(new Label("userAgent", dbSessionDetail.getUserAgent()));
        add(new Label("language", dbSessionDetail.getLanguage()));
        add(new Label("remoteHost", dbSessionDetail.getRemoteHost()));
        add(new Label("remoteAddr", dbSessionDetail.getRemoteAddr()));
        add(new Label("cookieId", dbSessionDetail.getCookieId()));
        add(new Label("javaScriptDetected", dbSessionDetail.isJavaScriptDetected() ? "Yes" : "No"));
        add(new Label("referer", dbSessionDetail.getReferer()));
        if (dbSessionDetail.getDbFacebookSource() != null) {
            add(new Label("dbFacebookSource.fbSource", dbSessionDetail.getDbFacebookSource().getFbSource()));
            add(new Label("dbFacebookSource.optionalAdValue", dbSessionDetail.getDbFacebookSource().getOptionalAdValue()));
            add(new Label("dbFacebookSource.wholeString", dbSessionDetail.getDbFacebookSource().getWholeString()));
        } else {
            add(new Label("dbFacebookSource.fbSource", ""));
            add(new Label("dbFacebookSource.optionalAdValue", ""));
            add(new Label("dbFacebookSource.wholeString", ""));
        }
        if(dbSessionDetail.getDbInvitationInfo() != null) {
            add(new Label("dbInvitationInfo.host.username", dbSessionDetail.getDbInvitationInfo().getHost().getUsername()));
            add(new Label("dbInvitationInfo.source", dbSessionDetail.getDbInvitationInfo().getSource()));
        } else {
            add(new Label("dbInvitationInfo.host.username", ""));
            add(new Label("dbInvitationInfo.source", ""));
        }
    }

    private void gameOverview(SessionDetailDto sessionDetailDto) {
        add(new Label("gameAttempts", Integer.toString(sessionDetailDto.getGameAttempts())));
        add(new Label("moveCommands", Integer.toString(sessionDetailDto.getMoveCommands())));
        add(new Label("builderCommands", Integer.toString(sessionDetailDto.getBuilderCommands())));
        add(new Label("factoryCommands", Integer.toString(sessionDetailDto.getFactoryCommands())));
        add(new Label("collectCommands", Integer.toString(sessionDetailDto.getMoneyCollectCommands())));
        add(new Label("attackCommands", Integer.toString(sessionDetailDto.getAttackCommands())));
    }
}