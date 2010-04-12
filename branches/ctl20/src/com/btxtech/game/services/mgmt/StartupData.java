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

package com.btxtech.game.services.mgmt;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: beat
 * Date: 31.03.2010
 * Time: 13:54:45
 */
@Entity(name = "MGMT_STARTUP")
public class StartupData implements Serializable{
    @Id
    @GeneratedValue
    private Integer id;
    private int startMoney;
    private int tutorialTimeout;
    private int registerDialogDelay;
    private int userActionCollectionTime;

    public int getStartMoney() {
        return startMoney;
    }

    public void setStartMoney(int startMoney) {
        this.startMoney = startMoney;
    }

    public int getTutorialTimeout() {
        return tutorialTimeout;
    }

    public void setTutorialTimeout(int tutorialTimeout) {
        this.tutorialTimeout = tutorialTimeout;
    }

    public int getRegisterDialogDelay() {
        return registerDialogDelay;
    }

    public void setRegisterDialogDelay(int registerDialogDelay) {
        this.registerDialogDelay = registerDialogDelay;
    }

    public int getUserActionCollectionTime() {
        return userActionCollectionTime;
    }

    public void setUserActionCollectionTime(int userActionCollectionTime) {
        this.userActionCollectionTime = userActionCollectionTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StartupData that = (StartupData) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
