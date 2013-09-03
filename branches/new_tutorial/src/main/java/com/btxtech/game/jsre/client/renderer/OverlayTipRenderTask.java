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
    private static final int ARROW_MOVE = 100;
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
        Index relativeArrowPosition = overlayTipVisualization.getArrowHotSpot(viewRect, timeStamp);
        context2d.translate(relativeArrowPosition.getX(), relativeArrowPosition.getY());
        context2d.rotate(overlayTipVisualization.getArrowAngel());
        int distance = (ARROW_MOVE - (int) (ARROW_MOVE * (timeStamp & 500) / 500.0)) / 2;
        context2d.drawImage(CanvasElementLibrary.getArrow(), -CanvasElementLibrary.ARROW_WIDTH_TOTAL / 2, distance);
        context2d.restore();
    }

}
