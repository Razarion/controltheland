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

package com.btxtech.game.services.base.impl;

import com.btxtech.game.jsre.client.AlreadyUsedException;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Level;
import com.btxtech.game.jsre.client.common.Message;
import com.btxtech.game.jsre.common.AccountBalancePacket;
import com.btxtech.game.jsre.common.BaseChangedPacket;
import com.btxtech.game.jsre.common.EnergyPacket;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.jsre.common.XpBalancePacket;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.impl.AbstractBaseServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseObject;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.tutorial.HouseSpacePacket;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.connection.Connection;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.connection.NoConnectionException;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.energy.ServerEnergyService;
import com.btxtech.game.services.energy.impl.BaseEnergy;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.services.market.impl.UserItemTypeAccess;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbRealGameLevel;
import com.btxtech.game.services.utg.ServerConditionService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 8:15:53 PM
 */
@Component
public class BaseServiceImpl extends AbstractBaseServiceImpl implements BaseService {
    private static final String DEFAULT_BASE_NAME_PREFIX = "Base ";
    private Log log = LogFactory.getLog(BaseServiceImpl.class);
    @Autowired
    private Session session;
    @Autowired
    private CollisionService collisionService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private UserService userService;
    @Autowired
    private ServerMarketService serverMarketService;
    @Autowired
    private ServerEnergyService serverEnergyService;
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private MgmtService mgmtService;
    @Autowired
    private BotService botService;
    @Autowired
    private ServerConditionService serverConditionService;
    private final HashMap<SimpleBase, Base> bases = new HashMap<SimpleBase, Base>();
    private int lastBaseId = 0;

    @Override
    public void checkBaseAccess(SyncBaseItem item) throws IllegalAccessException {
        if (!getBase().getSimpleBase().equals(item.getBase())) {
            throw new IllegalAccessException("Invalid access from base: " + getBaseName(item.getBase()));
        }
    }

    @Override
    public void checkCanBeAttack(SyncBaseItem victim) {
        if (victim.getBase().equals(getBase().getSimpleBase())) {
            throw new IllegalArgumentException("The Item: " + victim + " can not be attacked be the base: " + getBase());
        }
    }

    @Override
    public Base createNewBase(UserState userState, DbBaseItemType dbBaseItemType, Territory territory, int startItemFreeRange) throws AlreadyUsedException, NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException {
        Base base;
        synchronized (bases) {
            lastBaseId++;
            base = new Base(userState, lastBaseId);
            createBase(base.getSimpleBase(), setupBaseName(base), false);
            log.info("Base created: " + base);
            bases.put(base.getSimpleBase(), base);
        }
        historyService.addBaseStartEntry(base.getSimpleBase());
        BaseItemType startItem = (BaseItemType) itemService.getItemType(dbBaseItemType);
        sendBaseChangedPacket(BaseChangedPacket.Type.CREATED, base.getSimpleBase());
        Index startPoint = collisionService.getFreeRandomPosition(startItem, territory, startItemFreeRange, true);
        SyncBaseItem syncBaseItem = (SyncBaseItem) itemService.createSyncObject(startItem, startPoint, null, base.getSimpleBase(), 0);
        syncBaseItem.setBuildup(1.0);
        if (syncBaseItem.hasSyncTurnable()) {
            syncBaseItem.getSyncTurnable().setAngel(Math.PI / 4.0); // Cosmetics shows vehicle from side
        }
        if (userService.getUser() != null) {
            userTrackingService.onBaseCreated(userService.getUser(), setupBaseName(base));
        }
        return base;
    }

    @Override
    public void continueBase() {
        UserState userState = userService.getUserState();
        if (userState == null) {
            throw new IllegalStateException("No UserState available.");
        }
        boolean sendResurrectionMessage = false;
        Base base = userState.getBase();
        if (base == null) {
            userGuidanceService.executeResurrection(userState);
            sendResurrectionMessage = true;
        }

        base = userState.getBase();
        if (base == null) {
            throw new IllegalStateException("No Base in user UserState: " + userState);
        }

        connectionService.createConnection(base);
        if (sendResurrectionMessage) {
            userGuidanceService.sendResurrectionMessage(base.getSimpleBase());
        }
    }

