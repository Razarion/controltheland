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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.Region;
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
import com.btxtech.game.jsre.common.packets.EnergyPacket;
import com.btxtech.game.jsre.common.packets.HouseSpacePacket;
import com.btxtech.game.services.common.ServerGlobalServices;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.connection.NoBaseException;
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

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 8:15:53 PM
 */
public class BaseServiceImpl extends AbstractBaseServiceImpl implements BaseService {
    private static final String DEFAULT_BASE_NAME_PREFIX = "Base ";
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
    public Base createNewBase(UserState userState, DbBaseItemType dbBaseItemType, int startMoney, Region region, int startItemFreeRange) throws NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException {
        Base base;
        synchronized (bases) {
            lastBaseId++;
            base = new Base(userState, planet, lastBaseId);
            createBase(base.getSimpleBase(), setupBaseName(base), false);
            log.debug("Base created: " + base);
            bases.put(base.getSimpleBase(), base);
        }
        serverGlobalServices.getHistoryService().addBaseStartEntry(base.getSimpleBase());
        BaseItemType startItem = (BaseItemType) serverGlobalServices.getItemTypeService().getItemType(dbBaseItemType.getId());
        sendBaseChangedPacket(BaseChangedPacket.Type.CREATED, base.getSimpleBase());
        Index startPoint = planetServices.getCollisionService().getFreeRandomPosition(startItem, region, startItemFreeRange, true, false);
        SyncBaseItem syncBaseItem = (SyncBaseItem) planetServices.getItemService().createSyncObject(startItem, startPoint, null, base.getSimpleBase());
        syncBaseItem.setBuildup(1.0);
        syncBaseItem.getSyncItemArea().setCosmeticsAngel();
        if (userState.isRegistered()) {
            serverGlobalServices.getUserTrackingService().onBaseCreated(serverGlobalServices.getUserService().getUser(userState), setupBaseName(base));
            serverGlobalServices.getAllianceService().onBaseCreatedOrDeleted(userState.getUser());
        }
        base.setAccountBalance(startMoney);
        sendAccountBaseUpdate(base.getSimpleBase());
        return base;
    }

    @Override
    public SimpleBase createBotBase(BotConfig botConfig) {
        synchronized (bases) {
            lastBaseId++;
            Base base = new Base(planet, lastBaseId);
            createBase(base.getSimpleBase(), botConfig.getName(), false);
            log.debug("Bot Base created: " + botConfig.getName() + " " + " (" + base + ")");
            serverGlobalServices.getHistoryService().addBaseStartEntry(base.getSimpleBase());
            bases.put(base.getSimpleBase(), base);
            setBot(base.getSimpleBase(), true);
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
        UserState userState = getUserState(base.getSimpleBase());
        userState.setSendResurrectionMessage();
        planetServices.getConnectionService().closeConnection(base.getSimpleBase(), NoConnectionException.Type.BASE_SURRENDERED);
        makeBaseAbandoned(base);
        if (userState.isRegistered()) {
            serverGlobalServices.getAllianceService().onMakeBaseAbandoned(base.getSimpleBase());
            serverGlobalServices.getAllianceService().onBaseCreatedOrDeleted(userState.getUser());
        }
    }

    @Override
    public void setBot(SimpleBase simpleBase, boolean bot) {
        super.setBot(simpleBase, bot);
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
        if (planetServices.getConnectionService().hasConnection(base.getSimpleBase())) {
            planetServices.getConnectionService().closeConnection(base.getSimpleBase(), NoConnectionException.Type.BASE_LOST);
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
        base.removeItem(syncItem);
        if (!base.hasItems()) {
            serverGlobalServices.getHistoryService().addBaseDefeatedEntry(actor, base.getSimpleBase());
            if (actor != null) {
                sendDefeatedMessage(syncItem, actor);
            }
            Integer userId = null;
            if (base.getUserState() != null && base.getUserState().getUser() != null) {
                userId = base.getUserState().getUser();
                serverGlobalServices.getUserTrackingService().onBaseDefeated(serverGlobalServices.getUserService().getUser(base.getUserState().getUser()), base);
            }
            serverGlobalServices.getStatisticsService().onBaseKilled(base.getSimpleBase(), actor);
            deleteBase(actor, base);
            if (!base.isAbandoned() && base.getUserState() != null) {
                base.getUserState().setBase(null);
                base.getUserState().setSendResurrectionMessage();
            }
            if (userId != null) {
                serverGlobalServices.getAllianceService().onBaseCreatedOrDeleted(userId);
            }
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
            return serverGlobalServices.getUserService().getUser(base.getUserState().getUser()).getUsername();
        } else {
            return DEFAULT_BASE_NAME_PREFIX + base.getBaseId();
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
    public void sendAlliancesChanged(SimpleBase simpleBase) {
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
    public void onItemChanged(Change change, SyncItem syncItem) {
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
    public void setAlliances(SimpleBase simpleBase, Collection<SimpleBase> alliances) {
        updateBaseAlliance(simpleBase, alliances);
    }

    @Override
    protected GlobalServices getGlobalServices() {
        return serverGlobalServices;
    }

    @Override
    protected PlanetServices getPlanetServices() {
        return planetServices;
    }
}
