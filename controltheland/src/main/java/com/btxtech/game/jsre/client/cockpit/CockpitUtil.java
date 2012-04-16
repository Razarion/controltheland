package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;

/**
 * User: beat
 * Date: 16.04.12
 * Time: 12:56
 */
public class CockpitUtil {
    public static boolean isInsideCockpit(Index relativePosition) {
        return SideCockpit.getInstance().getAreaMainPanel().contains(relativePosition)
                || SideCockpit.getInstance().getAreaLevelPanel().contains(relativePosition)
                || ChatCockpit.getInstance().getArea().contains(relativePosition)
                || SideCockpit.getInstance().getQuestProgressCockpit().getArea().contains(relativePosition);
    }

    public static boolean isInsideCockpit(Index relativeMiddle, ItemType itemType) {
        return itemType.getBoundingBox().contains(relativeMiddle, SideCockpit.getInstance().getAreaMainPanel())
                || itemType.getBoundingBox().contains(relativeMiddle, SideCockpit.getInstance().getAreaLevelPanel())
                || itemType.getBoundingBox().contains(relativeMiddle, ChatCockpit.getInstance().getArea())
                || itemType.getBoundingBox().contains(relativeMiddle, SideCockpit.getInstance().getQuestProgressCockpit().getArea());
    }

}
