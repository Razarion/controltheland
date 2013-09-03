package com.btxtech.game.jsre.common.gameengine.services.collision.impl;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 07.06.12
 * Time: 12:31
 *
 * See digestingduck.blogspot.ch/2010/03/simple-stupid-funnel-algorithm.html
 */
public class FunnelAlgorithm {
    private Index start;
    private Index destination;
    private List<Portal> portals;

    public FunnelAlgorithm(Index start, Index destination) {
        this.start = start;
        this.destination = destination;
    }

    public void setTilePath(List<Index> tilePath, AbstractTerrainService terrainService) {
        portals = new ArrayList<Portal>();
        if (tilePath.isEmpty()) {
            return;
        }

        int width = Constants.TERRAIN_TILE_WIDTH - 1;
        int height = Constants.TERRAIN_TILE_HEIGHT - 1;

        tilePath.add(0, TerrainUtil.getTerrainTileIndexForAbsPosition(start));
        //addStartPoints(tilePath.get(0), tilePath.get(1), terrainService, border1, border2);
        tilePath.add(TerrainUtil.getTerrainTileIndexForAbsPosition(destination));
        Index previous = null;
        Index.Direction previousDirection = null;
        for (Index tile : tilePath) {
            Index absTileStart = TerrainUtil.getAbsolutIndexForTerrainTileIndex(tile);
            if (previous != null) {
                Index.Direction newDirection = previous.getDirection(absTileStart);
                if (previousDirection != null) {
                    if (previousDirection != newDirection) {
                        Rectangle previousTileRect = new Rectangle(previous.getX(), previous.getY(), width, height);
                        Index point1 = null;
                        Index point2 = null;
                        if (previousDirection == Index.Direction.N && newDirection == Index.Direction.E) {
                            point1 = previousTileRect.getCornerNW();
                            point2 = previousTileRect.getCornerSE();
                        } else if (previousDirection == Index.Direction.N && newDirection == Index.Direction.W) {
                            point1 = previousTileRect.getCornerSW();
                            point2 = previousTileRect.getCornerNE();
                        } else if (previousDirection == Index.Direction.E && newDirection == Index.Direction.N) {
                            point1 = previousTileRect.getCornerNW();
                            point2 = previousTileRect.getCornerSE();
                        } else if (previousDirection == Index.Direction.E && newDirection == Index.Direction.S) {
                            point1 = previousTileRect.getCornerNE();
                            point2 = previousTileRect.getCornerSW();
                        } else if (previousDirection == Index.Direction.S && newDirection == Index.Direction.E) {
                            point1 = previousTileRect.getCornerNE();
                            point2 = previousTileRect.getCornerSW();
                        } else if (previousDirection == Index.Direction.S && newDirection == Index.Direction.W) {
                            point1 = previousTileRect.getCornerSE();
                            point2 = previousTileRect.getCornerNW();
                        } else if (previousDirection == Index.Direction.W && newDirection == Index.Direction.N) {
                            point1 = previousTileRect.getCornerSW();
                            point2 = previousTileRect.getCornerNE();
                        } else if (previousDirection == Index.Direction.W && newDirection == Index.Direction.S) {
                            point1 = previousTileRect.getCornerSE();
                            point2 = previousTileRect.getCornerNW();
                        }
                        if (point1 != null && point2 != null) {
                            portals.add(new Portal(point2, point1));
                        }
                    }
                }
                previousDirection = newDirection;
            }
            previous = absTileStart;
        }
        portals.add(new Portal(destination, destination));
    }

    public List<Index> stringPull() {
        List<Index> path = new ArrayList<Index>();
        path.add(start);
        Index portalApex = start;
        Index portalLeft = start;
        Index portalRight = start;

        int apexIndex = 0;
        int leftIndex = 0;
        int rightIndex = 0;


        for (int i = 0; i < portals.size(); i++) {
            Portal portal = portals.get(i);
            Index left = portal.getLeft();
            Index right = portal.getRight();

            // Update right vertex.
            if (triarea2(portalApex, portalRight, right) <= 0.0) {
                if (portalApex.equals(portalRight) || triarea2(portalApex, portalLeft, right) > 0.0) {
                    // Tighten the funnel.
                    portalRight = right;
                    rightIndex = i;
                } else {
                    // Right over left, insert left to path and restart scan from portal left point.
                    path.add(portalLeft);
                    // Make current left the new apex.
                    portalApex = portalLeft;
                    apexIndex = leftIndex;
                    // Reset portal
                    portalLeft = portalApex;
                    portalRight = portalApex;
                    leftIndex = apexIndex;
                    rightIndex = apexIndex;
                    // Restart scan
                    i = apexIndex;
                    continue;
                }
            }

            // Update left vertex.
            if (triarea2(portalApex, portalLeft, left) >= 0.0) {
                if (portalApex.equals(portalLeft) || triarea2(portalApex, portalRight, left) < 0.0) {
                    // Tighten the funnel.
                    portalLeft = left;
                    leftIndex = i;
                } else {
                    // Left over right, insert right to path and restart scan from portal right point.
                    path.add(portalRight);
                    // Make current right the new apex.
                    portalApex = portalRight;
                    apexIndex = rightIndex;
                    // Reset portal
                    portalLeft = portalApex;
                    portalRight = portalApex;
                    leftIndex = apexIndex;
                    rightIndex = apexIndex;
                    // Restart scan
                    i = apexIndex;
                    continue;
                }
            }
        }

        if (!path.get(path.size() - 1).equals(destination)) {
            path.add(destination);
        }


        return path;
    }


    private double triarea2(Index a, Index b, Index c) {
        int bx = b.getX() - a.getX();
        int by = b.getY() - a.getY();
        int cx = c.getX() - a.getX();
        int cy = c.getY() - a.getY();
        return cx * by - bx * cy;
    }


    private class Portal {
        private Index left;
        private Index right;

        private Portal(Index left, Index right) {
            this.left = left;
            this.right = right;
        }

        public Index getLeft() {
            return left;
        }

        public Index getRight() {
            return right;
        }
    }
}
