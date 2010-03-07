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

package com.btxtech.game.services.utg;

import com.btxtech.game.services.user.User;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 07.03.2010
 * Time: 14:59:44
 */
@Entity(name = "TRACKER_USER_HISTORY")
public class UserHistory {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    private User user;
    private Date created;
    private Date loggedIn;
    private Date loggedOut;
    private Date baseCreated;
    private Date baseDefeated;
    private Date baseSurrender;
    private Date gameEntered;
    private Date gameLeft;
    private String baseName;
    private String sessionId;
    private String cookieId;

    /**
     * Used by hibernate
     */
    protected UserHistory() {
    }

    public UserHistory(User user) {
        this.user = user;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated() {
        created = new Date();
    }

    public Date getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn() {
        loggedIn = new Date();
    }

    public Date getLoggedOut() {
        return loggedOut;
    }

    public void setLoggedOut() {
        loggedOut = new Date();
    }

    public Date getBaseCreated() {
        return baseCreated;
    }

    public void setBaseCreated() {
        baseCreated = new Date();
    }

    public Date getBaseDefeated() {
        return baseDefeated;
    }

    public void setBaseDefeated() {
        baseDefeated = new Date();
    }

    public Date getBaseSurrender() {
        return baseSurrender;
    }

    public void setBaseSurrender() {
        baseSurrender = new Date();
    }

    public Date getGameEntered() {
        return gameEntered;
    }

    public void setGameEntered() {
        gameEntered = new Date();
    }

    public Date getGameLeft() {
        return gameLeft;
    }

    public void setGameLeft() {
        gameLeft = new Date();
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCookieId() {
        return cookieId;
    }

    public void setCookieId(String cookieId) {
        this.cookieId = cookieId;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserHistory that = (UserHistory) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
