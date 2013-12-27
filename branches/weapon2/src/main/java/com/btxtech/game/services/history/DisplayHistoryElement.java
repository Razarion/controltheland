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

package com.btxtech.game.services.history;

import com.btxtech.game.services.common.SimpleCrudChild;

import java.io.Serializable;

/**
 * User: beat
 * Date: 27.03.2010
 * Time: 15:44:05
 */
public class DisplayHistoryElement extends SimpleCrudChild implements Serializable {
    private long timeStamp;
    private String message;
    private int id;

    public DisplayHistoryElement(long timeStamp, int id) {
        this.timeStamp = timeStamp;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public Serializable getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisplayHistoryElement)) return false;

        DisplayHistoryElement that = (DisplayHistoryElement) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " | " + timeStamp + " | " + message;
    }
}
