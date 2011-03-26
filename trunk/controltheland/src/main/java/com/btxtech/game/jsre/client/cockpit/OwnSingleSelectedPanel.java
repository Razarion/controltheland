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

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.tutorial.CockpitSpeechBubbleHintConfig;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 17.11.2010
 * Time: 18:37:20
 */
public class OwnSingleSelectedPanel extends AbsolutePanel implements HintWidgetProvider {
    private static final int IMAGE_LEFT = 20;
    private static final int IMAGE_TOP = 20;
    private static final int DESCRIPTION_LEFT = 119;
    private static final int DESCRIPTION_TOP = 4;
    private static final int SPECIAL_FUNCTION_LEFT = 233;
    private static final int SPECIAL_FUNCTION_TOP = 8;
    private static final int DESCRIPTION_WIDTH = 84;
    private static final int DESCRIPTION_HEIGHT = 80;
    private static final int BUILDUP_ITEM_LEFT = 0;
    private static final int BUILDUP_ITEM_TOP = 100;
    private HTML description;
    private Image image;
    private SpecialFunctionPanel specialFunctionPanel;
    private BuildupItemPanel buildupItemPanel;

    public OwnSingleSelectedPanel() {
        getElement().getStyle().setBackgroundImage("url(/images/cockpit/ownsingleselection.png)");
        setPixelSize(SelectedItemPanel.WIDTH, SelectedItemPanel.HEIGHT);
        description = new HTML();
        description.setPixelSize(DESCRIPTION_WIDTH, DESCRIPTION_HEIGHT);
        add(description, DESCRIPTION_LEFT, DESCRIPTION_TOP);
        specialFunctionPanel = new SpecialFunctionPanel();
        add(specialFunctionPanel, SPECIAL_FUNCTION_LEFT, SPECIAL_FUNCTION_TOP);
        buildupItemPanel = new BuildupItemPanel();
        add(buildupItemPanel, BUILDUP_ITEM_LEFT, BUILDUP_ITEM_TOP);
    }

    public void display(ClientSyncItem clientSyncItem) {
        // Description
        SyncBaseItem syncBaseItem = clientSyncItem.getSyncBaseItem();
        StringBuilder builder = new StringBuilder();
        builder.append(syncBaseItem.getItemType().getDescription());
        if (!syncBaseItem.isReady()) {
            builder.append("<br/> Build up: ");
            builder.append(Integer.toString((int) (syncBaseItem.getBuildup() * 100.0)));
            builder.append("%");
        }
        if (Game.isDebug()) {
            builder.append("<br/>ID: ");
            builder.append(syncBaseItem.getId());
        }
        description.setHTML(builder.toString());
        specialFunctionPanel.display(syncBaseItem);

        // Image
        if (image != null) {
            remove(image);
        }
        image = ImageHandler.getItemTypeImage(syncBaseItem.getItemType());
        image.setSize("64px", "64px");
        add(image, IMAGE_LEFT, IMAGE_TOP);

        // Buildup
        buildupItemPanel.display(clientSyncItem);
    }

    @Override
    public Widget getHintWidgetAndEnsureVisible(CockpitSpeechBubbleHintConfig config) throws HintWidgetException {
        return buildupItemPanel.getHintWidgetAndEnsureVisible(config);
    }
}
