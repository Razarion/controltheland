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

package com.btxtech.game.services.playback.impl;

import com.btxtech.game.jsre.playback.Playback;
import com.btxtech.game.jsre.playback.PlaybackInfo;
import com.btxtech.game.services.gwt.AutowiredRemoteServiceServlet;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.sound.SoundService;
import com.btxtech.game.services.terrain.TerrainImageService;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: beat
 * Date: 04.08.2010
 * Time: 11:11:41
 */
public class PlaybackServiceImpl extends AutowiredRemoteServiceServlet implements Playback {
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private TerrainImageService terrainService;
    @Autowired
    private TutorialService tutorialService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private MgmtService mgmtService;
    @Autowired
    private UserService userService;
    @Autowired
    private CmsUiService cmsUiService;
    @Autowired
    private SoundService soundService;
    private Log log = LogFactory.getLog(PlaybackServiceImpl.class);

    @Override
    public PlaybackInfo getPlaybackInfo(String startUuid) {
        throw new UnsupportedOperationException();
        /*
        try {
            PlaybackInfo playbackInfo = new PlaybackInfo();

            // Tutorial
            MovableServiceImpl.setCommonInfo(playbackInfo, userService, serverItemTypeService, mgmtService, cmsUiService, soundService);
            LifecycleTrackingInfo lifecycleTrackingInfo = userTrackingService.getLifecycleTrackingInfo(startUuid);
            DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialConfig4Tracking(lifecycleTrackingInfo.getLevelTaskId());
            // TODO playbackInfo.setTutorialConfig(dbTutorialConfig.getTutorialConfig(serverItemTypeService));
            terrainService.setupTerrainTutorial(playbackInfo, dbTutorialConfig);

            playbackInfo.setEventTrackingStart(userTrackingService.getDbEventTrackingStart(startUuid).createEventTrackingStart());

            // Mouse tracking
            ArrayList<EventTrackingItem> eventTrackingItems = new ArrayList<EventTrackingItem>();
            for (DbEventTrackingItem dbEventTrackingItem : userTrackingService.getDbEventTrackingItem(startUuid)) {
                eventTrackingItems.add(dbEventTrackingItem.createEventTrackingItem());
            }
            playbackInfo.setEventTrackingItems(eventTrackingItems);

            // Selections
            ArrayList<SelectionTrackingItem> selectionTrackingItems = new ArrayList<SelectionTrackingItem>();
            for (DbSelectionTrackingItem dbSelectionTrackingItem : userTrackingService.getDbSelectionTrackingItems(startUuid)) {
                selectionTrackingItems.add(dbSelectionTrackingItem.createSelectionTrackingItem());
            }
            playbackInfo.setSelectionTrackingItems(selectionTrackingItems);

            // Commands
            ArrayList<SyncItemInfo> syncItemInfos = new ArrayList<SyncItemInfo>();
            for (DbSyncItemInfo dbSyncItemInfo : userTrackingService.getDbSyncItemInfos(startUuid)) {
                syncItemInfos.add(dbSyncItemInfo.getSyncItemInfo());
            }
            playbackInfo.setSyncItemInfos(syncItemInfos);

            // Scrolling
            ArrayList<TerrainScrollTracking> terrainScrollTrackings = new ArrayList<TerrainScrollTracking>();
            for (DbScrollTrackingItem dbScrollTrackingItem : userTrackingService.getDbScrollTrackingItems(startUuid)) {
                terrainScrollTrackings.add(dbScrollTrackingItem.createScrollTrackingItem());
            }
            playbackInfo.setScrollTrackingItems(terrainScrollTrackings);

            // Browser window tracking
            ArrayList<BrowserWindowTracking> browserWindowTrackings = new ArrayList<BrowserWindowTracking>();
            for (DbBrowserWindowTracking dbBrowserWindowTracking : userTrackingService.getDbBrowserWindowTrackings(startUuid)) {
                browserWindowTrackings.add(dbBrowserWindowTracking.createBrowserWindowTracking());
            }
            playbackInfo.setBrowserWindowTrackings(browserWindowTrackings);

            // Dialogs
            ArrayList<DialogTracking> dialogTrackings = new ArrayList<DialogTracking>();
            for (DbDialogTracking dbDialogTracking : userTrackingService.getDbDialogTrackings(startUuid)) {
                dialogTrackings.add(dbDialogTracking.createDialogTracking());
            }
            playbackInfo.setDialogTrackings(dialogTrackings);

            return playbackInfo;
        } catch (Throwable t) {
            log.error("", t);
            return null;
        } */
    }
}
