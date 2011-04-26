package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.user.UserService;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.test.annotation.DirtiesContext;

import java.sql.SQLException;
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
    private MovableService movableService;

    @Test
    @DirtiesContext
    public void createUser() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "");
        userService.login("U1", "test");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        List<UserHistory> userHistories = getUserHistory();
        Assert.assertEquals(3, userHistories.size());
        Assert.assertNotNull(userHistories.get(0).getLoggedOut());
        Assert.assertNotNull(userHistories.get(1).getLoggedIn());
        Assert.assertNotNull(userHistories.get(2).getCreated());
    }

    @Test
    @DirtiesContext
    public void loginLogout() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "");
        userService.login("U1", "test");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        userService.logout();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        List<UserHistory> userHistories = getUserHistory();
        Assert.assertEquals(7, userHistories.size());

        Assert.assertNotNull(userHistories.get(0).getLoggedOut());
        Assert.assertNotNull(userHistories.get(1).getLoggedIn());
        Assert.assertNotNull(userHistories.get(2).getLoggedOut());
        Assert.assertNotNull(userHistories.get(3).getLoggedIn());
        Assert.assertNotNull(userHistories.get(4).getLoggedOut());
        Assert.assertNotNull(userHistories.get(5).getLoggedIn());
        Assert.assertNotNull(userHistories.get(6).getCreated());
    }

    @Test
    @DirtiesContext
    public void enterGame() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "");
        userService.login("U1", "test");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        getMyBase(); // Setup connection

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        List<UserHistory> userHistories = getUserHistory();
        Assert.assertEquals(5, userHistories.size());
        Assert.assertNotNull(userHistories.get(0).getLoggedOut());
        Assert.assertNotNull(userHistories.get(1).getGameEntered());
        Assert.assertNotNull(userHistories.get(2).getBaseCreated());
        Assert.assertNotNull(userHistories.get(3).getLoggedIn());
        Assert.assertNotNull(userHistories.get(4).getCreated());
    }

    @Test
    @DirtiesContext
    public void surrendered() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "");
        userService.login("U1", "test");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        getMyBase(); // Setup connection
        movableService.surrenderBase();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        List<UserHistory> userHistories = getUserHistory();
        Assert.assertEquals(6, userHistories.size());
        Assert.assertNotNull(userHistories.get(0).getLoggedOut());
        Assert.assertNotNull(userHistories.get(1).getBaseSurrender());
        Assert.assertEquals("U1", userHistories.get(1).getBaseName());
        Assert.assertNotNull(userHistories.get(2).getGameEntered());
        Assert.assertNotNull(userHistories.get(3).getBaseCreated());
        Assert.assertNotNull(userHistories.get(4).getLoggedIn());
        Assert.assertNotNull(userHistories.get(5).getCreated());
    }

    @Test
    @DirtiesContext
    public void defeated() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "");
        userService.login("U1", "test");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        SimpleBase target = getMyBase(); // Setup connection

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        SimpleBase actor = getMyBase(); // Setup connection
        sendBuildCommand(getFirstSynItemId(actor, TEST_START_BUILDER_ITEM_ID), new Index(200, 200), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(actor, TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendAttackCommand(getFirstSynItemId(actor, TEST_ATTACK_ITEM_ID), getFirstSynItemId(target, TEST_START_BUILDER_ITEM_ID));
        waitForActionServiceDone();


        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        List<UserHistory> userHistories = getUserHistory();
        Assert.assertEquals(6, userHistories.size());
        Assert.assertEquals("U1", userHistories.get(0).getBaseName());
        Assert.assertNotNull(userHistories.get(0).getBaseDefeated());
        Assert.assertNotNull(userHistories.get(1).getLoggedOut());
        Assert.assertNotNull(userHistories.get(2).getGameEntered());
        Assert.assertNotNull(userHistories.get(3).getBaseCreated());
        Assert.assertNotNull(userHistories.get(4).getLoggedIn());
        Assert.assertNotNull(userHistories.get(5).getCreated());
    }

    private List<UserHistory> getUserHistory() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        List<UserHistory> userHistories = getHibernateTemplate().execute(new HibernateCallback<List<UserHistory>>() {
            @Override
            public List<UserHistory> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(UserHistory.class);
                criteria.addOrder(Order.desc("id"));
                return criteria.list();
            }
        });

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        return userHistories;
    }
}
