package com.btxtech.game.jsre.client.effects;

import com.btxtech.game.jsre.client.ClientClipHandler;
import com.btxtech.game.jsre.client.NoSuchClipException;
import com.btxtech.game.jsre.client.NoSuchImageSpriteMapInfoException;
import com.btxtech.game.jsre.client.common.info.ClipInfo;
import com.btxtech.game.jsre.client.renderer.ClipRendererModel;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemClipPosition;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

/**
 * User: beat
 * Date: 02.11.12
 * Time: 10:20
 */
public class ItemEffect extends ClipRendererModel {
    private ItemClipPosition demolitionClip;

    public ItemEffect(SyncBaseItem syncBaseItem, ItemClipPosition demolitionClip) throws NoSuchClipException, NoSuchImageSpriteMapInfoException {
        this.demolitionClip = demolitionClip;
        ClipInfo clipInfo = ClientClipHandler.getInstance().getItemClipPositionClipInfo(demolitionClip);
        initAndPlaySound(clipInfo, syncBaseItem.getSyncItemArea().getPosition().add(demolitionClip.getOffset(syncBaseItem)), 0.0, true);
    }

    public void refresh(SyncBaseItem syncBaseItem) {
        setAbsoluteMiddle(syncBaseItem.getSyncItemArea().getPosition().add(demolitionClip.getOffset(syncBaseItem)), 0.0);
    }
}
