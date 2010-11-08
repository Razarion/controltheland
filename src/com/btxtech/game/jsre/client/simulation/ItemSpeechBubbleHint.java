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

package com.btxtech.game.jsre.client.simulation;

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.SpeechBubble;
import com.btxtech.game.jsre.common.tutorial.ItemSpeechBubbleHintConfig;

/**
 * User: beat
 * Date: 05.11.2010
 * Time: 18:56:56
 */
public class ItemSpeechBubbleHint implements Hint {
    private SpeechBubble speechBubble;

    public ItemSpeechBubbleHint(ItemSpeechBubbleHintConfig itemSpeechBubbleHintConfig) {
        ClientSyncItem clientSyncItem = ItemContainer.getInstance().getSimulationItem(itemSpeechBubbleHintConfig.getSyncItemId());
        speechBubble = new SpeechBubble(clientSyncItem.getSyncItem(), itemSpeechBubbleHintConfig.getHtml());
    }

    @Override
    public void dispose() {
        speechBubble.close();
    }
}
