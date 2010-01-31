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

package com.btxtech.game.jsre.common.gameengine.services.utg;

import java.io.Serializable;
import java.util.Date;

/**
 * User: beat
 * Date: 13.01.2010
 * Time: 22:04:47
 */
public class UserAction implements Serializable {
    public static final String TERRAIN_CLICKED = "TERRAIN_CLICKED";
    public static final String OWN_ITEM_CLICKED = "OWN_ITEM_CLICKED";
    public static final String ENEMY_ITEM_CLICKED = "ENEMY_ITEM_CLICKED";
    public static final String RESOURCE_CLICKED = "RESOURCE_CLICKED";
    public static final String OWN_ITEM_SELECTION_CHANGE = "OWN_ITEM_SELECTION_CHANGE";
    public static final String TRAGET_SELECTION_CHANGED = "TRAGET_SELECTION_CHANGED";
    public static final String SCROLL = "SCROLL";
    public static final String SPEECH_BUBBLE_CLICKED = "SPEECH_BUBBLE_CLICKED";
    public static final String CLOSE_WINDOW = "CLOSE_WINDOW";
    private Date timeStamp;
    private String type;
    private String additionalString;
    private Date timeStampLast;
    private String additionalStringLast;
    private int repeatingCount = 0;

    /**
     * Used by GWT
     */
    public UserAction() {
    }

    public UserAction(String type, String additionalString) {
        timeStamp = new Date();
        this.type = type;
        this.additionalString = additionalString;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getType() {
        return type;
    }

    public String getAdditionalString() {
        return additionalString;
    }

    public void repeat(String additionalString) {
        repeatingCount++;
        timeStampLast = new Date();
        additionalStringLast = additionalString;
    }

    public Date getTimeStampLast() {
        return timeStampLast;
    }

    public String getAdditionalStringLast() {
        return additionalStringLast;
    }

    public int getRepeatingCount() {
        return repeatingCount;
    }
}
