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

package com.btxtech.game.services.tutorial.condition;

import com.btxtech.game.jsre.common.tutorial.condition.AbstractConditionConfig;
import com.btxtech.game.jsre.common.tutorial.condition.ScrollConditionConfig;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * User: beat
 * Date: 01.12.2010
 * Time: 13:26:46
 */
@Entity
@DiscriminatorValue("SCROLL")
public class DbScrollConditionConfig extends DbAbstractConditionConfig {
    @Override
    public AbstractConditionConfig createConditionConfig() {
        return new ScrollConditionConfig();
    }
}
