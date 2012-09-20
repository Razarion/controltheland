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

package com.btxtech.game.jsre.common.utg.config;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.utg.condition.AbstractComparison;

import java.io.Serializable;

/**
 * User: beat
 * Date: 27.12.2010
 * Time: 23:07:18
 */
public interface AbstractComparisonConfig extends Serializable {
    AbstractComparison createAbstractComparison(PlanetServices planetServices, SimpleBase simpleBase);
}
