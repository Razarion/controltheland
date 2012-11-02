package com.btxtech.game.jsre.common.gameengine.itemType;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

import java.io.Serializable;

/**
 * User: beat
 * Date: 01.11.12
 * Time: 14:04
 */
public class ItemClipPosition implements Serializable {
    private int clipId;
    private Index[] positions;

    /**
     * Used by GWT
     */
    ItemClipPosition() {
    }

    public ItemClipPosition(int clipId, Index[] positions) {
        this.clipId = clipId;
        this.positions = positions;
    }

    public int getClipId() {
        return clipId;
    }

    public Index getOffset(SyncBaseItem syncBaseItem) {
        return positions[syncBaseItem.getSyncItemArea().getAngelIndex()];
    }

    public Index[] getPositions() {
        return positions;
    }
}
