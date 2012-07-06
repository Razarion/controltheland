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
import com.btxtech.game.jsre.client.common.info.InvalidLevelState;
import com.btxtech.game.jsre.common.packets.Packet;
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
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserState;

import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 8:12:50 PM
 */
public interface BaseService extends AbstractBaseService, SyncItemListener {
    void checkCanBeAttack(SyncBaseItem victim);

    void sendAccountBaseUpdate(SyncBaseObject syncBaseObject);

    void sendEnergyUpdate(BaseEnergy baseEnergy, Base base);

    Base getBase();

    boolean hasBase();

    Base getBaseCms();

    Base createNewBase(UserState userState, DbBaseItemType dbBaseItemType, Territory territory, int startItemFreeRange) throws AlreadyUsedException, NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException;

    void setBot(SimpleBase simpleBase, boolean bot);

    void continueBase() throws InvalidLevelState;

    Base getBase(SyncBaseObject syncBaseObject);

    Base getBase(SimpleBase simpleBase);

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

    SimpleBase getSimpleBase(User user);

    void onSessionTimedOut(UserState userState);

    ContentProvider<BaseItemTypeCount> getBaseItems();

    void setAlliances(SimpleBase simpleBase, Collection<SimpleBase> alliances);

    void sendAlliancesChanged(SimpleBase simpleBase);
}
