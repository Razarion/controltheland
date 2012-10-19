package com.btxtech.game.jsre.client.effects;

import com.btxtech.game.jsre.client.ClientClipHandler;
import com.btxtech.game.jsre.client.NoSuchClipException;
import com.btxtech.game.jsre.client.NoSuchImageSpriteMapInfoException;
import com.btxtech.game.jsre.client.renderer.ClipRendererModel;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

/**
 * User: beat
 * Date: 17.10.12
 * Time: 14:52
 */
public class Detonation extends ClipRendererModel {
    public Detonation(SyncBaseItem attacker) throws NoSuchClipException, NoSuchImageSpriteMapInfoException {
        initAndPlaySound(ClientClipHandler.getInstance().getProjectileDetonationClipInfo(attacker.getBaseItemType()),
                attacker.getSyncWeapon().getProjectileTarget(), 0.0, false);

    }
}
