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

import java.io.Serializable;

/**
 * User: beat
 * Date: Aug 5, 2009
 * Time: 2:03:06 PM
 */
public class SimpleBase implements Serializable {
    private String name;
    private boolean bot;
    private String htmlColor;

    /**
     * Used by GWT
     */
    SimpleBase() {
    }

    public SimpleBase(String name, String htmlColor, boolean bot) {
        this.htmlColor = htmlColor;
        this.name = name;
        this.bot = bot;
    }

    public String getName() {
        return name;
    }

    public String getHtmlColor() {
        return htmlColor;
    }

    public boolean isBot() {
        return bot;
    }

    public void setBot(boolean bot) {
        this.bot = bot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleBase base = (SimpleBase) o;

        return !(name != null ? !name.equals(base.name) : base.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Base: " + name + ":" + htmlColor;
    }
}