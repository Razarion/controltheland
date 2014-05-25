package com.btxtech.game.jsre.common.gameengine.services.collision.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.CollisionTileContainer;

import java.util.*;

/**
 * User: beat
 * Date: 04.06.12
 * Time: 00:16
 */
public class AStar {
    private Map<Index, AStarNode> closedList = new HashMap<Index, AStarNode>();
    private OpenList openList = new OpenList();
    private AStarNode endNode;
    private CollisionTileContainer collisionTileContainer;
    private Index startTile;
    private Index endTile;
    private int absoluteRadius;
    private boolean pathFound;
    private List<Index> tilePath;
    private double smallestHeuristic = Double.MAX_VALUE;
    private AStarNode bestFitNode;

    public static AStar findTilePath(CollisionTileContainer collisionTileContainer, Index startTile, Index endTile, int absoluteRadius) throws NoBetterPathFoundException, BestPositionFoundException {
        // Index freePosition = FreePositionFinder.findFreePosition(collisionTileContainer, endTile, 100, startTile, absoluteRadius);
        // if(freePosition.equals(startTile)) {
        //   throw new BestPositionFoundException();
        //}
        AStar aStar = new AStar(collisionTileContainer, startTile, endTile, absoluteRadius);
        aStar.expandAllNodes();
        aStar.convertPath();
        return aStar;
    }

    private AStar(CollisionTileContainer collisionTileContainer, Index startTile, Index endTile, int absoluteRadius) {
        this.collisionTileContainer = collisionTileContainer;
        this.startTile = startTile;
        this.endTile = endTile;
        this.absoluteRadius = absoluteRadius;
        endNode = new AStarNode(endTile);
        openList.add(new AStarNode(startTile));
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

    public Index getEndTile() {
        return endTile;
    }

    private void expandAllNodes() {
        while (true) {
            if (openList.isEmpty()) {
                // Path hot found
                return;
            }
            AStarNode current = openList.removeFirst();
            if (current.equals(endNode)) {
                pathFound = true;
                return;
            } else {
                expandNode(current);
            }
        }
    }

    private void expandNode(AStarNode current) {
        handleAllSuccessorNodes(current);
        closedList.put(current.getTileIndex(), current);
    }

    private void handleAllSuccessorNodes(AStarNode current) {
        Index currentTilePosition = current.getTileIndex();

        // North
        if (currentTilePosition.getY() > 0) {
            Index indexN = currentTilePosition.add(0, -1);
            handleSuccessorNode(current, currentTilePosition, indexN);
        }
        // East
        if (currentTilePosition.getX() < collisionTileContainer.getXTiles() - 1) {
            Index indexE = currentTilePosition.add(1, 0);
            handleSuccessorNode(current, currentTilePosition, indexE);
        }
        // South
        if (currentTilePosition.getY() < collisionTileContainer.getYTiles() - 1) {
            Index indexS = currentTilePosition.add(0, 1);
            handleSuccessorNode(current, currentTilePosition, indexS);
        }
        // West
        if (currentTilePosition.getX() > 0) {
            Index indexW = currentTilePosition.add(-1, 0);
            handleSuccessorNode(current, currentTilePosition, indexW);
        }
    }

    private void handleSuccessorNode(AStarNode current, Index currentTilePosition, Index successorTilePosition) {
        if (collisionTileContainer.isBlocked(currentTilePosition, absoluteRadius)) {
            return;
        }

        if (!closedList.containsKey(successorTilePosition)) {
            double tentativeG = current.getG() + currentTilePosition.getDistanceDouble(successorTilePosition);
            AStarNode successor = openList.get(successorTilePosition);
            if (successor == null || tentativeG < successor.getG()) {
                if (successor == null) {
                    if (successorTilePosition.equals(endNode.getTileIndex())) {
                        successor = endNode;
                    } else {
                        successor = new AStarNode(successorTilePosition);
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

    private List<Index> convertPath() throws NoBetterPathFoundException {
        tilePath = new ArrayList<Index>();
        AStarNode tempNode;
        if (pathFound) {
            tempNode = endNode.getPredecessor();
        } else {
            if (bestFitNode == null) {
                throw new NoBetterPathFoundException(startTile, endTile);
            }
            tempNode = bestFitNode.getPredecessor();
        }
        // Omit addItems
        while (tempNode != null && tempNode.getPredecessor() != null) {
            tilePath.add(tempNode.getTileIndex());
            tempNode = tempNode.getPredecessor();
        }
        Collections.reverse(tilePath);
        return tilePath;
    }
}
