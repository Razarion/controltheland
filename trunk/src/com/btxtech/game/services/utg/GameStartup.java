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

import com.btxtech.game.jsre.client.StartupTask;
import com.btxtech.game.services.user.User;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: beat
 * Date: 12.01.2010
 * Time: 22:45:47
 */
@Entity(name = "TRACKER_GAME_STARTUP")
public class GameStartup implements Serializable {
    public static final String FINISHED = "FINISHED";
    public static final String FAILED = "FAILED";
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date niceTimeStamp;
    @Column(nullable = false)
    private long timeStamp;
    @Column(nullable = false)
    private long clientTimeStamp;
    @Column(nullable = false)
    private String sessionId;
    @Column(nullable = false)
    private StartupTask state;
    private long duration;
    @Column(length = 50000)    
    private String failureText;
    @Column(nullable = false)
    private String type;
    @Column(nullable = false)
    private String baseName;
    private String userName;
    private boolean tutorial;
    private String userStageName;

    /**
     * Used by Hibernate
     */
    public GameStartup() {
    }

    public GameStartup(Date clientTimeStamp, String type, StartupTask state, long duration, String failureText, String baseName, User user, String sessionId, boolean isTutorial, String userStageName) {
        tutorial = isTutorial;
        this.userStageName = userStageName;
        this.clientTimeStamp = clientTimeStamp.getTime();
        this.type = type;
        niceTimeStamp = new Date();
        timeStamp = niceTimeStamp.getTime();
        this.sessionId = sessionId;
        this.state = state;
        this.duration = duration;
        this.failureText = failureText;
        this.baseName = baseName;
        if (user != null) {
            userName = user.getName();
        }
    }

    public Date getClientTimeStamp() {
        return new Date(clientTimeStamp);
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public StartupTask getState() {
        return state;
    }

    public String getBaseName() {
        return baseName;
    }

    public long getDuration() {
        return duration;
    }

    public String getFailureText() {
        return failureText;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isTutorial() {
        return tutorial;
    }

    public String getUserStageName() {
        return userStageName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameStartup that = (GameStartup) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}