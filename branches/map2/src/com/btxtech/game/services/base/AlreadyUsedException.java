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

package com.btxtech.game.services.base;

/**
 * User: beat
 * Date: Jun 6, 2009
 * Time: 11:20:11 AM
 */
public class AlreadyUsedException extends Exception {
    private String name;
    private BaseColor baseColor;

    public AlreadyUsedException(String name) {
        this.name = name;
    }

    public AlreadyUsedException(BaseColor baseColor) {
        this.baseColor = baseColor;
    }

    public String getMessage() {
        if (name != null) {
            return "Name already used";
        } else {
            return "Color already used";
        }
    }

    public String getName() {
        return name;
    }

    public BaseColor getBaseColor() {
        return baseColor;
    }
}