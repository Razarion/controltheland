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

import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

/**
 * User: beat Date: 12.01.2011 Time: 12:05:40
 */
public abstract class AbstractSyncItemComparison extends AbstractUpdatingComparison {
    private AbstractConditionTrigger abstractConditionTrigger;

    protected abstract void privateOnSyncItem(SyncItem syncItem);

    public final void onSyncItem(SyncItem syncItem) {
        privateOnSyncItem(syncItem);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A, I> AbstractConditionTrigger<A, I> getAbstractConditionTrigger() {
        return abstractConditionTrigger;
    }

    @Override
    public void setAbstractConditionTrigger(AbstractConditionTrigger abstractConditionTrigger) {
        this.abstractConditionTrigger = abstractConditionTrigger;
    }
}
