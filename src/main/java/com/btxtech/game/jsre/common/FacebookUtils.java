package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ClientPlanetServices;
import com.btxtech.game.jsre.client.ClientUserService;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
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

    public static native void init() /*-{
        $wnd.RazFacebookUtilFbCallbackInvite = $entry(@com.btxtech.game.jsre.common.FacebookUtils::fbUiCallBackInvite(Lcom/google/gwt/core/client/JavaScriptObject;));
        $wnd.RazFacebookUtilFbCallbackLogin = $entry(@com.btxtech.game.jsre.common.FacebookUtils::fbUiCallBackLogin(Ljava/lang/String;));
        $wnd.RazFacebookUtilFbCallbackPostToFeed = $entry(@com.btxtech.game.jsre.common.FacebookUtils::fbUiCallBackPostToFeed(Lcom/google/gwt/core/client/JavaScriptObject;));
        $wnd.RazFacebookUtilFbCallCheckAppConnectionState = $entry(@com.btxtech.game.jsre.common.FacebookUtils::fbUiCallBackCheckAppConnectionState(ZLjava/lang/String;));
        $wnd.RazFacebookUtilFbCallbackGetEmail = $entry(@com.btxtech.game.jsre.common.FacebookUtils::fbUiCallBackReadEmail(Ljava/lang/String;));
        $wnd.RazFacebookUtilFbCallbackLogout = $entry(@com.btxtech.game.jsre.common.FacebookUtils::fbUiCallLogout());
    }-*/;

    public static void invite() {
        if (checkFbApiLoaded("invite")) {
            nativeInvite(ClientI18nHelper.CONSTANTS.fbInviteTitle(), ClientI18nHelper.CONSTANTS.fbInviteMessage());
        }
    }

    native private static void nativeInvite(String title, String message)/*-{
        $wnd.FB.ui({method: 'apprequests',
                title: title,
                message: message},
            $wnd.RazFacebookUtilFbCallbackInvite);
    }-*/;

    public static void fbUiCallBackInvite(JavaScriptObject object) {
        if (object != null) {
            try {
                JSONObject jsonObject = new JSONObject(object);
                String fbRequestId = jsonObject.get("request").isString().stringValue();
                JSONArray userIds = jsonObject.get("to").isArray();
                Collection<String> fbUserIds = new ArrayList<String>();
                for (int i = 0; i < userIds.size(); i++) {
                    fbUserIds.add(userIds.get(i).isString().stringValue());
                }
                Connection.getInstance().sendFacebookInvite(fbRequestId, fbUserIds);
            } catch (Exception e) {
                ClientExceptionHandler.handleException("FacebookUtils invite handle return value", e);
            }
        }
    }

    public static void checkAppConnectionState() {
        if (checkFbApiLoaded("checkAppConnectionState")) {
            nativeCheckAppConnectionState();
        } else {
            ClientUserService.getInstance().setFacebookAppConnected(false, null);
        }
    }

    native private static void nativeCheckAppConnectionState()/*-{
        $wnd.FB.getLoginStatus(function (response) {
            if (response.status === 'connected') {
                $wnd.RazFacebookUtilFbCallCheckAppConnectionState(true, response.authResponse.signedRequest);
            } else {
                $wnd.RazFacebookUtilFbCallCheckAppConnectionState(false, null);
            }
        }, true);
    }-*/;

    public static void fbUiCallBackCheckAppConnectionState(boolean connected, String signedRequest) {
        ClientUserService.getInstance().setFacebookAppConnected(connected, signedRequest);
    }

    public static void login() {
        if (checkFbApiLoaded("login")) {
            nativeLogin();
        }
    }

    native private static void nativeLogin()/*-{
        $wnd.FB.login(function (response1) {
            if (response1.authResponse) {
                $wnd.RazFacebookUtilFbCallbackLogin(response1.authResponse.signedRequest);
            }
        }, {scope: 'email'});
    }-*/;

    public static void fbUiCallBackLogin(String signedRequest) {
        ClientUserService.getInstance().onFacebookLoggedIn(signedRequest);
    }


    public static void logout() {
        if (checkFbApiLoaded("logout")) {
            nativeLogout();
        }
    }

    native private static void nativeLogout() /*-{
        $wnd.FB.logout(function (response) {
            $wnd.RazFacebookUtilFbCallbackLogout();
        });
    }-*/;

    public static void fbUiCallLogout() {
        ClientUserService.getInstance().onFacebookLoggedout();
    }

    public static void readEmail() {
        if (checkFbApiLoaded("readEmail")) {
            nativeReadEmail();
        }
    }

    native private static void nativeReadEmail() /*-{
        $wnd.FB.api('/me', function (response2) {
            $wnd.RazFacebookUtilFbCallbackGetEmail(response2.email);
        });
    }-*/;

    public static void fbUiCallBackReadEmail(String email) {
        ClientUserService.getInstance().onFacebookEmailReceived(email);
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
                // TODO localise
                nativePostToFeed(ClientUserService.getInstance().getUserName() + " completed a level task on Razarion",
                        "'" + questInfo.getTitle() + "' completed on " + ClientPlanetServices.getInstance().getPlanetInfo().getName(),
                        "Build your base, attack other players and gather resources. A browser multiplayer real-time strategy game.",
                        RAZARION_APP_URL, // TODO is this needed?
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
                // TODO localise
                nativePostToFeed(ClientUserService.getInstance().getUserName() + " leveled up on Razarion",
                        "Reached level " + levelScope.getNumber() + " on " + ClientPlanetServices.getInstance().getPlanetInfo().getName(),
                        "Build your base, attack other players and gather resources. A browser multiplayer real-time strategy game.",
                        RAZARION_APP_URL, // TODO is this needed?
                        ImageHandler.getFacebookFeedImageUrl());
            }
        } catch (Exception e) {
            ClientExceptionHandler.handleException("FacebookUtils.postToFeedLevelUp()", e);

        }
    }

    native private static void nativePostToFeed(String name, String caption, String description, String link, String picture)/*-{
        $wnd.FB.getLoginStatus(function (response) {
            if (response.status === 'connected') {
                var obj = {
                    method: 'feed',
                    link: link,
                    picture: picture,
                    name: name,
                    caption: caption,
                    description: description
                };
                $wnd.FB.ui(obj, $wnd.RazFacebookUtilFbCallbackPostToFeed);
            }
        });
    }-*/;

    public static void fbUiCallBackPostToFeed(JavaScriptObject object) {
        GwtCommon.sendDebug(GwtCommon.DEBUG_FACEBOOK_POST_FEED, new JSONObject(object).toString());
    }


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

    public static void callConversationRealRealGamePixel() {
        try {
            callConversionTrackingPixel("6008265624221", "0.00", "USD");
        } catch (Exception e) {
            ClientExceptionHandler.handleException("FacebookUtils.callConversationRealRealGamePixel()", e);
        }
    }

    public static void callConversationTrackingOnTaskDone() {
        try {
            callConversionTrackingPixel("6008265623621", "0.00", "USD");
        } catch (Exception e) {
            ClientExceptionHandler.handleException("FacebookUtils.callConversationTrackingOnTaskDone()", e);
        }
    }

    public static void callConversationTrackingOnLevelPromotionDone() {
        try {
            callConversionTrackingPixel("6008265539021", "0.00", "USD");
        } catch (Exception e) {
            ClientExceptionHandler.handleException("FacebookUtils.callConversationTrackingOnLevelPromotionDone()", e);
        }
    }

    private static void callConversionTrackingPixel(String pixelId, String value, String currency) {
        UrlBuilder builder = new UrlBuilder();
        builder.setProtocol("https");
        builder.setHost("www.facebook.com");
        builder.setPath("offsite_event.php");
        builder.setParameter("id", pixelId);
        builder.setParameter("value", value);
        builder.setParameter("currency", currency);
        builder.setParameter("preventCaching", Long.toString(System.currentTimeMillis()));
        loadConversionTrackingPixel(builder.buildString());
    }

    private static native void loadConversionTrackingPixel(String src) /*-{
        var img = new Image();
        img.src = src;
    }-*/;
}
