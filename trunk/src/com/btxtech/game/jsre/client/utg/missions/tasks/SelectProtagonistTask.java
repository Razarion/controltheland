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

import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.utg.SpeechBubble;

/**
 * User: beat
 * Date: 17.02.2010
 * Time: 20:03:23
 */
public class SelectProtagonistTask extends Task {

    public SelectProtagonistTask(String html) {
        super(html);
    }

    @Override
    public String getName() {
        return "Select protagonist";
    }

    @Override
    public void run() {
        setSpeechBubble(new SpeechBubble(getMission().getProtagonist(), getHtml(), false));
    }

    @Override
    public void onOwnSelectionChanged(Group selectedGroup) {
        activateNextTask();
    }

    @Override
    public boolean canSkip() {
        Group ownSelection = SelectionHandler.getInstance().getOwnSelection();
        return ownSelection != null && ownSelection.contains(getMission().getProtagonist());
    }
}