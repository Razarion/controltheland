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
import com.btxtech.game.jsre.common.utg.condition.CockpitButtonTrigger;
import com.btxtech.game.jsre.common.utg.condition.ContainedInTrigger;
import com.btxtech.game.jsre.common.utg.condition.PositionConditionTrigger;
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
        public <T> AbstractConditionTrigger<T> createAbstractConditionTrigger(AbstractComparison abstractComparison, T t) {
            return new SyncItemConditionTrigger<T>(this, abstractComparison, t);
        }},
    TUTORIAL(false) {
        @Override
        public <T> AbstractConditionTrigger<T> createAbstractConditionTrigger(AbstractComparison ignore, T t) {
            return new SimpleConditionTrigger<T>(this, t);
        }},
    SYNC_ITEM_SELECT(true) {
        @Override
        public <T> AbstractConditionTrigger<T> createAbstractConditionTrigger(AbstractComparison abstractComparison, T t) {
            return new SyncItemConditionTrigger<T>(this, abstractComparison, t);
        }},
    SYNC_ITEM_DEACTIVATE(true) {
        @Override
        public <T> AbstractConditionTrigger<T> createAbstractConditionTrigger(AbstractComparison abstractComparison, T t) {
            return new SyncItemConditionTrigger<T>(this, abstractComparison, t);
        }},
    SYNC_ITEM_BUILT(true) {
        @Override
        public <T> AbstractConditionTrigger<T> createAbstractConditionTrigger(AbstractComparison abstractComparison, T t) {
            return new SyncItemConditionTrigger<T>(this, abstractComparison, t);
        }},
    SCROLL(false) {
        @Override
        public <T> AbstractConditionTrigger<T> createAbstractConditionTrigger(AbstractComparison abstractComparison, T t) {
            return new SimpleConditionTrigger<T>(this, t);
        }},
    COCKPIT_BUTTON_EVENT(true) {
        @Override
        public <T> AbstractConditionTrigger<T> createAbstractConditionTrigger(AbstractComparison abstractComparison, T t) {
            return new CockpitButtonTrigger<T>(abstractComparison, t);
        }},
    MONEY_INCREASED(true) {
        @Override
        public <T> AbstractConditionTrigger<T> createAbstractConditionTrigger(AbstractComparison abstractComparison, T t) {
            return new ValueConditionTrigger<T>(this, abstractComparison, t);
        }},
    XP_INCREASED(true) {
        @Override
        public <T> AbstractConditionTrigger<T> createAbstractConditionTrigger(AbstractComparison abstractComparison, T t) {
            return new ValueConditionTrigger<T>(this, abstractComparison, t);
        }},
    CONTAINED_IN(true) {
        @Override
        public <T> AbstractConditionTrigger<T> createAbstractConditionTrigger(AbstractComparison abstractComparison, T t) {
            return new ContainedInTrigger<T>(abstractComparison, t);
        }},
    SCROLL_TO_POSITION(true) {
        @Override
        public <T> AbstractConditionTrigger<T> createAbstractConditionTrigger(AbstractComparison abstractComparison, T t) {
            return new PositionConditionTrigger<T>(this, abstractComparison, t);
        }};

    private boolean comparisonNeeded;

    ConditionTrigger(boolean comparisonNeeded) {
        this.comparisonNeeded = comparisonNeeded;
    }

    public abstract <T> AbstractConditionTrigger<T> createAbstractConditionTrigger(AbstractComparison abstractComparison, T t);

    public boolean isComparisonNeeded() {
        return comparisonNeeded;
    }
}
