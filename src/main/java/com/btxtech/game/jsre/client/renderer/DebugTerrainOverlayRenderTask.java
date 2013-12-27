package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainHandler;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImageBackground;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainTile;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.canvas.dom.client.Context2d;

import java.util.Collection;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 11:46
 */
public class DebugTerrainOverlayRenderTask extends AbstractRenderTask {
    private TerrainHandler terrainHandler;
    private Context2d context2d;

    public DebugTerrainOverlayRenderTask(TerrainHandler terrainHandler, Context2d context2d) {
        this.terrainHandler = terrainHandler;
        this.context2d = context2d;
    }

    @Override
    public void render(final long timeStamp, Collection<SyncItem> itemsInView, Rectangle viewRect, final Rectangle tileViewRect) {
        if (terrainHandler.getTerrainSettings() == null) {
            return;
        }
        TerrainTile[][] terrainTiles = terrainHandler.getTerrainTileField();
        if (terrainTiles == null) {
            return;
        }
        final int scrollXOffset = viewRect.getX() % Constants.TERRAIN_TILE_WIDTH;
        final int scrollYOffset = viewRect.getY() % Constants.TERRAIN_TILE_HEIGHT;
        final int tileWidth = Constants.TERRAIN_TILE_WIDTH;
        final int tileHeight = Constants.TERRAIN_TILE_HEIGHT;
        final TerrainImageBackground terrainImageBackground = terrainHandler.getCommonTerrainImageService().getTerrainImageBackground();

        terrainHandler.iteratorOverAllTerrainTiles(tileViewRect, new AbstractTerrainService.TerrainTileEvaluator() {
            @Override
            public void evaluate(int x, int y, TerrainTile terrainTile) {
                int relativeX = TerrainUtil.getAbsolutXForTerrainTile(x - tileViewRect.getX());
                int imageWidth = tileWidth;
                if (relativeX == 0) {
                    imageWidth = tileWidth - scrollXOffset;
                } else {
                    relativeX -= scrollXOffset;
                }

                int relativeY = TerrainUtil.getAbsolutYForTerrainTile(y - tileViewRect.getY());
                int imageHeight = tileHeight;
                if (relativeY == 0) {
                    imageHeight = tileHeight - scrollYOffset;
                } else {
                    relativeY -= scrollYOffset;
                }

                context2d.setFillStyle("#FF0000");

                context2d.fillText(terrainTile.getSurfaceType().name(), relativeX + 10 , relativeY + 10);


                context2d.setStrokeStyle("#FF0000");
                context2d.beginPath();
                context2d.moveTo(relativeX, relativeY);
                context2d.lineTo(relativeX + tileWidth, relativeY);
                context2d.moveTo(relativeX, relativeY);
                context2d.lineTo(relativeX, relativeY + tileHeight);
                context2d.stroke();
            }
        });
    }

}
