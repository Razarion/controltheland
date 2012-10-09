package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.common.info.ClipInfo;
import com.btxtech.game.jsre.client.common.info.CommonClipInfo;
import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.google.gwt.user.client.Random;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 07.10.12
 * Time: 13:48
 */
public class ClientClipHandler {
    private static final ClientClipHandler INSTANCE = new ClientClipHandler();
    private Map<Integer, ClipInfo> clipCache = new HashMap<Integer, ClipInfo>();
    private Map<Integer, ImageSpriteMapInfo> imageSpriteMapCache = new HashMap<Integer, ImageSpriteMapInfo>();
    private Map<CommonClipInfo.Type, List<Integer>> commonClips = new HashMap<CommonClipInfo.Type, List<Integer>>();
    
    public static ClientClipHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ClientClipHandler() {
    }

    public ClipInfo getClipInfo(CommonClipInfo.Type explosion) throws NoSuchClipException {
        List<Integer> clipIds = commonClips.get(explosion);
        if (clipIds == null || clipIds.isEmpty()) {
            throw new NoSuchClipException(explosion);
        }
        int clipId;
        if (clipIds.size() == 1) {
            clipId = clipIds.get(0);
        } else {
            clipId = clipIds.get(Random.nextInt(clipIds.size()));
        }
        return getClipInfo(clipId);
    }

    public ClipInfo getClipInfo(int clipId) throws NoSuchClipException {
        ClipInfo clipInfo = clipCache.get(clipId);
        if (clipInfo == null) {
            throw new NoSuchClipException(clipId);
        }
        return clipInfo;
    }

    public ImageSpriteMapInfo getImageSpriteMapInfo(int imageSpriteMapInfoId) throws NoSuchImageSpriteMapInfoException {
        ImageSpriteMapInfo imageSpriteMapInfo = imageSpriteMapCache.get(imageSpriteMapInfoId);
        if (imageSpriteMapInfo == null) {
            throw new NoSuchImageSpriteMapInfoException(imageSpriteMapInfoId);
        }
        return imageSpriteMapInfo;
    }

    public void inti(GameInfo gameInfo) {
        for (ClipInfo clipInfo : gameInfo.getClipLibrary()) {
            clipCache.put(clipInfo.getClipId(), clipInfo);
        }
        commonClips.putAll(gameInfo.getCommonClipInfo().getCommonClips());
        for (ImageSpriteMapInfo imageSpriteMapInfo : gameInfo.getImageSpriteMapLibrary()) {
            imageSpriteMapCache.put(imageSpriteMapInfo.getId(), imageSpriteMapInfo);
        }
    }
}
