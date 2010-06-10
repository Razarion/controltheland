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

import com.btxtech.game.services.base.Base;
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
@Entity(name = "TRACKING_LEVEL_PROMOTION")
public class DbLevelPromotion {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date niceTimeStamp;
    private long timeStamp;
    private String sessionId;
    private String level;
    private String targetLevel;
    private String base;
    private String user;
    private String interimPromotion;

    /**
     * Used by Hibernate
     */
    public DbLevelPromotion() {
    }

    public DbLevelPromotion(String sessionId, Base base, String oldLevel) {
        this.sessionId = sessionId;
        level = oldLevel;
        targetLevel = base.getLevel();
        this.base = base.getName();
        user = base.getUser() != null ? base.getUser().getName() : null;
        niceTimeStamp = new Date();
        timeStamp = niceTimeStamp.getTime();
    }

    public DbLevelPromotion(String sessionId, Base base, String targetLevel, String interimPromotion) {
        this.sessionId = sessionId;
        level = base.getLevel();
        this.targetLevel = targetLevel;
        this.base = base.getName();
        this.interimPromotion = interimPromotion;
        user = base.getUser() != null ? base.getUser().getName() : null;
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
