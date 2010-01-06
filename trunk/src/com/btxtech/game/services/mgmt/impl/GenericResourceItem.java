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

package com.btxtech.game.services.mgmt.impl;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * User: beat
 * Date: 05.12.2009
 * Time: 18:49:56
 */
@Entity
@DiscriminatorValue("RESOURCE")
public class GenericResourceItem extends GenericItem{
    private int amount;

    /**
     * Used by hibernate
     */
    public GenericResourceItem() {
    }

    public GenericResourceItem(BackupEntry backupEntry) {
        super(backupEntry);
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
