package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.cockpit.CockpitMode;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasPattern;
import com.google.gwt.canvas.dom.client.Context2d;

import java.util.Collection;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 11:46
 */
public class SelectionFrameRenderTask extends AbstractRenderTask {
    private Context2d context2d;
    private CanvasPattern strokePatter;

    public SelectionFrameRenderTask(Context2d context2d) {
        this.context2d = context2d;
        // Stroke pattern
        Canvas patterCanvas = Canvas.createIfSupported();
        patterCanvas.setCoordinateSpaceWidth(4);
        patterCanvas.setCoordinateSpaceHeight(4);
        Context2d patternContext = patterCanvas.getContext2d();
        patternContext.setFillStyle("#FFFFFF");
        patternContext.fillRect(0, 0, 2, 2);
        patternContext.fillRect(2, 2, 2, 2);
        patternContext.setFillStyle("#000000");
        patternContext.fillRect(2, 0, 2, 2);
        patternContext.fillRect(0,2,2,2);
        strokePatter = context2d.createPattern(patterCanvas.getCanvasElement(), Context2d.Repetition.REPEAT);

    }

    @Override
    public void render(long timeStamp, Collection<SyncItem> itemsInView, Rectangle viewRect, final Rectangle tileViewRect) {
        if (!CockpitMode.getInstance().hasGroupSelectionFrame()) {
            return;
        }
        Rectangle rectangle = CockpitMode.getInstance().getGroupSelectionFrame().getRectangle();
        if (rectangle == null) {
            return;
        }
        context2d.setStrokeStyle(strokePatter);
        context2d.setLineWidth(2);
        Index start = rectangle.getStart().sub(viewRect.getStart());
        context2d.strokeRect(start.getX(), start.getY(), rectangle.getWidth(), rectangle.getHeight());
    }
}
