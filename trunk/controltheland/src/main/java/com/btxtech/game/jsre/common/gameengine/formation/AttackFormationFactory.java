package com.btxtech.game.jsre.common.gameengine.formation;

import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;

import java.util.List;

/**
 * User: beat
 * Date: 07.09.2011
 * Time: 21:17:46
 */
public class AttackFormationFactory {
    static public AttackFormation create(SyncItemArea target, double startAngel, List<AttackFormationItem> attackFormationItems) {
        int maxDiameter = 0;
        int range = Integer.MAX_VALUE;

        for (AttackFormationItem attackFormationItem : attackFormationItems) {
            BaseItemType baseItemType = attackFormationItem.getSyncBaseItem().getBaseItemType();
            if (baseItemType.getBoundingBox().getMaxDiameter() > maxDiameter) {
                maxDiameter = baseItemType.getBoundingBox().getMaxDiameter();
            }
            if (attackFormationItem.getRange() < range) {
                range = attackFormationItem.getRange();
            }
        }
        return new RoundedRectangleAttackFormation(target, startAngel, attackFormationItems, maxDiameter, range);
    }
}
