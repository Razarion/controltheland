/*
 * Copyright (c) 2011.
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

package com.btxtech.game.services.user;

import com.btxtech.game.services.base.Base;

import java.io.Serializable;

/**
 * User: beat
 * Date: 19.01.2011
 * Time: 10:42:00
 */
public class UserState implements Serializable {
    private User user;
    private Base base;
    private int dbLevelId;
    private int xp;
    private String sessionId;
    private boolean sendResurrectionMessage = false;

    public boolean isRegistered() {
        return user != null;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    public int getDbLevelId() {
        return dbLevelId;
    }

    public void setDbLevelId(int dbLevelId) {
        this.dbLevelId = dbLevelId;
    }

    public Base getBase() {
        return base;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void increaseXp(int deltaXp) {
        xp += deltaXp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isOnline() {
        return sessionId != null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserState: user=" + user;
    }

    public void setSendResurrectionMessage() {
        sendResurrectionMessage = true;
    }

    public void clearSendResurrectionMessageAndClear() {
        sendResurrectionMessage = false;
    }

    public boolean isSendResurrectionMessage() {
        return sendResurrectionMessage;
    }
}
