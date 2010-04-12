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

import java.util.Date;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.btxtech.game.jsre.common.gameengine.services.utg.GameStartupState;

/**
 * User: beat
 * Date: 12.01.2010
 * Time: 22:45:47
 */
@Entity(name = "TRACKER_GAME_STARTUP")
public class GameStartup implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date timeStamp;
    @Column(nullable = false)
    private String sessionId;
    @Column(nullable = false)
    private GameStartupState state;
    private Date clientTimeStamp;
    private String baseName;

    /**
     * Used by Hibernate
     */
    public GameStartup() {
    }

    public GameStartup(String sessionId, GameStartupState state, Date timeStamp, String baseName) {
        this.baseName = baseName;
        this.timeStamp = new Date();
        this.sessionId = sessionId;
        this.state = state;
        clientTimeStamp = timeStamp;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public GameStartupState getState() {
        return state;
    }

    public Date getClientTimeStamp() {
        return clientTimeStamp;
    }

    public String getBaseName() {
        return baseName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameStartup that = (GameStartup) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}