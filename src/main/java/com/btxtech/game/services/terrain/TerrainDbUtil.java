package com.btxtech.game.services.terrain;

import com.btxtech.game.jsre.common.TerrainInfo;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.terrain.impl.ImagePositionKey;
import com.btxtech.game.services.terrain.impl.SurfaceRectKey;
import com.btxtech.game.services.tutorial.DbTutorialConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: beat
 * Date: 10.09.12
 * Time: 16:48
 */
public class TerrainDbUtil {
    public static void loadTerrainFromDb(DbTerrainSetting dbTerrainSetting, TerrainInfo terrainInfo) {
        terrainInfo.setTerrainSettings(dbTerrainSetting.createTerrainSettings());
        terrainInfo.setTerrainImagePositions(getTerrainImagePositions(dbTerrainSetting));
        terrainInfo.setSurfaceRects(getSurfaceRects(dbTerrainSetting));
    }

    private static Collection<TerrainImagePosition> getTerrainImagePositions(DbTerrainSetting dbTerrainSetting) {
        ArrayList<TerrainImagePosition> result = new ArrayList<>();
        for (DbTerrainImagePosition dbTerrainImagePosition : dbTerrainSetting.getDbTerrainImagePositionCrudServiceHelper().readDbChildren()) {
            result.add(dbTerrainImagePosition.createTerrainImagePosition());
        }
        return result;
    }

    private static Collection<SurfaceRect> getSurfaceRects(DbTerrainSetting dbTerrainSetting) {
        ArrayList<SurfaceRect> result = new ArrayList<>();
        for (DbSurfaceRect dbSurfaceRect : dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().readDbChildren()) {
            result.add(dbSurfaceRect.createSurfaceRect());
        }
        return result;
    }

    public static void modifyTerrainSetting(DbTerrainSetting dbTerrainSetting, Collection<TerrainImagePosition> terrainImagePositions, Collection<SurfaceRect> surfaceRects, TerrainImageService terrainImageService) {
        // Terrain Image Position
        Map<ImagePositionKey, TerrainImagePosition> newImagePosition = new HashMap<>(terrainImagePositions.size());
        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            newImagePosition.put(new ImagePositionKey(terrainImagePosition), terrainImagePosition);
        }
        Collection<DbTerrainImagePosition> dbTerrainImagePositions = dbTerrainSetting.getDbTerrainImagePositionCrudServiceHelper().readDbChildren();
        // Remove Same
        for (Iterator<DbTerrainImagePosition> iterator = dbTerrainImagePositions.iterator(); iterator.hasNext(); ) {
            DbTerrainImagePosition dbTerrainImagePosition = iterator.next();
            ImagePositionKey key = new ImagePositionKey(dbTerrainImagePosition);
            if (newImagePosition.containsKey(key)) {
                newImagePosition.remove(key);
            } else {
                iterator.remove();
            }
        }
        // Add new
        for (TerrainImagePosition terrainImagePosition : newImagePosition.values()) {
            DbTerrainImage dbTerrainImage = terrainImageService.getDbTerrainImage(terrainImagePosition.getImageId());
            dbTerrainSetting.getDbTerrainImagePositionCrudServiceHelper().addChild(new DbTerrainImagePosition(terrainImagePosition.getTileIndex(), dbTerrainImage, terrainImagePosition.getzIndex()), null);
        }

        // Surface Rects
        Map<SurfaceRectKey, SurfaceRect> newSurfaceRect = new HashMap<>(surfaceRects.size());
        for (SurfaceRect surfaceRect : surfaceRects) {
            newSurfaceRect.put(new SurfaceRectKey(surfaceRect), surfaceRect);
        }
        Collection<DbSurfaceRect> dbSurfaceRects = dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().readDbChildren();
        // Remove Same
        for (Iterator<DbSurfaceRect> iterator = dbSurfaceRects.iterator(); iterator.hasNext(); ) {
            DbSurfaceRect dbSurfaceRect = iterator.next();
            SurfaceRectKey key = new SurfaceRectKey(dbSurfaceRect);
            if (newSurfaceRect.containsKey(key)) {
                newSurfaceRect.remove(key);
            } else {
                iterator.remove();
            }
        }
        // Add new
        for (SurfaceRect surfaceRect : newSurfaceRect.values()) {
            DbSurfaceImage dbSurfaceImage = terrainImageService.getDbSurfaceImage(surfaceRect.getSurfaceImageId());
            dbTerrainSetting.getDbSurfaceRectCrudServiceHelper().addChild(new DbSurfaceRect(surfaceRect.getTileRectangle(), dbSurfaceImage), null);
        }
    }

}
