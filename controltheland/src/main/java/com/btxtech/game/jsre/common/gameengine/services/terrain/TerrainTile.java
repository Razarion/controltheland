package com.btxtech.game.jsre.common.gameengine.services.terrain;

/**
 * User: beat
 * Date: 02.06.12
 * Time: 10:51
 */
public class TerrainTile {
    private SurfaceType surfaceType;
    private int imageId;
    private int tileXOffset;
    private int tileYOffset;
    private boolean isSurface;

    public TerrainTile(SurfaceType surfaceType, boolean isSurface, int imageId, int tileXOffset, int tileYOffset) {
        this.surfaceType = surfaceType;
        this.isSurface = isSurface;
        this.imageId = imageId;
        this.tileXOffset = tileXOffset;
        this.tileYOffset = tileYOffset;
    }

    public void setSurfaceType(SurfaceType surfaceType, boolean isSurface, int imageId, int tileXOffset, int tileYOffset) {
        this.surfaceType = surfaceType;
        this.isSurface = isSurface;
        this.imageId = imageId;
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
}
