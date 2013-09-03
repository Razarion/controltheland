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
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

import java.util.Collection;

/**
 * User: beat
 * Date: 08.11.2009
 * Time: 11:55:18
 */
public class GroupSelectionFrame {
    private static final int MIN_PIXEL_FRAME_SIZE = 10;
    private int startX;
    private int startY;
    private Rectangle rectangle;

    public GroupSelectionFrame(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
    }

    public void onMove(int absoluteX, int absoluteY) {
        generateSelectionRectangle(absoluteX, absoluteY);
    }

    private void generateSelectionRectangle(int absoluteX, int absoluteY) {
        int x = Math.min(absoluteX, startX);
        int y = Math.min(absoluteY, startY);
        int width = Math.abs(absoluteX - startX);
        int height = Math.abs(absoluteY - startY);
        rectangle = new Rectangle(x, y, width, height);
    }

    public void doSelection() {
        if (rectangle == null) {
            return;
        }
        Collection<SyncBaseItem> selectedItems = ItemContainer.getInstance().getBaseItemsInRectangle(rectangle, ClientBase.getInstance().getSimpleBase(), null);
        if (selectedItems.isEmpty()) {
            return;
        }
        SelectionHandler.getInstance().setItemGroupSelected(new Group(selectedItems));
    }

    public boolean execute(int absoluteX, int absoluteY) {
        generateSelectionRectangle(absoluteX, absoluteY);
        if (rectangle == null) {
            return false;
        }
        if (!rectangle.hasMinSize(MIN_PIXEL_FRAME_SIZE)) {
            return false;
        }
        Collection<SyncBaseItem> selectedItems = ItemContainer.getInstance().getBaseItemsInRectangle(rectangle, ClientBase.getInstance().getSimpleBase(), null);
        if (selectedItems.isEmpty()) {
            return true;
        }
        SelectionHandler.getInstance().setItemGroupSelected(new Group(selectedItems));
        return true;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }
}
