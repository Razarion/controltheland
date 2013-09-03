package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.btxtech.game.jsre.client.terrain.TerrainHandler;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImageBackground;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainTile;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

import java.util.Collection;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 11:46
 */
public class TerrainRenderTask extends AbstractRenderTask {
    private TerrainHandler terrainHandler;
    private Context2d context2d;

    public TerrainRenderTask(TerrainHandler terrainHandler, Context2d context2d) {
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

                ImageElement imageElement = null;
                ImageSpriteMapInfo imageSpriteMapInfo = null;
                if (terrainTile == null) {
                    imageElement = null;
                } else if (terrainTile.isSurface()) {
                    if (terrainTile.hasImageSpriteMapInfo()) {
                        imageSpriteMapInfo = terrainTile.getImageSpriteMapInfo();
                    } else {
                        imageElement = SurfaceLoaderContainer.getInstance().getImage(terrainTile.getImageId());
                    }
                } else {
                    if (terrainTile.hasImageSpriteMapInfo()) {
                        imageSpriteMapInfo = terrainTile.getImageSpriteMapInfo();
                    }
                    imageElement = TerrainImageLoaderContainer.getInstance().getImage(terrainTile.getImageId());
                }
                boolean fallbackNeeded = true;
                // Render clip below image
                if (imageSpriteMapInfo != null) {
                    ImageElement clipImageElement = ImageSpriteMapContainer.getInstance().getImage(imageSpriteMapInfo);
                    // Check if image is loaded
                    if (clipImageElement != null && clipImageElement.getWidth() != 0 && clipImageElement.getHeight() != 0) {
                        int clipTileXCount = TerrainUtil.getTerrainTileIndexForAbsXPosition(imageSpriteMapInfo.getFrameWidth());
                        int clipTileYCount = TerrainUtil.getTerrainTileIndexForAbsYPosition(imageSpriteMapInfo.getFrameHeight());
                        int sourceClipXOffset = TerrainUtil.getAbsolutXForTerrainTile(x % clipTileXCount);
                        int sourceClipYOffset = TerrainUtil.getAbsolutYForTerrainTile(y % clipTileYCount);
                        if (relativeX == 0) {
                            sourceClipXOffset += scrollXOffset;
                        }
                        if (relativeY == 0) {
                            sourceClipYOffset += scrollYOffset;
                        }
                        Index sourceClipOffset = imageSpriteMapInfo.getSpriteMapOffset(imageSpriteMapInfo.getFrameInfinite(timeStamp));
                        sourceClipXOffset += sourceClipOffset.getX();
                        sourceClipYOffset += sourceClipOffset.getY();

                        context2d.drawImage(clipImageElement,
                                sourceClipXOffset, // Source x pos
                                sourceClipYOffset, // Source y pos
                                imageWidth, //the width in the source image you want to sample
                                imageHeight, //the height in the source image you want to sample
                                relativeX, //the start X position in the destination image
                                relativeY, //the start Y position in the destination image
                                imageWidth, //the width of drawn image in the destination
                                imageHeight // the height of the drawn image in the destination
                        );
                        fallbackNeeded = false;
                    } else {
                        ImageSpriteMapContainer.getInstance().startLoad();
                    }
                }

                // Render overlay image. Check if image is loaded
                if (imageElement != null && imageElement.getWidth() != 0 && imageElement.getHeight() != 0) {
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
                        fallbackNeeded = false;
                    } catch (Throwable t) {
                        logCanvasError(t, imageElement, sourceXOffset, sourceYOffset, imageWidth, imageHeight, relativeX, relativeY, imageWidth, imageHeight);
                    }
                }

                if (fallbackNeeded) {
                    if (terrainTile != null) {
                        if (terrainTile.isSurface()) {
                            context2d.setFillStyle(terrainHandler.getCommonTerrainImageService().getSurfaceImage(terrainTile.getImageId()).getHtmlBackgroundColor());
                        } else {
                            SurfaceType surfaceType = terrainHandler.getCommonTerrainImageService().getTerrainImage(terrainTile.getImageId()).getSurfaceType(terrainTile.getTileXOffset(), terrainTile.getTileYOffset());
                            context2d.setFillStyle(terrainImageBackground.get(terrainTile.getImageId(), surfaceType));
                        }
                    } else {
                        // No Image
                        context2d.setFillStyle("#000000");
                    }
                    context2d.fillRect(relativeX, relativeY, imageWidth, imageHeight);
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

        ClientExceptionHandler.handleExceptionOnlyOnce(builder.toString(), t);
    }
}
