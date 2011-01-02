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

package com.btxtech.game.services.mgmt;

import com.btxtech.game.services.common.db.RectangleUserType;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.TypeDef;

/**
 * User: beat
 * Date: 31.03.2010
 * Time: 13:54:45
 */
@Entity(name = "MGMT_STARTUP")
@TypeDef(name = "rectangle", typeClass = RectangleUserType.class)
public class StartupData implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    private int registerDialogDelay;

    public int getRegisterDialogDelay() {
        return registerDialogDelay;
    }

    public void setRegisterDialogDelay(int registerDialogDelay) {
        this.registerDialogDelay = registerDialogDelay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StartupData that = (StartupData) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
