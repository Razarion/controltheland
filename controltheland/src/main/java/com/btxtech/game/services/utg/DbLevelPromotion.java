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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: beat
 * Date: 19.05.2010
 * Time: 15:07:20
 */
@Entity(name = "TRACKER_LEVEL_PROMOTION")
public class DbLevelPromotion {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date niceTimeStamp;
    private long timeStamp;
    private String sessionId;
    private String level;
    private String user;

    /**
     * Used by Hibernate
     */
    public DbLevelPromotion() {
    }

    public DbLevelPromotion(String sessionId, User user, DbAbstractLevel oldAbstractLevel) {
        this.sessionId = sessionId;
        level = oldAbstractLevel.getName();
        this.user = user.getUsername();
        niceTimeStamp = new Date();
        timeStamp = niceTimeStamp.getTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbLevelPromotion)) return false;

        DbLevelPromotion that = (DbLevelPromotion) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
