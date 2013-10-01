package com.btxtech.game.jsre.regioneditor;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;
import com.google.gwt.canvas.dom.client.Context2d;

/**
 * User: beat
 * Date: 09.09.12
 * Time: 15:53
 */
public class GridRenderTask extends AbstractRegionEditorRenderTask {
    public GridRenderTask(RegionEditorModel regionEditorModel) {
        super(regionEditorModel);
    }

    @Override
    public void render(Context2d context2d, long timeStamp, double scale, Rectangle displayRectangle, Index viewOriginTerrain) {
        context2d.save();
        context2d.setStrokeStyle("#888888");
        context2d.setLineWidth(1);
        context2d.setGlobalAlpha(0.5);
        context2d.beginPath();

        int absoluteLength = (int) (displayRectangle.getWidth() / scale);
        int absoluteHeight = (int) (displayRectangle.getHeight() / scale);
        int scrollXOffset = viewOriginTerrain.getX() % Constants.TERRAIN_TILE_WIDTH;
        int scrollYOffset = viewOriginTerrain.getY() % Constants.TERRAIN_TILE_HEIGHT;
        for (int x = 0; x <= absoluteLength; x += Constants.TERRAIN_TILE_WIDTH) {
            int relativeX = x - scrollXOffset;

            relativeX = (int) (relativeX * scale) + displayRectangle.getX();
            context2d.moveTo(relativeX, displayRectangle.getY());
            context2d.lineTo(relativeX, displayRectangle.getEndY());
        }
        for (int y = 0; y <= absoluteHeight; y += Constants.TERRAIN_TILE_HEIGHT) {
            int relativeY = y - scrollYOffset;

            relativeY = (int) (relativeY * scale) + displayRectangle.getY();
            context2d.moveTo(displayRectangle.getX(), relativeY);
            context2d.lineTo(displayRectangle.getEndX(), relativeY);
        }
        context2d.stroke();
        context2d.restore();
    }
}
