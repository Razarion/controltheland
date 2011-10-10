package com.btxtech.game.jsre.common.gameengine.services.collision.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Line;
import com.btxtech.game.jsre.common.SimpleEntry;
import com.btxtech.game.jsre.common.gameengine.services.collision.Port;

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
    private List<Port> ports;
    private Index middlePoint;
    private List<Map.Entry<Index, Line>> pathPorts;
    private Set<Index> indexToRemove;

    public GumPath(Index start, Index destination, List<Port> ports) {
        this.start = start;
        this.destination = destination;
        this.ports = ports;
        middlePoint = start.getMiddlePoint(destination);
    }

    public void calculateShortestPath() {
        pathPorts = new ArrayList<Map.Entry<Index, Line>>();
        fillPath();
        optimizePath();
    }

    private void optimizePath() {
        indexToRemove = new HashSet<Index>();
        if (pathPorts.size() < 3) {
            return;
        }

        // Shift lower point
        for (int lowerPointIndex = 0; lowerPointIndex + 2 < pathPorts.size();) {
            Index point1 = pathPorts.get(lowerPointIndex).getKey();
            boolean allFits = true;
            // Shift upper point
            int lastUpperPointIndex = lowerPointIndex + 1;
            for (int upperPointIndex = lowerPointIndex + 2; upperPointIndex < pathPorts.size(); upperPointIndex++) {
                Index point2 = pathPorts.get(upperPointIndex).getKey();
                Line line = new Line(point1, point2);
                // Check all borders between lowerPointIndex and upperPointIndex
                for (int borderIndex = lowerPointIndex + 1; borderIndex < upperPointIndex; borderIndex++) {
                    Line crossLines = pathPorts.get(borderIndex).getValue();
                    if (crossLines.getCross(line) != null) {
                        indexToRemove.add(pathPorts.get(borderIndex).getKey());
                    } else {
                        allFits = false;
                        break;
                    }
                }

                if (!allFits) {
                    break;
                } else {
                    lastUpperPointIndex = upperPointIndex;
                }
            }
            lowerPointIndex = lastUpperPointIndex;
        }
    }

    private void fillPath() {
        pathPorts.add(new SimpleEntry<Index, Line>(start, null));
        for (Port port : ports) {
            pathPorts.add(new SimpleEntry<Index, Line>(port.getCurrentNearestCrossPoint(middlePoint), port.getCurrentCrossLine()));
            pathPorts.add(new SimpleEntry<Index, Line>(port.getDestinationNearestCrossPoint(middlePoint), (port.getDestinationCrossLine())));
        }
        pathPorts.add(new SimpleEntry<Index, Line>(destination, null));
    }

    public List<Index> getPath() {
        List<Index> path = new ArrayList<Index>();
        for (Map.Entry<Index, Line> pathBorder : pathPorts) {
            if (!indexToRemove.contains(pathBorder.getKey())) {
                path.add(pathBorder.getKey());
            }
        }
        return path;
    }

    public List<Port> getPorts() {
        return ports;
    }
}
