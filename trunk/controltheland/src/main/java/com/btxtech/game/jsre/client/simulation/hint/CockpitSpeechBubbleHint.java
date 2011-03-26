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

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.Cockpit;
import com.btxtech.game.jsre.client.cockpit.HintWidgetException;
import com.btxtech.game.jsre.client.utg.SpeechBubble;
import com.btxtech.game.jsre.common.tutorial.CockpitSpeechBubbleHintConfig;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 05.11.2010
 * Time: 18:56:56
 */
public class CockpitSpeechBubbleHint extends SpeechBubbleHint {

    public CockpitSpeechBubbleHint(CockpitSpeechBubbleHintConfig cockpitSpeechBubbleHintConfig) {
        try {
            Widget widget = Cockpit.getInstance().getHintWidgetAndEnsureVisible(cockpitSpeechBubbleHintConfig);
            int left = widget.getAbsoluteLeft() + widget.getOffsetWidth() / 2;
            int top = widget.getAbsoluteTop();
            setSpeechBubble(new SpeechBubble(left, top, cockpitSpeechBubbleHintConfig.getHtml(), false), cockpitSpeechBubbleHintConfig);
        } catch (HintWidgetException e) {
            GwtCommon.handleException(e);
            setSpeechBubble(new SpeechBubble(0, 0, "???", false), cockpitSpeechBubbleHintConfig);
        }
    }
}
