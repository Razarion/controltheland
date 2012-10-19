package com.btxtech.game.jsre.client.effects;

import com.btxtech.game.jsre.client.ClientClipHandler;
import com.btxtech.game.jsre.client.NoSuchClipException;
import com.btxtech.game.jsre.client.NoSuchImageSpriteMapInfoException;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.info.ClipInfo;
import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.btxtech.game.jsre.client.renderer.ClipRendererModel;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncWeapon;

/**
 * User: beat
 * Date: 16.10.12
 * Time: 21:42
 */
public class Projectile extends ClipRendererModel {
    private SyncBaseItem syncBaseItem;
    private int muzzleFlashNr;
    private boolean moving;

    public Projectile(SyncBaseItem syncBaseItem, int muzzleFlashNr) throws NoSuchClipException, NoSuchImageSpriteMapInfoException {
        this.syncBaseItem = syncBaseItem;
        this.muzzleFlashNr = muzzleFlashNr;
        SyncWeapon syncWeapon = syncBaseItem.getSyncWeapon();
        moving = syncWeapon.getWeaponType().getProjectileSpeed() != null;
        ClipInfo clipInfo = ClientClipHandler.getInstance().getProjectileClipInfo(syncBaseItem.getBaseItemType());
        Index absoluteMiddle = syncWeapon.getProjectilePosition(muzzleFlashNr);
        if (!moving) {
            setNoYMiddle();
            setMaxHeight(absoluteMiddle.getDistance(syncWeapon.getProjectileTarget()));
        }
        initAndPlaySound(clipInfo, absoluteMiddle, absoluteMiddle.getAngleToNord(syncWeapon.getProjectileTarget()), moving);
    }

    @Override
    public void prepareRender(long timeStamp, Rectangle viewRect) {
        if (!syncBaseItem.isAlive()) {
            stop();
            return;
        }
        if (moving) {
            SyncWeapon syncWeapon = syncBaseItem.getSyncWeapon();
            Index absoluteMiddle = syncWeapon.getProjectilePosition(muzzleFlashNr);
            if (absoluteMiddle == null) {
                stop();
                return;
            }
            if (absoluteMiddle.equals(syncWeapon.getProjectileTarget())) {
                stop();
                return;
            }
            setAbsoluteMiddle(absoluteMiddle, absoluteMiddle.getAngleToNord(syncWeapon.getProjectileTarget()));
        }
        super.prepareRender(timeStamp, viewRect);
    }
}
