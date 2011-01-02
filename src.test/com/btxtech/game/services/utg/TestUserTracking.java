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

package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.common.gameengine.services.utg.UserAction;
import com.btxtech.game.services.TestWebSessionContextLoader;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: beat
 * Date: 18.02.2010
 * Time: 21:44:33
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:war/WEB-INF/applicationContext.xml"}, loader = TestWebSessionContextLoader.class)
@Transactional
@TransactionConfiguration()
public class TestUserTracking {
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private UserGuidanceService userGuidanceService;

    //@Test

    public void testSaveUserActions() {
        ArrayList<UserAction> userActions = new ArrayList<UserAction>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 2001; i++) {
            stringBuilder.append("x");
        }
        userActions.add(new UserAction("XXXX", stringBuilder.toString()));
    }

    @Test
    public void testDbUserStageStorage() {
        /*      List<DbUserStage> dbUserStages = (List<DbUserStage>) userGuidanceService.getUserStageCrudServiceHelper().readDbChildren();
        Collections.swap(dbUserStages, 0, 1);
        userGuidanceService.getUserStageCrudServiceHelper().updateDbChildren(dbUserStages);
        List<DbUserStage> newDbUserStages = (List<DbUserStage>) userGuidanceService.getUserStageCrudServiceHelper().readDbChildren();
        for (int i = 0; i < dbUserStages.size(); i++) {
            Assert.assertEquals(dbUserStages.get(i), newDbUserStages.get(i));
        }*/
    }

    //@Test

    public void testNextDbUserStage() {
        //  List<DbUserStage> dbUserStages = (List<DbUserStage>) userGuidanceService.getUserStageCrudServiceHelper().readDbChildren();
        //  DbUserStage dbUserStage = dbUserStages.get(0);
        //  System.out.println(dbUserStage);
        //dbUserStage = userGuidanceService.getNextDbUserStage(dbUserStage);
        // System.out.println(dbUserStage);
    }

}
