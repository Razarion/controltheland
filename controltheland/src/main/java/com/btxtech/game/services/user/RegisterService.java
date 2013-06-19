package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.btxtech.game.jsre.common.gameengine.services.user.EmailAlreadyExitsException;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 26.12.12
 * Time: 10:14
 */
public interface RegisterService {
    SimpleUser register(String userName, String password, String confirmPassword, String email) throws UserAlreadyExistsException, PasswordNotMatchException, EmailAlreadyExitsException;

    User onVerificationPageCalled(String verificationId) throws EmailIsAlreadyVerifiedException, UserDoesNotExitException;

    void onForgotPassword(String email) throws EmailDoesNotExitException, UserIsNotConfirmedException;

    void onPasswordReset(String uuid, String password, String confirmPassword) throws PasswordNotMatchException, NoForgotPasswordEntryException;
}
