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

package com.btxtech.game.services.item.itemType;

import com.btxtech.game.jsre.client.common.RadarMode;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * User: beat
 * Date: 09.12.2009
 * Time: 15:00:18
 */
@Entity(name = "ITEM_SPECIAL_TYPE")
public class DbSpecialType implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Enumerated(EnumType.STRING)
    private RadarMode radarMode;

    public RadarMode getRadarMode() {
        return radarMode;
    }

    public void setRadarMode(RadarMode radarMode) {
        this.radarMode = radarMode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DbSpecialType that = (DbSpecialType) o;

        return id != null && id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}