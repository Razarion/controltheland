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

package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.dialogs.UnfrequentDialog;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.item.ItemViewContainer;
import com.btxtech.game.jsre.client.simulation.SimulationConditionServiceImpl;
import com.btxtech.game.jsre.client.utg.ClientLevelHandler;
import com.btxtech.game.jsre.common.BaseChangedPacket;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.impl.AbstractBaseServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

import java.util.Collection;

/**
 * User: beat
 * Date: Sep 9, 2009
 * Time: 5:07:34 PM
 */
public class ClientBase extends AbstractBaseServiceImpl implements AbstractBaseService {
    public static interface OwnBaseDestroyedListener {
        void onOwnBaseDestroyed();
    }

    private final static String OWN_BASE_COLOR = "#ffd800";
    private final static String ENEMY_BASE_COLOR = "#FF0000";
    private final static String BOT_BASE_COLOR = "#000000";
    private final static String UNKNOWN_BASE_COLOR = "#888888";
    private static final ClientBase INSTANCE = new ClientBase();
    private double accountBalance;
    private SimpleBase simpleBase;
    private int houseSpace;
    private boolean connectedToServer = true;
    private OwnBaseDestroyedListener ownBaseDestroyedListener;

    /**
     * Singleton
     */
    private ClientBase() {
    }

    public static ClientBase getInstance() {
        return INSTANCE;
    }

    public void createOwnSimulationBaseIfNotExist(String onwBaseName) {
        if (simpleBase == null) {
            int baseId = getFreeBaseId();
            simpleBase = new SimpleBase(baseId);
            createBase(simpleBase, onwBaseName, false);
        }
    }

    public void setBase(SimpleBase simpleBase) {
        this.simpleBase = simpleBase;
    }

    public SimpleBase getSimpleBase() {
        return simpleBase;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
        SideCockpit.getInstance().updateMoney();
    }

    public boolean isMyOwnProperty(SyncBaseItem syncItem) {
        return simpleBase.equals(syncItem.getBase());
    }

    public boolean isMyOwnBase(SimpleBase simpleBase) {
        return this.simpleBase.equals(simpleBase);
    }

    @Override
    public void depositResource(double price, SimpleBase simpleBase) {
        if (this.simpleBase.equals(simpleBase)) {
            if (Connection.getInstance().getGameInfo() instanceof RealGameInfo) {
                accountBalance += price;
                if (accountBalance > ClientLevelHandler.getInstance().getLevelScope().getMaxMoney()) {
                    accountBalance = ClientLevelHandler.getInstance().getLevelScope().getMaxMoney();
                }
            } else {
                accountBalance += price;
            }
            SideCockpit.getInstance().updateMoney();
            SimulationConditionServiceImpl.getInstance().onMoneyIncrease(simpleBase, price);
        }
    }

    @Override
    public void withdrawalMoney(double price, SimpleBase simpleBase) throws InsufficientFundsException {
        if (!this.simpleBase.equals(simpleBase)) {
            return;
        }
        if (Math.round(price) > Math.round(accountBalance)) {
            if (Connection.getInstance().getGameEngineMode() == GameEngineMode.PLAYBACK) {
                return;
            }
            UnfrequentDialog.open(UnfrequentDialog.Type.NO_MONEY);
            throw new InsufficientFundsException();
        } else {
            accountBalance -= price;
            SideCockpit.getInstance().updateMoney();
        }
    }

    public String getOwnBaseName() {
        return getBaseName(simpleBase);
    }


    public String getBaseHtmlColor(SimpleBase base) {
        if (isMyOwnBase(base)) {
            return getOwnBaseHtmlColor();
        }
        BaseAttributes baseAttributes = getBaseAttributes(base);
        if (baseAttributes == null) {
            return UNKNOWN_BASE_COLOR;
        }

        if (baseAttributes.isBot()) {
            return BOT_BASE_COLOR;
        }
        return ENEMY_BASE_COLOR;
    }


    public String getOwnBaseHtmlColor() {
        return OWN_BASE_COLOR;
    }

    public void onBaseChangedPacket(BaseChangedPacket baseChangedPacket) {
        switch (baseChangedPacket.getType()) {
            case CHANGED:
                updateBase(baseChangedPacket.getBaseAttributes());
                ItemViewContainer.getInstance().updateMarker();
                break;
            case CREATED:
                createBase(baseChangedPacket.getBaseAttributes());
                break;
            case REMOVED:
                removeBase(baseChangedPacket.getBaseAttributes().getSimpleBase());
                break;
            default:
                throw new IllegalArgumentException(this + " unknown type: " + baseChangedPacket.getType());
        }
    }

    public int getHouseSpace() {
        return houseSpace;
    }

    public void setHouseSpace(int houseSpace) {
        this.houseSpace = houseSpace;
    }

    @Override
    public int getHouseSpace(SimpleBase simpleBase) {
        check4OwnBase(simpleBase);
        return getHouseSpace();
    }


