package com.btxtech.game.services.media;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemClipPosition;

import java.util.List;

/**
 * User: beat
 * Date: 27.04.14
 * Time: 12:22
 */
public class DbClipUtil {

    public static ItemClipPosition createItemClipPosition(DbClip dbClip, List<Index> positions) {
        if (dbClip == null) {
            return null;
        }
        if (positions == null || positions.isEmpty()) {
            return null;
        }
        return new ItemClipPosition(dbClip.getId(), positions.toArray(new Index[positions.size()]));
    }

}
