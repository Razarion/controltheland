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

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 22:28:34
 */
public class ItemSpeechBubbleHintConfig implements HintConfig {
    private int syncItemId;
    private String html;

    /**
     * Used by GWT
     */
    public ItemSpeechBubbleHintConfig() {
    }

    public ItemSpeechBubbleHintConfig(int syncItemId, String html) {
        this.html = html;
        this.syncItemId = syncItemId;
    }

    public String getHtml() {
        return html;
    }

    public int getSyncItemId() {
        return syncItemId;
    }
}
