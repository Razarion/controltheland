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

package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.client.ExtendedCanvas;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.widgetideas.graphics.client.Color;

/**
 * User: beat
 * Date: 18.04.2010
 * Time: 19:23:21
 */
public abstract class ResizeablePreviewWidget extends AbsolutePanel implements MouseMoveHandler, MouseUpHandler, MouseDownHandler {
    public enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST,
        NORTH_EAST,
        SOUTH_EAST,
        SOUTH_WEST,
        NORTH_WEST
    }

    private boolean hasMoved = false;
    private Image image;
    private Direction direction;
    private Rectangle absRectangle;
    private ExtendedCanvas marker;
    private FocusPanel focusPanel;

    protected ResizeablePreviewWidget(Image image, Direction direction, Rectangle absRectangle) {
        this.image = image;
        this.direction = direction;
        this.absRectangle = absRectangle.copy();
        DOM.setCapture(getElement());
        Rectangle relRectangle = absRectangle.copy();
        relRectangle.shift(-TerrainView.getInstance().getViewOriginLeft(), -TerrainView.getInstance().getViewOriginTop());
        RootPanel.get().add(this, relRectangle.getX(), relRectangle.getY());
        setPixelSize(relRectangle.getWidth(), relRectangle.getHeight());
        addMouseMoveHandler(this);
        addMouseUpHandler(this);
        addMouseDownHandler(this);
        image.getElement().getStyle().setZIndex(2);
        add(image);
        getElement().getStyle().setZIndex(Constants.Z_INDEX_PLACEABLE_PREVIEW);
        if (!GwtCommon.isIe6()) {
            image.getElement().getStyle().setProperty("filter", "alpha(opacity=50)");
        }
        image.getElement().getStyle().setProperty("opacity", "0.5");
        image.getElement().getStyle().setProperty("cursor", "move");
        image.setSize("100%", "100%");
        setupMarker();
        addEscKeyHandler();
    }

    private void addEscKeyHandler() {
        focusPanel = new FocusPanel();
        focusPanel.setPixelSize(1, 1);
        focusPanel.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        add(focusPanel);
        focusPanel.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent keyDownEvent) {
                if (keyDownEvent.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                    close();
                }
            }
        });
        focusPanel.setFocus(true);
    }

    private void setupMarker() {
        marker = new ExtendedCanvas(image.getOffsetWidth(), image.getOffsetHeight());
        marker.getElement().getStyle().setZIndex(1);
        add(marker, 0, 0);
        marker.setVisible(false);
        marker.setStrokeStyle(Color.RED);
        marker.setLineWidth(10);
        marker.beginPath();
        marker.moveTo(0, 0);
        marker.lineTo(image.getOffsetWidth() - 1, image.getOffsetHeight() - 1);
        marker.moveTo(0, image.getOffsetWidth() - 1);
        marker.lineTo(image.getOffsetWidth() - 1, 0);
        marker.stroke();
    }

    public void close() {
        focusPanel.setFocus(false);
        DOM.releaseCapture(getElement());
        RootPanel.get().remove(this);
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        int width;
        int height;
        int x = getAbsoluteLeft();
        int y = getAbsoluteTop();
        switch (direction) {
            case NORTH:
                y = event.getClientY();
                width = getOffsetWidth();
                height = +getAbsoluteTop() + getOffsetHeight() - event.getClientY();
                break;
            case EAST:
                width = event.getClientX() - getAbsoluteLeft();
                height = getOffsetHeight();
                break;
            case SOUTH:
                width = getOffsetWidth();
                height = event.getClientY() - getAbsoluteTop();
                break;
            case WEST:
                x = event.getClientX();
                width = getAbsoluteLeft() + getOffsetWidth() - event.getClientX();
                height = getOffsetHeight();
                break;
            case NORTH_EAST:
                y = event.getClientY();
                height = getAbsoluteTop() + getOffsetHeight() - event.getClientY();
                width = event.getClientX() - getAbsoluteLeft();
                break;
            case SOUTH_EAST:
                width = event.getClientX() - getAbsoluteLeft();
                height = event.getClientY() - getAbsoluteTop();
                break;
            case SOUTH_WEST:
                x = event.getClientX();
                height = event.getClientY() - getAbsoluteTop();
                width = getAbsoluteLeft() + getOffsetWidth() - event.getClientX();
                break;
            case NORTH_WEST:
                y = event.getClientY();
                x = event.getClientX();
                height = getAbsoluteTop() + getOffsetHeight() - event.getClientY();
                width = getAbsoluteLeft() + getOffsetWidth() - event.getClientX();
                break;
            default:
                throw new IllegalArgumentException(this + " unknown direction " + direction);
        }

        Rectangle rectangle = specialResize(new Rectangle(x, y, width, height));
        RootPanel.get().setWidgetPosition(this, rectangle.getStart().getX(), rectangle.getStart().getY());
        setPixelSize(rectangle.getWidth(), rectangle.getHeight());
        hasMoved = true;
        DOM.setCapture(this.getElement()); //IE6 need this to prevent losing of image
        MapWindow.getInstance().onMouseMove(event);
        if (allowedToPlace(rectangle)) {
            marker.setVisible(false);
        } else {
            marker.setVisible(true);
        }
    }

    /**
     * Override in subclass if resize behaves special
     *
     * @param rectangle input
     * @return special rectangle
     */
    protected Rectangle specialResize(Rectangle rectangle) {
        return rectangle;
    }

    /**
     * Override in subclass
     *
     * @param rectangle rectangle
     * @return true of allowed to place
     */
    protected boolean allowedToPlace(Rectangle rectangle) {
        return true;
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        Rectangle rectangle = specialResize(new Rectangle(getAbsoluteLeft(), getAbsoluteTop(), getOffsetWidth(), getOffsetHeight()));
        if (hasMoved && allowedToPlace(rectangle)) {
            close();
            rectangle.shift(TerrainView.getInstance().getViewOriginLeft(), TerrainView.getInstance().getViewOriginTop());
            switch (direction) {
                case NORTH:
                    absRectangle.setY(rectangle.getY());
                    break;
                case EAST:
                    absRectangle.setEndX(rectangle.getEndX());
                    break;
                case SOUTH:
                    absRectangle.setEndY(rectangle.getEndY());
                    break;
                case WEST:
                    absRectangle.setX(rectangle.getX());
                    break;
                case NORTH_EAST:
                    absRectangle.setEndX(rectangle.getEndX());
                    absRectangle.setY(rectangle.getY());
                    break;
                case SOUTH_EAST:
                    absRectangle.setEndX(rectangle.getEndX());
                    absRectangle.setEndY(rectangle.getEndY());
                    break;
                case SOUTH_WEST:
                    absRectangle.setX(rectangle.getX());
                    absRectangle.setEndY(rectangle.getEndY());
                    break;
                case NORTH_WEST:
                    absRectangle.setX(rectangle.getX());
                    absRectangle.setY(rectangle.getY());
                    break;
                default:
                    throw new IllegalArgumentException(this + " unknown direction " + direction);
            }
            execute(absRectangle);
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        GwtCommon.preventDefault(event);
    }

    protected abstract void execute(Rectangle rectangle);

    private HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
        return addDomHandler(handler, MouseMoveEvent.getType());
    }

    private HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
        return addDomHandler(handler, MouseDownEvent.getType());
    }

    private HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
        return addDomHandler(handler, MouseUpEvent.getType());
    }

    public Image getImage() {
        return image;
    }
}