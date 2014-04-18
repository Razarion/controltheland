package com.btxtech.game.jsre.common.gameengine.services.collision.impl;

import com.btxtech.game.jsre.client.common.Index;

/**
* User: beat
* Date: 29.03.14
* Time: 11:09
*/
class AStarNode implements Comparable<AStarNode> {
    private Index tileIndex;
    private double f = 0;
    private double g; // Cost to this node
    private AStarNode predecessor;

    AStarNode(Index tileIndex) {
        this.tileIndex = tileIndex;
    }

    @Override
    public int compareTo(AStarNode o) {
        return Double.compare(f, o.f);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AStarNode node = (AStarNode) o;

        return tileIndex.equals(node.tileIndex);
    }

    @Override
    public int hashCode() {
        return tileIndex.hashCode();
    }

    public Index getTileIndex() {
        return tileIndex;
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

    public void setPredecessor(AStarNode predecessor) {
        this.predecessor = predecessor;
    }

    public AStarNode getPredecessor() {
        return predecessor;
    }
}
