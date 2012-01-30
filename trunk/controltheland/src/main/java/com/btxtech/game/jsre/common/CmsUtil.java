package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;

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
    public static final String TARGET_BLANK = "_blank";

    // If the predefined page urls are not yet available in the client
    public static final String PREDEFINED_PAGE_URL_NO_HTML_5 = getUrl4CmsPage(NO_HTML5_BROWSER_PAGE_STRING_ID);

    // CMS urls
    public static final String CHILD_ID = "childId";
    public static final String SECTION_ID = "sec";
    public static final String UNIT_SECTION = "units";
    public static final String LEVEL_SECTION = "level";
    public static final String LEVEL_TASK_SECTION = "leveltask";

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
        NO_HTML5_BROWSER,
        NOT_FOUND
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

    public static void getUrl4ItemTypePage(StringBuilder builder, ItemType itemType, String text) {
        builder.append("<a href=\"/");
        builder.append(MOUNT_GAME_CMS);
        builder.append('/');
        builder.append(SECTION_ID);
        builder.append('/');
        builder.append(UNIT_SECTION);
        builder.append('/');
        builder.append(CHILD_ID);
        builder.append('/');
        builder.append(itemType.getId());
        builder.append("\" target=\"_blank\" >");
        builder.append(text);
        builder.append("</a>");
    }

//    public static String getUrl4LevelPage(LevelScope levelScope, String text) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("<a href=\"/");
//        builder.append(MOUNT_GAME_CMS);
//        builder.append('/');
//        builder.append(SECTION_ID);
//        builder.append('/');
//        builder.append(LEVEL_SECTION);
//        builder.append('/');
//        builder.append(CHILD_ID);
//        builder.append('/');
//        builder.append(levelScope.getId());
//        builder.append("\" target=\"_blank\" style=\"color: white\">");
//        builder.append(text);
//        builder.append("</a>");
//        return builder.toString();
//    }

}
