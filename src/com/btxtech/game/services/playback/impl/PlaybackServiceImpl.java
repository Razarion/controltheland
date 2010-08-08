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
import com.btxtech.game.jsre.playback.PlaybackInfo;
import com.btxtech.game.services.playback.PlaybackService;
import com.btxtech.game.services.utg.DbEventTrackingItem;
import com.btxtech.game.services.utg.DbEventTrackingStart;
import com.btxtech.game.services.utg.UserTrackingService;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public PlaybackInfo getPlaybackInfo(String sessionId, long startTime) {
        PlaybackInfo playbackInfo = new PlaybackInfo();

        DbEventTrackingStart start = null;
        DbEventTrackingStart next = null;
        List<DbEventTrackingStart> dbEventTrackingStarts = userTrackingService.getDbEventTrackingStart(sessionId);
        for (int i = 0, dbEventTrackingStartsSize = dbEventTrackingStarts.size(); i < dbEventTrackingStartsSize; i++) {
            DbEventTrackingStart dbEventTrackingStart = dbEventTrackingStarts.get(i);
            if (dbEventTrackingStart.getClientTimeStamp() == startTime) {
                start = dbEventTrackingStart;
                i++;
                if (i < dbEventTrackingStartsSize) {
                    next = dbEventTrackingStarts.get(i);
                }
                break;
            }
        }
        if (start == null) {
            return playbackInfo;
        }


        ArrayList<EventTrackingItem> eventTrackingItems = new ArrayList<EventTrackingItem>();
        for (DbEventTrackingItem dbEventTrackingItem : userTrackingService.getDbEventTrackingItem(start, next)) {
            eventTrackingItems.add(dbEventTrackingItem.createEventTrackingItem());
        }
        playbackInfo.setEventTrackingItems(eventTrackingItems);
        playbackInfo.setEventTrackingStart(start.createEventTrackingStart());
        return playbackInfo;
    }
}
