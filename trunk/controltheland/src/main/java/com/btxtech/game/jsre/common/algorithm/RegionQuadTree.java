package com.btxtech.game.jsre.common.algorithm;


import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 09.09.12
 * Time: 11:19
 */
public class RegionQuadTree {
    private RegionQuadTreeNode root;

    public RegionQuadTree(Rectangle boundary) {
        root = new RegionQuadTreeNode(boundary);
    }

    public void insert(Index point) {
        if (!root.insert(point)) {
            throw new IllegalArgumentException("RegionQuadTree.insert() can not insert point: " + point + " " + root);
        }
    }

    public boolean queryPointInside(Index point) {
        return root.queryPointInside(point);
    }

    public Collection<Rectangle> queryRegions() {
        Collection<Rectangle> regions = new ArrayList<Rectangle>();
        root.queryRegions(regions);
        return regions;
    }

}
