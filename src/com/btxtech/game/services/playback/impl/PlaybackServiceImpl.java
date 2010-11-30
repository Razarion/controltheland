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

import com.btxtech.game.jsre.common.EventTrackingItem;
import com.btxtech.game.jsre.common.SelectionTrackingItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.playback.PlaybackInfo;
import com.btxtech.game.services.gwt.MovableServiceImpl;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.playback.PlaybackService;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.DbCommand;
import com.btxtech.game.services.utg.DbEventTrackingItem;
import com.btxtech.game.services.utg.DbEventTrackingStart;
import com.btxtech.game.services.utg.DbSelectionTrackingItem;
import com.btxtech.game.services.utg.DbUserStage;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: 04.08.2010
 * Time: 11:11:41
 */
@Component("playbackService")
public class PlaybackServiceImpl implements PlaybackService {
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private TerrainService terrainService;
    @Autowired
    private TutorialService tutorialService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private MgmtService mgmtService;
    @Autowired
    private UserService userService;
    private Log log = LogFactory.getLog(PlaybackServiceImpl.class);

    @Override
    public PlaybackInfo getPlaybackInfo(String sessionId, long startTime, String stageName) {
        try {
            PlaybackInfo playbackInfo = new PlaybackInfo();

            // Tutorial
            MovableServiceImpl.setCommonInfo(playbackInfo, userService, itemService, mgmtService);
            DbUserStage dbUserStage = userGuidanceService.getDbUserStage(stageName);
            playbackInfo.setTutorialConfig(tutorialService.getTutorialConfig(dbUserStage));
            terrainService.setupTerrain(playbackInfo, dbUserStage);

            // Mouse tracker
            DbEventTrackingStart start = null;
            DbEventTrackingStart next = null;
            Long endTime = null;
            List<DbEventTrackingStart> dbEventTrackingStarts = userTrackingService.getDbEventTrackingStart(sessionId);
            for (int i = 0, dbEventTrackingStartsSize = dbEventTrackingStarts.size(); i < dbEventTrackingStartsSize; i++) {
                DbEventTrackingStart dbEventTrackingStart = dbEventTrackingStarts.get(i);
                if (dbEventTrackingStart.getClientTimeStamp() == startTime) {
                    start = dbEventTrackingStart;
                    i++;
                    if (i < dbEventTrackingStartsSize) {
                        next = dbEventTrackingStarts.get(i);
                        endTime = next.getClientTimeStamp();
                    }
                    break;
                }
            }
            if (start != null) {
                ArrayList<EventTrackingItem> eventTrackingItems = new ArrayList<EventTrackingItem>();
                for (DbEventTrackingItem dbEventTrackingItem : userTrackingService.getDbEventTrackingItem(start, next)) {
                    eventTrackingItems.add(dbEventTrackingItem.createEventTrackingItem());
                }
                playbackInfo.setEventTrackingItems(eventTrackingItems);
                playbackInfo.setEventTrackingStart(start.createEventTrackingStart());
            }

            // Selections
            ArrayList<SelectionTrackingItem> selectionTrackingItems = new ArrayList<SelectionTrackingItem>();
            for (DbSelectionTrackingItem dbSelectionTrackingItem : userTrackingService.getDbSelectionTrackingItems(sessionId, startTime, endTime)) {
                selectionTrackingItems.add(dbSelectionTrackingItem.createSelectionTrackingItem());
            }
            playbackInfo.setSelectionTrackingItems(selectionTrackingItems);

            // Commands
            ArrayList<BaseCommand> baseCommands = new ArrayList<BaseCommand>();
            for (DbCommand dbCommand : userTrackingService.getDbCommands(sessionId, startTime, endTime)) {
                baseCommands.add(dbCommand.getBaseCommand());
            }
            playbackInfo.setCommands(baseCommands);

            return playbackInfo;
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
    }
}
