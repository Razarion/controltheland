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

package com.btxtech.game.services.planet.impl;

import com.btxtech.game.jsre.client.PositionInBotException;
import com.btxtech.game.jsre.client.StartPointItemPlacerChecker;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.client.dialogs.starmap.StarMapPlanetInfo;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.impl.AbstractBaseServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseObject;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.packets.AccountBalancePacket;
import com.btxtech.game.jsre.common.packets.BaseChangedPacket;
import com.btxtech.game.jsre.common.packets.BaseLostPacket;
import com.btxtech.game.jsre.common.packets.EnergyPacket;
import com.btxtech.game.jsre.common.packets.HouseSpacePacket;
import com.btxtech.game.services.common.ServerGlobalServices;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.connection.NoBaseException;
import com.btxtech.game.services.gwt.MovableServiceImpl;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.Planet;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 8:15:53 PM
 */
public class BaseServiceImpl extends AbstractBaseServiceImpl implements BaseService {
    private static final String DEFAULT_BASE_NAME_PREFIX = "Base ";
    private static final String DEFAULT_BASE_NAME_FAKE = "Your Base";
    private Log log = LogFactory.getLog(BaseServiceImpl.class);
    private ServerPlanetServices planetServices;
    private ServerGlobalServices serverGlobalServices;
    private Planet planet;
    private final HashMap<SimpleBase, Base> bases = new HashMap<>();
    private int lastBaseId = 0;

    public BaseServiceImpl(Planet planet) {
        this.planet = planet;
    }

    public void init(ServerPlanetServices planetServices, ServerGlobalServices serverGlobalServices) {
        this.planetServices = planetServices;
        this.serverGlobalServices = serverGlobalServices;
    }

    @Override
    public void checkBaseAccess(SyncBaseItem item) throws NotYourBaseException {
        if (!getBase().getSimpleBase().equals(item.getBase())) {
            throw new NotYourBaseException(getBaseName(getBase().getSimpleBase()), getBaseName(item.getBase()));
        }
    }

    @Override
    public void checkCanBeAttack(SyncBaseItem victim) {
        if (!victim.isEnemy(getBase().getSimpleBase())) {
            throw new IllegalArgumentException("The Item: " + victim + " can not be attacked be the base: " + getBase());
        }
    }

    @Override
    public Base createNewBase(UserState userState, DbBaseItemType dbBaseItemType, int startMoney, Index startPoint, int startItemFreeRange) throws NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException, PositionInBotException {
        BaseItemType startItem = (BaseItemType) serverGlobalServices.getItemTypeService().getItemType(dbBaseItemType.getId());
        checkPosition(userState, startItem, startPoint, startItemFreeRange);
        Base base;
        synchronized (bases) {
            lastBaseId++;
            base = new Base(userState, planet, lastBaseId);
            createBase(base.getSimpleBase(), setupBaseName(base), false, serverGlobalServices.getGuildService().getGuildId(userState));
            log.debug("Base created: " + base);
            bases.put(base.getSimpleBase(), base);
        }
        serverGlobalServices.getHistoryService().addBaseStartEntry(base.getSimpleBase());
        sendBaseChangedPacket(BaseChangedPacket.Type.CREATED, base.getSimpleBase());
        SyncBaseItem syncBaseItem = (SyncBaseItem) planetServices.getItemService().createSyncObject(startItem, startPoint, null, base.getSimpleBase());
        syncBaseItem.setBuildup(1.0);
        syncBaseItem.getSyncItemArea().setCosmeticsAngel();
        if (userState.isRegistered()) {
            serverGlobalServices.getUserTrackingService().onBaseCreated(serverGlobalServices.getUserService().getUser(userState), setupBaseName(base));
        }
        base.setAccountBalance(startMoney);
        sendAccountBaseUpdate(base.getSimpleBase());
        return base;
    }

    private void checkPosition(UserState userState, BaseItemType startItem, final Index startPoint, final int startItemFreeRange) throws PositionInBotException {
        final Set<SimpleBase> friendlyBases = serverGlobalServices.getGuildService().getGuildBases(userState, planetServices.getPlanetInfo().getPlanetId());
        StartPointItemPlacerChecker startPointItemPlacerChecker = new StartPointItemPlacerChecker(startItem, startItemFreeRange, planetServices) {

            @Override
            protected boolean hasEnemyInRange(Index absoluteMiddlePosition, int itemFreeRadius) {
                return planetServices.getItemService().hasEnemyInRange(friendlyBases, startPoint, startItemFreeRange);
            }
        };
        startPointItemPlacerChecker.check(startPoint);
        if (!startPointItemPlacerChecker.isEnemiesOk()) {
            throw new IllegalArgumentException("Enemy items too near " + startItem + " " + userState);
        }
        if (!startPointItemPlacerChecker.isItemsOk()) {
            throw new IllegalArgumentException("Can not place over other items " + startItem + " " + userState);
        }
        if (!startPointItemPlacerChecker.isTerrainOk()) {
            throw new IllegalArgumentException("Terrain is not free " + startItem + " " + userState);
        }

        if (planetServices.getBotService().isInRealm(startPoint)) {
            throw new PositionInBotException(startPoint);
        }
    }

