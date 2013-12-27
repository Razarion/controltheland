package com.btxtech.game.jsre.common.gameengine.services.collision.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainTile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

/**
 * User: beat
 * Date: 04.06.12
 * Time: 00:16
 */
public class AStar {
    private Map<Index, Node> closedList = new HashMap<Index, Node>();
    private OpenList openList = new OpenList();
    private Node endNode;
    private TerrainTile[][] terrainTiles;
    private Set<SurfaceType> allowedSurfaces;
    private int columns;
    private int rows;
    private boolean pathFound;
    private List<Index> tilePath;
    private double smallestHeuristic = Double.MAX_VALUE;
    private Node bestFitNode;

    public static AStar findTilePath(TerrainTile[][] terrainTiles, Index startTile, Index endTile, Collection<SurfaceType> allowedSurfaces) {
        AStar aStar = new AStar(terrainTiles, startTile, endTile, allowedSurfaces);
        aStar.expandAllNodes();
        aStar.convertPath();
        return aStar;
    }

    private AStar(TerrainTile[][] terrainTiles, Index startTile, Index endTile, Collection<SurfaceType> allowedSurfaces) {
        this.terrainTiles = terrainTiles;
        this.allowedSurfaces = new TreeSet<SurfaceType>(allowedSurfaces);
        columns = terrainTiles.length;
        rows = terrainTiles[0].length;
        endNode = new Node(endTile);
        openList.add(new Node(startTile));
    }

    public boolean isPathFound() {
        return pathFound;
    }

    public List<Index> getTilePath() {
        return tilePath;
    }

    public Index getBestFitTile() {
        return bestFitNode.getTileIndex();
    }

    private void expandAllNodes() {
        while (true) {
            if (openList.isEmpty()) {
                // Path hot found
                return;
            }
            Node current = openList.removeFirst();
            if (current.equals(endNode)) {
                pathFound = true;
                return;
            } else {
                expandNode(current);
            }
        }
    }

    private List<Index> convertPath() {
        tilePath = new ArrayList<Index>();
        Node tempNode;
        if (pathFound) {
            tempNode = endNode.getPredecessor();
        } else {
            if (bestFitNode == null) {
                throw new IllegalStateException("AStar: bestFitNode == null");
            }
            tempNode = bestFitNode.getPredecessor();
        }
        // Omit start
        while (tempNode != null && tempNode.getPredecessor() != null) {
            tilePath.add(tempNode.getTileIndex());
            tempNode = tempNode.getPredecessor();
        }
        Collections.reverse(tilePath);
        return tilePath;
    }

    private void expandNode(Node current) {
        handleAllSuccessorNodes(current);
        closedList.put(current.getTileIndex(), current);
    }

    private void handleAllSuccessorNodes(Node current) {
        Index currentTilePosition = current.getTileIndex();

        // North
        if (currentTilePosition.getY() > 0) {
            Index indexN = currentTilePosition.add(0, -1);
            handleSuccessorNode(current, currentTilePosition, indexN);
        }
        // East
        if (currentTilePosition.getX() < columns - 1) {
            Index indexE = currentTilePosition.add(1, 0);
            handleSuccessorNode(current, currentTilePosition, indexE);
        }
        // South
        if (currentTilePosition.getY() < rows - 1) {
            Index indexS = currentTilePosition.add(0, 1);
            handleSuccessorNode(current, currentTilePosition, indexS);
        }
        // West
        if (currentTilePosition.getX() > 0) {
            Index indexW = currentTilePosition.add(-1, 0);
            handleSuccessorNode(current, currentTilePosition, indexW);
        }
    }

    private void handleSuccessorNode(Node current, Index currentTilePosition, Index successorTilePosition) {
        TerrainTile terrainTile = terrainTiles[successorTilePosition.getX()][successorTilePosition.getY()];
        if (terrainTile == null || !allowedSurfaces.contains(terrainTile.getSurfaceType())) {
            return;
        }

        if (!closedList.containsKey(successorTilePosition)) {
            double tentativeG = current.getG() + currentTilePosition.getDistanceDouble(successorTilePosition);
            Node successor = openList.get(successorTilePosition);
            if (successor == null || tentativeG < successor.getG()) {
                if (successor == null) {
                    if (successorTilePosition.equals(endNode.getTileIndex())) {
                        successor = endNode;
                    } else {
                        successor = new Node(successorTilePosition);
                    }
                } else {
                    openList.remove(successorTilePosition);
                }
                successor.setPredecessor(current);
                successor.setG(tentativeG);
                double heuristic = successorTilePosition.getDistanceDouble(endNode.getTileIndex());
                successor.setF(tentativeG + heuristic);
                openList.add(successor);
                if (smallestHeuristic > heuristic) {
                    smallestHeuristic = heuristic;
                    bestFitNode = successor;
                }
            }
        }
    }

    private class OpenList {
        private PriorityQueue<Node> sortedList = new PriorityQueue<Node>();
        private Map<Index, Node> map = new HashMap<Index, Node>();

        public void add(Node node) {
            sortedList.add(node);
            map.put(node.getTileIndex(), node);
        }

        public Node removeFirst() {
            Node node = sortedList.poll();
            map.remove(node.getTileIndex());
            return node;
        }

        public Node get(Index index) {
            return map.get(index);
        }

        public void remove(Index index) {
            Node node = map.remove(index);
            if (node != null) {
                sortedList.remove(node);
            }
        }

        public boolean isEmpty() {
            return sortedList.isEmpty();
        }
    }

    private class Node implements Comparable<Node> {
        private Index tileIndex;
        private double f = 0;
        private double g; // Cost to this node
        private Node predecessor;

        private Node(Index tileIndex) {
            this.tileIndex = tileIndex;
        }

        @Override
        public int compareTo(Node o) {
            return Double.compare(f, o.f);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;

            return tileIndex.equals(node.tileIndex);
        }

        @Override
        public int hashCode() {
            return tileIndex.hashCode();
        }

        public Index getTileIndex() {
            return tileIndex;
        }

        public double getF() {
            return f;
        }

        public void setF(double f) {
            this.f = f;
        }

        public double getG() {
            return g;
        }

        public void setG(double g) {
            this.g = g;
        }

        public void setPredecessor(Node predecessor) {
            this.predecessor = predecessor;
        }

        public Node getPredecessor() {
            return predecessor;
        }
    }
}
