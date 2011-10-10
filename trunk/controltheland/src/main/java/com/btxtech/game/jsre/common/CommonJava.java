package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.client.common.Index;

import java.util.Iterator;
import java.util.List;

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
}
