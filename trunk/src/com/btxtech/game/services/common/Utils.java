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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.StringTokenizer;

/**
 * User: beat
 * Date: 27.07.2010
 * Time: 19:04:53
 */
public class Utils {
    public static final String DELIMITER = ";";

    public static Collection<Integer> stringToIntegers(String s) {
        if(s == null) {
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
}
