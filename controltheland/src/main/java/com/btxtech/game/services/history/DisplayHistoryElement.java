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

import java.io.Serializable;
import java.util.Date;

/**
 * User: beat
 * Date: 27.03.2010
 * Time: 15:44:05
 */
public class DisplayHistoryElement implements Serializable {
    private Date timeStamp;
    private String message;

    public DisplayHistoryElement(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " | " + timeStamp + " | " + message;
    }
}
