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

import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.Planet;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.user.UserState;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
    private boolean abandoned = false;
    private int baseId;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "base", fetch = FetchType.LAZY)
    private DbUserState userState;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbPlanet dbPlanet;

    /**
     * Used by hibernate
     */
    public DbBase() {
    }

    public DbBase(Base base) {
        accountBalance = base.getAccountBalance();
        startTime = base.getStartTime();
        abandoned = base.isAbandoned();
        baseId = base.getBaseId();
    }

    public Base createBase(UserState userState, Planet planet) {
        return new Base(accountBalance,
                startTime,
                userState == null || abandoned,
                baseId,
                userState,
                planet);
    }

    public DbUserState getUserState() {
        return userState;
    }

    public void setUserState(DbUserState userState) {
        this.userState = userState;
    }

    public void setDbPlanet(DbPlanet dbPlanet) {
        this.dbPlanet = dbPlanet;
    }

    public DbPlanet getDbPlanet() {
        return dbPlanet;
    }
}
