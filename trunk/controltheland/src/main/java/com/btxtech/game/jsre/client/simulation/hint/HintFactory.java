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

import com.btxtech.game.jsre.common.tutorial.CockpitSpeechBubbleHintConfig;
import com.btxtech.game.jsre.common.tutorial.HintConfig;
import com.btxtech.game.jsre.common.tutorial.ItemSpeechBubbleHintConfig;
import com.btxtech.game.jsre.common.tutorial.ItemTypeSpeechBubbleHintConfig;
import com.btxtech.game.jsre.common.tutorial.ResourceHintConfig;
import com.btxtech.game.jsre.common.tutorial.TerrainPositionSpeechBubbleHintConfig;

/**
 * User: beat
 * Date: 05.11.2010
 * Time: 18:48:14
 */
public class HintFactory {
    public static Hint createHint(HintConfig hintConfig) {
        if (hintConfig instanceof ResourceHintConfig) {
            return new ResourceHint((ResourceHintConfig) hintConfig);
        } else if (hintConfig instanceof ItemSpeechBubbleHintConfig) {
            return new ItemSpeechBubbleHint((ItemSpeechBubbleHintConfig) hintConfig);
        } else if (hintConfig instanceof ItemTypeSpeechBubbleHintConfig) {
            return new ItemSpeechBubbleHint((ItemTypeSpeechBubbleHintConfig) hintConfig);
        } else if (hintConfig instanceof TerrainPositionSpeechBubbleHintConfig) {
            return new TerrainPositionSpeechBubbleHint((TerrainPositionSpeechBubbleHintConfig) hintConfig);
        } else if (hintConfig instanceof CockpitSpeechBubbleHintConfig) {
            return new CockpitSpeechBubbleHint((CockpitSpeechBubbleHintConfig) hintConfig);
        }
        throw new IllegalArgumentException("Unknown hint config in HintFactory: " + hintConfig);
    }
}
