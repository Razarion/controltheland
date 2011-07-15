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

import com.btxtech.game.jsre.common.tutorial.HintConfig;
import com.btxtech.game.jsre.common.tutorial.ItemTypeSpeechBubbleHintConfig;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 20.04.2011
 * Time: 19:19:28
 */
@Entity
@DiscriminatorValue("ITEM_TYPE_SPEECH_BUBBLE")
public class DbItemTypeSpeechBubbleHintConfig extends DbHintConfig {
    @ManyToOne
    private DbItemType dbItemType;
    @Column(length = 50000)
    private String html;
    private int blinkDelay;
    private int blinkInterval;

    public DbItemType getDbItemType() {
        return dbItemType;
    }

    public void setDbItemType(DbItemType dbItemType) {
        this.dbItemType = dbItemType;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
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
        blinkDelay = 0;
        blinkInterval = 0;
    }

    @Override
    public HintConfig createHintConfig(ResourceHintManager resourceHintManager, ItemService itemService) {
        return new ItemTypeSpeechBubbleHintConfig(isCloseOnTaskEnd(), itemService.getItemType(dbItemType), html, blinkDelay, blinkInterval);
    }
}
