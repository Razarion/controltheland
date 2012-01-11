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

import com.btxtech.game.jsre.client.common.Index;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 22:28:34
 */
@Deprecated
public class TerrainPositionSpeechBubbleHintConfig extends SpeechBubbleHintConfig {
    private Index position;
    private String html;

    /**
     * Used by GWT
     */
    public TerrainPositionSpeechBubbleHintConfig() {
    }

    public TerrainPositionSpeechBubbleHintConfig(boolean closeOnTaskEnd, Index position, String html, int blinkDelay, int blinkInterval) {
        super(closeOnTaskEnd, blinkDelay, blinkInterval);
        this.html = html;
        this.position = position;
    }

    public String getHtml() {
        return html;
    }

    public Index getPosition() {
        return position;
    }
}
