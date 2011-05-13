package com.btxtech.game.services.collision.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleEntry;
import com.btxtech.game.services.terrain.TerrainService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: beat
 * Date: 06.05.2011
 * Time: 14:47:14
 */
public class GumPath {
    private Index start;
    private Index destination;
    private List<Rectangle> borders;
    private TerrainService terrainService;
    private Index middlePoint;
    private List<Map.Entry<Index, Rectangle>> pathBorders;
    private Set<Index> indexToRemove;

    public GumPath(Index start, Index destination, List<Rectangle> borders, TerrainService terrainService) {
        this.start = start;
        this.destination = destination;
        this.borders = borders;
        this.terrainService = terrainService;
        middlePoint = start.getMiddlePoint(destination);
    }

    public void calculateShortestPath() {
        pathBorders = new ArrayList<Map.Entry<Index, Rectangle>>();
        fillPath();
        optimizePath();
    }

    private void optimizePath() {
        indexToRemove = new HashSet<Index>();
        if (pathBorders.size() < 3) {
            return;
        }

        // Shift lower point
        for (int lowerPointIndex = 0; lowerPointIndex + 2 < pathBorders.size();lowerPointIndex++) {
            Index point1 = pathBorders.get(lowerPointIndex).getKey();
            boolean allFits = true;
            int lastSkipCount = 0;
            // Shift upper point
            for (int upperPointIndex = lowerPointIndex + 2; upperPointIndex < pathBorders.size(); upperPointIndex++) {
                Index point2 = pathBorders.get(upperPointIndex).getKey();
                // Check all borders between lowerPointIndex and upperPointIndex
                for (int borderIndex = lowerPointIndex + 1; borderIndex < upperPointIndex; borderIndex++) {
                    Rectangle absBorder = terrainService.convertToAbsolutePosition(pathBorders.get(borderIndex).getValue());
                    if (!absBorder.doesLineCut(point1, point2)) {
                        allFits = false;
                        break;
                    } else {
                        lastSkipCount = borderIndex;
                        indexToRemove.add(pathBorders.get(borderIndex).getKey());
                    }
                }

                if (!allFits) {
                    break;
                }
            }
            lowerPointIndex += lastSkipCount;
        }
    }

    private void fillPath() {
        pathBorders.add(new SimpleEntry<Index, Rectangle>(start, null));
        for (Rectangle border : borders) {
            Rectangle absBorder = terrainService.convertToAbsolutePosition(border);
            pathBorders.add(new SimpleEntry<Index, Rectangle>(absBorder.getNearestPoint(middlePoint), border));
        }
        pathBorders.add(new SimpleEntry<Index, Rectangle>(destination, null));
    }

    public List<Index> getPath() {
        List<Index> path = new ArrayList<Index>();
        for (Map.Entry<Index, Rectangle> pathBorder : pathBorders) {
            if (!indexToRemove.contains(pathBorder.getKey())) {
                path.add(pathBorder.getKey());
            }
        }
        return path;
    }

}
