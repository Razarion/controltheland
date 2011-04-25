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

package com.btxtech.game.services.mgmt.impl;

import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.user.UserState;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Date;

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 8:16:46 PM
 */
@Entity(name = "BACKUP_BASE")
public class DbBase {
    @Id
    @GeneratedValue
    private Integer id;
    private double accountBalance;
    private Date startTime;
    private int kills;
    private int created;
    private int lost;
    private double totalSpent;
    private double totalEarned;
    private boolean abandoned = false;
    private int baseId;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "base", fetch = FetchType.LAZY)
    private DbUserState userState;

    /**
     * Used by hibernate
     */
    public DbBase() {
    }

    public DbBase(Base base) {
        accountBalance = base.getAccountBalance();
        startTime = base.getStartTime();
        kills = base.getKills();
        created = base.getCreated();
        lost = base.getLost();
        totalSpent = base.getTotalSpent();
        totalEarned = base.getTotalEarned();
        abandoned = base.isAbandoned();
        baseId = base.getBaseId();
    }

    public Base createBase(UserState userState) {
        return new Base(accountBalance,
                startTime,
                kills,
                created,
                lost,
                totalSpent,
                totalEarned,
                userState == null || abandoned,
                baseId,
                userState);
    }

    public DbUserState getUserState() {
        return userState;
    }

    public void setUserState(DbUserState userState) {
        this.userState = userState;
    }
}
