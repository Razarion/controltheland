package com.btxtech.game.services.terrain.impl;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.services.terrain.DbSurfaceRect;

public class SurfaceRectKey {
    private Rectangle rectangle;
    private int imageId;

    public SurfaceRectKey(SurfaceRect surfaceRect) {
        rectangle = surfaceRect.getTileRectangle();
        imageId = surfaceRect.getSurfaceImageId();
    }

    public SurfaceRectKey(DbSurfaceRect dbSurfaceRect) {
        rectangle = dbSurfaceRect.getRectangle();
        imageId = dbSurfaceRect.getDbSurfaceImage().getId();
    }

    Rectangle getRectangle() {
        return rectangle;
    }

    int getImageId() {
        return imageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SurfaceRectKey)) return false;

        SurfaceRectKey that = (SurfaceRectKey) o;

        return imageId == that.imageId && !(rectangle != null ? !rectangle.equals(that.rectangle) : that.rectangle != null);

    }

    @Override
    public int hashCode() {
        int result = rectangle != null ? rectangle.hashCode() : 0;
        result = 31 * result + imageId;
        return result;
    }
}
