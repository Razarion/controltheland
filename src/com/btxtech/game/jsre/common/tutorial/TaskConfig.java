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

package com.btxtech.game.jsre.common.tutorial;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.tutorial.condition.AbstractConditionConfig;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 17.07.2010
 * Time: 17:25:27
 */
public class TaskConfig implements Serializable {
    private boolean clearGame;
    private String taskText;
    private Collection<ItemTypeAndPosition> ownItems;
    private boolean isScrollingAllowed;
    private boolean sellingAllowed;
    private boolean optionAllowed;
    private Index scroll;
    private List<StepConfig> stepConfigs;
    private AbstractConditionConfig completionConditionConfig;
    private Collection<Integer> allowedItemTypes;
    private int houseCount;
    private int itemLimit;
    private int accountBalance;
    private int finishImageDuration;
    private String name;
    private Integer finishImageId;

    /**
     * Used by GWT
     */
    public TaskConfig() {
    }

    public TaskConfig(boolean clearGame, String taskText, ArrayList<ItemTypeAndPosition> ownItems, boolean scrollingAllowed, boolean sellingAllowed, boolean optionAllowed, Index scroll, ArrayList<StepConfig> stepConfigs, AbstractConditionConfig completionConditionConfig, Collection<Integer> allowedItemTypes, int houseCount, int itemLimit, int accountBalance, int finishImageDuration, String name, Integer finishImageId) {
        this.clearGame = clearGame;
        this.taskText = taskText;
        this.ownItems = ownItems;
        isScrollingAllowed = scrollingAllowed;
        this.sellingAllowed = sellingAllowed;
        this.optionAllowed = optionAllowed;
        this.scroll = scroll;
        this.stepConfigs = stepConfigs;
        this.completionConditionConfig = completionConditionConfig;
        this.allowedItemTypes = allowedItemTypes;
        this.houseCount = houseCount;
        this.itemLimit = itemLimit;
        this.accountBalance = accountBalance;
        this.finishImageDuration = finishImageDuration;
        this.name = name;
        this.finishImageId = finishImageId;
    }

    public boolean isClearGame() {
        return clearGame;
    }

    public Collection<ItemTypeAndPosition> getOwnItems() {
        return ownItems;
    }

    public boolean isScrollingAllowed() {
        return isScrollingAllowed;
    }

    public Index getScroll() {
        return scroll;
    }

    public List<StepConfig> getStepConfigs() {
        return stepConfigs;
    }

    public AbstractConditionConfig getCompletionConditionConfig() {
        return completionConditionConfig;
    }

    public Collection<Integer> getAllowedItemTypes() {
        return allowedItemTypes;
    }

    public int getAccountBalance() {
        return accountBalance;
    }

    public int getFinishImageDuration() {
        return finishImageDuration;
    }

    public String getName() {
        return name;
    }

    public Integer getFinishImageId() {
        return finishImageId;
    }

    public boolean isSellingAllowed() {
        return sellingAllowed;
    }

    public boolean isOptionAllowed() {
        return optionAllowed;
    }

    public int getHouseCount() {
        return houseCount;
    }

    public int getItemLimit() {
        return itemLimit;
    }

    public String getTaskText() {
        return taskText;
    }
}
