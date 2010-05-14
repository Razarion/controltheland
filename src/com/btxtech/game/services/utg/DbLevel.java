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

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;

/**
 * User: beat
 * Date: 13.05.2010
 * Time: 12:20:32
 */
@Entity(name = "GUIDANCE_LEVEL")
public class DbLevel implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(unique = true)
    private String name;
    @Column(unique = true)
    private int rank;
    private String missionTarget;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getMissionTarget() {
        return missionTarget;
    }

    public void setMissionTarget(String missionTarget) {
        this.missionTarget = missionTarget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbLevel)) return false;

        DbLevel dbLevel = (DbLevel) o;

        return !(id != null ? !id.equals(dbLevel.id) : dbLevel.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
