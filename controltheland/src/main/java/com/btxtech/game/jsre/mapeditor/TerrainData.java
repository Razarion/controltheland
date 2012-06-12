package com.btxtech.game.jsre.mapeditor;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 10.06.12
 * Time: 18:11
 */
public class TerrainData {
    private Collection<TerrainImagePosition> terrainImagePositions;
    private Collection<SurfaceRect> surfaceRects;
    private Logger log = Logger.getLogger(TerrainView.class.getName());

    public Collection<TerrainImagePosition> getTerrainImagePositions() {
        return terrainImagePositions;
    }

    public void setTerrainImagePositions(Collection<TerrainImagePosition> terrainImagePositions) {
        this.terrainImagePositions = terrainImagePositions;
    }

    public Collection<SurfaceRect> getSurfaceRects() {
        return surfaceRects;
    }

    public void setSurfaceRects(Collection<SurfaceRect> surfaceRects) {
        this.surfaceRects = surfaceRects;
    }

    public void moveTerrainImagePosition(int relX, int relY, TerrainImagePosition terrainImagePosition) {
        Index absoluteIndex = TerrainView.getInstance().toAbsoluteIndex(new Index(relX, relY));
        Index index = TerrainView.getInstance().getTerrainHandler().getTerrainTileIndexForAbsPosition(absoluteIndex);
        terrainImagePosition.setTileIndex(index);
        fireTerrainChanged();
    }

    public void moveSurfaceRect(int relX, int relY, SurfaceRect surfaceRect) {
        Index absoluteIndex = TerrainView.getInstance().toAbsoluteIndex(new Index(relX, relY));
        Index index = TerrainView.getInstance().getTerrainHandler().getTerrainTileIndexForAbsPosition(absoluteIndex);
        Rectangle rectangle = surfaceRect.getTileRectangle().moveTo(index.getX(), index.getY());
        surfaceRect.setTileRectangle(rectangle);
        fireTerrainChanged();
    }

    public void moveSurfaceRect(Rectangle rectangle, SurfaceRect surfaceRect) {
        Rectangle tileRect = TerrainView.getInstance().getTerrainHandler().convertToTilePosition(rectangle);
        surfaceRect.setTileRectangle(tileRect);
        fireTerrainChanged();
    }

    public void addNewTerrainImagePosition(int relX, int relY, TerrainImage terrainImage, TerrainImagePosition.ZIndex zIndex) {
        Index absoluteIndex = TerrainView.getInstance().toAbsoluteIndex(new Index(relX, relY));
        Index tileIndex = TerrainView.getInstance().getTerrainHandler().getTerrainTileIndexForAbsPosition(absoluteIndex);
        terrainImagePositions.add(new TerrainImagePosition(tileIndex, terrainImage.getId(), zIndex));
        fireTerrainChanged();
    }

    public void addNewSurfaceRect(int relX, int relY, int width, int height, SurfaceImage surfaceImage) {
        Index absoluteStart = TerrainView.getInstance().toAbsoluteIndex(new Index(relX, relY));
        Rectangle tileRect = TerrainView.getInstance().getTerrainHandler().convertToTilePosition(new Rectangle(absoluteStart.getX(), absoluteStart.getY(), width, height));
        surfaceRects.add(new SurfaceRect(tileRect, surfaceImage.getImageId()));
        fireTerrainChanged();
    }

    public void removeTerrainImagePosition(TerrainImagePosition terrainImagePosition) {
        terrainImagePositions.remove(terrainImagePosition);
        fireTerrainChanged();
    }

    public void removeSurfaceRect(SurfaceRect surfaceRect) {
        surfaceRects.remove(surfaceRect);
        fireTerrainChanged();
    }

    public List<TerrainImagePosition> getTerrainImagesInRegion(Rectangle absRectangle) {
        // TODO slow
        ArrayList<TerrainImagePosition> result = new ArrayList<TerrainImagePosition>();
        Rectangle tileRect = TerrainView.getInstance().getTerrainHandler().convertToTilePositionRoundUp(absRectangle);
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            if (tileRect.adjoinsEclusive(getTerrainImagePositionRectangle(terrainImagePosition))) {
                result.add(terrainImagePosition);
            }
        }
        return result;
    }

    public TerrainImagePosition getTerrainImagePosition(TerrainImagePosition.ZIndex zIndex, int absoluteX, int absoluteY) {
        // TODO slow
        Index tileIndex = TerrainView.getInstance().getTerrainHandler().getTerrainTileIndexForAbsPosition(absoluteX, absoluteY);
        TerrainImagePosition layer2Image = null;
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            if (getTerrainImagePositionRectangle(terrainImagePosition).containsExclusive(tileIndex)) {
                if (terrainImagePosition.getzIndex() == zIndex) {
                    return terrainImagePosition;
                }
                layer2Image = terrainImagePosition;
            }
        }
        return layer2Image;
    }

    private Rectangle getTerrainImagePositionRectangle(TerrainImagePosition terrainImagePosition) {
        TerrainImage terrainImage = TerrainView.getInstance().getTerrainHandler().getTerrainImage(terrainImagePosition.getImageId());
        return new Rectangle(terrainImagePosition.getTileIndex().getX(),
                terrainImagePosition.getTileIndex().getY(),
                terrainImage.getTileWidth(),
                terrainImage.getTileHeight());
    }

    public SurfaceRect getSurfaceRect(int absoluteX, int absoluteY) {
        // TODO slow!!!
        Index tileIndex = TerrainView.getInstance().getTerrainHandler().getTerrainTileIndexForAbsPosition(absoluteX, absoluteY);
        for (SurfaceRect surfaceRect : surfaceRects) {
            if (surfaceRect.getTileRectangle().containsExclusive(tileIndex)) {
                return surfaceRect;
            }
        }
        return null;
    }

    private void fireTerrainChanged() {
        TerrainView.getInstance().getTerrainHandler().createTerrainTileField(terrainImagePositions, surfaceRects);
        TerrainView.getInstance().getTerrainHandler().fireTerrainChanged();
    }
}
