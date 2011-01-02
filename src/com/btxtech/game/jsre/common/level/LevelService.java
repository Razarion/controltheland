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

package com.btxtech.game.jsre.common.level;

import com.btxtech.game.jsre.common.level.impl.ConditionServiceImpl;

/**
 * User: beat
 * Date: 27.12.2010
 * Time: 15:17:39
 */
public abstract class LevelService {
    protected abstract ConditionServiceImpl getConditionTriggerService();

    /*protected abstract Level getLevel();

    protected abstract ScopeAndLimitService getScopeAndLimitService();

    protected abstract void saveLevel(Level level);


    protected abstract Level getNextLevel(Level currentLevel);*/

    public abstract void levelPassed();    
    /*{
        Level currentLevel = getLevel();
        currentLevel = getNextLevel(currentLevel);
        saveLevel(currentLevel);
        getConditionTriggerService().activate(currentLevel);
        getScopeAndLimitService().activate(currentLevel);
    }*/
}
