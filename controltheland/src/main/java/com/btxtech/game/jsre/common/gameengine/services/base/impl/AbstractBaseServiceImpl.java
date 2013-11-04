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

import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

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
    final private HashMap<SimpleBase, BaseAttributes> bases = new HashMap<SimpleBase, BaseAttributes>();

    protected abstract GlobalServices getGlobalServices();

    protected abstract PlanetServices getPlanetServices();

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
    public SimpleGuild getGuild(SimpleBase simpleBase) {
        BaseAttributes baseAttributes = bases.get(simpleBase);
        if(baseAttributes == null) {
          return null;
        }
        return baseAttributes.getSimpleGuild();
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
                if (simpleBase.getBaseId() == baseId) {
                    return simpleBase;
                }
            }
        }
        throw new IllegalArgumentException("No such base: " + baseId);
    }

    protected void createBase(SimpleBase simpleBase, String name, boolean abandoned, SimpleGuild simpleGuild) {
        createBase(new BaseAttributes(simpleBase, name, abandoned, simpleGuild));
    }

    protected void clear() {
        bases.clear();
    }

    protected void createBase(BaseAttributes baseAttributes) {
        if (bases.containsKey(baseAttributes.getSimpleBase())) {
            throw new IllegalArgumentException(this + " The base already exits: " + baseAttributes.getName() + "(" + baseAttributes.getSimpleBase() + ")");
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
            // TODO temporary ignore this exception due to allaince & startpoint hack
            // TODO throw new IllegalArgumentException(this + " base does not exits " + baseAttributes.getSimpleBase());
            return;
        }
        bases.put(baseAttributes.getSimpleBase(), baseAttributes);
    }

    protected void updateGuild(SimpleBase simpleBase, SimpleGuild simpleGuild) {
        BaseAttributes baseAttributes = getBaseAttributes(simpleBase);
        if (baseAttributes == null) {
            throw new IllegalArgumentException(this + " base does not exits " + simpleBase);
        }
        baseAttributes.setSimpleGuild(simpleGuild);
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

    protected void setBot(SimpleBase simpleBase, boolean bot, boolean attacksOtherBot) {
        BaseAttributes baseAttributes = getBaseAttributes(simpleBase);
        if (baseAttributes == null) {
            throw new IllegalArgumentException(this + " base does not exits " + simpleBase);
        }
        baseAttributes.setBot(bot, attacksOtherBot);
    }

    @Override
    public void checkItemLimit4ItemAdding(BaseItemType newItemType, SimpleBase simpleBase) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException {
        if (isLevelLimitation4ItemTypeExceeded(newItemType, simpleBase)) {
            throw new ItemLimitExceededException();
        }
        if (isHouseSpaceExceeded(simpleBase, newItemType)) {
            throw new HouseSpaceExceededException();
        }
    }

    @Override
    public boolean isLevelLimitation4ItemTypeExceeded(BaseItemType newItemType, SimpleBase simpleBase) throws NoSuchItemTypeException {
        return getItemCount(simpleBase, newItemType.getId()) >= getLimitation4ItemType(simpleBase, newItemType);
    }

    @Override
    public boolean isLevelLimitation4ItemTypeExceeded(BaseItemType newItemType, int toAddCount, SimpleBase simpleBase) throws NoSuchItemTypeException {
        return getItemCount(simpleBase, newItemType.getId()) + toAddCount > getLimitation4ItemType(simpleBase, newItemType);
    }

    public int getLimitation4ItemType(SimpleBase simpleBase, BaseItemType itemType) {
        int levelCount = getGlobalServices().getCommonUserGuidanceService().getLevelScope(simpleBase).getLimitation4ItemType(itemType.getId());
        int planetCount = getPlanetServices().getPlanetInfo().getLimitation4ItemType(itemType.getId());
        return Math.min(levelCount, planetCount);
    }

    @Override
    public boolean isHouseSpaceExceeded(SimpleBase simpleBase, BaseItemType toBeBuiltType) {
        return isHouseSpaceExceeded(simpleBase, toBeBuiltType, 1);
    }

    @Override
    public boolean isHouseSpaceExceeded(SimpleBase simpleBase, BaseItemType toBeBuiltType, int itemCountToAdd) {
        return getUsedHouseSpace(simpleBase) + itemCountToAdd * toBeBuiltType.getConsumingHouseSpace() > getHouseSpace(simpleBase) + getPlanetServices().getPlanetInfo().getHouseSpace();
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

        if (baseAttributes1 == null) {
            throw new IllegalArgumentException("AbstractBaseServiceImpl.isEnemy() baseAttributes1 == null for base: " + simpleBase1);
        }

        if (baseAttributes2 == null) {
            throw new IllegalArgumentException("AbstractBaseServiceImpl.isEnemy() baseAttributes2 == null for base: " + simpleBase2);
        }

        if(baseAttributes1.isBot() && baseAttributes1.isAttacksOtherBot()) {
            return true;
        }
        if(baseAttributes2.isBot() && baseAttributes2.isAttacksOtherBot()) {
            return true;
        }

        return !(baseAttributes1.isBot() && baseAttributes2.isBot())
                && (baseAttributes1.isBot() != baseAttributes2.isBot() || !baseAttributes1.isSameGuild(baseAttributes2));
    }
}
