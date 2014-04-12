package com.btxtech.game.jsre.common.gameengine.services.collision.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.CollisionTileContainer;

import java.util.PriorityQueue;

/**
 * User: beat
 * Date: 04.06.12
 * Time: 00:16
 */
public class FreePositionFinder {
    private CollisionTileContainer collisionTileContainer;
    private Index targetTile;
    private int edgeTileLength;
    private Index itemTile;
    private int absoluteItemRadius;
    private PriorityQueue<PositionFinderNode> positionFinderNodes;

    public static Index findFreePosition(CollisionTileContainer collisionTileContainer, Index targetTile, int edgeTileLength, Index itemTile, int absoluteItemRadius) {
        FreePositionFinder positionFinder = new FreePositionFinder(collisionTileContainer, targetTile, edgeTileLength, itemTile, absoluteItemRadius);
        positionFinder.fillPossibilityList();
        return positionFinder.find();
    }

    private FreePositionFinder(CollisionTileContainer collisionTileContainer, Index targetTile, int edgeTileLength, Index itemTile, int absoluteItemRadius) {
        this.collisionTileContainer = collisionTileContainer;
        this.targetTile = targetTile;
        this.edgeTileLength = edgeTileLength;
        this.itemTile = itemTile;
        this.absoluteItemRadius = absoluteItemRadius;
    }

    private Index find() {
        return positionFinderNodes.poll().getTileIndex();
    }

    private void fillPossibilityList() {
        positionFinderNodes = new PriorityQueue<PositionFinderNode>();
        for (int x = targetTile.getX() - edgeTileLength; x < targetTile.getX() + edgeTileLength; x++) {
            for (int y = targetTile.getY() - edgeTileLength; y < targetTile.getY() + edgeTileLength; y++) {
                Index tileIndex = new Index(x, y);
                if (!collisionTileContainer.isBlocked(tileIndex, absoluteItemRadius)) {
                    positionFinderNodes.add(new PositionFinderNode(tileIndex, targetTile, itemTile));
                }
            }
        }
    }
}