    @Override
    public Base createBotBase(UserState userState, String name) {
        synchronized (bases) {
            lastBaseId++;
            Base base = new Base(userState, lastBaseId);
            createBase(base.getSimpleBase(), name, false);
            log.info("Bot Base created: " + base);
            bases.put(base.getSimpleBase(), base);
            sendBaseChangedPacket(BaseChangedPacket.Type.CREATED, base.getSimpleBase());
            return base;
        }
    }

    private void deleteBase(Base base) {
        log.info("Base deleted: " + base);
        boolean isBot = isBot(base.getSimpleBase());
        synchronized (bases) {
            if (bases.remove(base.getSimpleBase()) == null) {
                throw new IllegalArgumentException("Base does not exist: " + getBaseName(base.getSimpleBase()));
            }
            sendBaseChangedPacket(BaseChangedPacket.Type.REMOVED, base.getSimpleBase());
            removeBase(base.getSimpleBase());
            serverEnergyService.onBaseKilled(base);
        }
        if (!isBot) {
            userGuidanceService.onBaseDeleted(base.getSimpleBase(), base.getUserState());
        }
    }

    @Override
    public Base getBase() {
        Connection connection = session.getConnection();
        if (connection == null) {
            throw new NoConnectionException("No connection", session.getSessionId());
        }
        Base base = connection.getBase();
        if (base == null) {
            throw new NoConnectionException("Base does not exist", session.getSessionId());
        }
        return base;
    }

    private boolean hasBase() {
        Connection connection = session.getConnection();
        if (connection == null) {
            return false;
        }
        Base base = connection.getBase();
        return base != null;
    }

    @Override
    public Base getBase(SyncBaseObject syncBaseObject) {
        Base base = bases.get(syncBaseObject.getBase());
        if (base == null) {
            throw new IllegalArgumentException("Base does not exist: " + syncBaseObject.getBase());
        }
        return base;
    }

    @Override
    public Base getBase(SimpleBase simpleBase) {
        return bases.get(simpleBase);
    }

    private Base getBaseThrow(SimpleBase simpleBase) {
        Base base = getBase(simpleBase);
        if (base == null) {
            throw new NoConnectionException("Base does not exist", session.getSessionId());
        }
        return base;
    }

    @Override
    public Base getBase(UserState userState) {
        return userState.getBase();
    }

    @Override
    public UserState getUserState(SimpleBase simpleBase) {
        Base base = getBase(simpleBase);
        if (base == null) {
            return null;
        }
        return base.getUserState();
    }

    @Override
    public boolean isAlive(SimpleBase simpleBase) {
        return bases.containsKey(simpleBase);
    }

    @Override
    public void itemCreated(SyncBaseItem syncItem) {
        Base base = getBase(syncItem);
        base.addItem(syncItem);
        if (syncItem.hasSyncHouse() && syncItem.isReady()) {
            handleHouseSpaceChanged(getBase(syncItem));
        }
    }

    @Override
    public void itemDeleted(SyncBaseItem syncItem, SimpleBase actor) {
        Base base = getBase(syncItem);
        base.removeItem(syncItem);
        if (!base.hasItems()) {
            if (actor != null) {
                historyService.addBaseDefeatedEntry(actor, base.getSimpleBase());
                sendDefeatedMessage(syncItem, actor);
            }
            if (base.getUserState() != null && base.getUserState().getUser() != null) {
                userTrackingService.onBaseDefeated(base.getUserState().getUser(), base);
            }
            deleteBase(base);
            if (!base.isAbandoned()) {
                base.getUserState().setBase(null);
            }
        } else {
            if (syncItem.hasSyncHouse()) {
                handleHouseSpaceChanged(base);
            }
        }
    }

    private void sendDefeatedMessage(SyncBaseItem victim, SimpleBase actor) {
        Message message = new Message();
        message.setMessage("You defeated " + getBaseName(victim.getBase()));
        connectionService.sendPacket(actor, message);
    }

