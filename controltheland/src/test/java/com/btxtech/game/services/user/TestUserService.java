package com.btxtech.game.services.user;

import com.btxtech.game.services.BaseTestService;
import com.btxtech.game.services.utg.UserGuidanceService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.easymock.EasyMock.createNiceMock;

/**
 * User: beat
 * Date: Jul 11, 2009
 * Time: 12:00:44 PM
 */
@ContextConfiguration(locations = {"classpath:UtgMockTestService-context.xml"})
public class TestUserService extends BaseTestService {
    @Autowired
    private UserService userService;

    @Test
    public void createUser() throws Exception {
        beginHttpSession();
        beginHttpRequest();
        beforeOpenSessionInViewFilter();

        Assert.assertNull(userService.getUser());
        userService.createUserAndLoggin("U1", "test", "test", "test");
        Assert.assertEquals("U1", userService.getUser().getUsername());

        System.out.println("***1 " + userService.getUser());

        afterOpenSessionInViewFilter();
        endHttpRequest();


        beginHttpRequest();
        beforeOpenSessionInViewFilter();

        Assert.assertEquals("U1", userService.getUser().getUsername());

        afterOpenSessionInViewFilter();
        endHttpRequest();
        endHttpSession();

        beginHttpSession();
        beginHttpRequest();
        beforeOpenSessionInViewFilter();

        Assert.assertNull(userService.getUser());

        afterOpenSessionInViewFilter();
        endHttpRequest();
        endHttpSession();
    }

    public static UserGuidanceService createUserGuidanceServiceMock() {
        return createNiceMock(UserGuidanceService.class);
    }
}