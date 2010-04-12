package com.btxtech.game.services.overall;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.services.TestWebSessionContextLoader;
import com.btxtech.game.services.overall.helpers.BaseHelper;
import com.btxtech.game.services.overall.helpers.GameTestHelper;
import com.btxtech.game.services.overall.helpers.ResultObject;
import java.util.ArrayList;
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
public class TestRandomItemMove {
    @Autowired
    private GameTestHelper gameTestHelper;

    @Test
    public void testRandomItemMove() throws Exception {
        gameTestHelper.emptyGame();

        ArrayList<TestCreateAndMove> workers = new ArrayList<TestCreateAndMove>();
        ThreadGroup threadGroup = new ThreadGroup("Test create and move thread group");
        for (int i = 0; i < GameTestHelper.BASE_COUNT; i++) {
            TestCreateAndMove testCreateAndMove = new TestCreateAndMove(threadGroup, i);
            testCreateAndMove.start();
            workers.add(testCreateAndMove);
        }
        long time = System.currentTimeMillis();
        for (TestCreateAndMove worker : workers) {
            worker.join();
        }
        System.out.println("----------------------------------------------------");
        System.out.println("Time used: " + (System.currentTimeMillis() - time));
        boolean hasFailed = false;
        for (TestCreateAndMove worker : workers) {
           if(worker.getResultObject() != null) {
               System.out.println(worker.getResultObject());
               hasFailed = true;
           }
        }
        if(hasFailed) {
            Assert.fail("Falied. See output above.");
        }
    }

    private ResultObject createAndMove(int number) throws Exception {
        BaseHelper baseHelper;
        baseHelper = gameTestHelper.createBase(number);
        SimpleBase myBase = baseHelper.getGameInfo().getBase();
        Id cvId = baseHelper.getConstructionVehicleSyncInfo().getId();
        Index start = baseHelper.getConstructionVehicleSyncInfo().getAbsolutePosition();

        Index destination = gameTestHelper.getRandomPassableAbsolutePosition();
        gameTestHelper.sendMoveCommand(cvId, destination);
        return gameTestHelper.waitToReachTarget(myBase, cvId, start, destination);
    }

    class TestCreateAndMove extends Thread {
        private int number;
        private ResultObject resultObject;

        public TestCreateAndMove(ThreadGroup threadGroup, int number) {
            super(threadGroup, "TestCreateAndMove " + number);
            this.number = number;
        }

        public ResultObject getResultObject() {
            return resultObject;
        }

        @Override
        public void run() {
            try {
                resultObject = createAndMove(number);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

    }
}