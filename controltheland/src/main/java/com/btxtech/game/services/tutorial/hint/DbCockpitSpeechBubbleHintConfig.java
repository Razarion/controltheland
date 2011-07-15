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

package com.btxtech.game.services.tutorial.hint;

import com.btxtech.game.jsre.common.tutorial.CockpitSpeechBubbleHintConfig;
import com.btxtech.game.jsre.common.utg.config.CockpitWidgetEnum;
import com.btxtech.game.jsre.common.tutorial.HintConfig;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 27.07.2010
 * Time: 19:19:28
 */
@Entity
@DiscriminatorValue("COCKPIT_SPEECH_BUBBLE")
public class DbCockpitSpeechBubbleHintConfig extends DbHintConfig {
    private CockpitWidgetEnum cockpitWidgetEnum;
    @Column(length = 50000)
    private String html;
    @ManyToOne
    private DbBaseItemType baseItemType;
    private int blinkDelay;
    private int blinkInterval;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public CockpitWidgetEnum getCockpitWidgetEnum() {
        return cockpitWidgetEnum;
    }

    public void setCockpitWidgetEnum(CockpitWidgetEnum cockpitWidgetEnum) {
        this.cockpitWidgetEnum = cockpitWidgetEnum;
    }

    public DbBaseItemType getBaseItemType() {
        return baseItemType;
    }

    public void setBaseItemType(DbBaseItemType baseItemType) {
        this.baseItemType = baseItemType;
    }

    public int getBlinkDelay() {
        return blinkDelay;
    }

    public void setBlinkDelay(int blinkDelay) {
        this.blinkDelay = blinkDelay;
    }

    public int getBlinkInterval() {
        return blinkInterval;
    }

    public void setBlinkInterval(int blinkInterval) {
        this.blinkInterval = blinkInterval;
    }

    @Override
    public void init(UserService userService) {
        cockpitWidgetEnum = CockpitWidgetEnum.SELL_BUTTON;
        blinkDelay = 0;
        blinkInterval = 0;
    }

    @Override
    public HintConfig createHintConfig(ResourceHintManager resourceHintManager, ItemService itemService) {
        int itemTypeId = 0;
        if (cockpitWidgetEnum.isItemTypeNeeded()) {
            if (baseItemType != null) {
                itemTypeId = baseItemType.getId();
            } else {
                throw new IllegalStateException("Base item type not set");
            }
        }
        return new CockpitSpeechBubbleHintConfig(isCloseOnTaskEnd(), cockpitWidgetEnum, itemTypeId, html, blinkDelay, blinkInterval);
    }
}
