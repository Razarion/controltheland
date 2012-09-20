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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;

/**
 * User: beat
 * Date: 09.12.2009
 * Time: 15:12:27
 */
@Entity(name = "ITEM_LAUNCHER_TYPE")
public class DbLauncherType implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    private double progress;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DbProjectileItemType dbProjectileItemType;

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public DbProjectileItemType getDbProjectileItemType() {
        return dbProjectileItemType;
    }

    public void setDbProjectileItemType(DbProjectileItemType dbProjectileItemType) {
        this.dbProjectileItemType = dbProjectileItemType;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbLauncherType that = (DbLauncherType) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
