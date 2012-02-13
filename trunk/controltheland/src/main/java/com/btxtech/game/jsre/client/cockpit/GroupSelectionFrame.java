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
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainMouseButtonListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import java.util.Collection;

/**
 * User: beat
 * Date: 08.11.2009
 * Time: 11:55:18
 */
public class GroupSelectionFrame extends AbsolutePanel implements MouseMoveHandler, MouseDownHandler, MouseUpHandler {
    public static final int MIN_PIXEL_FRAME_SIZE = 10;
    private int originX;
    private int originY;
    private TerrainMouseButtonListener alternativeTerrainMouseButtonListener;
    private Rectangle selection;

    public GroupSelectionFrame(int x, int y, TerrainMouseButtonListener alternativeTerrainMouseButtonListener) {
        originX = x;
        originY = y;
        this.alternativeTerrainMouseButtonListener = alternativeTerrainMouseButtonListener;
        setPixelSize(0, 0);
        getElement().getStyle().setBorderColor("#FFFFFF");
        getElement().getStyle().setBorderWidth(1.0, Style.Unit.PX);
        getElement().getStyle().setBorderStyle(Style.BorderStyle.DASHED);
        getElement().getStyle().setZIndex(Constants.Z_INDEX_GROUP_SELECTION_FRAME);
        MapWindow.getAbsolutePanel().add(this, x, y);
        DOM.setCapture(getElement());
        addMouseMoveHandler(this);
        addMouseDownHandler(this);
        addMouseUpHandler(this);
    }

    private HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
        return addDomHandler(handler, MouseMoveEvent.getType());
    }

    private HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
        return addDomHandler(handler, MouseDownEvent.getType());
    }

    private HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
        return addDomHandler(handler, MouseUpEvent.getType());
    }

    @Override
    public void onMouseMove(MouseMoveEvent mouseMoveEvent) {
        int relX = mouseMoveEvent.getRelativeX(MapWindow.getAbsolutePanel().getElement());
        if (relX < 0) {
            relX = 0;
        }
        int width = Math.abs(relX - originX);
        int x = Math.min(originX, relX);

        int relY = mouseMoveEvent.getRelativeY(MapWindow.getAbsolutePanel().getElement());
        if (relY < 0) {
            relY = 0;
        }
        int height = Math.abs(relY - originY);
        int y = Math.min(originY, relY);

        MapWindow.getAbsolutePanel().setWidgetPosition(this, x, y);
        setPixelSize(width, height);
        selection = new Rectangle(x + TerrainView.getInstance().getViewOriginLeft(), y + TerrainView.getInstance().getViewOriginTop(), width, height);
    }

    @Override
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
        finalizeSelection(mouseDownEvent);
    }

    @Override
    public void onMouseUp(MouseUpEvent mouseUpEvent) {
        finalizeSelection(mouseUpEvent);
    }

    private void finalizeSelection(MouseEvent mouseEvent) {
        DOM.releaseCapture(getElement());
        MapWindow.getAbsolutePanel().remove(this);
        if (selection == null) {
            unsuccessfulSelection(mouseEvent);
            return;
        }
        if (!selection.hasMinSize(MIN_PIXEL_FRAME_SIZE)) {
            unsuccessfulSelection(mouseEvent);
            return;
        }
        Collection<SyncBaseItem> selectedItems = ItemContainer.getInstance().getBaseItemsInRectangle(selection, ClientBase.getInstance().getSimpleBase(), null);
        if (selectedItems.isEmpty()) {
            return;
        }
        SelectionHandler.getInstance().setItemGroupSelected(new Group(selectedItems));
    }

    private void unsuccessfulSelection(MouseEvent mouseEvent) {
        int x = mouseEvent.getRelativeX(TerrainView.getInstance().getCanvas().getElement()) + TerrainView.getInstance().getViewOriginLeft();
        int y = mouseEvent.getRelativeY(TerrainView.getInstance().getCanvas().getElement()) + TerrainView.getInstance().getViewOriginTop();

        if (mouseEvent instanceof MouseUpEvent) {
            alternativeTerrainMouseButtonListener.onMouseUp(x, y, (MouseUpEvent) mouseEvent);
        } else if (mouseEvent instanceof MouseDownEvent) {
            alternativeTerrainMouseButtonListener.onMouseDown(x, y, (MouseDownEvent) mouseEvent);
        }
    }


}
