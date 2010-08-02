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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: beat
 * Date: 01.08.2010
 * Time: 17:07:20
 */
@Entity(name = "TRACKER_TUTORIAL")
public class DbTutorialProgress {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date niceTimeStamp;
    private long timeStamp;
    private String sessionId;
    private long duration;
    private String type;
    private String name;
    private String parent;

    /**
     * Used by Hibernate
     */
    public DbTutorialProgress() {
    }

    public DbTutorialProgress(String sessionId, String type, String name, String parent, long duration) {
        this.sessionId = sessionId;
        niceTimeStamp = new Date();
        timeStamp = niceTimeStamp.getTime();
        this.type = type;
        this.name = name;
        this.parent = parent;
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbTutorialProgress)) return false;

        DbTutorialProgress that = (DbTutorialProgress) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}