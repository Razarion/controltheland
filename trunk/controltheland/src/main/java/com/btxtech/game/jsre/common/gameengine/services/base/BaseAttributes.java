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
 *
 *
 * See
 * http://code.google.com/p/google-web-toolkit/issues/detail?id=3577
 *
 */
public class BaseAttributes implements Serializable {
    private SimpleBase simpleBase;
    private String name;
    private boolean bot = false;
    private boolean abandoned;
    private Set<SimpleBase> alliances = new HashSet<SimpleBase>();
    /**
     * Used by GWT
     */
    BaseAttributes() {
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

    public boolean isAlliance(SimpleBase other) {
        return alliances.contains(other);
    }

    public void setAlliances(Set<SimpleBase> alliances) {
        this.alliances = alliances;
    }

    public Set<SimpleBase> getAlliances() {
        return alliances;
    }

    @Override
    public String toString() {
        StringBuilder allianceString = new StringBuilder();
        if (alliances != null) {
            for (SimpleBase alliance : alliances) {
                allianceString.append(alliance.getBaseId());
                allianceString.append(";");
            }
        }
        return "BaseAttributes{" +
                "simpleBase=" + simpleBase +
                ", name='" + name + '\'' +
                ", bot=" + bot +
                ", abandoned=" + abandoned +
                ", alliances=" + allianceString +
                '}';
    }
}
