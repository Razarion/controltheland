package com.btxtech.game.jsre.regioneditor;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;
import com.google.gwt.canvas.dom.client.Context2d;

import java.util.Collection;

/**
 * User: beat
 * Date: 09.09.12
 * Time: 15:53
 */
public class MouseRenderTask extends AbstractRegionEditorRenderTask {
    public MouseRenderTask(RegionEditorModel regionEditorModel) {
        super(regionEditorModel);
    }

    @Override
    public void render(Context2d context2d, long timeStamp, double scale, Rectangle displayRectangle, Index viewOriginTerrain) {
        Collection<Index> mouseOverTiles = getRegionEditorModel().getMouseOverTiles();
        if (mouseOverTiles != null) {
            context2d.save();
            context2d.setFillStyle("#000000");
            context2d.setGlobalAlpha(0.5);

            for (Index mouseOverTile : mouseOverTiles) {
                int relativeX = TerrainUtil.getAbsolutXForTerrainTile(mouseOverTile.getX()) - viewOriginTerrain.getX()/* - scrollXOffset*/;
                relativeX = (int) (relativeX * scale) + displayRectangle.getX();
                int relativeY = TerrainUtil.getAbsolutYForTerrainTile(mouseOverTile.getY()) - viewOriginTerrain.getY()/* - scrollYOffset*/;
                relativeY = (int) (relativeY * scale) + displayRectangle.getY();
                context2d.fillRect(relativeX, relativeY, Constants.TERRAIN_TILE_WIDTH * scale, Constants.TERRAIN_TILE_HEIGHT * scale);
            }
            context2d.restore();
        }
    }
}
