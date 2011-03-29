/*
 * Copyright (c) 2011.
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

package com.btxtech.game.jsre.common.utg.condition;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 21:06:41
 */
public class ContainedInComparison implements AbstractComparison {
    private boolean fulfilled = false;
    private boolean containedIn;

    public ContainedInComparison(boolean containedIn) {
        this.containedIn = containedIn;
    }

    @Override
    public boolean isFulfilled() {
        return fulfilled;
    }

    public void onContainedInChanged(boolean containedIn) {
        if (containedIn == this.containedIn) {
            fulfilled = true;
        }
    }
}