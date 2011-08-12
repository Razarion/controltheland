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

import com.btxtech.game.jsre.client.AlreadyUsedException;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseObject;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;
import com.btxtech.game.services.common.ContentProvider;
import com.btxtech.game.services.energy.impl.BaseEnergy;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.market.impl.UserItemTypeAccess;
import com.btxtech.game.services.user.UserState;

import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 8:12:50 PM
 */
public interface BaseService extends AbstractBaseService, SyncItemListener {
    void checkBaseAccess(SyncBaseItem item) throws IllegalAccessException;

    void checkCanBeAttack(SyncBaseItem victim);

    void itemCreated(SyncBaseItem syncItem);

    void itemDeleted(SyncBaseItem syncItem, SimpleBase actor);

    void sendPackage(Packet packet);

    void sendAccountBaseUpdate(SyncBaseObject syncBaseObject);

    void sendAccountBaseUpdate(Base base);

    void sendXpUpdate(UserItemTypeAccess userItemTypeAccess, Base base);

    void sendEnergyUpdate(BaseEnergy baseEnergy, Base base);

    Base getBase();

    Base createNewBase(UserState userState, DbBaseItemType dbBaseItemType, Territory territory, int startItemFreeRange) throws AlreadyUsedException, NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException;

    Base createBotBase(UserState userState, String name);

    void setBot(SimpleBase simpleBase, boolean bot);

    void continueBase();

    Base getBase(SyncBaseObject syncBaseObject);

    Base getBase(SimpleBase simpleBase);

    boolean isAlive(SimpleBase simpleBase);

    UserState getUserState(SimpleBase simpleBase);

    void surrenderBase(Base base);

    List<Base> getBases();

    List<SimpleBase> getSimpleBases();

    void restoreBases(Collection<Base> bases);

    void onUserRegistered();

    void changeBotBaseName(Base base, String name);

    void sendHouseSpacePacket(Base base);

    int getTotalHouseSpace();

    Base getBase(UserState userState);

    void onSessionTimedOut(UserState userState);

    ContentProvider<BaseItemTypeCount> getBaseItems();
}
