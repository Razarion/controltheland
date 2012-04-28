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

import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 24.08.2010
 * Time: 18:57:14
 */
abstract public class AbstractBaseServiceImpl implements AbstractBaseService {
    private static final String UNDEFINED_NAME = "undefined";
    final private HashMap<SimpleBase, BaseAttributes> bases = new HashMap<SimpleBase, BaseAttributes>();
    private Logger log = Logger.getLogger(AbstractBaseServiceImpl.class.getName());

    @Override
    public String getBaseName(SimpleBase simpleBase) {
        BaseAttributes baseAttributes = bases.get(simpleBase);
        if (baseAttributes != null) {
            if (baseAttributes.getName() != null) {
                return baseAttributes.getName();
            } else {
                return simpleBase.toString();
            }
        } else {
            return UNDEFINED_NAME;
        }
    }

    @Override
    public boolean isBot(SimpleBase simpleBase) {
        BaseAttributes baseAttributes = bases.get(simpleBase);
        return baseAttributes != null && baseAttributes.isBot();
    }

    @Override
    public boolean isAbandoned(SimpleBase simpleBase) {
        BaseAttributes baseAttributes = bases.get(simpleBase);
        return baseAttributes == null || baseAttributes.isAbandoned();
    }

    @Override
    public Collection<BaseAttributes> getAllBaseAttributes() {
        return new ArrayList<BaseAttributes>(bases.values());
    }

    protected Collection<SimpleBase> getAllSimpleBases() {
        return bases.keySet();
    }

    public void setAllBaseAttributes(Collection<BaseAttributes> allBaseAttributes) {
        synchronized (bases) {
            bases.clear();
            for (BaseAttributes baseAttributes : allBaseAttributes) {
                bases.put(baseAttributes.getSimpleBase(), baseAttributes);
            }
        }
    }

    @Override
    public SimpleBase getSimpleBase4Id(int baseId) {
        synchronized (bases) {
            for (SimpleBase simpleBase : bases.keySet()) {
                if (simpleBase.getId() == baseId) {
                    return simpleBase;
                }
            }
        }
        throw new IllegalArgumentException("No such base: " + baseId);
    }

    protected void createBase(SimpleBase simpleBase, String name, boolean abandoned) {
        createBase(new BaseAttributes(simpleBase, name, abandoned));
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

    protected void updateBaseAlliance(SimpleBase simpleBase, Collection<SimpleBase> alliances) {
        BaseAttributes baseAttributes = getBaseAttributes(simpleBase);
        if (baseAttributes == null) {
            throw new IllegalArgumentException(this + " base does not exits " + simpleBase);
        }
        Set<BaseAttributes> allianceSet = new HashSet<BaseAttributes>();
        for (SimpleBase alliance : alliances) {
            BaseAttributes allianceBase = getBaseAttributes(alliance);
            if (allianceBase != null) {
                allianceSet.add(allianceBase);
            } else {
                log.warning("Base does not exist: " + alliance);
            }
        }
        baseAttributes.setAlliances(allianceSet);
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

    protected void setBot(SimpleBase simpleBase, boolean bot) {
        BaseAttributes baseAttributes = getBaseAttributes(simpleBase);
        if (baseAttributes == null) {
            throw new IllegalArgumentException(this + " base does not exits " + simpleBase);
        }
        baseAttributes.setBot(bot);
    }

    @Override
    public void checkItemLimit4ItemAdding(BaseItemType newItemType, SimpleBase simpleBase) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException {
        if (isLevelLimitation4ItemTypeExceeded(newItemType, simpleBase)) {
            throw new ItemLimitExceededException();
        }
        if (isHouseSpaceExceeded(simpleBase)) {
            throw new HouseSpaceExceededException();
        }
    }

    @Override
    public boolean isLevelLimitation4ItemTypeExceeded(BaseItemType newItemType, SimpleBase simpleBase) throws NoSuchItemTypeException {
        LevelScope levelScope = getLevel(simpleBase);
        return getItemCount(simpleBase, newItemType.getId()) >= levelScope.getLimitation4ItemType(newItemType.getId());
    }

    @Override
    public boolean isHouseSpaceExceeded(SimpleBase simpleBase) throws NoSuchItemTypeException {
        LevelScope levelScope = getLevel(simpleBase);
        return getItemCount(simpleBase) >= getHouseSpace(simpleBase) + levelScope.getHouseSpace();
    }

    @Override
    public boolean isAlive(SimpleBase simpleBase) {
        return bases.containsKey(simpleBase);
    }

    @Override
    public boolean isEnemy(SyncBaseItem syncBaseItem1, SyncBaseItem syncBaseItem2) {
        SimpleBase simpleBase1 = syncBaseItem1.getBase();
        SimpleBase simpleBase2 = syncBaseItem2.getBase();
        return isEnemy(simpleBase1, simpleBase2);
    }

    @Override
    public boolean isEnemy(SimpleBase simpleBase1, SimpleBase simpleBase2) {
        if (simpleBase1.equals(simpleBase2)) {
            return false;
        }
        BaseAttributes baseAttributes1 = getBaseAttributes(simpleBase1);
        BaseAttributes baseAttributes2 = getBaseAttributes(simpleBase2);

        return !(baseAttributes1.isBot() && baseAttributes2.isBot())
                && (baseAttributes1.isBot() != baseAttributes2.isBot() || !baseAttributes1.isAlliance(baseAttributes2));
    }
}
