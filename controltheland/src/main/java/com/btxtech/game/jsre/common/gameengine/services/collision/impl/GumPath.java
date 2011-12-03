package com.btxtech.game.jsre.common.gameengine.services.collision.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Line;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.SimpleEntry;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
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
    private static final double MAX_DISTANCE = 10;
    private Index start;
    private Index destination;
    private List<Port> ports;
    private BoundingBox boundingBox;
    private Index middlePoint;
    private List<Map.Entry<Index, Line>> pathPorts;
    private Set<Index> indexToRemove;

    public GumPath(Index start, Index destination, List<Port> ports, BoundingBox boundingBox) {
        this.start = start;
        this.destination = destination;
        this.ports = ports;
        this.boundingBox = boundingBox;
        middlePoint = start.getMiddlePoint(destination);
    }

    private void calculateShortestPath() {
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

    public List<Index> getOptimizedPath() {
        calculateShortestPath();
        List<Index> path = optimazePath();
        path = toItemAngel(path, boundingBox);
        return path;
    }

    public static List<Index> toItemAngelSameAtom(Index start, Index destination, BoundingBox boundingBox) {
        return toItemAngel(start, destination, boundingBox);
    }

    private static List<Index> toItemAngel(List<Index> path, BoundingBox boundingBox) {
        List<Index> newPath = new ArrayList<Index>();
        Index point1 = null;
        for (Index index : path) {
            if (point1 != null) {
                newPath.addAll(toItemAngel(point1, index, boundingBox));
            }
            point1 = index;
        }
        return newPath;
    }

    private static List<Index> toItemAngel(Index start, Index destination, BoundingBox boundingBox) {
        List<Index> path = new ArrayList<Index>();
        path.add(start);
        double angel = MathHelper.normaliseAngel(start.getAngleToNord(destination));
        double allowedAngel1 = boundingBox.getAllowedAngel(angel);
        double allowedAngel2 = boundingBox.getAllowedAngel(angel, allowedAngel1);
        Line line = new Line(start, destination);

        if (MathHelper.getAngel(angel, allowedAngel1) < MathHelper.ZERO_DOT_ONE_DEGREE_IN_RAD) {
            path.add(destination);
            return path;
        }

        while (true) {
            int pathSizeBefore = path.size();
            double remainingLength = line.getNearestPointOnLine(path.get(path.size() - 1)).getDistanceDouble(destination);
            if (remainingLength < 5.0) {
                path.add(destination);
                return path;
            }
            boolean callAgain = setupIntermediatePoint(angel, allowedAngel1, MAX_DISTANCE, remainingLength, path, line);
            if (pathSizeBefore != path.size()) {
                // Swap angels
                double tmpAllowedAngel1 = allowedAngel1;
                allowedAngel1 = allowedAngel2;
                allowedAngel2 = tmpAllowedAngel1;
            }
            if (!callAgain) {
                setupLastPoint(path, allowedAngel1, allowedAngel2, destination);
                return path;
            }
        }
    }

    private static void setupLastPoint(List<Index> path, double allowedAngel, double allowedOtherAngel, Index destination) {
        if (path.size() == 1) {
            setupLastPointSingle(path, allowedAngel, allowedOtherAngel, destination);
        } else {
            setupLastPointMulti(path, allowedAngel, destination);
        }
        path.add(destination);
    }

    private static void setupLastPointMulti(List<Index> path, double allowedAngel, Index destination) {
        Index pointToRemove = path.remove(path.size() - 1);
        Index lastPoint = path.get(path.size() - 1);
        Line secondLastLine = new Line(lastPoint, pointToRemove);
        Line lastLine = new Line(destination, MathHelper.HALF_RADIANT + allowedAngel, 100000);
        Index cross = lastLine.getCrossInfinite(secondLastLine);
        if (cross == null) {
            throw new IllegalArgumentException(secondLastLine + " +++ " + lastLine + "\n" + path);
        }
        if (!cross.equals(destination)) {
            path.add(cross);
        }
    }

    private static void setupLastPointSingle(List<Index> path, double allowedAngel, double allowedOtherAngel, Index destination) {
        Index start = path.get(0);
        Line line1 = new Line(start, allowedAngel, 100000);
        Line line2 = new Line(destination, MathHelper.HALF_RADIANT + allowedOtherAngel, 100000);
        Index cross = line1.getCrossInfinite(line2);
        if (cross == null) {
            throw new IllegalArgumentException(line1 + " +++ " + line2 + "\n" + path);
        }
        path.add(cross);
    }

    /**
     * @param angel           angel
     * @param allowedAngel    allowedAngel
     * @param maxDistance     maxDistance
     * @param remainingLength remainingLength
     * @param path            path
     * @param line            line
     * @return Returns true if this method must be called again to calculate the whole path
     */
    private static boolean setupIntermediatePoint(double angel, double allowedAngel, double maxDistance, double remainingLength, List<Index> path, Line line) {
        Index start = path.get(path.size() - 1);
        double pointDistance = (line.getShortestDistanceOnInfiniteLine(start) + maxDistance) / Math.sin(MathHelper.getAngel(angel, allowedAngel));
        Index end = start.getPointFromAngelToNord(allowedAngel, pointDistance);
        double neededLength = line.projectOnInfiniteLine(start).getDistanceDouble(line.projectOnInfiniteLine(end));

        if (remainingLength > neededLength) {
            path.add(end);
            return true;
        } else {
            if (remainingLength == neededLength || remainingLength >= neededLength / 2.0) {
                // this point will be removed in setupLastPoint
                path.add(end);
            }
            return false;
        }
    }

    private List<Index> optimazePath() {
        List<Index> path = new ArrayList<Index>();
        for (Map.Entry<Index, Line> pathBorder : pathPorts) {
            if (!indexToRemove.contains(pathBorder.getKey())) {
                path.add(pathBorder.getKey());
            } else {
                indexToRemove.remove(pathBorder.getKey());
            }
        }
        return path;
    }

    public List<Port> getPorts() {
        return ports;
    }

    public Index getStart() {
        return start;
    }

    public Index getDestination() {
        return destination;
    }
}