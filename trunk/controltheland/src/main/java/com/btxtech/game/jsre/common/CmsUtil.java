package com.btxtech.game.jsre.common;

/**
 * User: beat
 * Date: 05.08.2011
 * Time: 15:55:09
 */
public class CmsUtil {
    public static final String MOUNT_GAME_CMS = "game_cms";
    public static final String MOUNT_GAME = "game_run";
    public static final String ID = "page";
    public static final String NO_HTML5_BROWSER_PAGE_STRING_ID = "NoHtml5Browser";

    public static final String TARGET_GAME = "game";
    public static final String TARGET_USER_PAGE = "userPage";
    public static final String TARGET_USER_INFO = "info";
    public static final String TARGET_SELF = "_self";

    // If the predefined page urls are not yet available in the client
    public static final String PREDEFINED_PAGE_URL_NO_HTML_5 = getUrl4CmsPage(NO_HTML5_BROWSER_PAGE_STRING_ID);

    /**
     * User: beat
     * Date: 05.08.2011
     * Time: 11:21:33
     */
    public static enum CmsPredefinedPage {
        HOME,
        USER_PAGE,
        REGISTER,
        MESSAGE,
        HIGH_SCORE,
        INFO,
        NO_HTML5_BROWSER
    }

    public static String getUrl4CmsPage(String id) {
        StringBuilder builder = new StringBuilder();
        builder.append(MOUNT_GAME_CMS);
        builder.append('/');
        builder.append(ID);
        builder.append('/');
        builder.append(id);
        return builder.toString();
    }

}
