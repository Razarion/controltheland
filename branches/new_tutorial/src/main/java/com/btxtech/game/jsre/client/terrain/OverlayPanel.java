package com.btxtech.game.jsre.client.terrain;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.common.Html5NotSupportedException;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

/**
 * User: beat
 * Date: 24.08.12
 * Time: 18:20
 */
public class OverlayPanel {
    private static final OverlayPanel INSTANCE = new OverlayPanel();
    private Canvas canvas;
    private HandlerRegistration resizeHandlerRegistration;

    public static OverlayPanel getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private OverlayPanel() {
    }

    public void create() {
        destroy();
        canvas = Canvas.createIfSupported();
        if (canvas == null) {
            throw new Html5NotSupportedException("OverlayPanel.create()");
        }
        canvas.setTabIndex(1); // IE9 need this to receive the focus
        MapWindow.getAbsolutePanel().add(canvas, 0, 0);
        updateSize();
        resizeHandlerRegistration = Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent resizeEvent) {
                updateSize();
            }
        });
        canvas.getElement().getStyle().setZIndex(Constants.Z_INDEX_GAME_OVERLAY);
        canvas.addMouseMoveHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                TerrainView.getInstance().getTerrainMouseHandler().onMouseMove(event);
            }
        });
        canvas.addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                TerrainView.getInstance().getTerrainMouseHandler().onOverlayMouseUp(event);
            }
        });
        canvas.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                TerrainView.getInstance().getTerrainMouseHandler().onOverlayMouseDown(event);
            }
        });
        canvas.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                TerrainView.getInstance().getTerrainKeyHandler().onKeyDown(event);
            }
        });
        canvas.setFocus(true);
    }

    public void destroy() {
        if (canvas != null) {
            MapWindow.getAbsolutePanel().remove(canvas);
            canvas = null;
        }
        if (resizeHandlerRegistration != null) {
            resizeHandlerRegistration.removeHandler();
            resizeHandlerRegistration = null;
        }
    }


    public void updateSize() {
        canvas.setCoordinateSpaceWidth(MapWindow.getAbsolutePanel().getOffsetWidth());
        canvas.setCoordinateSpaceHeight(MapWindow.getAbsolutePanel().getOffsetHeight());
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
