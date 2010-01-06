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

import com.btxtech.game.jsre.common.SimpleBase;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * User: beat
 * Date: Jul 5, 2009
 * Time: 7:57:55 PM
 */
@Entity
@DiscriminatorValue("USER_ENTERED")
public class UserEntered extends HistoryElement{
    public UserEntered(SimpleBase base) {
        super(base);
    }
     /**
     * Used by hibernate
     */
    protected UserEntered() {
    }

    @Override
    public String getMessage() {
        return "'" + getUser() + "' entered the game";
    }
}
