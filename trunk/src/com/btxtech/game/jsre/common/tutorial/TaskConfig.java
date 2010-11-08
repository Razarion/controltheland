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
    private Collection<ItemTypeAndPosition> ownItems;
    private boolean isScrollingAllowed;
    private boolean sellingAllowed;
    private boolean optionAllowed;
    private boolean isOnlineBoxVisible;
    private boolean isInfoBoxVisible;
    private Index scroll;
    private List<StepConfig> stepConfigs;
    private AbstractConditionConfig completionConditionConfig;
    private Collection<Integer> allowedItemTypes;
    private int houseCount;
    private int itemLimit;
    private int accountBalance;
    private String description;
    private String finishedText;
    private int finishedTextDuration;
    private String name;
    private Integer imageId;

    /**
     * Used by GWT
     */
    public TaskConfig() {
    }

    public TaskConfig(boolean clearGame, ArrayList<ItemTypeAndPosition> ownItems, boolean scrollingAllowed, boolean sellingAllowed, boolean optionAllowed, boolean onlineBoxVisible, boolean infoBoxVisible, Index scroll, ArrayList<StepConfig> stepConfigs, AbstractConditionConfig completionConditionConfig, Collection<Integer> allowedItemTypes, int houseCount, int itemLimit, int accountBalance, String description, String finishedText, int finishedTextDuration, String name, Integer imageId) {
        this.clearGame = clearGame;
        this.ownItems = ownItems;
        isScrollingAllowed = scrollingAllowed;
        this.sellingAllowed = sellingAllowed;
        this.optionAllowed = optionAllowed;
        isOnlineBoxVisible = onlineBoxVisible;
        isInfoBoxVisible = infoBoxVisible;
        this.scroll = scroll;
        this.stepConfigs = stepConfigs;
        this.completionConditionConfig = completionConditionConfig;
        this.allowedItemTypes = allowedItemTypes;
        this.houseCount = houseCount;
        this.itemLimit = itemLimit;
        this.accountBalance = accountBalance;
        this.description = description;
        this.finishedText = finishedText;
        this.finishedTextDuration = finishedTextDuration;
        this.name = name;
        this.imageId = imageId;
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

    public boolean isOnlineBoxVisible() {
        return isOnlineBoxVisible;
    }

    public boolean isInfoBoxVisible() {
        return isInfoBoxVisible;
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

    public String getDescription() {
        return description;
    }

    public String getFinishedText() {
        return finishedText;
    }

    public int getFinishedTextDuration() {
        return finishedTextDuration;
    }

    public String getName() {
        return name;
    }

    public Integer getImageId() {
        return imageId;
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
}
