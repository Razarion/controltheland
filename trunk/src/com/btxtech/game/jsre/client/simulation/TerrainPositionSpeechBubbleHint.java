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

import com.btxtech.game.jsre.client.utg.SpeechBubble;
import com.btxtech.game.jsre.common.tutorial.TerrainPositionSpeechBubbleHintConfig;

/**
 * User: beat
 * Date: 07.11.2010
 * Time: 22:08:41
 */
public class TerrainPositionSpeechBubbleHint implements Hint {
    private SpeechBubble speechBubble;

    public TerrainPositionSpeechBubbleHint(TerrainPositionSpeechBubbleHintConfig terrainPositionSpeechBubbleHintConfig) {
        speechBubble = new SpeechBubble(terrainPositionSpeechBubbleHintConfig.getPosition().getX(),
                terrainPositionSpeechBubbleHintConfig.getPosition().getY(),
                terrainPositionSpeechBubbleHintConfig.getHtml());
    }

    @Override
    public void dispose() {
        speechBubble.close();
    }
}
