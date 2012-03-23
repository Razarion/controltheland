package com.btxtech.game.services.common;

/**
 * User: beat
 * Date: 20.03.2012
 * Time: 14:49:46
 */
public class ImageHolder {
    private byte[] data;
    private String contentType;

    public ImageHolder(byte[] data, String contentType) {
        this.data = data;
        this.contentType = contentType;
    }

    public byte[] getData() {
        return data;
    }

    public String getContentType() {
        return contentType;
    }
}
