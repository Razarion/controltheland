/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.jsre.common.gameengine.itemType;

import java.io.Serializable;
import java.util.Collection;

/**
 * User: beat
 * Date: 21.11.2009
 * Time: 23:53:27
 */
public class BuilderType implements Serializable {
    private int range;
    private int progress;
    private Collection<Integer> ableToBuild;

    /**
     * Used by GWT
     */
    BuilderType() {
    }

    public BuilderType(int range, int progress, Collection<Integer> ableToBuild) {
        this.range = range;
        this.progress = progress;
        this.ableToBuild = ableToBuild;
    }

    public int getRange() {
        return range;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isAbleToBuild(int itemTypeId) {
       return ableToBuild.contains(itemTypeId); 
    }

    public Collection<Integer> getAbleToBuild() {
        return ableToBuild;
    }

    public void changeTo(BuilderType builderType) {
        range = builderType.range;
        progress = builderType.progress;
        ableToBuild = builderType.ableToBuild;
    }
}