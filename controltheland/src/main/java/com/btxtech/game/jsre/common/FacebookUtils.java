package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientPlanetServices;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.NickNameDialog;
import com.btxtech.game.jsre.client.dialogs.RegisterDialog;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
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
    private static final String RAZARION_APP_URL = "https://apps.facebook.com/razarion/";
    private static Logger log = Logger.getLogger(FacebookUtils.class.getName());
    private static boolean isLoadedChecked = false;
    private static boolean isLoaded = false;

    public static void invite() {
        if (checkFbApiLoaded("invite")) {
            nativeInvite("Play Razarion with me!", "Been having a blast playing Razarion, come check it out.");
        }
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
        if (checkFbApiLoaded("login")) {
            nativeLogin(registerDialog);
        }
    }

    native private static void nativeLogin(RegisterDialog registerDialog)/*-{
        $wnd.FB.getLoginStatus(function (response) {
            if (response.status === 'connected') {
                $wnd.RazFacebookUtilFbCallbackLogin(response.authResponse.signedRequest, registerDialog);
            } else {
                $wnd.FB.login(function (response1) {
                    if (response1.authResponse) {
                        $wnd.FB.api('/me', function (response2) {
                            $wnd.RazFacebookUtilFbCallbackLogin(response1.authResponse.signedRequest, response2.email, registerDialog);
                        });
                    }
                }, {scope:'email'});
            }
        });
    }-*/;

    public static void fbUiCallBackLoginResponse(final String signedRequest, final String email, final RegisterDialog registerDialog) {
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
                        DialogManager.showDialog(new NickNameDialog(signedRequest, email, registerDialog), DialogManager.Type.STACK_ABLE);
                    }
                }
            });
        }
    }

    public static void postToFeedLevelTaskDone(QuestInfo questInfo) {
        try {
            if (questInfo == null) {
                return;
            }
            if (Connection.getInstance().getGameEngineMode() != GameEngineMode.SLAVE) {
                return;
            }
            if (checkFbApiLoaded("postToFeedLevelTaskDone")) {
                nativePostToFeed(Connection.getInstance().getUserName() + " completed a level task on Razarion",
                        "'" + questInfo.getTitle() + "' completed on " + ClientPlanetServices.getInstance().getPlanetInfo().getName(),
                        "Build your base, attack other players and gather resources. A browser multiplayer real-time strategy game.",
                        GwtCommon.getPredefinedUrl(CmsUtil.CmsPredefinedPage.FACEBOOK_START),
                        RAZARION_APP_URL,
                        ImageHandler.getFacebookFeedImageUrl());
            }
        } catch (Exception e) {
            ClientExceptionHandler.handleException("FacebookUtils.postToFeedLevelTaskDone()", e);
        }
    }

    public static void postToFeedLevelUp(LevelScope levelScope) {
        try {
            if (levelScope == null) {
                return;
            }
            if (Connection.getInstance().getGameEngineMode() != GameEngineMode.SLAVE) {
                return;
            }
            if (checkFbApiLoaded("postToFeedLevelUp")) {
                nativePostToFeed(Connection.getInstance().getUserName() + " leveled up on Razarion",
                        "Reached level " + levelScope.getNumber() + " on " + ClientPlanetServices.getInstance().getPlanetInfo().getName(),
                        "Build your base, attack other players and gather resources. A browser multiplayer real-time strategy game.",
                        GwtCommon.getPredefinedUrl(CmsUtil.CmsPredefinedPage.FACEBOOK_START),
                        RAZARION_APP_URL,
                        ImageHandler.getFacebookFeedImageUrl());
            }
        } catch (Exception e) {
            ClientExceptionHandler.handleException("FacebookUtils.postToFeedLevelUp()", e);

        }
    }

    native private static void nativePostToFeed(String name, String caption, String description, String redirectUri, String link, String picture)/*-{
        $wnd.FB.getLoginStatus(function (response) {
            if (response.status === 'connected') {
                var obj = {
                    method:'feed',
                    redirect_uri:redirectUri,
                    link:link,
                    picture:picture,
                    name:name,
                    caption:caption,
                    description:description
                };
                $wnd.FB.ui(obj, $wnd.RazFacebookUtilFbCallbackPostToFeed);
            }
        });
    }-*/;

    public static void fbUiCallBackPostToFeed(JavaScriptObject object) {
        log.severe("FacebookUtils.fbUiCallBackPostToFeed() " + object);
    }


    public static native void exportStaticMethod() /*-{
        $wnd.RazFacebookUtilFbCallback = $entry(@com.btxtech.game.jsre.common.FacebookUtils::fbUiCallBack(Lcom/google/gwt/core/client/JavaScriptObject;));
        $wnd.RazFacebookUtilFbCallbackLogin = $entry(@com.btxtech.game.jsre.common.FacebookUtils::fbUiCallBackLoginResponse(Ljava/lang/String;Ljava/lang/String;Lcom/btxtech/game/jsre/client/dialogs/RegisterDialog;));
        $wnd.RazFacebookUtilFbCallbackPostToFeed = $entry(@com.btxtech.game.jsre.common.FacebookUtils::fbUiCallBackPostToFeed(Lcom/google/gwt/core/client/JavaScriptObject;));
    }-*/;


    private static boolean checkFbApiLoaded(String debugInfo) {
        if (!isLoadedChecked) {
            isLoaded = nativeCheckFbApiLoaded();
            isLoadedChecked = true;
            if (!isLoaded) {
                log.warning("FacebookUtils.checkFbApiLoaded() Facebook API not loaded: " + debugInfo);
            }
        }
        return isLoaded;
    }

    native private static boolean nativeCheckFbApiLoaded()/*-{
        return $wnd.FB != undefined && typeof $wnd.FB.getLoginStatus == 'function';
    }-*/;
}
