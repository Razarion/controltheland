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

package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.cockpit.CursorHandler;
import com.btxtech.game.jsre.client.cockpit.CursorItemState;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

/**
 * User: beat
 * Date: May 20, 2009
 * Time: 2:48:36 PM
 */
public abstract class ClientSyncItemView extends AbsolutePanel implements MouseDownHandler, MouseOverHandler, SyncItemListener {
    private int viewOriginLeft;
    private int viewOriginTop;
    private Image image;
    private SyncItem syncItem;
    private CursorItemState cursorItemState;

    public ClientSyncItemView(SyncItem syncItem) {
        this.syncItem = syncItem;
        this.viewOriginLeft = TerrainView.getInstance().getViewOriginLeft();
        this.viewOriginTop = TerrainView.getInstance().getViewOriginTop();
        setupSize();
        setupImage();
        sinkEvents(Event.ONMOUSEMOVE);
        addDomHandler(this, MouseDownEvent.getType());
        addDomHandler(this, MouseOverEvent.getType());
        MapWindow.getAbsolutePanel().add(this, 0, 0);
        syncItem.addSyncItemListener(this);
    }

    protected void setupSize() {
        setPixelSize(syncItem.getItemType().getWidth(), syncItem.getItemType().getHeight());
    }

    public void setupImage() {
        if (image != null) {
            remove(image);
        }
        image = ImageHandler.getItemTypeImage(syncItem);
        image.addMouseDownHandler(this);
        image.sinkEvents(Event.ONMOUSEMOVE);
        image.getElement().getStyle().setZIndex(1);
        add(image);
        setWidgetPosition(image, 0, 0);
    }

    public void setViewOrigin(int left, int top) {
        viewOriginLeft = left;
        viewOriginTop = top;
        setPosition();
    }

    protected void setPosition() {
        if (syncItem.getPosition() == null) {
            return;
        }
        int x = syncItem.getPosition().getX() - viewOriginLeft - (syncItem.getItemType().getWidth() / 2);
        int y = syncItem.getPosition().getY() - viewOriginTop - (syncItem.getItemType().getHeight() / 2);
        MapWindow.getAbsolutePanel().setWidgetPosition(this, x, y);
    }

    public int getRelativeMiddleX() {
        return syncItem.getPosition().getX() - viewOriginLeft;
    }

    public int getRelativeMiddleY() {
        return syncItem.getPosition().getY() - viewOriginTop;
    }


    public void setSelected(boolean selected) {
        if (selected) {
            setStyleName("gwt-marked");
        } else {
            setStyleName("gwt-unmarked");
        }
    }

    public void dispose() {
        MapWindow.getAbsolutePanel().remove(this);
    }

    public SyncItem getSyncItem() {
        return syncItem;
    }

    protected Image getImage() {
        return image;
    }

    public abstract void update();

    public Id getId() {
        return syncItem.getId();
    }

    public void onMouseOver(MouseOverEvent event) {
        CursorHandler.getInstance().setItemCursor(this, cursorItemState);
    }

    public void setCursorItemState(CursorItemState cursorItemState) {
        this.cursorItemState = cursorItemState;
    }
}