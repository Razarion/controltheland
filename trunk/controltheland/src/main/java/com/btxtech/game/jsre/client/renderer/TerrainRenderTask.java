package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainHandler;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainTile;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 11:46
 */
public class TerrainRenderTask extends AbstractRenderTask {
    private TerrainHandler terrainHandler;
    private Context2d context2d;
    private Logger log = Logger.getLogger(TerrainRenderTask.class.getName());

    public TerrainRenderTask(TerrainHandler terrainHandler, Context2d context2d) {
        this.terrainHandler = terrainHandler;
        this.context2d = context2d;
    }

    @Override
    public void render(long timeStamp, Rectangle viewRect, final Rectangle tileViewRect) {
        if (terrainHandler.getTerrainSettings() == null) {
            return;
        }
        TerrainTile[][] terrainTiles = terrainHandler.getTerrainTileField();
        if (terrainTiles == null) {
            return;
        }
        final int scrollXOffset = viewRect.getX() % terrainHandler.getTerrainSettings().getTileWidth();
        final int scrollYOffset = viewRect.getY() % terrainHandler.getTerrainSettings().getTileHeight();
        final int tileWidth = terrainHandler.getTerrainSettings().getTileWidth();
        final int tileHeight = terrainHandler.getTerrainSettings().getTileHeight();

        terrainHandler.iteratorOverAllTerrainTiles(tileViewRect, new AbstractTerrainService.TerrainTileEvaluator() {
            @Override
            public void evaluate(int x, int y, TerrainTile terrainTile) {
                if (terrainTile == null) {
                    return;
                }

                int relativeX = terrainHandler.getAbsolutXForTerrainTile(x - tileViewRect.getX());
                int imageWidth = tileWidth;
                if (relativeX == 0) {
                    imageWidth = tileWidth - scrollXOffset;
                } else {
                    relativeX -= scrollXOffset;
                }

                int relativeY = terrainHandler.getAbsolutYForTerrainTile(y - tileViewRect.getY());
                int imageHeight = tileHeight;
                if (relativeY == 0) {
                    imageHeight = tileHeight - scrollYOffset;
                } else {
                    relativeY -= scrollYOffset;
                }

                ImageElement imageElement;
                if (terrainTile.isSurface()) {
                    imageElement = terrainHandler.getSurfaceImageElement(terrainTile.getImageId());
                } else {
                    imageElement = terrainHandler.getTerrainImageElement(terrainTile.getImageId());
                }
                if (imageElement == null || imageElement.getWidth() == 0 || imageElement.getHeight() == 0) {
                    return;
                }

                int sourceXOffset = terrainHandler.getAbsolutXForTerrainTile(terrainTile.getTileXOffset());
                int sourceYOffset = terrainHandler.getAbsolutYForTerrainTile(terrainTile.getTileYOffset());
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
