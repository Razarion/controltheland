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

package com.btxtech.game.jsre.common.gameengine.services.base.impl;

import com.btxtech.game.jsre.client.AlreadyUsedException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * User: beat
 * Date: 24.08.2010
 * Time: 18:57:14
 */
abstract public class AbstractBaseServiceImpl implements AbstractBaseService {
    private static final String UNDEFINED_NAME = "undefined";
    private static final String UNDEFINED_COLOR = "#888888";
    final private HashMap<SimpleBase, BaseAttributes> bases = new HashMap<SimpleBase, BaseAttributes>();

    @Override
    public String getBaseName(SimpleBase simpleBase) {
        BaseAttributes baseAttributes = bases.get(simpleBase);
        if (baseAttributes != null) {
            return baseAttributes.getName();
        } else {
            return UNDEFINED_NAME;
        }
    }

    @Override
    public String getBaseHtmlColor(SimpleBase simpleBase) {
        BaseAttributes baseAttributes = bases.get(simpleBase);
        if (baseAttributes != null) {
            return baseAttributes.getHtmlColor();
        } else {
            return UNDEFINED_COLOR;
        }
    }

    @Override
    public boolean isBot(SimpleBase simpleBase) {
        BaseAttributes baseAttributes = bases.get(simpleBase);
        return baseAttributes != null && baseAttributes.isBot();
    }

    @Override
    public Collection<BaseAttributes> getAllBaseAttributes() {
        return new ArrayList<BaseAttributes>(bases.values());
    }

    public void setAllBaseAttributes(Collection<BaseAttributes> allBaseAttributes) {
        synchronized (bases) {
            bases.clear();
            for (BaseAttributes baseAttributes : allBaseAttributes) {
                bases.put(baseAttributes.getSimpleBase(), baseAttributes);
            }
        }
    }

    protected void createBase(SimpleBase simpleBase, String name, String htmlColor, boolean isBot, boolean abandoned) {
        createBase(new BaseAttributes(simpleBase, name, htmlColor, isBot, abandoned));
    }

    protected void clear() {
        bases.clear();
    }

    protected void createBase(BaseAttributes baseAttributes) {
        if (bases.containsKey(baseAttributes.getSimpleBase())) {
            throw new IllegalArgumentException(this + " The base already exits: " + baseAttributes.getSimpleBase());
        }
        synchronized (bases) {
            bases.put(baseAttributes.getSimpleBase(), baseAttributes);
        }
    }

    protected void removeBase(SimpleBase simpleBase) {
        synchronized (bases) {
            if (bases.remove(simpleBase) == null) {
                throw new IllegalArgumentException(this + " Base does not exits: " + simpleBase);
            }
        }
    }

    protected BaseAttributes getBaseAttributes(SimpleBase simpleBase) {
        return bases.get(simpleBase);
    }

    protected void updateBase(BaseAttributes baseAttributes) {
        BaseAttributes oldBaseAttributes = getBaseAttributes(baseAttributes.getSimpleBase());
        if (oldBaseAttributes == null) {
            throw new IllegalArgumentException(this + " base does not exits " + baseAttributes.getSimpleBase());
        }
        bases.put(baseAttributes.getSimpleBase(), baseAttributes);
    }

    protected Collection<String> getHtmlColors() {
        ArrayList<String> colors = new ArrayList<String>(bases.size());
        for (BaseAttributes baseAttributes : bases.values()) {
            colors.add(baseAttributes.getHtmlColor());
        }
        return colors;
    }

    protected void setBaseAbandoned(SimpleBase simpleBase, boolean abandoned) {
        BaseAttributes baseAttributes = getBaseAttributes(simpleBase);
        if (baseAttributes == null) {
            throw new IllegalArgumentException(this + " base does not exits " + simpleBase);
        }
        baseAttributes.setAbandoned(abandoned);
    }

    protected void setBaseName(SimpleBase simpleBase, String name) {
        BaseAttributes baseAttributes = getBaseAttributes(simpleBase);
        if (baseAttributes == null) {
            throw new IllegalArgumentException(this + " base does not exits " + simpleBase);
        }
        baseAttributes.setName(name);
    }

    protected void setBaseColor(SimpleBase simpleBase, String color) throws AlreadyUsedException {
        BaseAttributes baseAttributes = getBaseAttributes(simpleBase);
        if (baseAttributes == null) {
            throw new IllegalArgumentException(this + " base does not exits " + simpleBase);
        }
        for (BaseAttributes attributes : bases.values()) {
            if (attributes.getHtmlColor().equals(color)) {
                throw new AlreadyUsedException(null, color);
            }
        }
        baseAttributes.setHtmlColor(color);
    }

}
