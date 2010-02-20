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

package com.btxtech.game.jsre.client.utg.missions.tasks;

import com.btxtech.game.jsre.client.InfoPanel;
import com.btxtech.game.jsre.client.utg.SpeechBubble;
import com.btxtech.game.jsre.client.utg.missions.HtmlConstants;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 17.02.2010
 * Time: 20:03:23
 */
public class SelectProtagonistAndMoneyTask extends Task {
    private SpeechBubble moneySpeechBubble;

    public SelectProtagonistAndMoneyTask(String html) {
        super(html);
    }

    @Override
    public String getName() {
        return "Select protagonist and money";
    }

    @Override
    public void run() {
        setSpeechBubble(new SpeechBubble(getMission().getProtagonist(), getHtml(), false));
        Widget w = InfoPanel.getInstance().getMoney();
        int x = w.getAbsoluteLeft() + w.getOffsetWidth() / 2;
        int y = w.getAbsoluteTop() + w.getOffsetHeight() / 2;
        moneySpeechBubble = new SpeechBubble(x, y, HtmlConstants.COLLECT_HTML1, true);
    }

    @Override
    public void closeBubble() {
        super.closeBubble();
        if (moneySpeechBubble != null) {
            moneySpeechBubble.close();
            moneySpeechBubble = null;
        }
    }

    @Override
    public void blink() {
        super.blink();
        if (moneySpeechBubble != null) {
            moneySpeechBubble.blink();
        }
    }
}