package com.btxtech.game.jsre.common.gameengine.services.terrain;

/**
 * User: beat
 * Date: 16.09.13
 * Time: 00:06
 */
public class Terrain {
    private int xCount;
    private int yCount;
    private TerrainTile terrainTileField[][];

    public void init(int xCount, int yCount) {
        this.xCount = xCount;
        this.yCount = yCount;
        terrainTileField = new TerrainTile[xCount][yCount];
        for (int x = 0; x < xCount; x++) {
            for (int y = 0; y < yCount; y++) {
                terrainTileField[x][y] = new TerrainTile(SurfaceType.FREE);
            }
        }
    }

    public void overrideTerrainTile(int xTileIndex, int yTileIndex, TerrainTile terrainTile) {
        terrainTileField[xTileIndex][yTileIndex] = terrainTile;
    }

    public int getXCount() {
        return xCount;
    }

    public int getYCount() {
        return yCount;
    }

    public TerrainTile getTerrainTile(int xTileIndex, int yTileIndex) {
        return terrainTileField[xTileIndex][yTileIndex];
    }

    public TerrainTile[][] getTerrainTileField() {
        return terrainTileField;
    }
}
