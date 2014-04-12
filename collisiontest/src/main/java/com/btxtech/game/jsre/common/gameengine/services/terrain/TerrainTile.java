package com.btxtech.game.jsre.common.gameengine.services.terrain;

/**
 * User: beat
 * Date: 04.04.13
 * Time: 14:52
 */
public class TerrainTile {
    private SurfaceType surfaceType;

    public TerrainTile(SurfaceType surfaceType) {
        this.surfaceType = surfaceType;
    }

    public SurfaceType getSurfaceType() {
        return surfaceType;
    }
}
