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

import com.btxtech.game.jsre.client.ClientSyncItemView;
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
 * Date: 14.11.2009
 * Time: 15:09:42
 */
public class SelectionPanel extends HorizontalPanel implements SelectionListener {

    public SelectionPanel() {
        SelectionHandler.getInstance().addSelectionListener(this);
    }

    @Override
    public void onTargetSelectionChanged(ClientSyncItemView selection) {
        clear();
        addSelectedImage(selection.getSyncItem().getItemType(), 1);
    }

    @Override
    public void onSelectionCleared() {
        clear();
    }

    @Override
    public void onOwnSelectionChanged(Group selectedGroup) {
        clear();
        for (Map.Entry<ItemType, Collection<ClientSyncItemView>> itemTypeCollectionEntry : selectedGroup.getGroupedItems().entrySet()) {
            addSelectedImage(itemTypeCollectionEntry.getKey(), itemTypeCollectionEntry.getValue().size());
        }
    }

    private void addSelectedImage(ItemType itemType, int count) {
        AbsolutePanel absolutePanel = new AbsolutePanel();
        add(absolutePanel);
        absolutePanel.setPixelSize(64, 64);
        Image selectedImage = ImageHandler.getItemTypeImage(itemType);
        selectedImage.setSize("64px", "64px");
        absolutePanel.add(selectedImage);
        if (count > 1) {
            Label number = new Label(Integer.toString(count));
            number.getElement().getStyle().setColor("#FFFFFF");
            absolutePanel.add(number, 0, 0);
        }
    }

}
