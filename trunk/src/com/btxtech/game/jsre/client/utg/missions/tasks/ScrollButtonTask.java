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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;

/**
 * User: beat
 * Date: 17.02.2010
 * Time: 20:03:23
 */
public class ScrollButtonTask extends Task implements ClickHandler {
    private HandlerRegistration registration;

    public ScrollButtonTask(String html) {
        super(html);
    }

    @Override
    public String getName() {
        return "Scroll Button";
    }

    @Override
    public void run() {
        Button button = InfoPanel.getInstance().getScrollHome();
        int x = button.getAbsoluteLeft() + button.getOffsetWidth() / 2;
        int y = button.getAbsoluteTop();
        setSpeechBubble(new SpeechBubble(x, y, HtmlConstants.SCROLL_HTML2, true));
        registration = button.addClickHandler(this);

    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        activateNextTask();
    }

    @Override
    public void closeBubble() {
        super.closeBubble();
        registration.removeHandler();
    }
}