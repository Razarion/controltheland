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
    public PlaybackInfo getPlaybackInfo() {
        ///////
        String sessionId = "DEB6CE8EA52C1AA76B3F4A21B981D909";
        int index = 0;
        ///////
        PlaybackInfo playbackInfo = new PlaybackInfo();

        List<DbEventTrackingStart> dbEventTrackingStarts = userTrackingService.getDbEventTrackingStart(sessionId);
        DbEventTrackingStart begin = dbEventTrackingStarts.get(index);
        DbEventTrackingStart end = null;
        if (dbEventTrackingStarts.size() > index + 1) {
            end = dbEventTrackingStarts.get(index + 1);
        }

        ArrayList<EventTrackingItem> eventTrackingItems = new ArrayList<EventTrackingItem>();
        for (DbEventTrackingItem dbEventTrackingItem : userTrackingService.getDbEventTrackingItem(begin, end)) {
            eventTrackingItems.add(dbEventTrackingItem.createEventTrackingItem());
        }
        playbackInfo.setEventTrackingItems(eventTrackingItems);
        playbackInfo.setEventTrackingStart(begin.createEventTrackingStart());
        return playbackInfo;
    }
}
