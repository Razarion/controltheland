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

import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.utg.tracking.BrowserWindowTracking;
import com.btxtech.game.jsre.common.utg.tracking.DialogTracking;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingStart;
import com.btxtech.game.jsre.common.utg.tracking.SelectionTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.TerrainScrollTracking;
import com.btxtech.game.services.history.GameHistoryFilter;
import com.btxtech.game.services.history.GameHistoryFrame;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.user.DbForgotPassword;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.tracker.DbBrowserWindowTracking;
import com.btxtech.game.services.utg.tracker.DbDialogTracking;
import com.btxtech.game.services.utg.tracker.DbEventTrackingItem;
import com.btxtech.game.services.utg.tracker.DbEventTrackingStart;
import com.btxtech.game.services.utg.tracker.DbScrollTrackingItem;
import com.btxtech.game.services.utg.tracker.DbSelectionTrackingItem;
import com.btxtech.game.services.utg.tracker.DbSessionDetail;
import com.btxtech.game.services.utg.tracker.DbSyncItemInfo;

import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 12.01.2010
 * Time: 22:04:31
 */
public interface UserTrackingService {
    void saveBrowserDetails(DbSessionDetail dbSessionDetail);

    void pageAccess(String pageName, String additional);

    List<SessionOverviewDto> getSessionOverviewDtos(UserTrackingFilter filter);

    List<SessionOverviewDto> getSessionOverviewDtos(User user);

    SessionDetailDto getSessionDetailDto(String sessionId);

    List<User> getNewUsers(NewUserTrackingFilter newUserTrackingFilter);

    void saveUserCommand(BaseCommand baseCommand);

    void onUserCreated(User user);

    void onUserVerified(User user);

    void onUnverifiedUserRemoved(User user);

    void onUserLoggedIn(User user, UserState userState);

    void onUserLoggedOut(User user);

    void onPasswordForgotRequested(User user, String forgotPasswordUuid);

    void onPasswordForgotRequestedRemoved(DbForgotPassword dbForgotPassword);

    void onPasswordReset(User user, String forgotPasswordUuid);

    void onBaseCreated(User user, String baseName);

    void onBaseDefeated(User user, Base base);

    void onBaseSurrender(User user, Base base);

    void onUserEnterGame(User user);

    void onUserLeftGame(User user);

    void onUserLeftGameNoSession(User user);

    void trackChatMessage(ChatMessage chatMessage);

    void trackWindowsClosed(String startUUid);

    void onJavaScriptDetected(Boolean html5Support);

    boolean isJavaScriptDetected();

    boolean isHtml5Support();

    void onTutorialProgressChanged(TutorialConfig.TYPE type, String startUuid, int taskId, int dbId, String tutorialTaskName, long duration, long clientTimeStamp);

    void onEventTrackingStart(EventTrackingStart eventTrackingStart);

    void onEventTrackerItems(Collection<EventTrackingItem> eventTrackingItems, Collection<SyncItemInfo> syncItemInfos, Collection<SelectionTrackingItem> selectionTrackingItems, Collection<TerrainScrollTracking> terrainScrollTrackings, Collection<BrowserWindowTracking> browserWindowTrackings, Collection<DialogTracking> dialogTrackings);

    List<DbEventTrackingItem> getDbEventTrackingItem(String startUuid);

    DbEventTrackingStart getDbEventTrackingStart(String startUuid);

    void saveStartupTask(StartupTaskInfo startupTaskInfo, String startUuid, Integer levelTaskId);

    void saveStartupTerminated(boolean successful, long totalTime, String startUuid, Integer levelTaskId);

    RealGameTrackingInfo getGameTracking(GameHistoryFrame gameHistoryFrame, GameHistoryFilter gameHistoryFilter);

    TutorialTrackingInfo getTutorialTrackingInfo(LifecycleTrackingInfo lifecycleTrackingInfo);

    List<DbSelectionTrackingItem> getDbSelectionTrackingItems(String startUuid);

    List<DbSyncItemInfo> getDbSyncItemInfos(String startUuid);

    List<DbScrollTrackingItem> getDbScrollTrackingItems(String startUuid);

    List<DbBrowserWindowTracking> getDbBrowserWindowTrackings(String startUuid);

    List<DbDialogTracking> getDbDialogTrackings(String startUuid);

    LifecycleTrackingInfo getLifecycleTrackingInfo(String startUuid);

    long calculateInGameTime(User user);

    int getLoginCount(User user);

    List<NewUserDailyDto> getNewUserDailyDto(NewUserDailyTrackingFilter newUserDailyTrackingFilter);

    TutorialStatisticDto getTutorialStatistic(QuestTrackingFilter questTrackingFilter);
}