    @Override
    public void sendPackage(Packet packet) {
        Base base = getBase();
        connectionService.sendPacket(base.getSimpleBase(), packet);
    }

    @Override
    public void sendAccountBaseUpdate(SyncBaseObject syncBaseObject) {
        if (!isAlive(syncBaseObject.getBase())) {
            return;
        }
        Base base = getBase(syncBaseObject);
        AccountBalancePacket packet = new AccountBalancePacket();
        packet.setAccountBalance(base.getAccountBalance());
        connectionService.sendPacket(base.getSimpleBase(), packet);
    }

    @Override
    public void sendAccountBaseUpdate(Base base) {
        AccountBalancePacket packet = new AccountBalancePacket();
        packet.setAccountBalance(base.getAccountBalance());
        connectionService.sendPacket(base.getSimpleBase(), packet);
    }

    @Override
    public void sendXpUpdate(UserItemTypeAccess userItemTypeAccess, Base base) {
        XpBalancePacket packet = new XpBalancePacket();
        packet.setXp(userItemTypeAccess.getXp());
        connectionService.sendPacket(base.getSimpleBase(), packet);
    }

    @Override
    public void sendEnergyUpdate(BaseEnergy baseEnergy, Base base) {
        EnergyPacket packet = new EnergyPacket();
        packet.setConsuming(baseEnergy.getConsuming());
        packet.setGenerating(baseEnergy.getGenerating());
        connectionService.sendPacket(base.getSimpleBase(), packet);
    }

    @Override
    public void surrenderBase(Base base) {
        historyService.addBaseSurrenderedEntry(base.getSimpleBase());
        userTrackingService.onBaseSurrender(userService.getUser(), base);
        makeBaseAbandoned(base);
    }

    private void makeBaseAbandoned(Base base) {
        setBaseAbandoned(base.getSimpleBase(), true);
        UserState userState = base.getUserState();
        userState.setBase(null);
        setBaseName(base.getSimpleBase(), setupBaseName(base));
        base.setAbandoned();
        sendBaseChangedPacket(BaseChangedPacket.Type.CHANGED, base.getSimpleBase());
    }

    @Override
    public List<Base> getBases() {
        return new ArrayList<Base>(bases.values());
    }

    @Override
    public List<SimpleBase> getSimpleBases() {
        ArrayList<SimpleBase> simpleBases = new ArrayList<SimpleBase>();
        for (Base base : bases.values()) {
            simpleBases.add(base.getSimpleBase());
        }
        return simpleBases;
    }

    @Override
    public void restoreBases(Collection<Base> newBases) {
        synchronized (bases) {
            bases.clear();
            lastBaseId = 0;
            clear();
            for (Base newBase : newBases) {
                bases.put(newBase.getSimpleBase(), newBase);
                createBase(newBase.getSimpleBase(), setupBaseName(newBase), newBase.isAbandoned());
                if (newBase.getBaseId() > lastBaseId) {
                    lastBaseId = newBase.getBaseId();
                }
                newBase.updateHouseSpace();
            }
        }
    }

    private String setupBaseName(Base base) {
        if (!base.isAbandoned() && base.getUserState() != null && base.getUserState().isRegistered()) {
            return userService.getUser(base.getUserState()).getUsername();
        } else {
            return DEFAULT_BASE_NAME_PREFIX + base.getBaseId();
        }
    }

    @Override
    public void depositResource(double price, SimpleBase simpleBase) {
        Base base = getBase(simpleBase);
        if (!isBot(simpleBase)) {
            DbRealGameLevel dbRealGameLevel = userGuidanceService.getDbLevel(simpleBase);
            double money = base.getAccountBalance();
            if (money == dbRealGameLevel.getMaxMoney()) {
                return;
            } else if (money > dbRealGameLevel.getMaxMoney()) {
                base.setAccountBalance(dbRealGameLevel.getMaxMoney());
            } else if (money + price > dbRealGameLevel.getMaxMoney()) {
                base.depositMoney(dbRealGameLevel.getMaxMoney() - money);
            } else {
                base.depositMoney(price);
            }
            serverConditionService.onMoneyIncrease(base.getSimpleBase(), price);
        }
    }

