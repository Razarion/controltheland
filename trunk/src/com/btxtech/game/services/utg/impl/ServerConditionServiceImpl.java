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
import com.btxtech.game.jsre.common.level.condition.AbstractConditionTrigger;
import com.btxtech.game.jsre.common.level.condition.TutorialConditionTrigger;
import com.btxtech.game.jsre.common.level.impl.ConditionServiceImpl;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.user.User;
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
public class ServerConditionServiceImpl extends ConditionServiceImpl<User> implements ServerConditionService {
    @Autowired
    private BaseService baseService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    private Map<User, AbstractConditionTrigger<User>> triggerMap = new HashMap<User, AbstractConditionTrigger<User>>();

    @Override
    protected void saveAbstractConditionTrigger(AbstractConditionTrigger<User> abstractConditionTrigger) {
        triggerMap.put(abstractConditionTrigger.getUserObject(), abstractConditionTrigger);
    }

    @Override
    protected AbstractConditionTrigger<User> getAbstractConditionPrivate(SimpleBase simpleBase) {
        User user = baseService.getUser(simpleBase);
        AbstractConditionTrigger<User> abstractConditionTrigger = triggerMap.get(user);
        if (abstractConditionTrigger == null) {
            throw new IllegalArgumentException("No entry for " + simpleBase);
        }
        return abstractConditionTrigger;
    }

    private <U> U getAbstractCondition(User user, Class<U> theClass) {
        AbstractConditionTrigger<User> abstractConditionTrigger = triggerMap.get(user);
        if (abstractConditionTrigger == null) {
            return null;
        }
        if (theClass.equals(abstractConditionTrigger.getClass())) {
            return (U) abstractConditionTrigger;
        } else {
            return null;
        }
    }

    @Override
    protected void conditionPassed(User user) {
        userGuidanceService.promote(user);
    }

    @Override
    public void onTutorialFinished(User user) {
        TutorialConditionTrigger<User> userTutorialConditionTrigger = getAbstractCondition(user, TutorialConditionTrigger.class);
        if (userTutorialConditionTrigger == null) {
            return;
        }
        userTutorialConditionTrigger.onTutorialFinished();
        if (userTutorialConditionTrigger.isFulfilled()) {
            conditionPassed(userTutorialConditionTrigger.getUserObject());
        }
    }
}
