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

package com.btxtech.game.jsre.common.tutorial;

import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;

/**
 * User: beat
 * Date: 20.04.2011
 * Time: 22:28:34
 */
public class ItemTypeSpeechBubbleHintConfig extends SpeechBubbleHintConfig {
    private String html;
    private ItemType itemType;

    /**
     * Used by GWT
     */
    public ItemTypeSpeechBubbleHintConfig() {
    }

    public ItemTypeSpeechBubbleHintConfig(boolean closeOnTaskEnd, ItemType itemType, String html, int blinkDelay, int blinkInterval) {
        super(closeOnTaskEnd, blinkDelay, blinkInterval);
        this.itemType = itemType;
        this.html = html;
    }

    public String getHtml() {
        return html;
    }

    public ItemType getItemType() {
        return itemType;
    }
}
