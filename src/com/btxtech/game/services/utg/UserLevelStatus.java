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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 10.07.2010
 * Time: 23:23:02
 */
@Entity(name = "BACKUP_LEVEL_STATUS")
public class UserLevelStatus {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(optional = false)
    private DbAbstractLevel currentAbstractLevel;

    public DbAbstractLevel getCurrentLevel() {
        return currentAbstractLevel;
    }

    public void setCurrentLevel(DbAbstractLevel currentAbstractLevel) {
        this.currentAbstractLevel = currentAbstractLevel;
    }

    public void clearId() {
        id = null;
    }
}
