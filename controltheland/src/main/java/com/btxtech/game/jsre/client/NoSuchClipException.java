package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.common.info.CommonClipInfo;

/**
 * User: beat
 * Date: 07.10.12
 * Time: 13:51
 */
public class NoSuchClipException extends Exception {
    public NoSuchClipException(CommonClipInfo.Type explosion) {
        super("No common clip: " + explosion);
    }

    public NoSuchClipException(int clipId) {
        super("No clip for id: " + clipId);
    }
}