    public void checkItemLimit4ItemAdding(BaseItemType itemType) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException {
        try {
            checkItemLimit4ItemAdding(itemType, simpleBase);
        } catch (ItemLimitExceededException e) {
            UnfrequentDialog.open(UnfrequentDialog.Type.ITEM_LIMIT);
            throw new ItemLimitExceededException();

        } catch (HouseSpaceExceededException e) {
            UnfrequentDialog.open(UnfrequentDialog.Type.SPACE_LIMIT);
            throw new HouseSpaceExceededException();
        }
    }

    public boolean checkItemLimit4ItemAddingDialog(BaseItemType itemType) throws NoSuchItemTypeException {
        if (Connection.getInstance().getGameEngineMode() != GameEngineMode.MASTER) {
            return true;
        }
        try {
            checkItemLimit4ItemAdding(itemType, simpleBase);
        } catch (ItemLimitExceededException e) {
            UnfrequentDialog.open(UnfrequentDialog.Type.ITEM_LIMIT);
            return false;
        } catch (HouseSpaceExceededException e) {
            UnfrequentDialog.open(UnfrequentDialog.Type.SPACE_LIMIT);
            return false;
        }
        return true;
    }

    private void check4OwnBase(SimpleBase simpleBase) {
        if (!isMyOwnBase(simpleBase)) {
            throw new IllegalArgumentException("Wrong base given: " + simpleBase + " expected base: " + this.simpleBase);
        }
    }

    @Override
    public int getItemCount(SimpleBase simpleBase) {
        check4OwnBase(simpleBase);
        return ItemContainer.getInstance().getOwnItemCount();
    }

    @Override
    public int getItemCount(SimpleBase simpleBase, int itemTypeId) throws NoSuchItemTypeException {
        check4OwnBase(simpleBase);
        ItemType itemType = ItemContainer.getInstance().getItemType(itemTypeId);
        return ItemContainer.getInstance().getItems(itemType, simpleBase).size();
    }

    @Override
    public LevelScope getLevel(SimpleBase simpleBase) {
        check4OwnBase(simpleBase);
        return ClientLevelHandler.getInstance().getLevelScope();
    }

    public void recalculate4FakedHouseSpace(SyncBaseItem affectedSyncItem) {
        if (connectedToServer || !isMyOwnProperty(affectedSyncItem)) {
            return;
        }
        houseSpace = 0;
        for (ClientSyncItem clientSyncItem : ItemContainer.getInstance().getOwnItems()) {
            SyncBaseItem syncBaseItem = clientSyncItem.getSyncBaseItem();
            if (syncBaseItem.hasSyncHouse()) {
                houseSpace += syncBaseItem.getSyncHouse().getSpace();
            }
        }
        SideCockpit.getInstance().updateItemLimit();
    }

    public void setConnectedToServer4FakedHouseSpace(boolean connectedToServer) {
        this.connectedToServer = connectedToServer;
    }

    @Override
    public Collection<SyncBaseItem> getItems(SimpleBase simpleBase) {
        return ItemContainer.getInstance().getItems4Base(simpleBase);
    }

    @Override
    public SimpleBase createBotBase(BotConfig botConfig) {
        int baseId = getFreeBaseId();
        SimpleBase simpleBase = new SimpleBase(baseId);
        createBase(simpleBase, botConfig.getName(), false);
        setBot(simpleBase, true);
        return simpleBase;
    }

    private int getFreeBaseId() {
        int maxId = 0;
        for (SimpleBase simpleBase : getAllSimpleBases()) {
            if (simpleBase.getId() > maxId) {
                maxId = simpleBase.getId();
            }
        }
        maxId++;
        return maxId;
    }

    public void cleanup() {
        clear();
        ownBaseDestroyedListener = null;
    }

    public void onItemKilled(SyncBaseItem syncBaseItem, SimpleBase actor) {
        if (getItems(syncBaseItem.getBase()).size() > 0) {
            return;
        }

        if (isMyOwnProperty(syncBaseItem)) {
            if (ownBaseDestroyedListener != null) {
                ownBaseDestroyedListener.onOwnBaseDestroyed();
            }
        } else if (isMyOwnBase(actor)) {
            SimulationConditionServiceImpl.getInstance().onBaseDeleted(null);
        }
    }

    public void setOwnBaseDestroyedListener(OwnBaseDestroyedListener ownBaseDestroyedListener) {
        this.ownBaseDestroyedListener = ownBaseDestroyedListener;
    }

    @Override
    public void checkBaseAccess(SyncBaseItem syncBaseItem) throws NotYourBaseException {
        if (!isMyOwnProperty(syncBaseItem)) {
            throw new NotYourBaseException(getBaseName(simpleBase), getBaseName(syncBaseItem.getBase()));
        }
    }

    @Override
    public void sendAccountBaseUpdate(SimpleBase simpleBase) {
        // Do nothing here
    }
}