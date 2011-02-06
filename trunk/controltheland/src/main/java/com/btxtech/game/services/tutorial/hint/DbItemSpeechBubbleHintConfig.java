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
import com.btxtech.game.jsre.common.tutorial.ItemSpeechBubbleHintConfig;
import com.btxtech.game.services.common.db.IndexUserType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.hibernate.annotations.TypeDef;

/**
 * User: beat
 * Date: 27.07.2010
 * Time: 19:19:28
 */
@Entity
@DiscriminatorValue("ITEM_SPEECH_BUBBLE")
@TypeDef(name = "index", typeClass = IndexUserType.class)
public class DbItemSpeechBubbleHintConfig extends DbHintConfig {
    private int syncItemId;
    @Column(length = 50000)
    private String html;
    private int blinkDelay;
    private int blinkInterval;

    public int getSyncItemId() {
        return syncItemId;
    }

    public void setSyncItemId(int syncItemId) {
        this.syncItemId = syncItemId;
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
    public void init() {
        blinkDelay = 0;
        blinkInterval = 0;
    }

    @Override
    public HintConfig createHintConfig(ResourceHintManager resourceHintManager) {
        return new ItemSpeechBubbleHintConfig(isCloseOnTaskEnd(), syncItemId, html, blinkDelay, blinkInterval);
    }
}
