package com.btxtech.game.jsre.regioneditor;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.FocusWidget;

/**
 * User: beat
 * Date: 09.09.12
 * Time: 17:14
 */
public class MouseAndKeyRegionEditorControl implements MouseMoveHandler, MouseOutHandler, MouseDownHandler, MouseUpHandler, KeyDownHandler, MouseOverHandler {
    private RegionEditorModel regionEditorModel;
    private boolean leftMouseDown;

    public MouseAndKeyRegionEditorControl(RegionEditorModel regionEditorModel) {
        this.regionEditorModel = regionEditorModel;
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        Index mouseIndex = new Index(event.getX(), event.getY());
        regionEditorModel.setMouseOverTile(mouseIndex);
        if (leftMouseDown) {
            regionEditorModel.tileSelected(mouseIndex);
        }
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        regionEditorModel.setMouseOverTile(null);
        leftMouseDown = false;
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
            leftMouseDown = true;
            regionEditorModel.tileSelected(new Index(event.getX(), event.getY()));
        }
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
            leftMouseDown = false;
        }
    }

    @Override
    public void onMouseOver(MouseOverEvent event) {
        ((FocusWidget)(event.getSource())).setFocus(true);
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        switch (event.getNativeKeyCode()) {
            case KeyCodes.KEY_LEFT: {
                regionEditorModel.scroll(-1, 0);
                break;
            }
            case KeyCodes.KEY_RIGHT: {
                regionEditorModel.scroll(1, 0);
                break;
            }
            case KeyCodes.KEY_UP: {
                regionEditorModel.scroll(0, -1);
                break;
            }
            case KeyCodes.KEY_DOWN: {
                regionEditorModel.scroll(0, 1);
                break;
            }
        }


    }
}
