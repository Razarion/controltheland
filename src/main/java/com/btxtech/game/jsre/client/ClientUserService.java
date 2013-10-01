package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.cockpit.menu.MenuBarCockpit;
import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.btxtech.game.jsre.client.dialogs.YesNoDialog;
import com.btxtech.game.jsre.client.dialogs.register.FacebookNickNameDialog;
import com.btxtech.game.jsre.client.dialogs.register.LoginDialog;
import com.btxtech.game.jsre.client.dialogs.register.RegisterButtonDialog;
import com.btxtech.game.jsre.client.dialogs.register.RegisterDialog;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.jsre.common.FacebookUtils;
import com.btxtech.game.jsre.common.gameengine.services.user.EmailAlreadyExitsException;
import com.btxtech.game.jsre.common.gameengine.services.user.LoginFailedException;
import com.btxtech.game.jsre.common.gameengine.services.user.LoginFailedNotVerifiedException;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.jsre.common.packets.UserPacket;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * User: beat
 * Date: 27.08.13
 * Time: 13:07
 */
public class ClientUserService implements DialogManager.DialogListener {
    private static ClientUserService INSTANCE = new ClientUserService();
    private SimpleUser simpleUser;
    private Boolean facebookAppConnected;
    private String facebookEmail;
    private String facebookSignedRequest;
    private Timer timer;
    private RegisterButtonDialog registerOrNicknameDialog;
    private LoginDialog loginDialog;

    public static ClientUserService getInstance() {
        return INSTANCE;
    }

    public void init() {
        checkFacebookAppConnected();

        if (!isRegistered()) {
            DialogManager.getInstance().addDialogListener(this);
            startTimer();
        }
    }

    public void cleanup() {
        stopTimer();
    }

    public void promptRegister() {
        try {
            if (isRegistered()) {
                return;
            }
            privatePromptRegister();
        } catch (Exception e) {
            ClientExceptionHandler.handleException("ClientUserService timer", e);
        }
    }

    public void promptLogin() {
        if (isRegistered()) {
            throw new IllegalStateException("ClientUserService.promptLogin(): user is already registered");
        }
        closeDialog();
        loginDialog = new LoginDialog();
        DialogManager.showDialog(loginDialog, DialogManager.Type.PROMPTLY);
    }

