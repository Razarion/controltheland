package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.AlreadyUsedException;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.services.BaseTestService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: beat
 * Date: Jul 11, 2009
 * Time: 12:00:44 PM
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"file:war/WEB-INF/applicationContext.xml"}, loader = WebSessionContextLoader.class)
//@Transactional
//@TransactionConfiguration()
public class TestUserService extends BaseTestService {
    @Autowired
    private UserService userService;

    @Test
    public void testAddUser() throws AlreadyUsedException, UserAlreadyExistsException, PasswordNotMatchException {
        userService.createUserAndLoggin("testU13", "test", "test", "test", false);
    }


}