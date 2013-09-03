package com.btxtech.game.services.user;

import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.utg.UserGuidanceService;
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
    private UserGuidanceService userGuidanceService;

    @Test
    @DirtiesContext
    public void testTwoRegUsers() throws Exception {
        configureMultiplePlanetsAndLevels();
        // U1 no real base, first level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userGuidanceService.getDefaultLevelTaskId();

        // Verify
        List<UserState> userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        UserState userStateTest = getRegUserState("U1");
        Assert.assertTrue(userStateTest.isOnline());
        Assert.assertNotNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);


        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        userStateTest = getRegUserState("U1");
        Assert.assertFalse(userStateTest.isOnline());
        Assert.assertNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);

        // U2 real base, second level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        int levelTaskId = userGuidanceService.getDefaultLevelTaskId();
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", levelTaskId, "", 0, 0);

        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(2, userStates.size());
        userStateTest = getRegUserState("U1");
        Assert.assertFalse(userStateTest.isOnline());
        Assert.assertNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);
        userStateTest = getRegUserState("U2");
        Assert.assertTrue(userStateTest.isOnline());
        Assert.assertNotNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getDbLevelId(), TEST_LEVEL_2_REAL_ID);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Unregistered base, fist level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.getDefaultLevelTaskId();        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(3, userStates.size());
        userStateTest = getRegUserState("U1");
        Assert.assertFalse(userStateTest.isOnline());
        Assert.assertNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);
        userStateTest = getRegUserState("U2");
        Assert.assertFalse(userStateTest.isOnline());
        Assert.assertNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getDbLevelId(), TEST_LEVEL_2_REAL_ID);
        userStateTest = getUnregUserState();
        Assert.assertTrue(userStateTest.isOnline());
        Assert.assertNotNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(2, userStates.size());
        userStateTest = getRegUserState("U1");
        Assert.assertFalse(userStateTest.isOnline());
        Assert.assertNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);
        userStateTest = getRegUserState("U2");
        Assert.assertFalse(userStateTest.isOnline());
        Assert.assertNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getDbLevelId(), TEST_LEVEL_2_REAL_ID);
    }

    @Test
    @DirtiesContext
    public void testOneRegUsersOneUnregOnlineUser() throws Exception {
        configureSimplePlanetNoResources();
        // U1 no real base, first level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        getMovableService().getRealGameInfo(START_UID_1, null);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Unregistered user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().getRealGameInfo(START_UID_2, null);
        Assert.assertEquals(2, userService.getAllUserStates().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Unregistered user left
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, userService.getAllUserStates().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

    @Test
    @DirtiesContext
    public void testRegUserLogOutLogIn() throws Exception {
        configureMultiplePlanetsAndLevels();
        // U1 no real base, first level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userGuidanceService.getDefaultLevelTaskId();
        // Verify
        List<UserState> userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        UserState userStateTest = getRegUserState("U1");
        Assert.assertTrue(userStateTest.isOnline());
        Assert.assertNotNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        userStateTest = getRegUserState("U1");
        Assert.assertFalse(userStateTest.isOnline());
        Assert.assertNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        userStateTest = getRegUserState("U1");
        Assert.assertTrue(userStateTest.isOnline());
        Assert.assertNotNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        userStateTest = getRegUserState("U1");
        Assert.assertFalse(userStateTest.isOnline());
        Assert.assertNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        userStateTest = getRegUserState("U1");
        Assert.assertTrue(userStateTest.isOnline());
        Assert.assertNotNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);
        userService.logout();
        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        userStateTest = getRegUserState("U1");
        Assert.assertFalse(userStateTest.isOnline());
        Assert.assertNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        userStateTest = getRegUserState("U1");
        Assert.assertFalse(userStateTest.isOnline());
        Assert.assertNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);
    }

    @Test
    @DirtiesContext
    public void testSameSessionUnregRegUnreg() throws Exception {
        configureMultiplePlanetsAndLevels();
        // U1 no real base, first level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userGuidanceService.getDefaultLevelTaskId();
        // Verify
        List<UserState> userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        UserState userStateTest = getRegUserState("U1");
        Assert.assertTrue(userStateTest.isOnline());
        Assert.assertNotNull(userStateTest.getSessionId());
        Assert.assertEquals(userStateTest.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // User goes online again but unregistered
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.getDefaultLevelTaskId();
        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(2, userStates.size());
        UserState regUserState = getRegUserState("U1");
        Assert.assertFalse(regUserState.isOnline());
        Assert.assertNull(regUserState.getSessionId());
        Assert.assertEquals(regUserState.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);
        UserState unregUserState = getUnregUserState();
        Assert.assertTrue(unregUserState.isOnline());
        Assert.assertNotNull(unregUserState.getSessionId());
        Assert.assertEquals(unregUserState.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        // User logs in
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        // Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        regUserState = getRegUserState("U1");
        Assert.assertTrue(regUserState.isOnline());
        Assert.assertNotNull(regUserState.getSessionId());
        Assert.assertEquals(regUserState.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);
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
        Assert.assertEquals(regUserState.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);
        //Enters game again
        userGuidanceService.getDefaultLevelTaskId();
        userStates = userService.getAllUserStates();
        Assert.assertEquals(2, userStates.size());
        regUserState = getRegUserState("U1");
        Assert.assertFalse(regUserState.isOnline());
        Assert.assertNull(regUserState.getSessionId());
        Assert.assertEquals(regUserState.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);
        unregUserState = getUnregUserState();
        Assert.assertTrue(unregUserState.isOnline());
        Assert.assertNotNull(unregUserState.getSessionId());
        Assert.assertEquals(unregUserState.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        //Verify
        userStates = userService.getAllUserStates();
        Assert.assertEquals(1, userStates.size());
        regUserState = getRegUserState("U1");
        Assert.assertFalse(regUserState.isOnline());
        Assert.assertNull(regUserState.getSessionId());
        Assert.assertEquals(regUserState.getDbLevelId(), TEST_LEVEL_1_SIMULATED_ID);
    }

    private UserState getRegUserState(String userName) {
        User user = userService.getUser(userName);
        if(user == null) {
            Assert.fail("No such user: " + userName);
        }
        for (UserState userState : userService.getAllUserStates()) {
            if (userState.isRegistered() && userState.getUser().equals(user.getId())) {
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
