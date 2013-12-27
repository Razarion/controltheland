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

package com.btxtech.game.services.planet;

import com.btxtech.game.jsre.client.PositionInBotException;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.client.dialogs.starmap.StarMapPlanetInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseObject;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.planet.impl.BaseEnergy;
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

    void sendEnergyUpdate(BaseEnergy baseEnergy, Base base);

    Base getBase();

    boolean hasBase();

    String getBaseName();

    Base getBaseCms();

    Base createNewBase(UserState userState, DbBaseItemType dbBaseItemType, int startMoney, Index startPoint, int startItemFreeRange) throws NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException, PositionInBotException;

    void setBot(SimpleBase simpleBase, boolean bot, boolean attacksOtherBot);

    Base getBase(SyncBaseObject syncBaseObject);

    Base getBase(SimpleBase simpleBase);

    UserState getUserState(SimpleBase simpleBase);

    List<Base> getBases();

    List<SimpleBase> getSimpleBases();

    void restore(Collection<Base> bases);

    void onUserRegistered();

    void changeBotBaseName(Base base, String name);

    void sendHouseSpacePacket(Base base);

    Base getBase(UserState userState);

    SimpleBase getSimpleBase(User user);

    void onUserStateRemoved(UserState userState);

    void setGuild(SimpleBase simpleBase, SimpleGuild simpleGuild);

    void sendGuildChanged(SimpleBase simpleBase);

    void surrenderBase(Base base);

    Collection<BaseAttributes> createAllBaseAttributes4FakeBase(SimpleBase fakeBase, UserState uSerState, int planetId);

    void sendGuildChanged4FakeBase(UserState uSerState, SimpleGuild simpleGuild);

    void fillBaseStatistics(StarMapPlanetInfo starMapPlanetInfo);
}
