package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.utg.tip.GameTipManager;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.TerrainInGameTipVisualization;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 11:46
 */
public class InGameTipRenderTask extends AbstractRenderTask {
    private static final int TERRAIN_HINT_LENGTH = 150;
    private static final int CORNER_LINE_THICKNESS = 2;
    private static final int CORNER_LENGTH = 8;
    private static final int CORNER_TOTAL_LENGTH = 2 * CORNER_LINE_THICKNESS + CORNER_LENGTH;
    private Context2d context2d;
    private CanvasElement tlCorner;
    private CanvasElement trCorner;
    private CanvasElement blCorner;
    private CanvasElement brCorner;

    public InGameTipRenderTask(Context2d context2d) {
        this.context2d = context2d;
    }

    @Override
    public void render(long timeStamp, Rectangle viewRect, final Rectangle tileViewRect) {
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
            if (tlCorner == null) {
                createCorners();
            }
            context2d.drawImage(tlCorner, -distance, -distance);
            context2d.drawImage(trCorner, distance, -distance);
            context2d.drawImage(blCorner, -distance, distance);
            context2d.drawImage(brCorner, distance, distance);
            context2d.restore();
        }
        // Render Arrow
        context2d.save();
        Index relativeArrowPosition = gameTipVisualization.getArrowHotSpot(viewRect);
        context2d.translate(relativeArrowPosition.getX(), relativeArrowPosition.getY());
        context2d.rotate(gameTipVisualization.getArrowAngel());
        context2d.drawImage(CanvasElementLibrary.getArrow(), -CanvasElementLibrary.ARROW_WIDTH_TOTAL / 2, 0);
        context2d.restore();
        // Render Mouse
        Index relativeMousePosition = gameTipVisualization.getMousePosition(viewRect);
        context2d.drawImage(CanvasElementLibrary.getMouse(), relativeMousePosition.getX(), relativeMousePosition.getY());
        if (timeStamp / 300 % 2 == 0) {
            context2d.drawImage(CanvasElementLibrary.getMouseButtonDown(), relativeMousePosition.getX(), relativeMousePosition.getY());
        }
    }

    private void createCorners() {
        Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(CORNER_TOTAL_LENGTH);
        canvas.setCoordinateSpaceHeight(CORNER_TOTAL_LENGTH);
        Context2d context2d = canvas.getContext2d();
        context2d.setStrokeStyle("#FF0000");
        context2d.setLineWidth(CORNER_LINE_THICKNESS);
        context2d.translate(CORNER_LINE_THICKNESS, CORNER_LINE_THICKNESS);
        context2d.beginPath();
        context2d.moveTo(0, CORNER_LENGTH);
        context2d.lineTo(0, 0);
        context2d.lineTo(CORNER_LENGTH, 0);
        context2d.setShadowBlur(3);
        context2d.setShadowColor("black");
        context2d.stroke();
        tlCorner = canvas.getCanvasElement();

        canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(CORNER_TOTAL_LENGTH);
        canvas.setCoordinateSpaceHeight(CORNER_TOTAL_LENGTH);
        context2d = canvas.getContext2d();
        context2d.setStrokeStyle("#FF0000");
        context2d.setLineWidth(CORNER_LINE_THICKNESS);
        context2d.translate(CORNER_LINE_THICKNESS, CORNER_LINE_THICKNESS);
        context2d.beginPath();
        context2d.moveTo(0, 0);
        context2d.lineTo(CORNER_LENGTH, 0);
        context2d.lineTo(CORNER_LENGTH, CORNER_LENGTH);
        context2d.setShadowBlur(3);
        context2d.setShadowColor("black");
        context2d.stroke();
        trCorner = canvas.getCanvasElement();

        canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(CORNER_TOTAL_LENGTH);
        canvas.setCoordinateSpaceHeight(CORNER_TOTAL_LENGTH);
        context2d = canvas.getContext2d();
        context2d.setStrokeStyle("#FF0000");
        context2d.setLineWidth(CORNER_LINE_THICKNESS);
        context2d.translate(CORNER_LINE_THICKNESS, CORNER_LINE_THICKNESS);
        context2d.beginPath();
        context2d.moveTo(0, 0);
        context2d.lineTo(0, CORNER_LENGTH);
        context2d.lineTo(CORNER_LENGTH, CORNER_LENGTH);
        context2d.setShadowBlur(3);
        context2d.setShadowColor("black");
        context2d.stroke();
        blCorner = canvas.getCanvasElement();

        canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(CORNER_TOTAL_LENGTH);
        canvas.setCoordinateSpaceHeight(CORNER_TOTAL_LENGTH);
        context2d = canvas.getContext2d();
        context2d.setStrokeStyle("#FF0000");
        context2d.setLineWidth(CORNER_LINE_THICKNESS);
        context2d.translate(CORNER_LINE_THICKNESS, CORNER_LINE_THICKNESS);
        context2d.beginPath();
        context2d.moveTo(0, CORNER_LENGTH);
        context2d.lineTo(CORNER_LENGTH, CORNER_LENGTH);
        context2d.lineTo(CORNER_LENGTH, 0);
        context2d.setShadowBlur(3);
        context2d.setShadowColor("black");
        context2d.stroke();
        brCorner = canvas.getCanvasElement();
    }
}
