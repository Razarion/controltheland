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
import javax.persistence.Transient;

/**
 * User: beat
 * Date: Jul 5, 2009
 * Time: 9:21:05 PM
 */
@Entity
@DiscriminatorValue("BASE_HAS_BEEN_DEFEATED")
public class BaseHasBeenDefeated extends HistoryElement {
    /**
     * Used by hibernate
     */
    protected BaseHasBeenDefeated() {
    }

    public BaseHasBeenDefeated(SimpleBase base) {
        super(base);
    }

    @Override
    @Transient    
    public String getMessage() {
        return "'" + getUser() + "' has been defeated";
    }


}
