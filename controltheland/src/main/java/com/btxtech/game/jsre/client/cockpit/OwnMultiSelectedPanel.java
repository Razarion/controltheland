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
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import java.util.Collection;
import java.util.Map;

/**
 * User: beat
 * Date: 17.11.2010
 * Time: 18:37:20
 */
public class OwnMultiSelectedPanel extends AbsolutePanel {
    private static final int ITEM_TYPES_LEFT = 20;
    private static final int ITEM_TYPES_TOP = 15;
    private static final int BUILDUP_ITEM_LEFT = 0;
    private static final int BUILDUP_ITEM_TOP = 100;
    private BuildupItemPanel buildupItemPanel;
    private HorizontalPanel itemTypes;

    public OwnMultiSelectedPanel() {
        getElement().getStyle().setBackgroundImage("url(/images/cockpit/ownmultiselection.png)");
        setPixelSize(SelectedItemPanel.WIDTH, SelectedItemPanel.HEIGHT);

        buildupItemPanel = new BuildupItemPanel();
        add(buildupItemPanel, BUILDUP_ITEM_LEFT, BUILDUP_ITEM_TOP);

        itemTypes = new HorizontalPanel();
        add(itemTypes, ITEM_TYPES_LEFT, ITEM_TYPES_TOP);

    }

    public void display(Group selectedGroup) {
        itemTypes.clear();
        for (Map.Entry<ItemType, Collection<ClientSyncItem>> entry : selectedGroup.getGroupedItems().entrySet()) {
            int count = entry.getValue().size();
            AbsolutePanel absolutePanel = new AbsolutePanel();
            itemTypes.add(absolutePanel);
            absolutePanel.setPixelSize(64, 64);
            Image selectedImage = ImageHandler.getItemTypeImage(entry.getKey());
            selectedImage.setSize("64px", "64px");
            absolutePanel.add(selectedImage);
            if (count > 1) {
                Label number = new Label(Integer.toString(count));
                number.getElement().getStyle().setColor("#FFFFFF");
                absolutePanel.add(number, 0, 0);
            }
        }
        buildupItemPanel.display(selectedGroup);

    }

    public void onMoneyChanged(double accountBalance) {
        buildupItemPanel.onMoneyChanged(accountBalance);
    }

    public void onStateChanged() {
        buildupItemPanel.onStateChanged();
    }
}
