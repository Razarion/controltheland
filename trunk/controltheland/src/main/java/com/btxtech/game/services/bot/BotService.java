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

package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.common.gameengine.services.bot.CommonBotService;
import com.btxtech.game.services.planet.db.DbPlanet;

/**
 * User: beat
 * Date: 14.03.2010
 * Time: 17:17:01
 */
public interface BotService extends CommonBotService {
    void activate(DbPlanet dbPlanet);

    void deactivate();

    void reactivate(DbPlanet dbPlanet);
}
