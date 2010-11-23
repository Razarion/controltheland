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

/**
 * User: beat
 * Date: 22.11.2010
 * Time: 19:45:33
 */
public class CockpitSpeechBubbleHintConfig extends HintConfig {
    private CockpitWidgetEnum cockpitWidgetEnum;
    private int baseItemTypeId;
    private String html;

    /**
     * Used by GWT
     */
    public CockpitSpeechBubbleHintConfig() {
    }

    public CockpitSpeechBubbleHintConfig(boolean closeOnTaskEnd, CockpitWidgetEnum cockpitWidgetEnum, int baseItemTypeId, String html) {
        super(closeOnTaskEnd);
        this.cockpitWidgetEnum = cockpitWidgetEnum;
        this.baseItemTypeId = baseItemTypeId;
        this.html = html;
    }

    public CockpitWidgetEnum getCockpitWidgetEnum() {
        return cockpitWidgetEnum;
    }

    public int getBaseItemTypeId() {
        return baseItemTypeId;
    }

    public String getHtml() {
        return html;
    }
}
