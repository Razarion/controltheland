package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.common.info.PreloadedImageSpriteMapInfo;

/**
 * User: beat
 * Date: 07.10.12
 * Time: 13:51
 */
public class NoSuchClipException extends Exception {
    public NoSuchClipException(PreloadedImageSpriteMapInfo.Type explosion) {
        super("No common clip: " + explosion);
    }

    public NoSuchClipException(int clipId) {
        super("No clip for id: " + clipId);
    }

    public NoSuchClipException(String s) {
        super(s);
    }
}
