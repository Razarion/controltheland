package com.btxtech.game.jsre.mapeditor;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 18.09.12
 * Time: 21:32
 */
public class TerrainImageSurfaceGroup {
    private boolean placeAllowed = true;
    private Collection<TerrainImagePosition> selectionImages;
    private Collection<TerrainImageModifier> terrainImageModifiers;
    private Collection<SurfaceRect> surfaceRects;
    private Collection<SurfaceModifier> surfaceModifiers;

    public TerrainImageSurfaceGroup(Collection<TerrainImagePosition> selectionImages, Collection<SurfaceRect> surfaceRects, Index absoluteMouse, Rectangle viewRectangle, TerrainData terrainData) {
        this.selectionImages = selectionImages;
        this.surfaceRects = surfaceRects;
        terrainImageModifiers = new ArrayList<TerrainImageModifier>();
        for (TerrainImagePosition terrainImagePosition : selectionImages) {
            terrainImageModifiers.add(new TerrainImageModifier(terrainImagePosition, absoluteMouse, viewRectangle, terrainData));
        }
        surfaceModifiers = new ArrayList<SurfaceModifier>();
        for (SurfaceRect surfaceRect : surfaceRects) {
            surfaceModifiers.add(new SurfaceModifier(surfaceRect, absoluteMouse, viewRectangle, terrainData, new EditMode()));
        }
    }

    public void startMove(Index absoluteMouse) {
        for (TerrainImageModifier terrainImageModifier : terrainImageModifiers) {
            terrainImageModifier.resetMouseOffset(absoluteMouse);
        }
        for (SurfaceModifier surfaceModifier : surfaceModifiers) {
            surfaceModifier.resetMouseOffset(absoluteMouse);
        }
    }

    public Collection<TerrainImageModifier> getTerrainImageModifiers() {
        return terrainImageModifiers;
    }

    public Collection<SurfaceModifier> getSurfaceModifiers() {
        return surfaceModifiers;
    }

    public void mouseMove(Index absoluteMouse, Rectangle viewRectangle) {
        placeAllowed = true;
        for (TerrainImageModifier terrainImageModifier : terrainImageModifiers) {
            terrainImageModifier.onMouseMove(absoluteMouse, viewRectangle, selectionImages);
            if (!terrainImageModifier.isPlaceAllowed()) {
                placeAllowed = false;
            }
        }
        for (SurfaceModifier surfaceModifier : surfaceModifiers) {
            surfaceModifier.onMouseMove(absoluteMouse, viewRectangle, surfaceRects);
            if (!surfaceModifier.isPlaceAllowed()) {
                placeAllowed = false;
            }
        }
    }

    public void onScroll(Rectangle viewRectangle) {
        for (TerrainImageModifier terrainImageModifier : terrainImageModifiers) {
            terrainImageModifier.onScroll(viewRectangle);
        }
        for (SurfaceModifier surfaceModifier : surfaceModifiers) {
            surfaceModifier.onScroll(viewRectangle);
        }
    }

    public boolean isPlaceAllowed() {
        return placeAllowed;
    }

    public void updateModel() {
        for (TerrainImageModifier terrainImageModifier : terrainImageModifiers) {
            terrainImageModifier.updateModel();
        }
        for (SurfaceModifier surfaceModifier : surfaceModifiers) {
            surfaceModifier.updateModel();
        }
    }

    public void deleteAll(TerrainData terrainData) {
        for (TerrainImageModifier terrainImageModifier : terrainImageModifiers) {
            terrainData.removeTerrainImagePosition(terrainImageModifier.getTerrainImagePosition());
        }
        for (SurfaceModifier surfaceModifier : surfaceModifiers) {
            terrainData.removeSurfaceRect(surfaceModifier.getSurfaceRect());
        }
    }
}
