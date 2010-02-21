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

import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

/**
 * User: beat
 * Date: Sep 9, 2009
 * Time: 5:07:34 PM
 */
public class ClientBase implements BaseService {
    private static final ClientBase INSTANCE = new ClientBase();
    private int accountBalance;
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

    public int getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(int accountBalance) {
        this.accountBalance = accountBalance;
        InfoPanel.getInstance().updateMoney();
    }

    public boolean isMyOwnProperty(SyncBaseItem syncItem) {
        return simpleBase.equals(syncItem.getBase());
    }

    @Override
    public void depositResource(int price, SimpleBase simpleBase) {
        if (this.simpleBase.equals(simpleBase)) {
            accountBalance += price;
            InfoPanel.getInstance().updateMoney();
            if(depositResourceListener != null) {
                depositResourceListener.onDeposit();
            }
        }
    }

    @Override
    public void withdrawalMoney(int price, SimpleBase simpleBase) throws InsufficientFundsException {
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
}