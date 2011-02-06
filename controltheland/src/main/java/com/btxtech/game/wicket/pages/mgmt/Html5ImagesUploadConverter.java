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

import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.wicket.util.crypt.Base64;

/**
 * User: beat
 * Date: 14.12.2009
 * Time: 22:12:07
 */
public class Html5ImagesUploadConverter {
    private Set<DbItemTypeImage> images = new HashSet<DbItemTypeImage>();

    public Html5ImagesUploadConverter(String dataString, DbItemType dbItemType) {
        if (dataString == null) {
            return;
        }
        StringTokenizer tokenizer = new StringTokenizer(dataString, ",");
        while (tokenizer.hasMoreElements()) {
            DbItemTypeImage itemTypeImage = new DbItemTypeImage();
            itemTypeImage.setItemType(dbItemType);
            itemTypeImage.setNumber(extractNumber(tokenizer.nextToken()));
            itemTypeImage.setContentType(extractContentType(tokenizer.nextToken()));
            itemTypeImage.setData(Base64.decodeBase64(tokenizer.nextToken().getBytes()));
            images.add(itemTypeImage);
        }
    }

    private String extractContentType(String rawString) {
        rawString = rawString.substring(5);
        rawString = rawString.substring(0, rawString.indexOf(";base64"));
        return rawString;
    }

    private int extractNumber(String fileName) {
        String numberString = fileName.substring(fileName.lastIndexOf("_") + 1, fileName.lastIndexOf("."));
        return Integer.parseInt(numberString);
    }

    public Set<DbItemTypeImage> getImages() {
        return images;
    }

    public boolean isEmpty() {
        return images.isEmpty();
    }

    public DbItemTypeImage getFirst() {
        return images.iterator().next();
    }
}
