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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * User: beat
 * Date: 14.12.2009
 * Time: 22:47:20
 */
@Entity(name = "ITEM_TYPE_SOUND_DATA")
public class DbItemTypeSoundData implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(length = 500000)
    private byte[] dataMp3;
    @Column(length = 500000)
    private byte[] dataOgg;

    public byte[] getDataMp3() {
        return dataMp3;
    }

    public void setDataMp3(byte[] dataMp3) {
        this.dataMp3 = dataMp3;
    }

    public byte[] getDataOgg() {
        return dataOgg;
    }

    public void setDataOgg(byte[] dataOgg) {
        this.dataOgg = dataOgg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbItemTypeSoundData that = (DbItemTypeSoundData) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}