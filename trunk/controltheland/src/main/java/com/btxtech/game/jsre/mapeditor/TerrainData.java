package com.btxtech.game.jsre.mapeditor;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 10.06.12
 * Time: 18:11
 */
public class TerrainData {
    private Collection<TerrainImagePosition> terrainImagePositions;
    private Collection<SurfaceRect> surfaceRects;

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

    public void moveTerrainImagePosition(Index absoluteIndex, TerrainImagePosition terrainImagePosition) {
        Index index = TerrainUtil.getTerrainTileIndexForAbsPosition(absoluteIndex);
        terrainImagePosition.setTileIndex(index);
        fireTerrainChanged();
    }

    public void moveSurfaceRect(int relX, int relY, SurfaceRect surfaceRect) {
        Index absoluteIndex = TerrainView.getInstance().toAbsoluteIndex(new Index(relX, relY));
        Index index = TerrainUtil.getTerrainTileIndexForAbsPosition(absoluteIndex);
        Rectangle rectangle = surfaceRect.getTileRectangle().moveTo(index.getX(), index.getY());
        surfaceRect.setTileRectangle(rectangle);
        fireTerrainChanged();
    }

    public void moveSurfaceRect(Rectangle rectangle, SurfaceRect surfaceRect) {
        Rectangle tileRect = TerrainUtil.convertToTilePosition(rectangle);
        surfaceRect.setTileRectangle(tileRect);
        fireTerrainChanged();
    }

    public void addNewTerrainImagePosition(Index absoluteIndex, TerrainImage terrainImage, TerrainImagePosition.ZIndex zIndex) {
        Index tileIndex = TerrainUtil.getTerrainTileIndexForAbsPosition(absoluteIndex);
        terrainImagePositions.add(new TerrainImagePosition(tileIndex, terrainImage.getId(), zIndex));
        fireTerrainChanged();
    }

    public void addNewSurfaceRect(Rectangle newRectangle, int imageId) {
        Rectangle tileRect = TerrainUtil.convertToTilePosition(newRectangle);
        surfaceRects.add(new SurfaceRect(tileRect, imageId));
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

    public boolean hasTerrainImagesInRegion(Rectangle absRectangle, TerrainImagePosition.ZIndex selectedZIndex, Collection<TerrainImagePosition> exceptThem) {
        // TODO slow
        Rectangle tileRect = TerrainUtil.convertToTilePosition(absRectangle);
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            if (selectedZIndex != terrainImagePosition.getzIndex()) {
                continue;
            }
            if (exceptThem != null && exceptThem.contains(terrainImagePosition)) {
                continue;
            }
            if(tileRect.adjoinsEclusive(getTerrainImagePositionRectangle(terrainImagePosition))) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTerrainImagesInRegion(Rectangle absRectangle, TerrainImagePosition master, Collection<TerrainImagePosition> exceptThem) {
        // TODO slow
        Rectangle tileRect = TerrainUtil.convertToTilePosition(absRectangle);
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            if (master.getzIndex() != terrainImagePosition.getzIndex()) {
                continue;
            }
            if (master.equals(terrainImagePosition)) {
                continue;
            }
            if (exceptThem != null && exceptThem.contains(terrainImagePosition)) {
                continue;
            }
            if(tileRect.adjoinsEclusive(getTerrainImagePositionRectangle(terrainImagePosition))) {
                return true;
            }
        }
        return false;
    }

    public TerrainImagePosition getTerrainImagePosition(TerrainImagePosition.ZIndex zIndex, int absoluteX, int absoluteY) {
        // TODO slow
        Index tileIndex = TerrainUtil.getTerrainTileIndexForAbsPosition(absoluteX, absoluteY);
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            if (getTerrainImagePositionRectangle(terrainImagePosition).containsExclusive(tileIndex)) {
                if (terrainImagePosition.getzIndex() == zIndex) {
                    return terrainImagePosition;
                }
            }
        }
        return null;
    }

    private Rectangle getTerrainImagePositionRectangle(TerrainImagePosition terrainImagePosition) {
        TerrainImage terrainImage = TerrainView.getInstance().getTerrainHandler().getCommonTerrainImageService().getTerrainImage(terrainImagePosition.getImageId());
        return new Rectangle(terrainImagePosition.getTileIndex().getX(),
                terrainImagePosition.getTileIndex().getY(),
                terrainImage.getTileWidth(),
                terrainImage.getTileHeight());
    }

    public SurfaceRect getSurfaceRect(int absoluteX, int absoluteY) {
        // TODO slow!!!
        Index tileIndex = TerrainUtil.getTerrainTileIndexForAbsPosition(absoluteX, absoluteY);
        for (SurfaceRect surfaceRect : surfaceRects) {
            if (surfaceRect.getTileRectangle().containsExclusive(tileIndex)) {
                return surfaceRect;
            }
        }
        return null;
    }

    public boolean hasSurfaceRectInRegion(Rectangle absoluteRegion, SurfaceRect exceptThat, Collection<SurfaceRect> surfaceRects) {
        // TODO slow!!!
        Rectangle tileRect = TerrainUtil.convertToTilePosition(absoluteRegion);
        for (SurfaceRect surfaceRect : this.surfaceRects) {
            if (exceptThat != null && surfaceRect.equals(exceptThat)) {
                continue;
            }
            if (surfaceRects != null && surfaceRects.contains(surfaceRect)) {
                continue;
            }
            if (surfaceRect.getTileRectangle().adjoinsEclusive(tileRect)) {
                return true;
            }
        }
        return false;
    }

    private void fireTerrainChanged() {
        TerrainView.getInstance().getTerrainHandler().createTerrainTileField(terrainImagePositions, surfaceRects);
        TerrainView.getInstance().getTerrainHandler().fireTerrainChanged();
    }

    public Collection<TerrainImagePosition> getTerrainImagePositionInRegion(Rectangle absoluteRectangle, boolean layer1, boolean layer2) {
        // TODO slow
        Collection<TerrainImagePosition> collection = new ArrayList<TerrainImagePosition>();
        Rectangle tileRect = TerrainUtil.convertToTilePosition(absoluteRectangle);
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            if (!tileRect.adjoinsEclusive(getTerrainImagePositionRectangle(terrainImagePosition))) {
                continue;
            }

            if (layer1 && terrainImagePosition.getzIndex() == TerrainImagePosition.ZIndex.LAYER_1) {
                collection.add(terrainImagePosition);
            }

            if (layer2 && terrainImagePosition.getzIndex() == TerrainImagePosition.ZIndex.LAYER_2) {
                collection.add(terrainImagePosition);
            }
        }
        return collection;
    }

    public Collection<SurfaceRect> getSurfaceRectInRegion(Rectangle absoluteRegion) {
        // TODO slow!!!
        Rectangle tileRect = TerrainUtil.convertToTilePosition(absoluteRegion);
        Collection<SurfaceRect> result = new ArrayList<SurfaceRect>();
        for (SurfaceRect surfaceRect : this.surfaceRects) {
            if (surfaceRect.getTileRectangle().adjoinsEclusive(tileRect)) {
                result.add(surfaceRect);
            }
        }
        return result;
    }
}
