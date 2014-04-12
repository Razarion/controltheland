package com.btxtech.game.jsre.common.gameengine.services.collision.impl;

import com.btxtech.game.jsre.client.common.Index;

/**
 * User: beat
 * Date: 05.04.14
 * Time: 20:21
 */
public class PositionFinderNode implements Comparable<PositionFinderNode> {
    private Index tileIndex;
    private double distanceTarget;
    private double distanceOrigin;

    public PositionFinderNode(Index tileIndex, Index targetTile, Index itemTile) {
        this.tileIndex = tileIndex;
        distanceTarget = tileIndex.getDistanceDouble(targetTile);
        distanceOrigin = tileIndex.getDistanceDouble(itemTile);
    }

    @Override
    public int compareTo(PositionFinderNode o) {
        int result = Double.compare(distanceTarget, o.distanceTarget);
        if (result == 0) {
            return Double.compare(distanceOrigin, o.distanceOrigin);
        } else {
            return result;
        }
    }

    public Index getTileIndex() {
        return tileIndex;
    }
}
