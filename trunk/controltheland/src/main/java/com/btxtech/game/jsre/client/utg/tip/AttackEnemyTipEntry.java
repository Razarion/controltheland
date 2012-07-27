package com.btxtech.game.jsre.client.utg.tip;

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.perfmon.Perfmon;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.google.gwt.core.client.Scheduler;

/**
 * User: beat
 * Date: 19.12.2011
 * Time: 17:36:20
 */
public class AttackEnemyTipEntry extends TipEntry {
    private AttackEnemyArrow attackEnemyArrow;

    public AttackEnemyTipEntry(int duration, int showTime, boolean repeat) {
        super(duration, showTime, repeat);
    }

    @Override
    public void show() {
        close();
        SyncBaseItem enemyBaseItem = getEnemyItem();
        if (enemyBaseItem == null) {
            return;
        }
        attackEnemyArrow = new AttackEnemyArrow(enemyBaseItem);
        RadarPanel.getInstance().showHint(enemyBaseItem);
        Scheduler.get().scheduleFixedPeriod(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                try {
                    Perfmon.getInstance().onEntered(PerfmonEnum.TIP_ARROW);
                    if (attackEnemyArrow != null) {
                        attackEnemyArrow.blink();
                        RadarPanel.getInstance().blinkHint();
                        return true;
                    } else {
                        return false;
                    }
                } finally {
                    Perfmon.getInstance().onLeft(PerfmonEnum.TIP_ARROW);
                }
            }
        }, 250);
    }

    @Override
    public void close() {
        if (attackEnemyArrow != null) {
            attackEnemyArrow.close();
            attackEnemyArrow = null;
            RadarPanel.getInstance().hideHint();
        }
    }

    private SyncBaseItem getEnemyItem() {
        SyncBaseItem enemy = null;
        for (ClientSyncItem item : ItemContainer.getInstance().getItems()) {
            if (!item.isEnemy()) {
                continue;
            }
            SyncBaseItem enemyItem = item.getSyncBaseItem();
            if (!enemyItem.hasSyncMovable()) {
                if (enemyItem.hasSyncFactory()) {
                    return enemyItem;
                }
                enemy = enemyItem;
            }
            if (enemy == null) {
                enemy = enemyItem;
            }
        }
        return enemy;
    }

}