    @Override
    public void withdrawalMoney(double price, SimpleBase simpleBase) throws InsufficientFundsException {
        Base base = getBase(simpleBase);
        if (!isBot(simpleBase)) {
            base.withdrawalMoney(price);
        }
    }

    private void sendBaseChangedPacket(BaseChangedPacket.Type type, SimpleBase simpleBase) {
        BaseAttributes baseAttributes = getBaseAttributes(simpleBase);
        if (baseAttributes != null) {
            BaseChangedPacket baseChangedPacket = new BaseChangedPacket();
            baseChangedPacket.setType(type);
            baseChangedPacket.setBaseAttributes(baseAttributes);
            connectionService.sendPacket(baseChangedPacket);
        } else {
            log.error("Base does not exist: " + simpleBase);
        }
    }

    @Override
    public void onUserRegistered() {
        if (!hasBase()) {
            return;
        }
        Base base = getBase();
        setBaseName(base.getSimpleBase(), setupBaseName(base));
        sendBaseChangedPacket(BaseChangedPacket.Type.CHANGED, base.getSimpleBase());
    }

    @Override
    public void changeBotBaseName(Base base, String name) {
        setBaseName(base.getSimpleBase(), name);
        sendBaseChangedPacket(BaseChangedPacket.Type.CHANGED, base.getSimpleBase());
    }

    @Override
    public void onItemChanged(Change change, SyncItem syncItem) {
        switch (change) {
            case BUILD: {
                if (((SyncBaseItem) syncItem).hasSyncHouse() && ((SyncBaseItem) syncItem).isReady()) {
                    handleHouseSpaceChanged(getBase((SyncBaseItem) syncItem));
                }
                if (((SyncBaseItem) syncItem).isReady()) {
                    serverConditionService.onSyncItemBuilt((SyncBaseItem) syncItem);
                }
                break;
            }
            case ITEM_TYPE_CHANGED: {
                handleHouseSpaceChanged(getBase((SyncBaseItem) syncItem));
                break;
            }
        }
    }

    private void handleHouseSpaceChanged(Base base) {
        if (!base.updateHouseSpace()) {
            return;
        }
        sendHouseSpacePacket(base);
    }

    public void sendHouseSpacePacket(Base base) {
        HouseSpacePacket houseSpacePacket = new HouseSpacePacket();
        houseSpacePacket.setHouseSpace(base.getHouseSpace());
        connectionService.sendPacket(houseSpacePacket);
    }

    @Override
    public void setBot(Base base, boolean isBot) {
        setBot(base.getSimpleBase(), isBot);
        sendBaseChangedPacket(BaseChangedPacket.Type.CHANGED, base.getSimpleBase());
    }

    @Override
    public int getTotalHouseSpace() {
        return userGuidanceService.getDbLevel().getHouseSpace() + getBase().getHouseSpace();
    }

    @Override
    public void onSessionTimedOut(UserState userState) {
        Base base = userState.getBase();
        if (base == null || userState.isRegistered()) {
            return;
        }
        makeBaseAbandoned(base);
    }

    @Override
    public int getHouseSpace(SimpleBase simpleBase) {
        return getBaseThrow(simpleBase).getHouseSpace();
    }

    @Override
    public int getItemCount(SimpleBase simpleBase) {
        return getBaseThrow(simpleBase).getItemCount();
    }

    @Override
    public int getItemCount(SimpleBase simpleBase, int itemTypeId) throws NoSuchItemTypeException {
        ItemType itemType = itemService.getItemType(itemTypeId);
        return getBaseThrow(simpleBase).getItemCount(itemType);
    }

    @Override
    public Level getLevel(SimpleBase simpleBase) {
        return userGuidanceService.getDbLevel(simpleBase).getLevel();
    }
}