    @Override
    public SimpleBase createBotBase(BotConfig botConfig) {
        synchronized (bases) {
            lastBaseId++;
            Base base = new Base(planet, lastBaseId);
            createBase(base.getSimpleBase(), botConfig.getName(), false, null);
            log.debug("Bot Base created: " + botConfig.getName() + " " + " (" + base + ")");
            serverGlobalServices.getHistoryService().addBaseStartEntry(base.getSimpleBase());
            bases.put(base.getSimpleBase(), base);
            setBot(base.getSimpleBase(), true, botConfig.isAttacksOtherBot());
            sendBaseChangedPacket(BaseChangedPacket.Type.CREATED, base.getSimpleBase());
            return base.getSimpleBase();
        }
    }

    @Override
    public void surrenderBase(Base base) {
        serverGlobalServices.getHistoryService().addBaseSurrenderedEntry(base.getSimpleBase());
        User user = serverGlobalServices.getUserService().getUser();
        if (user != null) {
            serverGlobalServices.getUserTrackingService().onBaseSurrender(user, base);
        }
        getPlanetServices().getItemService().killSyncItems(base.getItems());
    }

    @Override
    public void setBot(SimpleBase simpleBase, boolean bot, boolean attacksOtherBot) {
        super.setBot(simpleBase, bot, attacksOtherBot);
    }

    private void deleteBase(SimpleBase actor, Base base) {
        log.debug("Base deleted: " + getBaseName(base.getSimpleBase()) + " (" + base + ")");
        boolean isBot = isBot(actor);
        synchronized (bases) {
            if (bases.remove(base.getSimpleBase()) == null) {
                throw new IllegalArgumentException("Base does not exist: " + getBaseName(base.getSimpleBase()));
            }
            sendBaseChangedPacket(BaseChangedPacket.Type.REMOVED, base.getSimpleBase());
            removeBase(base.getSimpleBase());
            planetServices.getEnergyService().onBaseKilled(base.getSimpleBase());
        }
        if (!isBot) {
            if (actor != null) {
                serverGlobalServices.getConditionService().onBaseDeleted(actor);
            }
        }
    }

    private void askForStartPosition(UserState userState) {
        if (userState != null && planetServices.getConnectionService().hasConnection(userState)) {
            RealGameInfo realGameInfo = new RealGameInfo();
            MovableServiceImpl.askForStartPosition(planetServices, userState, realGameInfo, serverGlobalServices.getPlanetSystemService(), true);
            BaseLostPacket baseLostPacket = new BaseLostPacket();
            baseLostPacket.setRealGameInfo(realGameInfo);
            planetServices.getConnectionService().sendPacket(userState, baseLostPacket);
        }
    }

    @Override
    public Base getBase() {
        Base base = serverGlobalServices.getUserService().getUserState().getBase();
        if (base == null) {
            throw new NoBaseException("Base does not exist");
        }
        return base;
    }

    @Override
    public Base getBaseCms() {
        // Prevent creating a UserState -> search engine
        if (!serverGlobalServices.getUserService().hasUserState()) {
            return null;
        }
        Base base = serverGlobalServices.getUserService().getUserState().getBase();
        if (base == null) {
            throw new NoBaseException("Base does not exist");
        }
        return base;
    }

    @Override
    public boolean hasBase() {
        return serverGlobalServices.getUserService().getUserState().getBase() != null;
    }

