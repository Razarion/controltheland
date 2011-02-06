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

package com.btxtech.game.jsre.playback;

/**
 * User: beat
 * Date: 11.08.2010
 * Time: 18:50:15
 */
public class Frame implements Comparable<Frame> {
    private long timeStamp;
    private Object load;

    public Frame(long timeStamp, Object load) {
        this.timeStamp = timeStamp;
        this.load = load;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public Object getLoad() {
        return load;
    }

    @Override
    public int compareTo(Frame frame) {
        return (int) (timeStamp - frame.timeStamp);
    }
}
