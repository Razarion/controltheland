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

package com.btxtech.game.jsre.client.utg.missions;

import com.btxtech.game.jsre.client.ClientSyncBaseItemView;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.SpeechBubble;
import java.util.Collection;

/**
 * User: beat
 * Date: 26.01.2010
 * Time: 22:20:04
 */
public class FinishedMission extends Mission {
    public static final int DELAY_TIME = 4000;
    private SpeechBubble speechBubble;
    private long lastAction;

    public FinishedMission() {
        super("FinishedMission", HtmlConstants.FINISHED);
    }

    public void start() throws MissionAportedException {
        Collection<ClientSyncBaseItemView> items = ItemContainer.getInstance().getOwnItems();

        ClientSyncBaseItemView item = null;
        for (ClientSyncBaseItemView clientSyncBaseItemView : items) {
            if (clientSyncBaseItemView.getSyncBaseItem().hasSyncBuilder()) {
                item = clientSyncBaseItemView;
            }
        }

        if (item == null) {
            throw new MissionAportedException("No Builder Item found");
        }

        scrollToItem(item);

        speechBubble = new SpeechBubble(item, HtmlConstants.FINISHED, false);
        lastAction = System.currentTimeMillis();
    }

    @Override
    public void tick() {
        super.tick();
        speechBubble.blink();
    }

    @Override
    public boolean isAccomplished() {
        return System.currentTimeMillis() > lastAction + DELAY_TIME;
    }

    @Override
    public long getLastTaskChangeTime() {
        return lastAction;
    }

    @Override
    public void close() {
        if (speechBubble != null) {
            speechBubble.close();
        }
    }
}