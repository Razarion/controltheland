package com.btxtech.game.jsre.common.gameengine.services.collision.impl;

import com.btxtech.game.jsre.client.common.Index;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
* User: beat
* Date: 29.03.14
* Time: 11:10
*/
class OpenList {
    private PriorityQueue<AStarNode> sortedList = new PriorityQueue<AStarNode>();
    private Map<Index, AStarNode> map = new HashMap<Index, AStarNode>();

    public void add(AStarNode node) {
        sortedList.add(node);
        map.put(node.getTileIndex(), node);
    }

    public AStarNode removeFirst() {
        AStarNode node = sortedList.poll();
        map.remove(node.getTileIndex());
        return node;
    }

    public AStarNode get(Index index) {
        return map.get(index);
    }

    public void remove(Index index) {
        AStarNode node = map.remove(index);
        if (node != null) {
            sortedList.remove(node);
        }
    }

    public boolean isEmpty() {
        return sortedList.isEmpty();
    }
}
