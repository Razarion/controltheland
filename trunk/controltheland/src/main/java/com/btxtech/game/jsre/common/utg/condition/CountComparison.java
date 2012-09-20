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

package com.btxtech.game.jsre.common.utg.condition;

import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

/**
 * User: beat
 * Date: 27.12.2010
 * Time: 16:31:29
 */
public class CountComparison extends AbstractSyncItemComparison {
    private double count;
    private double countTotal;

    public CountComparison(int count, String htmlProgressTamplate) {
        super(htmlProgressTamplate);
        this.count = count;
        countTotal = count;
    }

    @Override
    protected void privateOnSyncItem(SyncItem syncItem) {
        count -= 1.0;
        onProgressChanged();
    }

    public void onValue(double value) {
        count -= value;
        onProgressChanged();
    }

    @Override
    public boolean isFulfilled() {
        return count <= 0.0;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    @Override
    public void fillGenericComparisonValues(GenericComparisonValueContainer genericComparisonValueContainer) {
        genericComparisonValueContainer.addChild(GenericComparisonValueContainer.Key.REMAINING_COUNT, count);
    }

    @Override
    public void restoreFromGenericComparisonValue(GenericComparisonValueContainer genericComparisonValueContainer) {
        count = (Double) genericComparisonValueContainer.getValue(GenericComparisonValueContainer.Key.REMAINING_COUNT);
    }

    @Override
    protected String getValue(char parameter, Integer number) {
        if (parameter == TEMPLATE_PARAMETER_COUNT) {
            return Integer.toString((int) (countTotal - count));
        } else {
            throw new IllegalArgumentException("CountComparison.getValue() parameter is not known: " + parameter);
        }
    }
}
