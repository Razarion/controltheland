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

import com.btxtech.game.jsre.client.item.ItemViewContainer;
import com.btxtech.game.jsre.client.simulation.Simulation;
import com.btxtech.game.jsre.common.BaseChangedPacket;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.base.impl.AbstractBaseServiceImpl;
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
    private DepositResourceListener depositResourceListener;

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
        InfoPanel.getInstance().updateMoney();
    }

    public boolean isMyOwnProperty(SyncBaseItem syncItem) {
        return simpleBase.equals(syncItem.getBase());
    }

    @Override
    public void depositResource(double price, SimpleBase simpleBase) {
        if (this.simpleBase.equals(simpleBase)) {
            accountBalance += price;
            InfoPanel.getInstance().updateMoney();
            Simulation.getInstance().onDeposit();
            if (depositResourceListener != null) {
                depositResourceListener.onDeposit();
            }
        }
    }

    @Override
    public void withdrawalMoney(double price, SimpleBase simpleBase) throws InsufficientFundsException {
        if (!this.simpleBase.equals(simpleBase)) {
            return;
        }
        if (price > accountBalance) {
            throw new InsufficientFundsException();
        } else {
            accountBalance -= price;
            InfoPanel.getInstance().updateMoney();
        }
    }

    public void setDepositResourceListener(DepositResourceListener depositResourceListener) {
        this.depositResourceListener = depositResourceListener;
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
                    InfoPanel.getInstance().updateBase();
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
}