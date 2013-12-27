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

import com.btxtech.game.jsre.client.cockpit.quest.QuestProgressInfo;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryArtifactInfo;
import com.btxtech.game.jsre.common.utg.ConditionService;

import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 06.09.2013
 * Time: 21:06:41
 */
public class ArtifactItemIdComparison extends AbstractUpdatingComparison {
    private Map<Integer, Integer> remaining;
    private Map<Integer, Integer> total;
    private AbstractConditionTrigger abstractConditionTrigger;

    public ArtifactItemIdComparison(Map<Integer, Integer> artifactIds) {
        remaining = new HashMap<Integer, Integer>(artifactIds);
        total = new HashMap<Integer, Integer>(artifactIds);
    }

    public void onArtifactItemId(int artifactItemId) {
        Integer remainingCount = remaining.get(artifactItemId);
        if (remainingCount == null) {
            return;
        }
        remainingCount--;
        if (remainingCount == 0) {
            remaining.remove(artifactItemId);
        } else {
            remaining.put(artifactItemId, remainingCount);
        }
        onProgressChanged();
    }

    @Override
    public boolean isFulfilled() {
        return remaining.isEmpty();
    }

    @Override
    public void fillGenericComparisonValues(GenericComparisonValueContainer genericComparisonValueContainer) {
        GenericComparisonValueContainer artifactIdCounts = genericComparisonValueContainer.createChildContainer(GenericComparisonValueContainer.Key.REMAINING_ITEM_TYPES);
        for (Map.Entry<Integer, Integer> entry : remaining.entrySet()) {
            artifactIdCounts.addChild(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void restoreFromGenericComparisonValue(GenericComparisonValueContainer genericComparisonValueContainer) {
        remaining.clear();
        GenericComparisonValueContainer artifactIdCounts = genericComparisonValueContainer.getChildContainer(GenericComparisonValueContainer.Key.REMAINING_ITEM_TYPES);
        for (Map.Entry entry : artifactIdCounts.getEntries()) {
            remaining.put((Integer) entry.getKey(), ((Number) entry.getValue()).intValue());
        }
    }

    @Override
    public void fillQuestProgressInfo(QuestProgressInfo questProgressInfo, ConditionService conditionService) {
        Map<InventoryArtifactInfo, QuestProgressInfo.Amount> InventoryArtifactInfoAmounts = new HashMap<InventoryArtifactInfo, QuestProgressInfo.Amount>();
        for (Map.Entry<Integer, Integer> entry : total.entrySet()) {
            Integer remaining = this.remaining.get(entry.getKey());
            if (remaining == null) {
                remaining = 0;
            }
            int amount = entry.getValue() - remaining;
            InventoryArtifactInfoAmounts.put(conditionService.createInventoryArtifactInfo(entry.getKey()), new QuestProgressInfo.Amount(amount, entry.getValue()));
        }
        questProgressInfo.setInventoryArtifactInfoAmount(InventoryArtifactInfoAmounts);
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