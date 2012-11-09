package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.utg.tip.GameTipManager;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.TerrainInGameTipVisualization;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.canvas.dom.client.Context2d;

import java.util.Collection;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 11:46
 */
public class InGameTipRenderTask extends AbstractRenderTask {
    private static final int TERRAIN_HINT_LENGTH = 150;
    private static final String CORNER_COLOR = "#00FF00";
    private Context2d context2d;

    public InGameTipRenderTask(Context2d context2d) {
        this.context2d = context2d;
    }

    @Override
    public void render(long timeStamp, Collection<SyncItem> itemsInView, Rectangle viewRect, final Rectangle tileViewRect) {
        GameTipVisualization gameTipVisualization = GameTipManager.getInstance().getGameTipVisualization();
        if (gameTipVisualization == null) {
            return;
        }
        // To be placed hint
        if (gameTipVisualization instanceof TerrainInGameTipVisualization) {
            context2d.save();
            TerrainInGameTipVisualization terrainInGameTipVisualization = (TerrainInGameTipVisualization) gameTipVisualization;
            Index terrainPos = terrainInGameTipVisualization.getTerrainPosition(viewRect);
            context2d.translate(terrainPos.getX(), terrainPos.getY());
            int distance = (TERRAIN_HINT_LENGTH - (int) (TERRAIN_HINT_LENGTH * (timeStamp & 1000) / 1000.0)) / 2;
            context2d.drawImage(CanvasElementLibrary.getTlCorner(CORNER_COLOR), -distance, -distance);
            context2d.drawImage(CanvasElementLibrary.getTrCorner(CORNER_COLOR), distance, -distance);
            context2d.drawImage(CanvasElementLibrary.getBlCorner(CORNER_COLOR), -distance, distance);
            context2d.drawImage(CanvasElementLibrary.getBrCorner(CORNER_COLOR), distance, distance);
            context2d.restore();
        }
        // Render Arrow
        context2d.save();
        Index relativeArrowPosition = gameTipVisualization.getArrowHotSpot(viewRect, timeStamp);
        context2d.translate(relativeArrowPosition.getX(), relativeArrowPosition.getY());
        context2d.rotate(gameTipVisualization.getArrowAngel());
        context2d.drawImage(CanvasElementLibrary.getArrow(), -CanvasElementLibrary.ARROW_WIDTH_TOTAL / 2, 0);
        context2d.restore();
        // Render Mouse
        Index relativeMousePosition = gameTipVisualization.getMousePosition(viewRect, timeStamp);
        if (relativeMousePosition != null) {
            context2d.drawImage(CanvasElementLibrary.getMouse(), relativeMousePosition.getX(), relativeMousePosition.getY());
            if (timeStamp / 300 % 2 == 0) {
                context2d.drawImage(CanvasElementLibrary.getMouseButtonDown(), relativeMousePosition.getX(), relativeMousePosition.getY());
            }
        }
    }
}
