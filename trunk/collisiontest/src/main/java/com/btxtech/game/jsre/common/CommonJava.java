package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.client.common.Index;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import com.google.gwt.i18n.shared.DateTimeFormat;

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

    public static boolean compareExceptionsDeep(Throwable t1, Throwable t2) {
        if (compareExceptions(t1, t2)) {
            if (t1.getCause() != null && t2.getCause() != null) {
                return compareExceptionsDeep(t1.getCause(), t2.getCause());
            }
            return !(t1.getCause() != null && t2.getCause() == null) && !(t1.getCause() == null && t2.getCause() != null);
        } else {
            return false;
        }
    }

    public static boolean compareExceptions(Throwable t1, Throwable t2) {
        return equals(t1.getMessage(), t2.getMessage()) && compareStackTrace(t1.getStackTrace(), t2.getStackTrace());
    }

    public static boolean compareStackTrace(StackTraceElement[] sA1, StackTraceElement[] sA2) {
        if (sA1 == sA2) {
            return true;
        }
        if (sA1 == null || sA2 == null) {
            return false;
        }

        int length = sA1.length;
        if (sA2.length != length) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            StackTraceElement s1 = sA1[i];
            StackTraceElement s2 = sA2[i];
            if (!compareStackTraceElement(s1, s2)) {
                return false;
            }
        }

        return true;
    }


    private static boolean compareStackTraceElement(StackTraceElement s1, StackTraceElement s2) {
        // GWT does not override StackTraceElement.equals()
        return s1 == s2 || s1 != null && s2 != null && equals(s1.toString(), s2.toString());
    }

    public static String pathToDestinationAsString(List<Index> pathToDestination) {
        StringBuilder builder = new StringBuilder();
        if (pathToDestination != null) {
            builder.append("{");
            Iterator<Index> iterator = pathToDestination.iterator();
            while (iterator.hasNext()) {
                Index index = iterator.next();
                builder.append(index.toString());
                if (iterator.hasNext()) {
                    builder.append(", ");
                }
            }
            builder.append("}");
        } else {
            builder.append("{-}");
        }
        return builder.toString();
    }

    public static <T> T getFirst(Iterable<T> iterable) {
        return iterable.iterator().next();
    }

    public static <T> List<T> saveArrayListCopy(List<T> list) {
        if (list != null) {
            return new ArrayList<T>(list);
        } else {
            return null;
        }
    }

    public static boolean equals(Object o1, Object o2) {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:[a-zA-Z]{2,6})$");
    }
}
