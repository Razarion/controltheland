package com.btxtech.game.jsre.mapeditor.render;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.terrain.TerrainHandler;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainTile;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;
import com.btxtech.game.jsre.mapeditor.MapEditorModel;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 11:46
 */
public class TerrainEditorRenderTask extends AbstractMapEditorRenderTask {
    private Logger log = Logger.getLogger(TerrainEditorRenderTask.class.getName());

    @Override
    public void render(long timeStamp, final Context2d context2d, final MapEditorModel mapEditorModel) {
        final TerrainHandler terrainHandler = TerrainView.getInstance().getTerrainHandler();
        if (terrainHandler.getTerrainSettings() == null) {
            return;
        }
        TerrainTile[][] terrainTiles = terrainHandler.getTerrainTileField();
        if (terrainTiles == null) {
            return;
        }
        final int scrollXOffset = mapEditorModel.getViewRectangle().getX() % Constants.TERRAIN_TILE_WIDTH;
        final int scrollYOffset = mapEditorModel.getViewRectangle().getY() % Constants.TERRAIN_TILE_HEIGHT;
        final int tileWidth = Constants.TERRAIN_TILE_WIDTH;
        final int tileHeight = Constants.TERRAIN_TILE_HEIGHT;

        terrainHandler.iteratorOverAllTerrainTiles(mapEditorModel.getViewTileRectangle(), new AbstractTerrainService.TerrainTileEvaluator() {
            @Override
            public void evaluate(int x, int y, TerrainTile terrainTile) {
                int relativeX = TerrainUtil.getAbsolutXForTerrainTile(x - mapEditorModel.getViewTileRectangle().getX());
                int imageWidth = tileWidth;
                if (relativeX == 0) {
                    imageWidth = tileWidth - scrollXOffset;
                } else {
                    relativeX -= scrollXOffset;
                }

                int relativeY = TerrainUtil.getAbsolutYForTerrainTile(y - mapEditorModel.getViewTileRectangle().getY());
                int imageHeight = tileHeight;
                if (relativeY == 0) {
                    imageHeight = tileHeight - scrollYOffset;
                } else {
                    relativeY -= scrollYOffset;
                }

                ImageElement imageElement;
                if (terrainTile == null) {
                    imageElement = null;
                } else if (terrainTile.isSurface()) {
                    imageElement = SurfaceLoaderContainer.getInstance().getImage(terrainTile.getImageId());
                } else {
                    imageElement = TerrainImageLoaderContainer.getInstance().getImage(terrainTile.getImageId());
                }
                if (imageElement == null || imageElement.getWidth() == 0 || imageElement.getHeight() == 0) {
                    context2d.setFillStyle("#000000");
                    context2d.fillRect(relativeX, relativeY, imageWidth, imageHeight);
                    return;
                }

                int sourceXOffset = TerrainUtil.getAbsolutXForTerrainTile(terrainTile.getTileXOffset());
                int sourceYOffset = TerrainUtil.getAbsolutYForTerrainTile(terrainTile.getTileYOffset());
                if (relativeX == 0) {
                    sourceXOffset += scrollXOffset;
                }
                if (relativeY == 0) {
                    sourceYOffset += scrollYOffset;
                }

                if (terrainTile.isSurface()) {
                    sourceXOffset = sourceXOffset % imageElement.getWidth();
                    sourceYOffset = sourceYOffset % imageElement.getHeight();
                }

                try {
                    context2d.drawImage(imageElement,
                            sourceXOffset, //the start X position in the source image
                            sourceYOffset, //the start Y position in the source image
                            imageWidth, //the width in the source image you want to sample
                            imageHeight, //the height in the source image you want to sample
                            relativeX, //the start X position in the destination image
                            relativeY, //the start Y position in the destination image
                            imageWidth, //the width of drawn image in the destination
                            imageHeight // the height of the drawn image in the destination
                    );
                } catch (Throwable t) {
                    logCanvasError(t, imageElement, sourceXOffset, sourceYOffset, imageWidth, imageHeight, relativeX, relativeY, imageWidth, imageHeight);
                }
            }
        });
        TerrainImageLoaderContainer.getInstance().startLoad();
        SurfaceLoaderContainer.getInstance().startLoad();
    }

    private void logCanvasError(Throwable t, ImageElement imageElement, int srcXStart, int srcYStart, int srcXWidth, int srcYWidth, int posX, int posY, int imageWidth, int imageHeight) {
        StringBuilder builder = new StringBuilder();
        builder.append("TerrainView.drawTerrain() error in canvas drawImage");
        builder.append("\n");

        builder.append("imageElement: ");
        builder.append(imageElement);
        builder.append("\n");

        builder.append("srcXStart: ");
        builder.append(srcXStart);
        builder.append("\n");

        builder.append("srcYStart: ");
        builder.append(srcYStart);
        builder.append("\n");

        builder.append("srcXWidth: ");
        builder.append(srcXWidth);
        builder.append("\n");

        builder.append("srcYWidth: ");
        builder.append(srcYWidth);
        builder.append("\n");

        builder.append("posX: ");
        builder.append(posX);
        builder.append("\n");

        builder.append("posY: ");
        builder.append(posY);
        builder.append("\n");

        builder.append("imageWidth: ");
        builder.append(imageWidth);
        builder.append("\n");

        builder.append("imageHeight: ");
        builder.append(imageHeight);
        builder.append("\n");

        log.log(Level.SEVERE, builder.toString(), t);
    }
}
