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

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.google.gwt.media.client.Audio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 01.01.2010
 * Time: 20:32:53
 */
public class SoundHandler {
    private static final SoundHandler INSTANCE = new SoundHandler();
    private static final int PARALLEL_PLAY_COUNT = 5;
    private static final String EXPLODE_MP3 = "/sounds/explosion.mp3";
    private static final String EXPLODE_OGG = "/sounds/explosion.ogg";
    private Logger log = Logger.getLogger(SoundHandler.class.getName());
    private Map<BaseItemType, Collection<Audio>> muzzleSound = new HashMap<BaseItemType, Collection<Audio>>();
    private Collection<Audio> explodeSounds = new ArrayList<Audio>();
    private String mimeType;
    private String explodeSrc;
    private boolean logNoCreation = true;
    private boolean logNoMimeType = true;
    private boolean isMute = false;

    public static SoundHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private SoundHandler() {
    }

    public void playMuzzleFlashSound(BaseItemType baseItemType) {
        Audio audio = getMuzzleAudio(baseItemType);
        if (audio != null) {
            audio.play();
        }
    }

    private Audio getMuzzleAudio(BaseItemType baseItemType) {
        Collection<Audio> available = muzzleSound.get(baseItemType);
        if (available == null) {
            available = new ArrayList<Audio>();
            muzzleSound.put(baseItemType, available);
        }
        Audio audio = null;
        for (Audio availableAudio : available) {
            if (availableAudio.hasEnded()) {
                audio = availableAudio;
                break;
            }
        }
        if (audio != null) {
            return audio;
        }
        if (available.size() < PARALLEL_PLAY_COUNT) {
            audio = Audio.createIfSupported();
            if (audio == null) {
                if (logNoCreation) {
                    log.severe("Audio not supported for muzzle");
                    logNoCreation = false;
                }
                return null;
            }
            String codec = determineMimeType(audio);
            if (codec == null) {
                return null;
            }
            setVolume(audio);
            audio.setSrc(buildUrl(baseItemType, codec));
            available.add(audio);
            return audio;
        } else {
            return null;
        }
    }

    private Audio getExplodeAudio() {
        Audio audio = null;
        for (Audio availableAudio : explodeSounds) {
            if (availableAudio.hasEnded()) {
                audio = availableAudio;
                break;
            }
        }
        if (audio != null) {
            return audio;
        }
        if (explodeSounds.size() < PARALLEL_PLAY_COUNT) {
            audio = Audio.createIfSupported();
            if (audio == null) {
                if (logNoCreation) {
                    log.severe("Audio not supported for explode");
                    logNoCreation = false;
                }
                return null;
            }
            if (explodeSrc == null) {
                String codec = determineMimeType(audio);
                if (codec == null) {
                    return null;
                } else if (codec.equals(Constants.CODEC_TYPE_MP3)) {
                    explodeSrc = EXPLODE_MP3;
                } else if (codec.equals(Constants.CODEC_TYPE_OGG)) {
                    explodeSrc = EXPLODE_OGG;
                } else {
                    return null;
                }
            }
            setVolume(audio);
            audio.setSrc(explodeSrc);
            explodeSounds.add(audio);
            return audio;
        } else {
            return null;
        }
    }

    private void setVolume(Audio audio) {
        if (isMute) {
            audio.setVolume(0.0);
        } else {
            audio.setVolume(1.0);
        }
    }

    private String determineMimeType(Audio audio) {
        // TODO will be handled by GWT 2.4
        if (mimeType != null) {
            return mimeType;
        }
        if (!audio.canPlayType(Constants.CODEC_TYPE_MP3).equals("")) {
            mimeType = Constants.CODEC_TYPE_MP3;
            return mimeType;
        } else if (!audio.canPlayType(Constants.CODEC_TYPE_OGG).equals("")) {
            mimeType = Constants.CODEC_TYPE_OGG;
            return mimeType;
        } else {
            if (logNoMimeType) {
                log.severe("Can not play sound mime type OGG or MP3");
                logNoMimeType = false;
            }
            return null;
        }
    }

    public void playItemExplode() {
        Audio audio = getExplodeAudio();
        if (audio != null) {
            audio.play();
        }
    }

    private static String buildUrl(BaseItemType baseItemType, String codec) {
        StringBuilder url = new StringBuilder();
        url.append(Constants.MUZZLE_ITEM_IMAGE_URL);
        url.append("?");
        url.append(Constants.ITEM_TYPE_ID);
        url.append("=");
        url.append(baseItemType.getId());
        url.append("&");
        url.append(Constants.TYPE);
        url.append("=");
        url.append(Constants.TYPE_SOUND);
        url.append("&");
        url.append(Constants.CODEC);
        url.append("=");
        url.append(codec);
        return url.toString();
    }

    public void mute(boolean mute) {
        if (mute == isMute) {
            return;
        }
        isMute = mute;
        for (Collection<Audio> audios : muzzleSound.values()) {
            for (Audio audio : audios) {
                setVolume(audio);
            }
        }
        for (Audio audio : explodeSounds) {
            setVolume(audio);
        }
    }
}
