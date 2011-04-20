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

package com.btxtech.game.jsre.client.simulation.hint;

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.SpeechBubble;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.tutorial.ItemSpeechBubbleHintConfig;
import com.btxtech.game.jsre.common.tutorial.ItemTypeSpeechBubbleHintConfig;

import java.util.List;

/**
 * User: beat
 * Date: 05.11.2010
 * Time: 18:56:56
 */
public class ItemSpeechBubbleHint extends SpeechBubbleHint {
    public ItemSpeechBubbleHint(ItemSpeechBubbleHintConfig itemSpeechBubbleHintConfig) {
        ClientSyncItem clientSyncItem = ItemContainer.getInstance().getSimulationItem(itemSpeechBubbleHintConfig.getSyncItemId());
        setSpeechBubble(new SpeechBubble(clientSyncItem.getSyncItem(), itemSpeechBubbleHintConfig.getHtml(), true), itemSpeechBubbleHintConfig);
    }

    public ItemSpeechBubbleHint(ItemTypeSpeechBubbleHintConfig itemTypeSpeechBubbleHintConfig) {
        List<? extends SyncItem> syncItems = ItemContainer.getInstance().getItems(itemTypeSpeechBubbleHintConfig.getItemType(), null);
        if (syncItems.isEmpty()) {
            GwtCommon.sendLogToServer("ItemSpeechBubbleHint: Not sync item found for ItemType: " + itemTypeSpeechBubbleHintConfig.getItemType());
            return;
        }
        setSpeechBubble(new SpeechBubble(syncItems.get(0), itemTypeSpeechBubbleHintConfig.getHtml(), true), itemTypeSpeechBubbleHintConfig);
    }
}
