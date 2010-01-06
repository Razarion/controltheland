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

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: beat
 * Date: Jun 18, 2009
 * Time: 5:03:28 PM
 */
@Entity(name = "BASE_COLOR")
public class BaseColor implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false, unique = true)
    private String htmlColor;

    /**
     * Hibernate
     */
    public BaseColor() {
    }

    public BaseColor(int red, int green, int blue) {
        red = checkValue(red);
        green = checkValue(green);
        blue = checkValue(blue);
        htmlColor = String.format("#%02X%02X%02X", red, green, blue);
    }


    private int checkValue(int value) {
        if (value > 255 || value < 0) {
            throw new IllegalArgumentException("color value must be between 0..255");
        }
        return value;
    }

    public String getHtmlColor() {
        return htmlColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseColor baseColor = (BaseColor) o;

        if (!htmlColor.equals(baseColor.htmlColor)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return htmlColor.hashCode();
    }

    @Override
    public String toString() {
        return htmlColor;
    }
}
