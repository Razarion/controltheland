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

package com.btxtech.game.jsre.playback;

import com.btxtech.game.jsre.client.TopMapPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 08.08.2010
 * Time: 12:01:44
 */
public class PlaybackControlPanel extends TopMapPanel {
    private Label state;
    private Label time;
    private PlaybackEntry playbackEntry;

    public PlaybackControlPanel(PlaybackEntry playbackEntry) {
        this.playbackEntry = playbackEntry;
    }

    @Override
    protected Widget createBody() {
        VerticalPanel verticalPanel = new VerticalPanel();

        // Play state
        state = new Label("???");
        verticalPanel.add(state);

        // Time
        time = new Label("???");
        verticalPanel.add(time);

        // Reply button
        verticalPanel.add(new Button("Replay", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                playbackEntry.play();
            }
        }));

        // Skip frame
        verticalPanel.add(new Button("Skip", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                playbackEntry.skip();
            }
        }));

        return verticalPanel;
    }

    public void setState(String text) {
        state.setText(text);
    }

    public void setTime(long time) {
        this.time.setText(Double.toString(time / 1000.0));
    }
}
