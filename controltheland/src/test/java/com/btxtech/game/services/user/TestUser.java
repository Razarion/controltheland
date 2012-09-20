package com.btxtech.game.services.user;

import com.btxtech.game.services.AbstractServiceTest;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 27.04.12
 * Time: 16:56
 */
public class TestUser extends AbstractServiceTest {
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    public void allianceOffers() throws Exception {
        configureSimplePlanet();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u1", "xxx", "xxx", "");
        userService.login("u1", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u2", "xxx", "xxx", "");
        userService.login("u2", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u3", "xxx", "xxx", "");
        userService.login("u3", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        User u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        User u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        User u3 = userService.getUser("u3");
        Assert.assertNotNull(u3);
        u1.getAllianceOffers().add(u2);
        u1.getAllianceOffers().add(u3);
        userService.save(u1);
        Assert.assertTrue(u1.getAllianceOffers().contains(u2));
        Assert.assertTrue(u1.getAllianceOffers().contains(u3));
        Assert.assertFalse(u2.getAllianceOffers().contains(u1));
        Assert.assertFalse(u2.getAllianceOffers().contains(u3));
        Assert.assertFalse(u3.getAllianceOffers().contains(u1));
        Assert.assertFalse(u3.getAllianceOffers().contains(u2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertTrue(u1.getAllianceOffers().contains(u2));
        Assert.assertTrue(u1.getAllianceOffers().contains(u3));
        Assert.assertFalse(u2.getAllianceOffers().contains(u1));
        Assert.assertFalse(u2.getAllianceOffers().contains(u3));
        Assert.assertFalse(u3.getAllianceOffers().contains(u1));
        Assert.assertFalse(u3.getAllianceOffers().contains(u2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertNotNull(u3);
        u2.getAllianceOffers().add(u1);
        u2.getAllianceOffers().add(u3);
        userService.save(u2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertTrue(u1.getAllianceOffers().contains(u2));
        Assert.assertTrue(u1.getAllianceOffers().contains(u3));
        Assert.assertTrue(u2.getAllianceOffers().contains(u1));
        Assert.assertTrue(u2.getAllianceOffers().contains(u3));
        Assert.assertFalse(u3.getAllianceOffers().contains(u1));
        Assert.assertFalse(u3.getAllianceOffers().contains(u2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertNotNull(u3);
        u3.getAllianceOffers().add(u1);
        u3.getAllianceOffers().add(u2);
        userService.save(u3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertTrue(u1.getAllianceOffers().contains(u2));
        Assert.assertTrue(u1.getAllianceOffers().contains(u3));
        Assert.assertTrue(u2.getAllianceOffers().contains(u1));
        Assert.assertTrue(u2.getAllianceOffers().contains(u3));
        Assert.assertTrue(u3.getAllianceOffers().contains(u1));
        Assert.assertTrue(u3.getAllianceOffers().contains(u2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertNotNull(u3);
        u3.getAllianceOffers().remove(u1);
        u2.getAllianceOffers().remove(u1);
        userService.save(u2);
        userService.save(u3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertTrue(u1.getAllianceOffers().contains(u2));
        Assert.assertTrue(u1.getAllianceOffers().contains(u3));
        Assert.assertFalse(u2.getAllianceOffers().contains(u1));
        Assert.assertTrue(u2.getAllianceOffers().contains(u3));
        Assert.assertFalse(u3.getAllianceOffers().contains(u1));
        Assert.assertTrue(u3.getAllianceOffers().contains(u2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertNotNull(u3);
        u1.getAllianceOffers().remove(u2);
        u3.getAllianceOffers().remove(u2);
        userService.save(u1);
        userService.save(u3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertFalse(u1.getAllianceOffers().contains(u2));
        Assert.assertTrue(u1.getAllianceOffers().contains(u3));
        Assert.assertFalse(u2.getAllianceOffers().contains(u1));
        Assert.assertTrue(u2.getAllianceOffers().contains(u3));
        Assert.assertFalse(u3.getAllianceOffers().contains(u1));
        Assert.assertFalse(u3.getAllianceOffers().contains(u2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertNotNull(u3);
        u1.getAllianceOffers().remove(u3);
        u2.getAllianceOffers().remove(u3);
        userService.save(u1);
        userService.save(u2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertFalse(u1.getAllianceOffers().contains(u2));
        Assert.assertFalse(u1.getAllianceOffers().contains(u3));
        Assert.assertFalse(u2.getAllianceOffers().contains(u1));
        Assert.assertFalse(u2.getAllianceOffers().contains(u3));
        Assert.assertFalse(u3.getAllianceOffers().contains(u1));
        Assert.assertFalse(u3.getAllianceOffers().contains(u2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void alliances() throws Exception {
        configureSimplePlanet();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u1", "xxx", "xxx", "");
        userService.login("u1", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u2", "xxx", "xxx", "");
        userService.login("u2", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u3", "xxx", "xxx", "");
        userService.login("u3", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        User u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        User u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        User u3 = userService.getUser("u3");
        Assert.assertNotNull(u3);
        u1.getAlliances().add(u2);
        u1.getAlliances().add(u3);
        userService.save(u1);
        Assert.assertTrue(u1.getAlliances().contains(u2));
        Assert.assertTrue(u1.getAlliances().contains(u3));
        Assert.assertFalse(u2.getAlliances().contains(u1));
        Assert.assertFalse(u2.getAlliances().contains(u3));
        Assert.assertFalse(u3.getAlliances().contains(u1));
        Assert.assertFalse(u3.getAlliances().contains(u2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertTrue(u1.getAlliances().contains(u2));
        Assert.assertTrue(u1.getAlliances().contains(u3));
        Assert.assertFalse(u2.getAlliances().contains(u1));
        Assert.assertFalse(u2.getAlliances().contains(u3));
        Assert.assertFalse(u3.getAlliances().contains(u1));
        Assert.assertFalse(u3.getAlliances().contains(u2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertNotNull(u3);
        u2.getAlliances().add(u1);
        u2.getAlliances().add(u3);
        userService.save(u2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertTrue(u1.getAlliances().contains(u2));
        Assert.assertTrue(u1.getAlliances().contains(u3));
        Assert.assertTrue(u2.getAlliances().contains(u1));
        Assert.assertTrue(u2.getAlliances().contains(u3));
        Assert.assertFalse(u3.getAlliances().contains(u1));
        Assert.assertFalse(u3.getAlliances().contains(u2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertNotNull(u3);
        u3.getAlliances().add(u1);
        u3.getAlliances().add(u2);
        userService.save(u3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertTrue(u1.getAlliances().contains(u2));
        Assert.assertTrue(u1.getAlliances().contains(u3));
        Assert.assertTrue(u2.getAlliances().contains(u1));
        Assert.assertTrue(u2.getAlliances().contains(u3));
        Assert.assertTrue(u3.getAlliances().contains(u1));
        Assert.assertTrue(u3.getAlliances().contains(u2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertNotNull(u3);
        u3.getAlliances().remove(u1);
        u2.getAlliances().remove(u1);
        userService.save(u2);
        userService.save(u3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertTrue(u1.getAlliances().contains(u2));
        Assert.assertTrue(u1.getAlliances().contains(u3));
        Assert.assertFalse(u2.getAlliances().contains(u1));
        Assert.assertTrue(u2.getAlliances().contains(u3));
        Assert.assertFalse(u3.getAlliances().contains(u1));
        Assert.assertTrue(u3.getAlliances().contains(u2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertNotNull(u3);
        u1.getAlliances().remove(u2);
        u3.getAlliances().remove(u2);
        userService.save(u1);
        userService.save(u3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertFalse(u1.getAlliances().contains(u2));
        Assert.assertTrue(u1.getAlliances().contains(u3));
        Assert.assertFalse(u2.getAlliances().contains(u1));
        Assert.assertTrue(u2.getAlliances().contains(u3));
        Assert.assertFalse(u3.getAlliances().contains(u1));
        Assert.assertFalse(u3.getAlliances().contains(u2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertNotNull(u3);
        u1.getAlliances().remove(u3);
        u2.getAlliances().remove(u3);
        userService.save(u1);
        userService.save(u2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        u1 = userService.getUser("u1");
        Assert.assertNotNull(u1);
        u2 = userService.getUser("u2");
        Assert.assertNotNull(u2);
        u3 = userService.getUser("u3");
        Assert.assertFalse(u1.getAlliances().contains(u2));
        Assert.assertFalse(u1.getAlliances().contains(u3));
        Assert.assertFalse(u2.getAlliances().contains(u1));
        Assert.assertFalse(u2.getAlliances().contains(u3));
        Assert.assertFalse(u3.getAlliances().contains(u1));
        Assert.assertFalse(u3.getAlliances().contains(u2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
