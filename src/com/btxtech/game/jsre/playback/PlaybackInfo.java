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

package com.btxtech.game.jsre.playback;

import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.common.EventTrackingItem;
import com.btxtech.game.jsre.common.EventTrackingStart;
import com.btxtech.game.jsre.common.SelectionTrackingItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 04.08.2010
 * Time: 11:15:58
 */
public class PlaybackInfo extends SimulationInfo {
    private EventTrackingStart eventTrackingStart;
    private List<EventTrackingItem> eventTrackingItems;
    private List<SelectionTrackingItem> selectionTrackingItems;
    private ArrayList<BaseCommand> baseCommands;

    public EventTrackingStart getEventTrackingStart() {
        return eventTrackingStart;
    }

    public void setEventTrackingStart(EventTrackingStart eventTrackingStart) {
        this.eventTrackingStart = eventTrackingStart;
    }

    public List<EventTrackingItem> getEventTrackingItems() {
        return eventTrackingItems;
    }

    public void setEventTrackingItems(List<EventTrackingItem> eventTrackingItems) {
        this.eventTrackingItems = eventTrackingItems;
    }

    public List<SelectionTrackingItem> getSelectionTrackingItems() {
        return selectionTrackingItems;
    }

    public void setSelectionTrackingItems(List<SelectionTrackingItem> selectionTrackingItems) {
        this.selectionTrackingItems = selectionTrackingItems;
    }

    public void setCommands(ArrayList<BaseCommand> baseCommands) {
        this.baseCommands = baseCommands;
    }

    public ArrayList<BaseCommand> getBaseCommands() {
        return baseCommands;
    }
}
