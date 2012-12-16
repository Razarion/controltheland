package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.utg.tip.GameTipManager;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.canvas.dom.client.Context2d;

import java.util.Collection;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 11:46
 */
public class InGameTipBottomRenderTask extends AbstractRenderTask {
    private static final String ITEM_MARKER = "#00FF00";
    private static final int TERRAIN_HINT_LENGTH = 150;
    private Context2d context2d;

    public InGameTipBottomRenderTask(Context2d context2d) {
        this.context2d = context2d;
    }

    @Override
    public void render(long timeStamp, Collection<SyncItem> itemsInView, Rectangle viewRect, final Rectangle tileViewRect) {
        GameTipVisualization gameTipVisualization = GameTipManager.getInstance().getGameTipVisualization();
        if (gameTipVisualization == null) {
            return;
        }
        // Render edges
        Index relativeItemMarker = gameTipVisualization.getItemMarkerMiddle(viewRect);
        if(relativeItemMarker != null) {
            int distance = (TERRAIN_HINT_LENGTH - (int) (TERRAIN_HINT_LENGTH * (timeStamp & 1000) / 1000.0)) / 2;
            context2d.save();
            context2d.translate(relativeItemMarker.getX(), relativeItemMarker.getY());
            context2d.drawImage(CanvasElementLibrary.getTlCorner(ITEM_MARKER), -distance, -distance);
            context2d.drawImage(CanvasElementLibrary.getTrCorner(ITEM_MARKER), distance, -distance);
            context2d.drawImage(CanvasElementLibrary.getBlCorner(ITEM_MARKER), -distance, distance);
            context2d.drawImage(CanvasElementLibrary.getBrCorner(ITEM_MARKER), distance, distance);
            context2d.restore();
        }
    }
}
