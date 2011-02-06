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

import com.btxtech.game.jsre.client.utg.SpeechBubble;
import com.btxtech.game.jsre.common.tutorial.SpeechBubbleHintConfig;
import com.google.gwt.user.client.Timer;

/**
 * User: beat
 * Date: 20.12.2010
 * Time: 13:55:38
 */
public abstract class SpeechBubbleHint implements Hint {
    private SpeechBubble speechBubble;
    private SpeechBubbleHintConfig speechBubbleHintConfig;
    private Timer delayTimer;
    private Timer intervalTimer;

    protected void setSpeechBubble(SpeechBubble speechBubble, SpeechBubbleHintConfig speechBubbleHintConfig) {
        this.speechBubble = speechBubble;
        this.speechBubbleHintConfig = speechBubbleHintConfig;
        if (speechBubbleHintConfig.isActive()) {
            if (speechBubbleHintConfig.hasDelay()) {
                delayTimer = new Timer() {
                    @Override
                    public void run() {
                        startBlink();
                    }
                };
                delayTimer.schedule(speechBubbleHintConfig.getBlinkDelay());
            } else {
                startBlink();
            }
        }
    }

    private void startBlink() {
        if (delayTimer != null) {
            delayTimer.cancel();
            delayTimer = null;
        }
        intervalTimer = new Timer() {
            @Override
            public void run() {
                speechBubble.blink();
            }
        };
        intervalTimer.scheduleRepeating(speechBubbleHintConfig.getBlinkInterval());
    }

    @Override
    public void dispose() {
        if (delayTimer != null) {
            delayTimer.cancel();
            delayTimer = null;
        }
        if (intervalTimer != null) {
            intervalTimer.cancel();
            intervalTimer = null;
        }
        speechBubble.close();
    }
}
