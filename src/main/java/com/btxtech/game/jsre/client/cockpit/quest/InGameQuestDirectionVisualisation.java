package com.btxtech.game.jsre.client.cockpit.quest;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

/**
 * User: beat
 * Date: 23.10.12
 * Time: 19:27
 */
public class InGameQuestDirectionVisualisation {
    private double angel;
    private Index relativeArrowHotSpot;

    public InGameQuestDirectionVisualisation(SyncItem syncItem, Rectangle viewRect) {
        this(syncItem.getSyncItemArea().getPosition(), viewRect);
    }

    public InGameQuestDirectionVisualisation(Index absolutePosition, Rectangle viewRect) {
        angel = viewRect.getCenter().getAngleToNord(absolutePosition);
        relativeArrowHotSpot = viewRect.getCenter().getPointFromAngelToNord(angel, 200).sub(viewRect.getStart());
    }

    public double getAngel() {
        return angel;
    }

    public Index getRelativeArrowHotSpot() {
        return relativeArrowHotSpot;
    }
}
