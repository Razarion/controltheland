package com.btxtech.game.jsre.client.effects;

import com.btxtech.game.jsre.client.ClientClipHandler;
import com.btxtech.game.jsre.client.NoSuchClipException;
import com.btxtech.game.jsre.client.NoSuchImageSpriteMapInfoException;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.info.ClipInfo;
import com.btxtech.game.jsre.client.renderer.ClipRendererModel;
import com.btxtech.game.jsre.common.gameengine.syncObjects.ActiveProjectile;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncWeapon;

/**
 * User: beat
 * Date: 16.10.12
 * Time: 21:42
 */
public class ProjectileVisualization extends ClipRendererModel {
    private SyncBaseItem syncBaseItem;
    private ActiveProjectile activeProjectile;
    private boolean moving;

    public ProjectileVisualization(long timeStamp, SyncBaseItem syncBaseItem, ActiveProjectile activeProjectile) throws NoSuchClipException, NoSuchImageSpriteMapInfoException {
        this.syncBaseItem = syncBaseItem;
        this.activeProjectile = activeProjectile;
        SyncWeapon syncWeapon = syncBaseItem.getSyncWeapon();
        moving = syncWeapon.getWeaponType().getProjectileSpeed() != null;
        ClipInfo clipInfo = ClientClipHandler.getInstance().getProjectileClipInfo(syncBaseItem.getBaseItemType());
        Index absoluteMiddle = activeProjectile.getPosition();
        if (!moving) {
            setNoYMiddle();
            setMaxHeight(absoluteMiddle.getDistance(syncWeapon.getProjectileTarget()));
        }
        initAndPlaySound(timeStamp, clipInfo, absoluteMiddle, absoluteMiddle.getAngleToNord(syncWeapon.getProjectileTarget()), moving);
    }

    @Override
    public void prepareRender(long timeStamp, Rectangle viewRect) {
        if (!syncBaseItem.isAlive()) {
            stop();
            return;
        }
        if (moving) {
            SyncWeapon syncWeapon = syncBaseItem.getSyncWeapon();
            if (!activeProjectile.isAlive()) {
                stop();
                return;
            }
            Index absoluteMiddle = activeProjectile.getInterpolatedPosition(timeStamp);
            if (absoluteMiddle.equals(syncWeapon.getProjectileTarget())) {
                stop();
                return;
            }
            setAbsoluteMiddle(absoluteMiddle, absoluteMiddle.getAngleToNord(syncWeapon.getProjectileTarget()));
        }
        super.prepareRender(timeStamp, viewRect);
    }
}
