package com.btxtech.game.services.terrain.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.services.terrain.DbTerrainImagePosition;

public class ImagePositionKey {
    private Index position;
    private int imageId;
    private TerrainImagePosition.ZIndex zIndex;

    public ImagePositionKey(TerrainImagePosition terrainImagePosition) {
        position = terrainImagePosition.getTileIndex();
        imageId = terrainImagePosition.getImageId();
        zIndex = terrainImagePosition.getzIndex();
    }

    public ImagePositionKey(DbTerrainImagePosition dbTerrainImagePosition) {
        position = new Index(dbTerrainImagePosition.getTileX(), dbTerrainImagePosition.getTileY());
        imageId = dbTerrainImagePosition.getTerrainImage().getId();
        zIndex = dbTerrainImagePosition.getzIndex();
    }

    public Index getPosition() {
        return position;
    }

    public int getImageId() {
        return imageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImagePositionKey that = (ImagePositionKey) o;

        return imageId == that.imageId
                && !(position != null ? !position.equals(that.position) : that.position != null)
                && zIndex == that.zIndex;
    }

    @Override
    public int hashCode() {
        int result = position != null ? position.hashCode() : 0;
        result = 31 * result + imageId;
        result = 31 * result + (zIndex != null ? zIndex.hashCode() : 0);
        return result;
    }
}