    public void logout() {
        if (!isRegisteredAndVerified()) {
            throw new IllegalStateException("ClientUserService.logout(): user is not logged in");
        }
        String text;
        if (checkFacebookAppConnected()) {
            text = ClientI18nHelper.CONSTANTS.logoutTextFacebook(ClientUserService.getInstance().getSimpleUser().getName());
        } else {
            text = ClientI18nHelper.CONSTANTS.logoutText(ClientUserService.getInstance().getSimpleUser().getName());
        }
        DialogManager.showDialog(new YesNoDialog(ClientI18nHelper.CONSTANTS.logout(),
                text,
                ClientI18nHelper.CONSTANTS.logout(),
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        if (checkFacebookAppConnected()) {
                            FacebookUtils.logout();
                        } else {
                            doLogout();
                        }
                    }
                },
                ClientI18nHelper.CONSTANTS.cancel(),
                null
        ), DialogManager.Type.PROMPTLY);
    }

    public void onFacebookLoggedout() {
        doLogout();
    }

    private void doLogout() {
        if (Connection.getMovableServiceAsync() != null) {
            Connection.getMovableServiceAsync().logout(new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {
                    ClientExceptionHandler.handleException("MovableServiceAsync.logout()", caught);
                }

                @Override
                public void onSuccess(Void result) {
                    Window.Location.replace(GwtCommon.getPredefinedUrl(CmsUtil.CmsPredefinedPage.HOME));
                }
            });
        }
    }

    private void startTimer() {
        stopTimer();
        timer = new Timer() {

            @Override
            public void run() {
                promptRegister();
            }
        };
        timer.schedule(Connection.getInstance().getGameInfo().getRegisterDialogDelay());
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void setFacebookAppConnected(boolean facebookAppConnected, String signedRequest) {
        this.facebookAppConnected = facebookAppConnected;
        facebookSignedRequest = signedRequest;
    }

    private void privatePromptRegister() {
        if (registerOrNicknameDialog != null) {
            registerOrNicknameDialog.close();
            registerOrNicknameDialog = null;
        }
        if (checkFacebookAppConnected()) {
            // Facebook app is connected
            if (facebookEmail == null) {
                FacebookUtils.readEmail();
            }
            registerOrNicknameDialog = new FacebookNickNameDialog();
        } else {
            // Facebook app not connected
            registerOrNicknameDialog = new RegisterDialog();
        }
        stopTimer();
        DialogManager.showDialog((Dialog) registerOrNicknameDialog, DialogManager.Type.PROMPTLY);
        registerOrNicknameDialog.setFocusOnRegisterButton();
    }

    public boolean isRegistered() {
        return simpleUser != null;
    }

    public boolean isRegisteredAndVerified() {
        return isRegistered() && simpleUser.isVerified();
    }

    public void setSimpleUser(SimpleUser simpleUser) {
        this.simpleUser = simpleUser;
    }

    public String getUserName() {
        if (simpleUser != null) {
            return simpleUser.getName();
        } else {
            return null;
        }
    }

    public SimpleUser getSimpleUser() {
        return simpleUser;
    }

    public void onUserPacket(UserPacket userPacket) {
        // Only sent for email verification
        SimpleUser oldSimpleUser = this.simpleUser;
        onRegisterStateChanged(userPacket.getSimpleUser());
        if ((oldSimpleUser == null || !oldSimpleUser.isVerified()) && simpleUser.isVerified()) {
            DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.registerThanks(), ClientI18nHelper.CONSTANTS.registerThanksLong()), DialogManager.Type.QUEUE_ABLE);
        }
    }

    @Override
    public void onDialogShown(Dialog dialog) {
        // Ignore
    }

    @Override
    public void onDialogHidden(Dialog dialog) {
        if (registerOrNicknameDialog != null && registerOrNicknameDialog.equals(dialog)) {
            registerOrNicknameDialog = null;
            if (!isRegistered()) {
                startTimer();
            }
        } else if (loginDialog != null && loginDialog.equals(dialog)) {
            loginDialog = null;
            if (!isRegistered()) {
                startTimer();
            }
        }
    }

    public void proceedNormalRegister(String nickname, String password, String confirmPassword, final String email) {
        if (Connection.getMovableServiceAsync() != null) {
            Connection.getMovableServiceAsync().register(nickname, password, confirmPassword, email, new AsyncCallback<SimpleUser>() {
                @Override
                public void onFailure(Throwable throwable) {
                    if (throwable instanceof UserAlreadyExistsException) {
                        DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.registrationFailed(), ClientI18nHelper.CONSTANTS.registrationUser()), DialogManager.Type.STACK_ABLE);
                    } else if (throwable instanceof PasswordNotMatchException) {
                        DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.registrationFailed(), ClientI18nHelper.CONSTANTS.registrationMatch()), DialogManager.Type.STACK_ABLE);
                    } else if (throwable instanceof EmailAlreadyExitsException) {
                        DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.registrationFailed(), ClientI18nHelper.CONSTANTS.registrationEmail(((EmailAlreadyExitsException) throwable).getEmail())), DialogManager.Type.STACK_ABLE);
                    } else {
                        ClientExceptionHandler.handleException(throwable);
                    }
                    registerOrNicknameDialog.enableRegisterButton();
                }

                @Override
                public void onSuccess(SimpleUser simpleUser) {
                    onRegisterStateChanged(simpleUser);
                    closeDialog();
                    DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.registerThanks(), ClientI18nHelper.CONSTANTS.registerConfirmationEmailSent(email)), DialogManager.Type.PROMPTLY);
                }
            });
        }
    }

    public void proceedFacebookRegister() {
        closeDialog();
        FacebookUtils.login();
    }

    public void onFacebookLoggedIn(String signedRequest) {
        facebookAppConnected = true;
        facebookSignedRequest = signedRequest;
        if (Connection.getMovableServiceAsync() != null) {
            Connection.getMovableServiceAsync().isFacebookUserRegistered(signedRequest, new AsyncCallback<Boolean>() {
                @Override
                public void onFailure(Throwable caught) {
                    ClientExceptionHandler.handleException("ClientUserService.onFacebookLoggedIn():isFacebookUserRegistered()", caught);
                }

                @Override
                public void onSuccess(Boolean result) {
                    if (result) {
                        if (Connection.getMovableServiceAsync() != null) {
                            Connection.getMovableServiceAsync().loginFacebookUser(facebookSignedRequest, new AsyncCallback<Void>() {
                                @Override
                                public void onFailure(Throwable caught) {
                                    ClientExceptionHandler.handleException("ClientUserService.onFacebookLoggedIn() loginFacebookUser", caught);
                                }

                                @Override
                                public void onSuccess(Void unused) {
                                    Window.Location.reload();
                                }
                            });
                        }
                    } else {
                        FacebookUtils.readEmail();
                        privatePromptRegister();
                    }
                }
            });
        }
    }

    public void onFacebookEmailReceived(String email) {
        facebookEmail = email;
    }

    public void onFacebookNicknameChosen(String nickName) {
        if (Connection.getMovableServiceAsync() != null) {
            Connection.getMovableServiceAsync().createAndLoginFacebookUser(facebookSignedRequest, nickName, facebookEmail, new AsyncCallback<SimpleUser>() {
                @Override
                public void onFailure(Throwable caught) {
                    if (caught instanceof UserAlreadyExistsException) {
                        DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.registrationFailed(), ClientI18nHelper.CONSTANTS.registrationUser()), DialogManager.Type.STACK_ABLE);
                    } else {
                        ClientExceptionHandler.handleException("ClientUserService.onFacebookNicknameChosen() createAndLoginFacebookUser", caught);
                    }
                    registerOrNicknameDialog.setFocusOnRegisterButton();
                }

                @Override
                public void onSuccess(SimpleUser simpleUser) {
                    onRegisterStateChanged(simpleUser);
                }
            });
        }
    }

    private void onRegisterStateChanged(SimpleUser simpleUser) {
        this.simpleUser = simpleUser;
        MenuBarCockpit.getInstance().updateUser();
    }

    private void closeDialog() {
        if (registerOrNicknameDialog != null) {
            registerOrNicknameDialog.close();
            registerOrNicknameDialog = null;
        }
    }

    public void proceedLogin(String name, String password) {
        if (Connection.getMovableServiceAsync() != null) {
            Connection.getMovableServiceAsync().login(name, password, new AsyncCallback<SimpleUser>() {
                @Override
                public void onFailure(Throwable caught) {
                    if (loginDialog != null) {
                        loginDialog.setLoginButtonEnabled();
                    }
                    if (caught instanceof LoginFailedException) {
                        DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.loginFailed(), ClientI18nHelper.CONSTANTS.loginFailedText()), DialogManager.Type.STACK_ABLE);
                    } else if (caught instanceof LoginFailedNotVerifiedException) {
                        DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.loginFailed(), ClientI18nHelper.CONSTANTS.loginFailedNotVerifiedText()), DialogManager.Type.STACK_ABLE);
                    } else {
                        ClientExceptionHandler.handleException("LoginDialog.setupPanel() login", caught);
                    }
                }

                @Override
                public void onSuccess(SimpleUser simpleUser) {
                    Window.Location.reload();
                }
            });
        }
    }

    private boolean checkFacebookAppConnected() {
        if (facebookAppConnected == null) {
            FacebookUtils.checkAppConnectionState();
            return false;
        } else {
            return facebookAppConnected;
        }
    }
}
