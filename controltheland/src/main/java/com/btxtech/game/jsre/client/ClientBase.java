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

import com.btxtech.game.jsre.client.cockpit.Cockpit;
import com.btxtech.game.jsre.client.common.Level;
import com.btxtech.game.jsre.client.common.info.RealityInfo;
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
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.impl.AbstractBaseServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

/**
 * User: beat
 * Date: Sep 9, 2009
 * Time: 5:07:34 PM
 */
public class ClientBase extends AbstractBaseServiceImpl implements AbstractBaseService {
    private static final ClientBase INSTANCE = new ClientBase();
    private double accountBalance;
    private SimpleBase simpleBase;
    private int houseSpace;
    private boolean connectedToServer = true;

    /**
     * Singleton
     */
    private ClientBase() {
    }

    public static ClientBase getInstance() {
        return INSTANCE;
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
        Cockpit.getInstance().updateMoney();
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
            if (Connection.getInstance().getGameInfo() instanceof RealityInfo) {
                accountBalance += price;
                if (accountBalance > ClientLevelHandler.getInstance().getLevel().getMaxMoney()) {
                    accountBalance = ClientLevelHandler.getInstance().getLevel().getMaxMoney();
                }
            } else {
                accountBalance += price;
            }
            Cockpit.getInstance().updateMoney();
            SimulationConditionServiceImpl.getInstance().onMoneyIncrease(simpleBase, price);
        }
    }

    @Override
    public void withdrawalMoney(double price, SimpleBase simpleBase) throws InsufficientFundsException {
        if (!this.simpleBase.equals(simpleBase)) {
            return;
        }
        if (Math.round(price) > Math.round(accountBalance)) {
            UnfrequentDialog.open(UnfrequentDialog.Type.NO_MONEY);
            throw new InsufficientFundsException();
        } else {
            accountBalance -= price;
            Cockpit.getInstance().updateMoney();
        }
        SimulationConditionServiceImpl.getInstance().onWithdrawalMoney();
    }

    public String getOwnBaseName() {
        return getBaseName(simpleBase);
    }

    public String getOwnBaseHtmlColor() {
        return getBaseHtmlColor(simpleBase);
    }

    public void onBaseChangedPacket(BaseChangedPacket baseChangedPacket) {
        switch (baseChangedPacket.getType()) {
            case CHANGED:
                updateBase(baseChangedPacket.getBaseAttributes());
                if (simpleBase.equals(baseChangedPacket.getBaseAttributes().getSimpleBase())) {
                    Cockpit.getInstance().updateBase();
                }
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
        if (!Connection.getInstance().getGameInfo().hasServerCommunication()) {
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
    public Level getLevel(SimpleBase simpleBase) {
        check4OwnBase(simpleBase);
        return ClientLevelHandler.getInstance().getLevel();
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
        Cockpit.getInstance().updateItemLimit();
    }

    public void setConnectedToServer4FakedHouseSpace(boolean connectedToServer) {
        this.connectedToServer = connectedToServer;
    }
}