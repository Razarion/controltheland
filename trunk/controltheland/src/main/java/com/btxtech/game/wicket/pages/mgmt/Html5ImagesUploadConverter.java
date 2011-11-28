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

package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import org.apache.wicket.util.crypt.Base64;

import javax.swing.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * User: beat
 * Date: 14.12.2009
 * Time: 22:12:07
 */
public class Html5ImagesUploadConverter {
    public static void convertAndSetImages(String dataString, DbBaseItemType dbBaseItemType) {
        if (dataString == null) {
            return;
        }
        Map<Integer, Double> angelMap = getAngelMap(dbBaseItemType.getItemTypeImageCrud().readDbChildren());
        dbBaseItemType.getItemTypeImageCrud().deleteAllChildren();

        StringTokenizer tokenizer = new StringTokenizer(dataString, ",");
        int count = 0;
        while (tokenizer.hasMoreElements()) {
            DbItemTypeImage itemTypeImage = dbBaseItemType.getItemTypeImageCrud().createDbChild();
            itemTypeImage.setNumber(extractNumber(tokenizer.nextToken()));
            itemTypeImage.setContentType(extractContentType(tokenizer.nextToken()));
            byte[] imageData = Base64.decodeBase64(tokenizer.nextToken().getBytes());
            if (angelMap.containsKey(itemTypeImage.getNumber())) {
                itemTypeImage.setAngel(angelMap.get(itemTypeImage.getNumber()));
            }
            itemTypeImage.setData(imageData);
            if (count == 0) {
                ImageIcon image = new ImageIcon(imageData);
                dbBaseItemType.setImageWidth(image.getIconWidth());
                dbBaseItemType.setImageHeight(image.getIconHeight());
            }
            count++;
        }
    }

    private static Map<Integer, Double> getAngelMap(Collection<DbItemTypeImage> dbItemTypeImages) {
        Map<Integer, Double> angelMap = new HashMap<Integer, Double>();
        for (DbItemTypeImage dbItemTypeImage : dbItemTypeImages) {
            angelMap.put(dbItemTypeImage.getNumber(), dbItemTypeImage.getAngel());
        }
        return angelMap;
    }

    private static String extractContentType(String rawString) {
        rawString = rawString.substring(5);
        rawString = rawString.substring(0, rawString.indexOf(";base64"));
        return rawString;
    }

    private static int extractNumber(String fileName) {
        String numberString = fileName.substring(fileName.lastIndexOf("_") + 1, fileName.lastIndexOf("."));
        return Integer.parseInt(numberString);
    }
}
