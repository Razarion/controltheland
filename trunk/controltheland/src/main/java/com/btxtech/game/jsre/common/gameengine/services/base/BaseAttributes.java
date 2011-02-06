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

package com.btxtech.game.jsre.common.gameengine.services.base;

import com.btxtech.game.jsre.common.SimpleBase;
import java.io.Serializable;

/**
 * User: beat
 * Date: 24.08.2010
 * Time: 22:05:59
 */
public class BaseAttributes implements Serializable {
    private SimpleBase simpleBase;
    private String name;
    private String htmlColor;
    private boolean bot = false;
    private boolean abandoned;

    /**
     * Used by GWT
     */
    public BaseAttributes() {
    }

    public BaseAttributes(SimpleBase simpleBase, String name, String htmlColor, boolean abandoned) {
        this.simpleBase = simpleBase;
        this.name = name;
        this.htmlColor = htmlColor;
        this.abandoned = abandoned;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHtmlColor() {
        return htmlColor;
    }

    public void setHtmlColor(String htmlColor) {
        this.htmlColor = htmlColor;
    }

    public boolean isBot() {
        return bot;
    }

    public void setBot(boolean bot) {
        this.bot = bot;
    }

    public SimpleBase getSimpleBase() {
        return simpleBase;
    }

    public boolean isAbandoned() {
        return abandoned;
    }

    public void setAbandoned(boolean abandoned) {
        this.abandoned = abandoned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseAttributes)) return false;

        BaseAttributes that = (BaseAttributes) o;

        return simpleBase.equals(that.simpleBase);

    }

    @Override
    public int hashCode() {
        return simpleBase.hashCode();
    }
}
