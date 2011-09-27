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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.services.bot.impl.BotRunner;
import com.btxtech.game.services.common.CrudRootServiceHelper;

/**
 * User: beat
 * Date: 14.03.2010
 * Time: 17:17:01
 */
public interface BotService {
    CrudRootServiceHelper<DbBotConfig> getDbBotConfigCrudServiceHelper();

    void start();

    void activate();

    BotRunner getBotRunner(DbBotConfig dbBotConfig);

    boolean isInRealm(Index point);
}