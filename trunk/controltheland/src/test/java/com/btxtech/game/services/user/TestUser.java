package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.SimpleUser;
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
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("u1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("u2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("u3");
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
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("u1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("u2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("u3");
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

    @Test
    @DirtiesContext
    public void createSimpleUser() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // User 1
        User user1 = new User();
        user1.registerUser("name1", null, null);
        saveOrUpdateInTransaction(user1);
        int user1Id = user1.getId();
        // Verify user 1
        SimpleUser simpleUser1 = user1.createSimpleUser();
        Assert.assertEquals(user1Id, simpleUser1.getId());
        Assert.assertEquals("name1", simpleUser1.getName());
        // User 2
        User user2 = new User();
        user2.registerUser("name2", null, null);
        saveOrUpdateInTransaction(user2);
        int user2Id = user2.getId();
        SimpleUser simpleUser2 = user2.createSimpleUser();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify user 2
        Assert.assertEquals(user2Id, simpleUser2.getId());
        Assert.assertEquals("name2", simpleUser2.getName());
    }
}