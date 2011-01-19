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

package com.btxtech.game.services.utg.impl;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.utg.condition.AbstractConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.impl.ConditionServiceImpl;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.ServerConditionService;
import com.btxtech.game.services.utg.UserGuidanceService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: 28.12.2010
 * Time: 18:16:33
 */
@Component("serverConditionService")
public class ServerConditionServiceImpl extends ConditionServiceImpl<UserState> implements ServerConditionService {
    @Autowired
    private BaseService baseService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private UserService userService;
    private Map<UserState, AbstractConditionTrigger<UserState>> triggerMap = new HashMap<UserState, AbstractConditionTrigger<UserState>>();

    @Override
    protected void saveAbstractConditionTrigger(AbstractConditionTrigger<UserState> abstractConditionTrigger) {
        triggerMap.put(abstractConditionTrigger.getUserObject(), abstractConditionTrigger);
    }

    @Override
    protected AbstractConditionTrigger<UserState> getAbstractConditionPrivate(SimpleBase simpleBase, ConditionTrigger conditionTrigger) {
        UserState userState;
        if (simpleBase != null) {
            userState = baseService.getUserState(simpleBase);
        } else {
            userState = userService.getUserState();
        }

        AbstractConditionTrigger<UserState> abstractConditionTrigger = triggerMap.get(userState);
        if (abstractConditionTrigger == null) {
            return null;
        }
        if (abstractConditionTrigger.getConditionTrigger() == conditionTrigger) {
            return abstractConditionTrigger;
        } else {
            return null;
        }
    }

    @Override
    protected void conditionPassed(UserState userState) {
        userGuidanceService.promote(userState);
    }

    @Override
    public void onTutorialFinished(UserState userState) {
        triggerSimple(ConditionTrigger.TUTORIAL);
    }
}
