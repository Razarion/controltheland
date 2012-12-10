package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.NickNameDialog;
import com.btxtech.game.jsre.client.dialogs.RegisterDialog;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.logging.Logger;

/**
 * User: beat
 * Date: 25.07.12
 * Time: 17:15
 */
public class FacebookUtils {
    private static Logger log = Logger.getLogger(FacebookUtils.class.getName());

    public static void invite() {
        nativeInvite("Play Razarion with me!", "Been having a blast playing Razarion, come check it out.");
    }

    native private static void nativeInvite(String title, String message)/*-{
        $wnd.FB.ui({method:'apprequests',
                    title:title,
                    message:message},
                $wnd.RazFacebookUtilFbCallback);
    }-*/;

    public static void fbUiCallBack(JavaScriptObject object) {
        log.severe("FacebookUtils.fbUiCallBack() " + object);
    }

    public static void login(RegisterDialog registerDialog) {
        nativeLogin(registerDialog);
    }

    native private static void nativeLogin(RegisterDialog registerDialog)/*-{
        $wnd.FB.getLoginStatus(function (response) {
            if (response.status === 'connected') {
                $wnd.RazFacebookUtilFbCallbackLogin(response.authResponse.signedRequest, registerDialog);
            } else {
                $wnd.FB.login(function (response) {
                    if (response.authResponse) {
                        $wnd.RazFacebookUtilFbCallbackLogin(response.authResponse.signedRequest, registerDialog);
                    }
                });
            }
        });
    }-*/;

    public static void fbUiCallBackLoginResponse(final String signedRequest, final RegisterDialog registerDialog) {
        if (Connection.getMovableServiceAsync() != null) {
            Connection.getMovableServiceAsync().isFacebookUserRegistered(signedRequest, new AsyncCallback<Boolean>() {
                @Override
                public void onFailure(Throwable caught) {
                    ClientExceptionHandler.handleException("FacebookUtils.fbUiCallBackLoginResponse()", caught);
                }

                @Override
                public void onSuccess(Boolean result) {
                    if (result) {
                        if (Connection.getMovableServiceAsync() != null) {
                            Connection.getMovableServiceAsync().loginFacebookUser(signedRequest, new AsyncCallback<Void>() {
                                @Override
                                public void onFailure(Throwable caught) {
                                    ClientExceptionHandler.handleException("FacebookUtils.fbUiCallBackLoginResponse() loginFacebookUser", caught);
                                }

                                @Override
                                public void onSuccess(Void unused) {
                                    Window.Location.reload();
                                }
                            });
                        }
                    } else {
                        DialogManager.showDialog(new NickNameDialog(signedRequest, registerDialog), DialogManager.Type.STACK_ABLE);
                    }
                }
            });
        }
    }

    public static native void exportStaticMethod() /*-{
        $wnd.RazFacebookUtilFbCallback = $entry(@com.btxtech.game.jsre.common.FacebookUtils::fbUiCallBack(Lcom/google/gwt/core/client/JavaScriptObject;));
        $wnd.RazFacebookUtilFbCallbackLogin = $entry(@com.btxtech.game.jsre.common.FacebookUtils::fbUiCallBackLoginResponse(Ljava/lang/String;Lcom/btxtech/game/jsre/client/dialogs/RegisterDialog;));
    }-*/;

}
