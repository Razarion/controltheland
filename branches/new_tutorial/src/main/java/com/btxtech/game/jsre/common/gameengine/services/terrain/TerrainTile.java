package com.btxtech.game.jsre.common.gameengine.services.terrain;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;

/**
 * User: beat
 * Date: 02.06.12
 * Time: 10:51
 */
public class TerrainTile {
    private SurfaceType surfaceType;
    private int imageId;
    private ImageSpriteMapInfo imageSpriteMapInfo;
    private int tileXOffset;
    private int tileYOffset;
    private boolean isSurface;
    private Integer scatterXOffset;

    public TerrainTile(SurfaceType surfaceType, boolean isSurface, int imageId, ImageSpriteMapInfo imageSpriteMapInfo, Integer scatterXOffset, int tileXOffset, int tileYOffset) {
        this.surfaceType = surfaceType;
        this.isSurface = isSurface;
        this.imageId = imageId;
        this.imageSpriteMapInfo = imageSpriteMapInfo;
        this.scatterXOffset = scatterXOffset;
        this.tileXOffset = tileXOffset;
        this.tileYOffset = tileYOffset;
    }

    public void setSurfaceType(SurfaceType surfaceType, boolean isSurface, int imageId, ImageSpriteMapInfo imageSpriteMapInfo, Integer scatterXOffset, int tileXOffset, int tileYOffset) {
        this.surfaceType = surfaceType;
        this.isSurface = isSurface;
        this.imageId = imageId;
        this.imageSpriteMapInfo = imageSpriteMapInfo;
        this.scatterXOffset = scatterXOffset;
        this.tileXOffset = tileXOffset;
        this.tileYOffset = tileYOffset;
    }

    public SurfaceType getSurfaceType() {
        return surfaceType;
    }

    public int getImageId() {
        return imageId;
    }

    public boolean isSurface() {
        return isSurface;
    }

    public int getTileXOffset() {
        return tileXOffset;
    }

    public int getTileYOffset() {
        return tileYOffset;
    }

    public ImageSpriteMapInfo getImageSpriteMapInfo() {
        return imageSpriteMapInfo;
    }

    public boolean hasImageSpriteMapInfo() {
        return imageSpriteMapInfo != null;
    }

    public Integer getScatterXOffset() {
        return scatterXOffset;
    }

    public boolean hasScatterXOffset() {
        return scatterXOffset != null;
    }
}
