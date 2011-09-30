package com.btxtech.game.jsre.common;

/**
 * User: beat
 * Date: 30.09.2011
 * Time: 10:13:56
 */
public class CommonJava {
    public static Throwable getMostInnerThrowable(Throwable t) {
        if (t.getCause() == null) {
            return t;
        } else if (t.getCause() == t) {
            return t;
        } else {
            return getMostInnerThrowable(t.getCause());
        }
    }
}
