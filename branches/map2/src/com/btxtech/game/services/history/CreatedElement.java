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

package com.btxtech.game.services.history;

import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * User: beat
 * Date: Jul 5, 2009
 * Time: 9:28:25 PM
 */
@Entity
@DiscriminatorValue("CREATED")
public class CreatedElement extends HistoryElement{
    private String item;

    /**
     * Used by hibernate
     */
    protected CreatedElement() {
    }

    public CreatedElement(SyncBaseItem item) {
        super(item.getBase());
        this.item = item.getItemType().getName();
    }

    @Override
    @Transient
    public String getMessage() {
        return "'" + getUser() + "' created a " + item;
    }
}