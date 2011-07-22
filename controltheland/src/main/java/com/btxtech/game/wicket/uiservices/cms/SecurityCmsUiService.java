package com.btxtech.game.wicket.uiservices.cms;

import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.services.user.AlreadyLoggedInException;

/**
 * User: beat
 * Date: 22.07.2011
 * Time: 11:42:16
 */
public interface SecurityCmsUiService {
    void signIn(String name, String password) throws AlreadyLoggedInException, UserAlreadyExistsException, PasswordNotMatchException;
}
