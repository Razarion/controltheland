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
import java.util.HashSet;
import java.util.Set;

/**
 * User: beat
 * Date: 24.08.2010
 * Time: 22:05:59
 */
public class BaseAttributes implements Serializable {
    private SimpleBase simpleBase;
    private String name;
    private boolean bot = false;
    private boolean abandoned;
    private Set<BaseAttributes> alliances = new HashSet<BaseAttributes>();

    /**
     * Used by GWT
     */
    public BaseAttributes() {
    }

    public BaseAttributes(SimpleBase simpleBase, String name, boolean abandoned) {
        this.simpleBase = simpleBase;
        this.name = name;
        this.abandoned = abandoned;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isAlliance(BaseAttributes other) {
        return alliances.contains(other);
    }

    public void setAlliances(Set<BaseAttributes> alliances) {
        this.alliances = alliances;
    }

    public Set<BaseAttributes> getAlliances() {
        return alliances;
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
