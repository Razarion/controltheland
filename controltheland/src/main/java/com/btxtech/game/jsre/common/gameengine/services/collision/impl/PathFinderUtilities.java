package com.btxtech.game.jsre.common.gameengine.services.collision.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.collision.PassableRectangle;
import com.btxtech.game.jsre.common.gameengine.services.collision.Path;
import com.btxtech.game.jsre.common.gameengine.services.collision.PathElement;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 06.05.2011
 * Time: 18:48:46
 */
public class PathFinderUtilities {

    public static List<PassableRectangle> buildPassableRectangleList(Collection<Rectangle> rectangles, AbstractTerrainService terrainService) {
        List<PassableRectangle> passableRectangles = new ArrayList<PassableRectangle>();

        for (Rectangle rectangle : rectangles) {
            PassableRectangle passableRectangle = new PassableRectangle(rectangle);
            passableRectangles.add(passableRectangle);
        }

        List<PassableRectangle> remaining = new ArrayList<PassableRectangle>(passableRectangles);
        while (!remaining.isEmpty()) {
            PassableRectangle passableRectangle = remaining.remove(0);
            for (PassableRectangle possibleNeighbor : remaining) {
                if (passableRectangle.getRectangle().adjoins(possibleNeighbor.getRectangle()) &&
                        !passableRectangle.getRectangle().getCrossSection(possibleNeighbor.getRectangle()).isEmpty()) {
                    passableRectangle.addNeighbor(possibleNeighbor, terrainService);
                    possibleNeighbor.addNeighbor(passableRectangle, terrainService);
                }
            }
        }

        return passableRectangles;
    }

    public static PassableRectangle getPassableRectangleOfAbsoluteIndex(Index absoluteIndex,
                                                                        TerrainType terrainType,
                                                                        Map<TerrainType, List<PassableRectangle>> passableRectangles4TerrainType,
                                                                        AbstractTerrainService terrainService) {
        List<PassableRectangle> passableRectangles = passableRectangles4TerrainType.get(terrainType);
        if (passableRectangles == null) {
            return null;
        }
        // TODO Slow!
        for (PassableRectangle passableRectangle : passableRectangles) {
            if (passableRectangle.containAbsoluteIndex(absoluteIndex, terrainService.getTerrainSettings())) {
                return passableRectangle;
            }
        }
        return null;

    }

    public static PassableRectangle getNearestPassableRectangleDifferentTerrainTypeOfAbsoluteIndex(Index absoluteIndexTaget,
                                                                                                   TerrainType terrainTypeTarget,
                                                                                                   TerrainType terrainType,
                                                                                                   Map<TerrainType, List<PassableRectangle>> passableRectangles4TerrainType,
                                                                                                   AbstractTerrainService terrainService) {
        PassableRectangle targetPassableRectangle = getPassableRectangleOfAbsoluteIndex(absoluteIndexTaget, terrainTypeTarget, passableRectangles4TerrainType, terrainService);
        if (targetPassableRectangle == null) {
            return null;
        }
        List<PassableRectangle> passableRectangles = passableRectangles4TerrainType.get(terrainType);
        if (passableRectangles == null) {
            return null;
        }
        Index target = targetPassableRectangle.getRectangle().getCenter();
        // Slow!
        double shortestDistance = Double.MAX_VALUE;
        PassableRectangle shortest = null;
        for (PassableRectangle passableRectangle : passableRectangles) {
            double distance = passableRectangle.getRectangle().getCenter().getDistance(target);
            if (distance < shortestDistance) {
                shortestDistance = distance;
                shortest = passableRectangle;
            }
        }
        return shortest;
    }

    public static Path optimizePath(Path path) {
        Path optimized = path;
        while (optimized != null) {
            optimized = optimizePathOnePass(optimized);
            if (optimized != null) {
                path = optimized;
            }
        }
        return path;
    }

    public static Path optimizePathOnePass(Path path) {
        if (path.length() < 3) {
            return null;
        }
        // Shift lower point
        for (int lowerPathElementIndex = 0; lowerPathElementIndex + 2 < path.length(); lowerPathElementIndex++) {
            PathElement point1 = path.getPathElements().get(lowerPathElementIndex);

            for (int upperPathElementIndex = path.length() - 1; upperPathElementIndex > lowerPathElementIndex + 1; upperPathElementIndex--) {
                PathElement point2 = path.getPathElements().get(upperPathElementIndex);
                if (point1.getPassableRectangle().isNeighbor(point2.getPassableRectangle())) {
                    return path.subPath(0, lowerPathElementIndex).add(path.subPath(upperPathElementIndex, path.length() - 1));
                }
            }

        }
        return null;
    }


}
