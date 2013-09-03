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

package com.btxtech.game.jsre.common;

/**
 * User: beat
 * Date: 21.12.2009
 * Time: 21:36:34
 */
public class NoConnectionException extends Exception {
    private Type type;

    public enum Type {
        NON_EXISTENT,
        ANOTHER_CONNECTION_EXISTS,
        TIMED_OUT,
        LOGGED_OUT
    }

    /**
     * Used by GWT
     */
    NoConnectionException() {
    }

    public NoConnectionException(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
