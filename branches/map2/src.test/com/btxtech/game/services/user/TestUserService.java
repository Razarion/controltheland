package com.btxtech.game.services.user;

import com.btxtech.game.services.TestWebSessionContextLoader;
import com.btxtech.game.services.base.AlreadyUsedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: beat
 * Date: Jul 11, 2009
 * Time: 12:00:44 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:war/WEB-INF/applicationContext.xml"}, loader = TestWebSessionContextLoader.class)
@Transactional
@TransactionConfiguration()
public class TestUserService {
    @Autowired
    private UserService userService;

    @Test
    public void testAddUser() throws AlreadyUsedException, UserAlreadyExistsException, PasswordNotMatchException {
        userService.createUserAndLoggin("testU13", "test", "test");
    }


}