package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.AbstractServiceTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * User: beat
 * Date: 28.03.2011
 * Time: 00:31:20
 */
public class TestUserState extends AbstractServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private MovableService movableService;

    @Test
    @DirtiesContext
    public void testTwoRegUsers() throws Exception {
        configureMinimalGame();
        // U1 no real base, first level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        movableService.getGameInfo();

        // Verify
        List<UserState> userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        UserState userStateTest = getRegUserState("U1");
        Assert.assertTrue(userStateTest.isOnline());
        Assert.assertNotNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);


        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        userStateTest = getRegUserState("U1");
        Assert.assertFalse(userStateTest.isOnline());
        Assert.assertNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);

        // U2 real base, second level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U2", "test", "test", "test");
        userService.login("U2", "test");
        movableService.getGameInfo();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);

        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(2, userStates.size());
        userStateTest = getRegUserState("U1");
        Assert.assertFalse(userStateTest.isOnline());
        Assert.assertNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);
        userStateTest = getRegUserState("U2");
        Assert.assertTrue(userStateTest.isOnline());
        Assert.assertNotNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_2_REAL);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Unregistered base, fist level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.getGameInfo();
        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(3, userStates.size());
        userStateTest = getRegUserState("U1");
        Assert.assertFalse(userStateTest.isOnline());
        Assert.assertNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);
        userStateTest = getRegUserState("U2");
        Assert.assertFalse(userStateTest.isOnline());
        Assert.assertNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_2_REAL);
        userStateTest = getUnregUserState();
        Assert.assertTrue(userStateTest.isOnline());
        Assert.assertNotNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(2, userStates.size());
        userStateTest = getRegUserState("U1");
        Assert.assertFalse(userStateTest.isOnline());
        Assert.assertNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);
        userStateTest = getRegUserState("U2");
        Assert.assertFalse(userStateTest.isOnline());
        Assert.assertNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_2_REAL);
    }

    @Test
    @DirtiesContext
    public void testOneRegUsersOneUnregOnlineUser() throws Exception {
        configureMinimalGame();
        // U1 no real base, first level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        movableService.getGameInfo();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Unregistered base, fist level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.getGameInfo();
        Assert.assertEquals(2, userService.getAllUserStates().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testRegUserLogOutLogIn() throws Exception {
        configureMinimalGame();
        // U1 no real base, first level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        movableService.getGameInfo();
        // Verify
        List<UserState> userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        UserState userStateTest = getRegUserState("U1");
        Assert.assertTrue(userStateTest.isOnline());
        Assert.assertNotNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        userStateTest = getRegUserState("U1");
        Assert.assertFalse(userStateTest.isOnline());
        Assert.assertNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        userStateTest = getRegUserState("U1");
        Assert.assertTrue(userStateTest.isOnline());
        Assert.assertNotNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        userStateTest = getRegUserState("U1");
        Assert.assertFalse(userStateTest.isOnline());
        Assert.assertNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        userStateTest = getRegUserState("U1");
        Assert.assertTrue(userStateTest.isOnline());
        Assert.assertNotNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);
        userService.logout();
        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        userStateTest = getRegUserState("U1");
        Assert.assertFalse(userStateTest.isOnline());
        Assert.assertNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        userStateTest = getRegUserState("U1");
        Assert.assertFalse(userStateTest.isOnline());
        Assert.assertNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);

    }

    @Test
    @DirtiesContext
    public void testSameSessionUnregRegUnreg() throws Exception {
        configureMinimalGame();
        // U1 no real base, first level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        movableService.getGameInfo();
        // Verify
        List<UserState> userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        UserState userStateTest = getRegUserState("U1");
        Assert.assertTrue(userStateTest.isOnline());
        Assert.assertNotNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // User goes online again but unregistered
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.getGameInfo();
        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(2, userStates.size());
        UserState regUserState = getRegUserState("U1");
        Assert.assertFalse(regUserState.isOnline());
        Assert.assertNull(regUserState.getSessionId());
        Assert.assertEquals(regUserState.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);
        UserState unregUserState = getUnregUserState();
        Assert.assertTrue(unregUserState.isOnline());
        Assert.assertNotNull(unregUserState.getSessionId());
        Assert.assertEquals(unregUserState.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);
        endHttpRequestAndOpenSessionInViewFilter();
        // User logs in
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        regUserState = getRegUserState("U1");
        Assert.assertTrue(regUserState.isOnline());
        Assert.assertNotNull(regUserState.getSessionId());
        Assert.assertEquals(regUserState.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);
        endHttpRequestAndOpenSessionInViewFilter();
        // User logs out
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.logout();
        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        regUserState = getRegUserState("U1");
        Assert.assertFalse(regUserState.isOnline());
        Assert.assertNull(regUserState.getSessionId());
        Assert.assertEquals(regUserState.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);
        //Enters game again
        movableService.getGameInfo();
        userStates = userService.getAllUserStates();
        Assert.assertEquals(2, userStates.size());
        regUserState = getRegUserState("U1");
        Assert.assertFalse(regUserState.isOnline());
        Assert.assertNull(regUserState.getSessionId());
        Assert.assertEquals(regUserState.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);
        unregUserState = getUnregUserState();
        Assert.assertTrue(unregUserState.isOnline());
        Assert.assertNotNull(unregUserState.getSessionId());
        Assert.assertEquals(unregUserState.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        //Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        regUserState = getRegUserState("U1");
        Assert.assertFalse(regUserState.isOnline());
        Assert.assertNull(regUserState.getSessionId());
        Assert.assertEquals(regUserState.getCurrentAbstractLevel().getLevel().getName(), TEST_LEVEL_1_SIMULATED);
    }
    
    private UserState getRegUserState(String userName) {
        for (UserState userState : userService.getAllUserStates()) {
            if (userState.isRegistered() && userState.getUser().getUsername().equals(userName)) {
                return userState;
            }
        }
        Assert.fail("No such user: " + userName);
        return null;
    }

    private UserState getUnregUserState() {
        for (UserState userState : userService.getAllUserStates()) {
            if (!userState.isRegistered()) {
                return userState;
            }
        }
        Assert.fail("No unreg user");
        return null;
    }

}
