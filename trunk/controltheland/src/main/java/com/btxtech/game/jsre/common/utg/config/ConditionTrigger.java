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

package com.btxtech.game.jsre.common.utg.config;

import com.btxtech.game.jsre.common.utg.condition.AbstractComparison;
import com.btxtech.game.jsre.common.utg.condition.AbstractConditionTrigger;
import com.btxtech.game.jsre.common.utg.condition.SimpleConditionTrigger;
import com.btxtech.game.jsre.common.utg.condition.SyncItemConditionTrigger;
import com.btxtech.game.jsre.common.utg.condition.ValueConditionTrigger;

/**
 * User: beat
 * Date: 27.12.2010
 * Time: 20:30:23
 */
public enum ConditionTrigger {
    SYNC_ITEM_KILLED(true) {
        @Override
        public <A, I> AbstractConditionTrigger<A, I> createAbstractConditionTrigger(AbstractComparison abstractComparison) {
            return new SyncItemConditionTrigger<A, I>(this, abstractComparison);
        }},
    MONEY_INCREASED(true) {
        @Override
        public <A, I> AbstractConditionTrigger<A, I> createAbstractConditionTrigger(AbstractComparison abstractComparison) {
            return new ValueConditionTrigger<A, I>(this, abstractComparison);
        }},
    SYNC_ITEM_BUILT(true) {
        @Override
        public <A, I> AbstractConditionTrigger<A, I> createAbstractConditionTrigger(AbstractComparison abstractComparison) {
            return new SyncItemConditionTrigger<A, I>(this, abstractComparison);
        }},
    XP_INCREASED(true) {
        @Override
        public <A, I> AbstractConditionTrigger<A, I> createAbstractConditionTrigger(AbstractComparison abstractComparison) {
            return new ValueConditionTrigger<A, I>(this, abstractComparison);
        }},
    BASE_KILLED(true) {
        @Override
        public <A, I> AbstractConditionTrigger<A, I> createAbstractConditionTrigger(AbstractComparison abstractComparison) {
            return new ValueConditionTrigger<A, I>(this, abstractComparison);
        }},
    TUTORIAL(false) {
        @Override
        public <A, I> AbstractConditionTrigger<A, I> createAbstractConditionTrigger(AbstractComparison abstractComparison) {
            return new SimpleConditionTrigger<A, I>(this);
        }},
    SYNC_ITEM_POSITION(true) {
        @Override
        public <A, I> AbstractConditionTrigger<A, I> createAbstractConditionTrigger(AbstractComparison abstractComparison) {
            return new SyncItemConditionTrigger<A, I>(this, abstractComparison);
        }};


    private boolean comparisonNeeded;

    ConditionTrigger(boolean comparisonNeeded) {
        this.comparisonNeeded = comparisonNeeded;
    }

    public abstract <A, I> AbstractConditionTrigger<A, I> createAbstractConditionTrigger(AbstractComparison abstractComparison);

    public boolean isComparisonNeeded() {
        return comparisonNeeded;
    }
}
