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

package com.btxtech.game.jsre.common.packets;

/**
 * User: beat
 * Date: Sep 10, 2009
 * Time: 11:35:20 AM
 */
public class AccountBalancePacket extends Packet {
    private double accountBalance;

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountBalancePacket that = (AccountBalancePacket) o;

        return Double.compare(that.accountBalance, accountBalance) == 0;

    }

    @Override
    public int hashCode() {
        return (int) accountBalance;
    }

    @Override
    public String toString() {
        return "AccountBalancePacket{accountBalance=" + accountBalance + '}';
    }
}
