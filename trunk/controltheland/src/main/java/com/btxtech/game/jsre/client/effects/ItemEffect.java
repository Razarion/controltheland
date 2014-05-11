package com.btxtech.game.jsre.client.effects;

import com.btxtech.game.jsre.client.ClientClipHandler;
import com.btxtech.game.jsre.client.NoSuchClipException;
import com.btxtech.game.jsre.client.NoSuchImageSpriteMapInfoException;
import com.btxtech.game.jsre.client.common.Index;
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
    private double rotation = 0.0;

    public ItemEffect(long timeStamp, SyncBaseItem syncBaseItem, ItemClipPosition demolitionClip, Index target) throws NoSuchClipException, NoSuchImageSpriteMapInfoException {
        this.demolitionClip = demolitionClip;
        ClipInfo clipInfo = ClientClipHandler.getInstance().getItemClipPositionClipInfo(demolitionClip);
        Index absoluteMiddle = syncBaseItem.getSyncItemArea().getPosition().add(demolitionClip.getOffset(syncBaseItem));
        if (target != null) {
            setNoYMiddle();
            setMaxHeight(absoluteMiddle.getDistance(target));
            rotation = syncBaseItem.getSyncItemArea().getPosition().getAngleToNord(target);
        }
        initAndPlaySound(timeStamp, clipInfo, absoluteMiddle, rotation, true);
    }

    public void refresh(SyncBaseItem syncBaseItem) {
        setAbsoluteMiddle(syncBaseItem.getSyncItemArea().getPosition().add(demolitionClip.getOffset(syncBaseItem)), rotation);
    }


}