    @Override
    public String getBaseName() {
        return getBaseName(serverGlobalServices.getUserService().getUserState().getBase().getSimpleBase());
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
            throw new NoBaseException("Base does not exist");
        }
        return base;
    }

    @Override
    public Base getBase(UserState userState) {
        return userState.getBase();
    }

    @Override
    public SimpleBase getSimpleBase(User user) {
        UserState userState = serverGlobalServices.getUserService().getUserState(user);
        if (userState == null) {
            return null;
        }
        Base base = getBase(userState);
        return base != null ? base.getSimpleBase() : null;
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
    public void onItemCreated(SyncBaseItem syncItem) {
        Base base = getBase(syncItem);
        base.addItem(syncItem);
        handleHouseSpaceChanged(getBase(syncItem));
    }

    @Override
    public void onItemDeleted(SyncBaseItem syncItem, SimpleBase actor) {
        Base base = getBase(syncItem);
        UserState userState = base.getUserState();
        base.removeItem(syncItem);
        if (!base.hasItems()) {
            serverGlobalServices.getHistoryService().addBaseDefeatedEntry(actor, base.getSimpleBase());
            if (actor != null) {
                sendDefeatedMessage(syncItem, actor);
            }
            if (userState != null && userState.isRegistered()) {
                serverGlobalServices.getUserTrackingService().onBaseDefeated(serverGlobalServices.getUserService().getUser(userState), base);
            }
            serverGlobalServices.getStatisticsService().onBaseKilled(base.getSimpleBase(), actor);
            deleteBase(actor, base);
            if (!base.isAbandoned() && userState != null) {
                base.getUserState().setBase(null);
            }
            askForStartPosition(userState);
        } else {
            handleHouseSpaceChanged(base);
        }
    }

    private void sendDefeatedMessage(SyncBaseItem victim, SimpleBase actor) {
        planetServices.getConnectionService().sendMessage(actor, "youDefeated", new Object[]{getBaseName(victim.getBase())}, false);
    }

    @Override
    public void sendAccountBaseUpdate(SyncBaseObject syncBaseObject) {
        if (!isAlive(syncBaseObject.getBase())) {
            return;
        }
        Base base = getBase(syncBaseObject);
        AccountBalancePacket packet = new AccountBalancePacket();
        packet.setAccountBalance(base.getAccountBalance());
        planetServices.getConnectionService().sendPacket(base.getSimpleBase(), packet);
    }

    @Override
    public void sendAccountBaseUpdate(SimpleBase simpleBase) {
        Base base = getBaseThrow(simpleBase);
        AccountBalancePacket packet = new AccountBalancePacket();
        packet.setAccountBalance(base.getAccountBalance());
        planetServices.getConnectionService().sendPacket(base.getSimpleBase(), packet);
    }

    @Override
    public void sendEnergyUpdate(BaseEnergy baseEnergy, Base base) {
        EnergyPacket packet = new EnergyPacket();
        packet.setConsuming(baseEnergy.getConsuming());
        packet.setGenerating(baseEnergy.getGenerating());
        planetServices.getConnectionService().sendPacket(base.getSimpleBase(), packet);
    }

    private void makeBaseAbandoned(Base base) {
        setBaseAbandoned(base.getSimpleBase(), true);
        UserState userState = base.getUserState();
        userState.setBase(null);
        base.setAbandoned();
        setBaseName(base.getSimpleBase(), setupBaseName(base));
        sendBaseChangedPacket(BaseChangedPacket.Type.CHANGED, base.getSimpleBase());
    }

    @Override
    public List<Base> getBases() {
        return new ArrayList<>(bases.values());
    }

    @Override
    public List<SimpleBase> getSimpleBases() {
        ArrayList<SimpleBase> simpleBases = new ArrayList<>();
        for (Base base : bases.values()) {
            simpleBases.add(base.getSimpleBase());
        }
        return simpleBases;
    }

    @Override
    public void restore(Collection<Base> newBases) {
        synchronized (bases) {
            bases.clear();
            lastBaseId = 0;
            clear();
            for (Base newBase : newBases) {
                bases.put(newBase.getSimpleBase(), newBase);
                createBase(newBase.getSimpleBase(), setupBaseName(newBase), newBase.isAbandoned(), serverGlobalServices.getGuildService().getGuildId(newBase.getUserState()));
                if (newBase.getBaseId() > lastBaseId) {
                    lastBaseId = newBase.getBaseId();
                }
                newBase.updateHouseSpace();
            }
        }
    }

    private String setupBaseName(Base base) {
        if (!base.isAbandoned() && base.getUserState() != null && base.getUserState().isRegistered()) {
            return serverGlobalServices.getUserService().getUser(base.getUserState().getUser()).getUsername();
        } else {
            return DEFAULT_BASE_NAME_PREFIX + base.getBaseId();
        }
    }

    private String setupFakeBaseName(UserState userState) {
        if (userState.isRegistered()) {
            return serverGlobalServices.getUserService().getUser(userState).getUsername();
        } else {
            return DEFAULT_BASE_NAME_FAKE;
        }
    }

    @Override
    public void depositResource(double price, SimpleBase simpleBase) {
        Base base = getBase(simpleBase);
        if (!isBot(simpleBase) && !base.isAbandoned()) {
            serverGlobalServices.getConditionService().onMoneyIncrease(base.getSimpleBase(), price);
            int maxMoney = planetServices.getPlanetInfo().getMaxMoney();
            double money = base.getAccountBalance();
            if (money > maxMoney) {
                base.setAccountBalance(maxMoney);
            } else if (money + price > maxMoney) {
                double amount = maxMoney - money;
                base.depositMoney(amount);
            } else {
                base.depositMoney(price);
            }
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
            planetServices.getConnectionService().sendPacket(baseChangedPacket);
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
    public void sendGuildChanged(SimpleBase simpleBase) {
        BaseAttributes baseAttributes = getBaseAttributes(simpleBase);
        if (baseAttributes != null) {
            BaseChangedPacket baseChangedPacket = new BaseChangedPacket();
            baseChangedPacket.setType(BaseChangedPacket.Type.CHANGED);
            baseChangedPacket.setBaseAttributes(baseAttributes);
            planetServices.getConnectionService().sendPacket(baseChangedPacket);
        } else {
            log.error("Base does not exist: " + simpleBase);
        }
    }

    @Override
    public void onItemChanged(Change change, SyncItem syncItem, Object additionalCustomInfo) {
        switch (change) {
            case BUILD: {
                if (((SyncBaseItem) syncItem).hasSyncHouse() && ((SyncBaseItem) syncItem).isReady()) {
                    handleHouseSpaceChanged(getBase((SyncBaseItem) syncItem));
                }
                if (((SyncBaseItem) syncItem).isReady()) {
                    serverGlobalServices.getConditionService().onSyncItemBuilt((SyncBaseItem) syncItem);
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
        planetServices.getConnectionService().sendPacket(base.getSimpleBase(), houseSpacePacket);
    }

    @Override
    public void onUserStateRemoved(UserState userState) {
        Base base = userState.getBase();
        if (base == null || userState.isRegistered()) {
            return;
        }
        makeBaseAbandoned(base);
    }

    @Override
    public int getUsedHouseSpace(SimpleBase simpleBase) {
        return getBaseThrow(simpleBase).getUsedHouseSpace();
    }

    @Override
    public int getHouseSpace(SimpleBase simpleBase) {
        return getBaseThrow(simpleBase).getHouseSpace();
    }

    @Override
    public int getItemCount(SimpleBase simpleBase, int itemTypeId) throws NoSuchItemTypeException {
        ItemType itemType = serverGlobalServices.getItemTypeService().getItemType(itemTypeId);
        return getBaseThrow(simpleBase).getItemCount(itemType);
    }

    @Override
    public Collection<SyncBaseItem> getItems(SimpleBase simpleBase) {
        Base base = getBase(simpleBase);
        if (base == null) {
            return null;
        }
        return base.getItems();
    }

    @Override
    public boolean isItemLimit4ItemAddingAllowed(BaseItemType newItemType, SimpleBase simpleBase) throws NoSuchItemTypeException {
        return isBot(simpleBase) || isAbandoned(simpleBase) || (!isLevelLimitation4ItemTypeExceeded(newItemType, simpleBase) && !isHouseSpaceExceeded(simpleBase, newItemType));
    }

    @Override
    public void setGuild(SimpleBase simpleBase, SimpleGuild simpleGuild) {
        updateGuild(simpleBase, simpleGuild);
    }

    @Override
    protected GlobalServices getGlobalServices() {
        return serverGlobalServices;
    }

    @Override
    protected PlanetServices getPlanetServices() {
        return planetServices;
    }

    @Override
    public Collection<BaseAttributes> createAllBaseAttributes4FakeBase(SimpleBase fakeBase, UserState userState, int planetId) {
        Collection<BaseAttributes> allBaseAttributes = getAllBaseAttributes();
        allBaseAttributes.add(new BaseAttributes(fakeBase, setupFakeBaseName(userState), false, serverGlobalServices.getGuildService().getGuildId(userState)));
        return allBaseAttributes;
    }

    @Override
    public void sendGuildChanged4FakeBase(UserState userState, SimpleGuild simpleGuild) {
        SimpleBase fakeBase = SimpleBase.createFakeUser(planetServices.getPlanetInfo().getPlanetId());
        BaseChangedPacket baseChangedPacket = new BaseChangedPacket();
        baseChangedPacket.setType(BaseChangedPacket.Type.CHANGED);
        baseChangedPacket.setBaseAttributes(new BaseAttributes(fakeBase, setupFakeBaseName(userState), false, simpleGuild));
        planetServices.getConnectionService().sendPacket(userState, baseChangedPacket);
    }

    @Override
    public void fillBaseStatistics(StarMapPlanetInfo starMapPlanetInfo) {
        int bases = 0;
        int bots = 0;
        Collection<BaseAttributes> baseAttributeses = getAllBaseAttributes();
        for (BaseAttributes baseAttributes : baseAttributeses) {
            if (baseAttributes.isBot()) {
                bots++;
            } else {
                bases++;
            }
        }
        starMapPlanetInfo.setBases(bases);
        starMapPlanetInfo.setBots(bots);
    }
}
