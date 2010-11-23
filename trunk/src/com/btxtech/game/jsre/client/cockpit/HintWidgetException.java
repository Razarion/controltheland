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

package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.common.tutorial.CockpitSpeechBubbleHintConfig;

/**
 * User: beat
 * Date: 22.11.2010
 * Time: 20:03:58
 */
public class HintWidgetException extends Exception {
    public HintWidgetException(String s, CockpitSpeechBubbleHintConfig config) {
        super(s + " " + config.getCockpitWidgetEnum());
    }
}
