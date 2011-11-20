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

package com.btxtech.game.jsre.client;

import com.allen_sauer.gwt.voices.client.Sound;
import com.allen_sauer.gwt.voices.client.SoundController;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 01.01.2010
 * Time: 20:32:53
 */
public class SoundHandler {
    private static final SoundHandler INSTANCE = new SoundHandler();
    private SoundController soundController = new SoundController();
    private HashMap<BaseItemType, Sound> sounds = new HashMap<BaseItemType, Sound>();
    private Logger log = Logger.getLogger(SoundHandler.class.getName());
    private boolean logCrash = true;

    /**
     * Singleton
     */
    private SoundHandler() {
    }

    public void playSound(BaseItemType baseItemType) {
        Sound sound = sounds.get(baseItemType);
        if (sound == null) {
            sound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG, buildUrl(baseItemType));
            sounds.put(baseItemType, sound);
        }
        try {
            sound.play();
        } catch (Throwable t) {
            if (logCrash) {
                log.log(Level.SEVERE, "Sound crashed", t);
                logCrash = false;
            }
        }
    }

    public static void playMuzzleFlashSound(BaseItemType baseItemType) {
        INSTANCE.playSound(baseItemType);
    }

    private static String buildUrl(BaseItemType baseItemType) {
        StringBuilder url = new StringBuilder();
        url.append(Constants.MUZZLE_ITEM_IMAGE_URL);
        url.append("?");
        url.append(Constants.ITEM_IMAGE_ID);
        url.append("=");
        url.append(baseItemType.getId());
        url.append("&");
        url.append(Constants.TYPE);
        url.append("=");
        url.append(Constants.TYPE_SOUND);
        return url.toString();
    }


}
