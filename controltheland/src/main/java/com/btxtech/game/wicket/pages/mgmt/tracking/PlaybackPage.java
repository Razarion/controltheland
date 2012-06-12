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

package com.btxtech.game.wicket.pages.mgmt.tracking;

import com.btxtech.game.jsre.playback.PlaybackEntry;
import javax.servlet.http.HttpSession;

import com.btxtech.game.jsre.playback.PlaybackStartupSeq;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.WebRequest;

/**
 * User: beat
 * Date: 04.08.2010
 * Time: 11:21:21
 */
public class PlaybackPage extends MgmtWebPage {
    public PlaybackPage() {
        HttpSession httpSession = ((WebRequest) getRequest()).getHttpServletRequest().getSession();
        Label startupSeqLabel = new Label("info", "");
        startupSeqLabel.add(new SimpleAttributeModifier("id", PlaybackEntry.ID));
        startupSeqLabel.add(new SimpleAttributeModifier(PlaybackEntry.START_UUID, (String)httpSession.getAttribute(PlaybackEntry.START_UUID)));
        add(startupSeqLabel);
        add(new Label("startupTaskText", PlaybackStartupSeq.COLD_PLAYBACK.getAbstractStartupTaskEnum()[0].getStartupTaskEnumHtmlHelper().getNiceText()));

    }
}
