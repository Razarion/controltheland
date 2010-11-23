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
public class CockpitSpeechBubbleHint implements Hint {
    private SpeechBubble speechBubble;

    public CockpitSpeechBubbleHint(CockpitSpeechBubbleHintConfig cockpitSpeechBubbleHintConfig) {
        try {
            Widget widget = Cockpit.getInstance().getHintWidget(cockpitSpeechBubbleHintConfig);
            int left = widget.getAbsoluteLeft() + widget.getOffsetWidth() / 2;
            int top = widget.getAbsoluteTop();
            speechBubble = new SpeechBubble(left, top, cockpitSpeechBubbleHintConfig.getHtml());
        } catch (HintWidgetException e) {
            GwtCommon.handleException(e);
            speechBubble = new SpeechBubble(0, 0, "???");
        }
    }

    @Override
    public void dispose() {
        speechBubble.close();
    }
}
