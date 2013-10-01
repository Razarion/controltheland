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

import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionListener;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.info.ClipInfo;
import com.btxtech.game.jsre.client.common.info.CommonSoundInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.dom.client.MediaElement;
import com.google.gwt.media.client.Audio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 01.01.2010
 * Time: 20:32:53
 */
public class SoundHandler implements SelectionListener {
    private static final SoundHandler INSTANCE = new SoundHandler();
    private static final int PARALLEL_PLAY_COUNT = 5;
    private Logger log = Logger.getLogger(SoundHandler.class.getName());
    private Map<Integer, Collection<Audio>> sounds = new HashMap<Integer, Collection<Audio>>();
    private boolean logNoCreation = true;
    private boolean isMute = false;
    private boolean isRunning = false;
    private CommonSoundInfo commonSoundInfo;

    public static SoundHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private SoundHandler() {
    }

    public static String buildUrl(int soundId, String codec) {
        StringBuilder url = new StringBuilder();
        url.append(Constants.SOUND_PATH);
        url.append("?");
        url.append(Constants.SOUND_ID);
        url.append("=");
        url.append(soundId);
        url.append("&");
        url.append(Constants.SOUND_CODEC);
        url.append("=");
        url.append(codec);
        return url.toString();
    }

    public void mute(boolean mute) {
        if (mute == isMute) {
            return;
        }
        isMute = mute;
        for (Collection<Audio> audios : sounds.values()) {
            for (Audio audio : audios) {
                setVolume(audio);
            }
        }
    }

    public void start(CommonSoundInfo commonSoundInfo) {
        this.commonSoundInfo = commonSoundInfo;
        SelectionHandler.getInstance().addSelectionListener(this);
        isRunning = true;
        playOnBackgroundMusicSoundId();
    }

    public void stop() {
        try {
            SelectionHandler.getInstance().removeSelectionListener(this);
            isRunning = false;
            for (Collection<Audio> audios : sounds.values()) {
                for (Audio audio : audios) {
                    audio.pause();
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "SoundHandler.stop()", e);
        }
    }

    public void playSelectionItemSound(ItemType itemType) {
        if (itemType.getSelectionSound() != null) {
            playSound(itemType.getSelectionSound(), false);
        }
    }

    public void playCommandSound(SyncBaseItem syncItem) {
        if (syncItem.getItemType().getCommandSound() != null) {
            playSound(syncItem.getItemType().getCommandSound(), false);
        }
    }

    public void playOnBuiltSound(SyncBaseItem syncBaseItem) {
        if (syncBaseItem.getItemType().getBuildupSound() != null) {
            playSound(syncBaseItem.getItemType().getBuildupSound(), false);
        }
    }

    public void onItemKilled(SyncBaseItem target, SimpleBase actor) {
        if (ClientBase.getInstance().isMyOwnProperty(target)) {
            if (target.hasSyncMovable()) {
                if (commonSoundInfo.getUnitLostSoundId() != null) {
                    playSound(commonSoundInfo.getUnitLostSoundId(), false);
                }
            } else {
                if (commonSoundInfo.getBuildingLostSoundId() != null) {
                    playSound(commonSoundInfo.getBuildingLostSoundId(), false);
                }
            }
        } else if (ClientBase.getInstance().isMyOwnBase(actor)) {
            if (target.hasSyncMovable()) {
                if (commonSoundInfo.getUnitKilledSoundId() != null) {
                    playSound(commonSoundInfo.getUnitKilledSoundId(), false);
                }
            } else {
                if (commonSoundInfo.getBuildingKilledSoundId() != null) {
                    playSound(commonSoundInfo.getBuildingKilledSoundId(), false);
                }
            }
        }
    }

    public void playClipSound(ClipInfo clipInfo) {
        if (clipInfo.hasSoundId()) {
            playSound(clipInfo.getSoundId(), false);
        }
    }

    private void playOnBackgroundMusicSoundId() {
        if (commonSoundInfo.getBackgroundMusicSoundId() != null) {
            playSound(commonSoundInfo.getBackgroundMusicSoundId(), true);
        }
    }

    private void playSound(int soundId, boolean loop) {
        if (!isRunning) {
            return;
        }
        Audio audio = getAudio(soundId);
        if (audio != null) {
            audio.play();
            if (loop) {
                audio.getMediaElement().setLoop(true);
            }
        }
    }

    private Audio getAudio(int soundId) {
        try {
            Collection<Audio> available = sounds.get(soundId);
            if (available == null) {
                available = new ArrayList<Audio>();
                sounds.put(soundId, available);
            }
            Audio audio = null;
            for (Iterator<Audio> iterator = available.iterator(); iterator.hasNext(); ) {
                Audio availableAudio = iterator.next();
                if (availableAudio.getNetworkState() == MediaElement.NETWORK_NO_SOURCE) {
                    iterator.remove();
                    continue;
                }
                if (availableAudio.hasEnded() || availableAudio.isPaused()) {
                    audio = availableAudio;
                    break;
                }
            }
            if (audio != null) {
                audio.setCurrentTime(0);
                return audio;
            }
            if (available.size() < PARALLEL_PLAY_COUNT) {
                audio = Audio.createIfSupported();
                if (audio == null) {
                    if (logNoCreation) {
                        log.severe("Audio not supported for sound id: " + soundId);
                        logNoCreation = false;
                    }
                    return null;
                }
                setVolume(audio);
                audio.addSource(buildUrl(soundId, Constants.SOUND_CODEC_TYPE_MP3), AudioElement.TYPE_MP3);
                audio.addSource(buildUrl(soundId, Constants.SOUND_CODEC_TYPE_OGG), AudioElement.TYPE_OGG);
                available.add(audio);
                return audio;
            } else {
                return null;
            }
        } catch (Exception e) {
            ClientExceptionHandler.handleExceptionOnlyOnce("SoundHandler.getAudio() " + soundId, e);
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

    @Override
    public void onTargetSelectionChanged(SyncItem target) {
        if (target instanceof SyncResourceItem || target instanceof SyncBoxItem) {
            playSelectionItemSound(target.getItemType());
        }
    }

    @Override
    public void onSelectionCleared() {
        //Ignore
    }

    @Override
    public void onOwnSelectionChanged(Group selectedGroup) {
        for (SyncBaseItem syncBaseItem : selectedGroup.getSyncBaseItems()) {
            playSelectionItemSound(syncBaseItem.getItemType());
        }
    }
}
