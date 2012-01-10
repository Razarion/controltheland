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

import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import org.apache.wicket.util.crypt.Base64;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * User: beat
 * Date: 14.12.2009
 * Time: 22:12:07
 */
public class Html5ImagesUploadConverter {
    private static final String START = "data:";
    private static final String MIME_ENCODING_DELIMITER = ";";
    private static final String ENCODING_CONTENT_DELIMITER = ",";
    private static final String BASE_64 = "base64";

    public static class Package {
        private String mime;
        private String base64Data;

        public Package(String mime, String base64Data) {
            this.mime = mime;
            this.base64Data = base64Data;
        }

        public String getMime() {
            return mime;
        }

        public String getBase64Data() {
            return base64Data;
        }

        public byte[] convertBase64ToBytes() {
            return Base64.decodeBase64(base64Data.getBytes());
        }
    }

    public static void convertAndSetImages(String dataString, DbBaseItemType dbBaseItemType) {
        if (dataString == null) {
            return;
        }
        Map<Integer, Double> oldAngelMap = getAngelMap(dbBaseItemType.getItemTypeImageCrud().readDbChildren());
        dbBaseItemType.getItemTypeImageCrud().deleteAllChildren();

        StringTokenizer tokenizer = new StringTokenizer(dataString, ",");
        List<DbItemTypeImage> dbItemTypeImages = new ArrayList<DbItemTypeImage>();
        while (tokenizer.hasMoreElements()) {
            DbItemTypeImage itemTypeImage = dbBaseItemType.getItemTypeImageCrud().createDbChild();
            itemTypeImage.setNumber(extractNumber(tokenizer.nextToken()));
            itemTypeImage.setContentType(extractContentType(tokenizer.nextToken()));
            byte[] imageData = Base64.decodeBase64(tokenizer.nextToken().getBytes());
            itemTypeImage.setData(imageData);
            if (dbItemTypeImages.isEmpty()) {
                ImageIcon image = new ImageIcon(imageData);
                dbBaseItemType.setImageWidth(image.getIconWidth());
                dbBaseItemType.setImageHeight(image.getIconHeight());
            }
            dbItemTypeImages.add(itemTypeImage);
        }

        if (oldAngelMap.size() == dbItemTypeImages.size()) {
            for (DbItemTypeImage dbItemTypeImage : dbItemTypeImages) {
                if (oldAngelMap.containsKey(dbItemTypeImage.getNumber())) {
                    dbItemTypeImage.setAngel(oldAngelMap.get(dbItemTypeImage.getNumber()));
                }
            }
        } else {
            double step = MathHelper.ONE_RADIANT / (double) dbItemTypeImages.size();
            Collections.sort(dbItemTypeImages, new Comparator<DbItemTypeImage>() {
                @Override
                public int compare(DbItemTypeImage o1, DbItemTypeImage o2) {
                    return o1.getNumber() - o2.getNumber();
                }
            });
            for (int i = 0; i < dbItemTypeImages.size(); i++) {
                dbItemTypeImages.get(i).setAngel(step * (double) i);
            }
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

    public static Package convertInlineImage(String inlineImage) {
        if (!inlineImage.startsWith(START)) {
            throw new IllegalArgumentException("Inline image does not start with: " + START);
        }
        int mimeIndex = inlineImage.indexOf(MIME_ENCODING_DELIMITER, START.length());
        if (mimeIndex < 0) {
            throw new IllegalArgumentException("Inline image does have the MIME encoding delimiter: " + MIME_ENCODING_DELIMITER);
        }
        String mime = inlineImage.substring(START.length(), mimeIndex);
        int contentIndex = inlineImage.indexOf(ENCODING_CONTENT_DELIMITER, mimeIndex);
        if (contentIndex < 0) {
            throw new IllegalArgumentException("Inline image does have the encoding content delimiter: " + ENCODING_CONTENT_DELIMITER);
        }
        String encoding = inlineImage.substring(mimeIndex + 1, contentIndex);
        if (!encoding.equalsIgnoreCase(BASE_64)) {
            throw new IllegalArgumentException("Encoding of inline image not supported: " + encoding);
        }
        return new Package(mime, inlineImage.substring(contentIndex + 1));
    }
}
