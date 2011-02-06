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

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

/**
 * User: beat
 * Date: 17.11.2010
 * Time: 18:37:20
 */
public class EnemySelectedPanel extends AbsolutePanel {
    private static final int IMAGE_LEFT = 20;
    private static final int IMAGE_TOP = 20;
    private static final int DESCRIPTION_LEFT = 6;
    private static final int DESCRIPTION_TOP = 102;
    private static final int DESCRIPTION_WIDTH = 124;
    private static final int DESCRIPTION_HEIGHT = 83;
    private HTML description;
    private Image image;

    public EnemySelectedPanel() {
        getElement().getStyle().setBackgroundImage("url(/images/cockpit/enemyselection.png)");
        setPixelSize(SelectedItemPanel.WIDTH, SelectedItemPanel.HEIGHT);
        description = new HTML();
        description.setPixelSize(DESCRIPTION_WIDTH,DESCRIPTION_HEIGHT);
        add(description, DESCRIPTION_LEFT, DESCRIPTION_TOP);
    }

    public void display(SyncBaseItem syncBaseItem) {
        // Description
        StringBuilder builder = new StringBuilder();
        builder.append("This item belongs to <b>");
        builder.append(ClientBase.getInstance().getBaseName(syncBaseItem.getBase()));
        builder.append("</b>. This is your enemy!<br/></b>Attack it!</b>");
        if (Game.isDebug()) {
            builder.append("<br/>ID: ");
            builder.append(syncBaseItem.getId());
        }
        description.setHTML(builder.toString());

        // Image
        if (image != null) {
            remove(image);
        }
        image = ImageHandler.getItemTypeImage(syncBaseItem.getItemType());
        image.setSize("64px", "64px");
        add(image, IMAGE_LEFT, IMAGE_TOP);

    }
}
