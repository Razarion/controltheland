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

import com.btxtech.game.jsre.client.cockpit.quest.QuestProgressInfo;

/**
 * User: beat
 * Date: 27.12.2010
 * Time: 17:25:49
 */
public interface AbstractComparison {
    boolean isFulfilled();

    <A, I> AbstractConditionTrigger<A, I> getAbstractConditionTrigger();

    void setAbstractConditionTrigger(AbstractConditionTrigger abstractConditionTrigger);

    void fillGenericComparisonValues(GenericComparisonValueContainer genericComparisonValueContainer);

    void restoreFromGenericComparisonValue(GenericComparisonValueContainer genericComparisonValueContainer);

    void fillQuestProgressInfo(QuestProgressInfo questProgressInfo);

    void handleDeferredUpdate();
}
