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

import org.apache.wicket.util.crypt.Base64;

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
