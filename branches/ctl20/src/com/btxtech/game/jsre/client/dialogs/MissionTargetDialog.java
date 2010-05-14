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

package com.btxtech.game.jsre.client.dialogs;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * User: beat
 * Date: 12.05.2010
 * Time: 10:10:50
 */
public class MissionTargetDialog extends PopupPanel {
    private HTML html;

    public MissionTargetDialog() {
        super(true);
        html = new HTML();
        html.setPixelSize(600, 500);
        setWidget(html);
    }

    public void setMissionTarget(String htmlString) {
        html.setHTML(htmlString);
    }

}
