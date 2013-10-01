package com.btxtech.game.jsre.client.utg.tip;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.renderer.CanvasElementLibrary;
import com.btxtech.game.jsre.client.renderer.OverlayTipRenderTask;
import com.btxtech.game.jsre.client.renderer.Renderer;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.common.Html5NotSupportedException;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;

/**
 * User: beat
 * Date: 23.08.12
 * Time: 23:36
 */
public class OverlayTipPanel {
    public static final int WIDTH = CanvasElementLibrary.MOUSE_WIDTH_TOTAL + CanvasElementLibrary.ARROW_WIDTH_TOTAL;
    public static final int HEIGHT = CanvasElementLibrary.MOUSE_HEIGHT_TOTAL;
    private Canvas canvas;
    private OverlayTipRenderTask overlayTipRenderTask;

    public void create(Index absoluteArrowHotSpot) {
        canvas = Canvas.createIfSupported();
        if (canvas == null) {
            throw new Html5NotSupportedException("OverlayTipPanel.create()");
        }

        canvas.setCoordinateSpaceWidth(WIDTH);
        canvas.setCoordinateSpaceHeight(HEIGHT);

        MapWindow.getAbsolutePanel().add(canvas, absoluteArrowHotSpot.getX() - CanvasElementLibrary.ARROW_WIDTH_TOTAL / 2, 0);
        canvas.getElement().getStyle().setZIndex(Constants.Z_INDEX_TIP_GAME_OVERLAY);
        canvas.getElement().getStyle().clearTop();
        canvas.getElement().getStyle().setBottom(MapWindow.getAbsolutePanel().getOffsetHeight() - absoluteArrowHotSpot.getY(), Style.Unit.PX);

        overlayTipRenderTask = new OverlayTipRenderTask(canvas.getContext2d());
        Renderer.getInstance().startOverlayRenderTask(overlayTipRenderTask);
    }

    public void close() {
        if (canvas != null) {
            MapWindow.getAbsolutePanel().remove(canvas);
            Renderer.getInstance().stopOverlayRenderTask(overlayTipRenderTask);
            canvas = null;
        }
    }
}
