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
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.market.impl.UserItemTypeAccess;
import com.btxtech.game.services.utg.DbAbstractLevel;

import javax.persistence.*;

/**
 * User: beat
 * Date: 19.01.2011
 * Time: 10:42:00
 */
@Entity(name = "BACKUP_USER_STATUS")
public class UserState {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    private DbBotConfig botConfig;
    @ManyToOne
    private User user;
    @OneToOne
    private Base base;
    @ManyToOne(optional = false)
    private DbAbstractLevel currentAbstractLevel;
    @OneToOne(optional = false)
    private UserItemTypeAccess userItemTypeAccess;
    @Transient
    private String sessionId;

    public boolean isRegistered() {
        return user != null;
    }

    public void setBotConfig(DbBotConfig botConfig) {
        this.botConfig = botConfig;
    }

    public boolean isBot() {
        return botConfig != null;
    }

    public DbBotConfig getBotConfig() {
        return botConfig;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    public DbAbstractLevel getCurrentAbstractLevel() {
        return currentAbstractLevel;
    }

    public void setCurrentAbstractLevel(DbAbstractLevel currentAbstractLevel) {
        this.currentAbstractLevel = currentAbstractLevel;
    }

    public Base getBase() {
        return base;
    }

    public UserItemTypeAccess getUserItemTypeAccess() {
        return userItemTypeAccess;
    }

    public void setUserItemTypeAccess(UserItemTypeAccess userItemTypeAccess) {
        this.userItemTypeAccess = userItemTypeAccess;
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

    public void clearId() {
        id = null;
        if (userItemTypeAccess != null) {
            userItemTypeAccess.clearId();
        }
    }
}
