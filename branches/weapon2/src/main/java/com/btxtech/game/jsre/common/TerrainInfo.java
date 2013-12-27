package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImageBackground;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;

import java.io.Serializable;
import java.util.Collection;

/**
 * User: beat
 * Date: 10.09.12
 * Time: 16:37
 */
public interface TerrainInfo extends Serializable {
    TerrainSettings getTerrainSettings();

    void setTerrainSettings(TerrainSettings terrainSettings);

    Collection<TerrainImagePosition> getTerrainImagePositions();

    void setTerrainImagePositions(Collection<TerrainImagePosition> terrainImagePositions);

    Collection<SurfaceRect> getSurfaceRects();

    void setSurfaceRects(Collection<SurfaceRect> surfaceRects);

    Collection<SurfaceImage> getSurfaceImages();

    void setSurfaceImages(Collection<SurfaceImage> surfaceImages);

    Collection<TerrainImage> getTerrainImages();

    void setTerrainImages(Collection<TerrainImage> terrainImages);

    TerrainImageBackground getTerrainImageBackground();

    void setTerrainImageBackground(TerrainImageBackground terrainImageBackground);
}
