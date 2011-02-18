package com.btxtech.game.services.overall;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.services.TestWebSessionContextLoader;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.overall.helpers.BaseHelper;
import com.btxtech.game.services.overall.helpers.GameTestHelper;
import com.btxtech.game.services.overall.helpers.ResultObject;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: beat
 * Date: Oct 3, 2009
 * Time: 10:39:24 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:war/WEB-INF/applicationContext.xml"}, loader = TestWebSessionContextLoader.class)
@Transactional
@TransactionConfiguration()
public class TestItemMove {
    @Autowired
    private GameTestHelper gameTestHelper;
    @Autowired
    private ItemService itemService;

    @Test
    public void testItemMove() throws Exception {
        gameTestHelper.emptyGame();
        BaseHelper baseHelper = gameTestHelper.createBase(0);

        Id id = baseHelper.getConstructionVehicleSyncInfo().getId();
        ConstructionVehicleSyncItem constructionVehicleSyncItem = (ConstructionVehicleSyncItem) itemService.getItem(id);

        Index start = new Index(344, 1995);
        Index destination = new Index(526, 2050);
        constructionVehicleSyncItem.setAbsolutePosition(start, true);
        gameTestHelper.sendMoveCommand(id, destination);

        ResultObject resultObject = gameTestHelper.waitToReachTarget(baseHelper.getGameInfo().getBase(), id, start, destination);
        if(resultObject != null) {
            System.out.println(resultObject);
            Assert.fail("See above");
        }
    }
}