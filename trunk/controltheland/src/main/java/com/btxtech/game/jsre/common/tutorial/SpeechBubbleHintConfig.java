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
 * Date: 20.12.2010
 * Time: 14:21:08
 */
@Deprecated
public class SpeechBubbleHintConfig extends HintConfig {
    private int blinkDelay;
    private int blinkInterval;

    /**
     * Used by GWT
     */
    public SpeechBubbleHintConfig() {
    }

    public SpeechBubbleHintConfig(boolean closeOnTaskEnd, int blinkDelay, int blinkInterval) {
        super(closeOnTaskEnd);
        this.blinkDelay = blinkDelay;
        this.blinkInterval = blinkInterval;
    }

    public int getBlinkDelay() {
        return blinkDelay;
    }

    public int getBlinkInterval() {
        return blinkInterval;
    }

    public boolean isActive() {
        return blinkInterval > 0;
    }

    public boolean hasDelay() {
        return blinkDelay > 0;
    }
}


