package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.tracker.DbUserHistory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * User: beat
 * Date: 26.04.2011
 * Time: 14:01:26
 */
public class TestUserHistory extends AbstractServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserGuidanceService userGuidanceService;

    @Test
    @DirtiesContext
    public void createUser() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        List<DbUserHistory> dbUserHistories = getUserHistory();
        Assert.assertEquals(3, dbUserHistories.size());
        Assert.assertNotNull(dbUserHistories.get(0).getLoggedOut());
        Assert.assertNotNull(dbUserHistories.get(1).getLoggedIn());
        Assert.assertNotNull(dbUserHistories.get(2).getCreated());
    }

    @Test
    @DirtiesContext
    public void loginLogout() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        userService.logout();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        List<DbUserHistory> dbUserHistories = getUserHistory();
        Assert.assertEquals(7, dbUserHistories.size());

        Assert.assertNotNull(dbUserHistories.get(0).getLoggedOut());
        Assert.assertNotNull(dbUserHistories.get(1).getLoggedIn());
        Assert.assertNotNull(dbUserHistories.get(2).getLoggedOut());
        Assert.assertNotNull(dbUserHistories.get(3).getLoggedIn());
        Assert.assertNotNull(dbUserHistories.get(4).getLoggedOut());
        Assert.assertNotNull(dbUserHistories.get(5).getLoggedIn());
        Assert.assertNotNull(dbUserHistories.get(6).getCreated());
    }

    @Test
    @DirtiesContext
    public void enterGame() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", userGuidanceService.getDefaultLevelTaskId(), 0, "", 0, 0);
        getMovableService().getRealGameInfo(START_UID_1, null);
        getMovableService().createBase(START_UID_1, new Index(1000, 1000));

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        List<DbUserHistory> dbUserHistories = getUserHistory();
        Assert.assertEquals(5, dbUserHistories.size());
        Assert.assertNotNull(dbUserHistories.get(0).getLoggedOut());
        Assert.assertNotNull(dbUserHistories.get(1).getBaseCreated());
        Assert.assertNotNull(dbUserHistories.get(2).getGameEntered());
        Assert.assertNotNull(dbUserHistories.get(3).getLoggedIn());
        Assert.assertNotNull(dbUserHistories.get(4).getCreated());
    }

    @Test
    @DirtiesContext
    public void defeated() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", userGuidanceService.getDefaultLevelTaskId(), 0, "", 0, 0);
        getMovableService().getRealGameInfo(START_UID_1, null);
        SimpleBase target = getMovableService().createBase(START_UID_1, new Index(1000, 1000)).getBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", userGuidanceService.getDefaultLevelTaskId(), 0, "", 0, 0);
        SimpleBase actor = createBase(new Index(3000, 3000));
        sendBuildCommand(getFirstSynItemId(actor, TEST_START_BUILDER_ITEM_ID), new Index(3300, 3300), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(actor, TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendAttackCommand(getFirstSynItemId(actor, TEST_ATTACK_ITEM_ID), getFirstSynItemId(target, TEST_START_BUILDER_ITEM_ID));
        waitForActionServiceDone();


        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        List<DbUserHistory> dbUserHistories = getUserHistory();
        Assert.assertEquals(6, dbUserHistories.size());
        Assert.assertEquals("U1", dbUserHistories.get(0).getBaseName());
        Assert.assertNotNull(dbUserHistories.get(0).getBaseDefeated());
        Assert.assertNotNull(dbUserHistories.get(1).getLoggedOut());
        Assert.assertNotNull(dbUserHistories.get(2).getBaseCreated());
        Assert.assertNotNull(dbUserHistories.get(3).getGameEntered());
        Assert.assertNotNull(dbUserHistories.get(4).getLoggedIn());
        Assert.assertNotNull(dbUserHistories.get(5).getCreated());
    }

    @SuppressWarnings("unchecked")
    private List<DbUserHistory> getUserHistory() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(DbUserHistory.class);
        criteria.addOrder(Order.desc("id"));
        List<DbUserHistory> dbUserHistories = criteria.list();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        return dbUserHistories;
    }
}
