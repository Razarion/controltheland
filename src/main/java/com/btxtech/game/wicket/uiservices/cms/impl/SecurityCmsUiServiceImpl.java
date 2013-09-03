package com.btxtech.game.wicket.uiservices.cms.impl;

import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.services.user.AlreadyLoggedInException;
import com.btxtech.game.wicket.WicketAuthenticatedWebSession;
import com.btxtech.game.wicket.uiservices.cms.SecurityCmsUiService;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: 22.07.2011
 * Time: 11:42:28
 */
@Component("securityCmsUiService")
public class SecurityCmsUiServiceImpl implements SecurityCmsUiService {
    @Override
    public void signIn(String name, String password) throws AlreadyLoggedInException, UserAlreadyExistsException, PasswordNotMatchException {
        WicketAuthenticatedWebSession.get().signIn(name, password);
    }
}
