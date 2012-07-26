package com.btxtech.game.jsre.common;

import com.google.gwt.core.client.JavaScriptObject;

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

    public static native void exportStaticMethod() /*-{
        $wnd.RazFacebookUtilFbCallback = $entry(@com.btxtech.game.jsre.common.FacebookUtils::fbUiCallBack(Lcom/google/gwt/core/client/JavaScriptObject;));
    }-*/;

}
