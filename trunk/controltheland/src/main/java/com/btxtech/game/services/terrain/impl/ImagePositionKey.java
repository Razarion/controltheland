package com.btxtech.game.services.terrain.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.services.terrain.DbTerrainImagePosition;

class ImagePositionKey {
    private Index position;
    private int imageId;

    ImagePositionKey(TerrainImagePosition terrainImagePosition) {
        position = terrainImagePosition.getTileIndex();
        imageId = terrainImagePosition.getImageId();
    }

    public ImagePositionKey(DbTerrainImagePosition dbTerrainImagePosition) {
        position = new Index(dbTerrainImagePosition.getTileX(), dbTerrainImagePosition.getTileY());
        imageId = dbTerrainImagePosition.getTerrainImage().getId();
    }

    public Index getPosition() {
        return position;
    }

    public int getImageId() {
        return imageId;
    }

    @Override
    public boolean equals(Object o) {
        if (null == o) return true;
        if (!(o instanceof ImagePositionKey)) return false;

        ImagePositionKey that = (ImagePositionKey) o;

        return imageId == that.imageId && !(position != null ? !position.equals(that.position) : that.position != null);

    }

    @Override
    public int hashCode() {
        int result = position != null ? position.hashCode() : 0;
        result = 31 * result + imageId;
        return result;
    }
}
