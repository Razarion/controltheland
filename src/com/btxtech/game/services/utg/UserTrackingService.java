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

package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.StartupTask;
import com.btxtech.game.jsre.client.common.UserMessage;
import com.btxtech.game.jsre.common.gameengine.services.utg.MissionAction;
import com.btxtech.game.jsre.common.gameengine.services.utg.UserAction;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.user.User;
import com.btxtech.game.wicket.pages.basepage.BasePage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: 12.01.2010
 * Time: 22:04:31
 */
public interface UserTrackingService {
    void saveBrowserDetails(BrowserDetails browserDetails);

    void pageAccess(BasePage basePage);

    void pageAccess(Class theClass);

    void saveUserActions(ArrayList<UserAction> userActions, ArrayList<MissionAction> missionActions);

    List<VisitorInfo> getVisitorInfos();

    VisitorDetailInfo getVisitorDetails(String sessionId);

    void saveUserCommand(BaseCommand baseCommand);

    void onUserCreated(User user);

    void onUserLoggedIn(User user, Base base);

    void onUserLoggedOut(User user);

    void onBaseCreated(User user, Base base);

    void onBaseDefeated(User user, Base base);

    void onBaseSurrender(User user, Base base);

    void onUserEnterGame(User user);

    void onUserLeftGame(User user);

    void trackUserMessage(UserMessage userMessage);

    void levelPromotion(Base base, String oldLevel);

    void levelInterimPromotion(Base base, String targetLevel, String interimPromotion);

    void startUpTaskFinished(StartupTask state, Date clientTimeStamp, long duration);

    void startUpTaskFailed(StartupTask state, Date clientTimeStamp, long duration, String failureText);

    void onJavaScriptDetected();

    boolean isJavaScriptDetected();
}
