package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.utg.tip.GameTipManager;
import com.btxtech.game.jsre.client.utg.tip.OverlayTipPanel;
import com.btxtech.game.jsre.client.utg.tip.visualization.ItemCockpitGameOverlayTipVisualization;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.canvas.dom.client.Context2d;

import java.util.Collection;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 11:46
 */
public class OverlayTipRenderTask extends AbstractRenderTask {
    private Context2d context2d;

    public OverlayTipRenderTask(Context2d context2d) {
        this.context2d = context2d;
    }

    @Override
    public void render(long timeStamp, Collection<SyncItem> itemsInView, Rectangle viewRect, final Rectangle tileViewRect) {
        context2d.clearRect(0, 0, OverlayTipPanel.WIDTH, OverlayTipPanel.WIDTH);
        ItemCockpitGameOverlayTipVisualization overlayTipVisualization = GameTipManager.getInstance().getOverlayVisualization();
        if (overlayTipVisualization == null) {
            return;
        }
        // Render Arrow
        context2d.save();
        Index relativeArrowPosition = overlayTipVisualization.getArrowHotSpot(viewRect);
        context2d.translate(relativeArrowPosition.getX(), relativeArrowPosition.getY());
        context2d.rotate(overlayTipVisualization.getArrowAngel());
        context2d.drawImage(CanvasElementLibrary.getArrow(), -CanvasElementLibrary.ARROW_WIDTH_TOTAL / 2, 0);
        context2d.restore();
        // Render Mouse
        // Render Mouse
        Index relativeMousePosition = overlayTipVisualization.getMousePosition(viewRect);
        context2d.drawImage(CanvasElementLibrary.getMouse(), relativeMousePosition.getX(), relativeMousePosition.getY());
        if(timeStamp / 300 % 2 == 0) {
            context2d.drawImage(CanvasElementLibrary.getMouseButtonDown(), relativeMousePosition.getX(), relativeMousePosition.getY());
        }
    }

}
