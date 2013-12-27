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
 * Time: 15:50
 */
public class RegionTileRenderTask extends AbstractRegionEditorRenderTask {
    public RegionTileRenderTask(RegionEditorModel regionEditorModel) {
        super(regionEditorModel);
    }

    @Override
    public void render(Context2d context2d, long timeStamp, double scale, Rectangle displayRectangle, Index viewOriginTerrain) {
        Collection<Index> tiles = getRegionEditorModel().getRegionBuilder().queryRegions();
        if(tiles.isEmpty()) {
            return;
        }
        context2d.save();
        context2d.setFillStyle("#FF0000");
        context2d.setGlobalAlpha(0.5);
        for (Index tile : tiles) {
            int relativeX = TerrainUtil.getAbsolutXForTerrainTile(tile.getX()) - viewOriginTerrain.getX();
            relativeX = (int) (relativeX * scale) + displayRectangle.getX();
            int relativeY = TerrainUtil.getAbsolutYForTerrainTile(tile.getY()) - viewOriginTerrain.getY();
            relativeY = (int) (relativeY * scale) + displayRectangle.getY();
            context2d.fillRect(relativeX, relativeY, Constants.TERRAIN_TILE_WIDTH * scale, Constants.TERRAIN_TILE_HEIGHT * scale);
        }
        context2d.restore();
    }
}
