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

import com.btxtech.game.jsre.client.common.UserMessage;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.utg.tracking.BrowserWindowTracking;
import com.btxtech.game.jsre.common.utg.tracking.DialogTracking;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingStart;
import com.btxtech.game.jsre.common.utg.tracking.SelectionTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.TerrainScrollTracking;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.user.User;
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

    boolean hasCookieToAdd();

    String getAndClearCookieToAdd();

    List<SessionOverviewDto> getSessionOverviewDtos(UserTrackingFilter filter);

    SessionDetailDto getSessionDetailDto(String sessionId);

    void saveUserCommand(BaseCommand baseCommand);

    void onUserCreated(User user);

    void onUserLoggedIn(User user, Base base);

    void onUserLoggedOut(User user);

    void onBaseCreated(User user, String baseName);

    void onBaseDefeated(User user, Base base);

    void onBaseSurrender(User user, Base base);

    void onUserEnterGame(User user);

    void onUserLeftGame(User user);

    void trackUserMessage(UserMessage userMessage);

    void trackWindowsClosed(String startUUid);

    void onJavaScriptDetected(Boolean html5Support);

    boolean isJavaScriptDetected();

    boolean isHtml5Support();

    void onTutorialProgressChanged(TutorialConfig.TYPE type, String startUuid, int taskId, String tutorialTaskName, long duration, long clientTimeStamp);

    void onEventTrackingStart(EventTrackingStart eventTrackingStart);

    void onEventTrackerItems(Collection<EventTrackingItem> eventTrackingItems, Collection<SyncItemInfo> syncItemInfos, Collection<SelectionTrackingItem> selectionTrackingItems, Collection<TerrainScrollTracking> terrainScrollTrackings, Collection<BrowserWindowTracking> browserWindowTrackings, Collection<DialogTracking> dialogTrackings);

    List<DbEventTrackingItem> getDbEventTrackingItem(String startUuid);

    DbEventTrackingStart getDbEventTrackingStart(String startUuid);

    void saveStartupTask(StartupTaskInfo startupTaskInfo, String startUuid, Integer levelTaskId);

    RealGameTrackingInfo getGameTracking(LifecycleTrackingInfo lifecycleTrackingInfo);

    TutorialTrackingInfo getTutorialTrackingInfo(LifecycleTrackingInfo lifecycleTrackingInfo);

    List<DbSelectionTrackingItem> getDbSelectionTrackingItems(String startUuid);

    List<DbSyncItemInfo> getDbSyncItemInfos(String startUuid);

    List<DbScrollTrackingItem> getDbScrollTrackingItems(String startUuid);

    List<DbBrowserWindowTracking> getDbBrowserWindowTrackings(String startUuid);

    List<DbDialogTracking> getDbDialogTrackings(String startUuid);

    LifecycleTrackingInfo getLifecycleTrackingInfo(String startUuid);
}
