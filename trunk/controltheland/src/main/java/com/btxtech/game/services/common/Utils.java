/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.services.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * User: beat
 * Date: 27.07.2010
 * Time: 19:04:53
 */
public class Utils {
    // VM parameters
    public static final String TEST_MODE_PROPERTY = "testmode";
    public static final String TEST_MODE_NO_GAME_ENGINE = "noGameEngine";

    public static final String DELIMITER = ";";

    private static Log log = LogFactory.getLog(Utils.class);

    public static Collection<Integer> stringToIntegers(String s) {
        if (s == null) {
            return Collections.emptyList();
        }
        Collection<Integer> result = new ArrayList<Integer>();
        StringTokenizer st = new StringTokenizer(s, DELIMITER);
        while (st.hasMoreTokens()) {
            int id = Integer.parseInt(st.nextToken());
            result.add(id);
        }
        return result;
    }

    public static String integerToSting(Collection<Integer> integers) {
        if (integers == null || integers.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Iterator<Integer> iterator = integers.iterator(); iterator.hasNext();) {
            Integer integer = iterator.next();
            builder.append(integer);
            if (iterator.hasNext()) {
                builder.append(DELIMITER);
            }
        }
        return builder.toString();
    }

    public static boolean isTestModeStatic() {
        try {
            return System.getProperty(TEST_MODE_PROPERTY) != null && Boolean.parseBoolean(System.getProperty(TEST_MODE_PROPERTY));
        } catch (Throwable t) {
            log.error("", t);
            return false;
        }
    }
}
