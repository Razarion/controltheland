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

package com.btxtech.game.services.bot.impl;

/**
 * User: beat
 * Date: 15.03.2010
 * Time: 22:07:46
 */
public class BotServiceConfig {
    public int getBotActionDelay() {
        return 10000;
    }

    public String getUserName() {
        return "EvilBot";
    }

    public int getMinMoney() {
        return 1000;
    }
}
