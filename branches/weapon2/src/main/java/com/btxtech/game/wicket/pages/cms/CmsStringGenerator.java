package com.btxtech.game.wicket.pages.cms;

/**
 * User: beat
 * Date: 24.07.2011
 * Time: 21:13:31
 */
public class CmsStringGenerator {
    public static final String REPLACEMENT = "\\$";

    public static String createNumberString(int value, String string0, String string1, String stringN) {
        if (value == 0) {
            return string0;
        } else if (value == 1) {
            return string1;
        } else {
            return stringN.replaceAll(REPLACEMENT, Integer.toString(value));
        }
    }
}
