package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.common.info.ClipInfo;
import com.btxtech.game.jsre.client.common.info.PreloadedImageSpriteMapInfo;
import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.btxtech.game.jsre.client.renderer.ImageSpriteMapContainer;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemClipPosition;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;

import java.util.HashMap;
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
    private Map<PreloadedImageSpriteMapInfo.Type, Integer> preloadedImageSpriteMapInfo = new HashMap<PreloadedImageSpriteMapInfo.Type, Integer>();

    public static ClientClipHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ClientClipHandler() {
    }

    public ClipInfo getClipInfo(int clipId) throws NoSuchClipException {
        ClipInfo clipInfo = clipCache.get(clipId);
        if (clipInfo == null) {
            throw new NoSuchClipException(clipId);
        }
        return clipInfo;
    }

    public ClipInfo getMuzzleFireClipInfo(BaseItemType baseItemType) throws NoSuchClipException {
        WeaponType weaponType = baseItemType.getWeaponType();
        if (weaponType == null) {
            throw new NoSuchClipException("BaseItemType " + baseItemType.getName() + " does not have a weapon");
        }
        Integer muzzleFlashClipId = weaponType.getMuzzleFlashClipId();
        if (muzzleFlashClipId == null) {
            throw new NoSuchClipException("BaseItemType " + baseItemType.getName() + " does not have a muzzle flash clip configured");
        }
        return getClipInfo(muzzleFlashClipId);
    }

    public ClipInfo getProjectileClipInfo(BaseItemType baseItemType) throws NoSuchClipException {
        WeaponType weaponType = baseItemType.getWeaponType();
        if (weaponType == null) {
            throw new NoSuchClipException("BaseItemType " + baseItemType.getName() + " does not have a weapon");
        }
        Integer projectileClipId = weaponType.getProjectileClipId();
        if (projectileClipId == null) {
            throw new NoSuchClipException("BaseItemType " + baseItemType.getName() + " does not have a projectile clip configured");
        }

        return getClipInfo(projectileClipId);
    }

    public ClipInfo getProjectileDetonationClipInfo(BaseItemType baseItemType) throws NoSuchClipException {
        WeaponType weaponType = baseItemType.getWeaponType();
        if (weaponType == null) {
            throw new NoSuchClipException("BaseItemType " + baseItemType.getName() + " does not have a weapon");
        }
        Integer projectileDetonationClipId = weaponType.getProjectileDetonationClipId();
        if (projectileDetonationClipId == null) {
            throw new NoSuchClipException("BaseItemType " + baseItemType.getName() + " does not have a projectile detonation clip configured");
        }

        return getClipInfo(projectileDetonationClipId);
    }

    public ClipInfo getItemClipPositionClipInfo(ItemClipPosition itemClipPosition) throws NoSuchClipException {
        return getClipInfo(itemClipPosition.getClipId());
    }

    public ImageSpriteMapInfo getImageSpriteMapInfo(int imageSpriteMapInfoId) throws NoSuchImageSpriteMapInfoException {
        ImageSpriteMapInfo imageSpriteMapInfo = imageSpriteMapCache.get(imageSpriteMapInfoId);
        if (imageSpriteMapInfo == null) {
            throw new NoSuchImageSpriteMapInfoException(imageSpriteMapInfoId);
        }
        return imageSpriteMapInfo;
    }

    public ImageSpriteMapInfo getPreloadedImageSpriteMapInfo(PreloadedImageSpriteMapInfo.Type preloaded) throws NoSuchImageSpriteMapInfoException {
        Integer id = preloadedImageSpriteMapInfo.get(preloaded);
        if(id == null) {
            throw new NoSuchImageSpriteMapInfoException(preloaded);
        }
        return getImageSpriteMapInfo(id);
    }

    public void preloadImageSpriteMaps() {
        for (Integer id : preloadedImageSpriteMapInfo.values()) {
            try {
                ImageSpriteMapContainer.getInstance().getImage(getImageSpriteMapInfo(id));
            } catch (NoSuchImageSpriteMapInfoException e) {
                ClientExceptionHandler.handleException("ClientClipHandler.preloadImageSpriteMaps()", e);
            }
        }
        ImageSpriteMapContainer.getInstance().startLoad();
    }

    public void inti(GameInfo gameInfo) {
        for (ClipInfo clipInfo : gameInfo.getClipLibrary()) {
            clipCache.put(clipInfo.getClipId(), clipInfo);
        }
        preloadedImageSpriteMapInfo.putAll(gameInfo.getPreloadedImageSpriteMapInfo().getPreloadedImageSpriteMapInfo());
        for (ImageSpriteMapInfo imageSpriteMapInfo : gameInfo.getImageSpriteMapLibrary()) {
            imageSpriteMapCache.put(imageSpriteMapInfo.getId(), imageSpriteMapInfo);
        }
    }
}
