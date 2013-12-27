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

package com.btxtech.game.wicket.pages.mgmt.items;

import com.btxtech.game.services.item.itemType.DbBaseItemType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * User: beat
 * Date: 19.05.2010
 * Time: 12:43:49
 */
public class ItemsUtil {
    public static final String DELIMITER = ";";

    public static String itemTypesToString(Collection<DbBaseItemType> dbBaseItemTypes) {
        if (dbBaseItemTypes == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (DbBaseItemType baseItemType : dbBaseItemTypes) {
            stringBuilder.append(baseItemType.getId());
            stringBuilder.append(DELIMITER);
        }
        return stringBuilder.toString();
    }

    public static Set<DbBaseItemType> stringToItemTypes(String itemsString, Collection<DbBaseItemType> dbBaseItemTypes) {
        if (itemsString == null) {
            return null;
        }
        Set<DbBaseItemType> result = new HashSet<>();
        StringTokenizer st = new StringTokenizer(itemsString, DELIMITER);
        while (st.hasMoreTokens()) {
            int id = Integer.parseInt(st.nextToken());
            result.add(getItemType4Id(id, dbBaseItemTypes));
        }
        return result;
    }

    public static DbBaseItemType getItemType4Id(int id, Collection<DbBaseItemType> dbBaseItemTypes) {
        for (DbBaseItemType dbBaseItemType : dbBaseItemTypes) {
            if (dbBaseItemType.getId() == id) {
                return dbBaseItemType;
            }
        }
        throw new IllegalArgumentException("Item type does not exist: " + id);
    }
}
