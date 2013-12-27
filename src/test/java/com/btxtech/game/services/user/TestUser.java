package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.btxtech.game.services.AbstractServiceTest;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 27.04.12
 * Time: 16:56
 */
public class TestUser extends AbstractServiceTest {

    @Test
    @DirtiesContext
    public void createSimpleUser() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // User 1
        User user1 = new User();
        user1.registerUser("name1", null, null, null);
        saveOrUpdateInTransaction(user1);
        int user1Id = user1.getId();
        // Verify user 1
        SimpleUser simpleUser1 = user1.createSimpleUser();
        Assert.assertEquals(user1Id, simpleUser1.getId());
        Assert.assertEquals("name1", simpleUser1.getName());
        // User 2
        User user2 = new User();
        user2.registerUser("name2", null, null, null);
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