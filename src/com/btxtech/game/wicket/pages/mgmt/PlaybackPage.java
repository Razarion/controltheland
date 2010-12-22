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

package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.jsre.playback.PlaybackEntry;
import javax.servlet.http.HttpSession;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.WebRequest;

/**
 * User: beat
 * Date: 04.08.2010
 * Time: 11:21:21
 */
public class PlaybackPage extends WebPage {
    public PlaybackPage() {
        HttpSession httpSession = ((WebRequest) getRequest()).getHttpServletRequest().getSession();
        Label startupSeqLabel = new Label("info", "");
        startupSeqLabel.add(new SimpleAttributeModifier("id", PlaybackEntry.ID));
        startupSeqLabel.add(new SimpleAttributeModifier(PlaybackEntry.SESSION_ID, (String)httpSession.getAttribute(PlaybackEntry.SESSION_ID)));
        startupSeqLabel.add(new SimpleAttributeModifier(PlaybackEntry.START_TIME, (String)httpSession.getAttribute(PlaybackEntry.START_TIME)));
        startupSeqLabel.add(new SimpleAttributeModifier(PlaybackEntry.STAGE_NAME, (String)httpSession.getAttribute(PlaybackEntry.STAGE_NAME)));
        add(startupSeqLabel);
    }
}
