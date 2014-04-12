package scenario.moving;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.services.collision.CollisionService;
import scenario.Scenario;

import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * User: beat
 * Date: 22.03.13
 * Time: 13:25
 */
public class CrossNsWe extends Scenario {
    private Timer timer;

    @Override
    public void addItems(final List<SyncItem> syncItems, CollisionService collisionService) {
 /*       stop();
        timer = new Timer("ItemTimerTask", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (syncItems) {
                    createNorth(syncItems);
                    createWest(syncItems);
                    cleanOldItems(syncItems);
                }
            }
        }, 0, TIMER_DELAY);*/

    }

    @Override
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void tick(List<SyncItem> syncItemLists) {
    }


    public void createNorth(List<SyncItem> syncItems) {
 /*      for (int x = 100; x < 500; x += 50) {
            SyncItem syncItem = new SyncItem(RADIUS, new Index(x, 100), "undefined");
            syncItem.setTargetPosition(new Index(300, 600));
            syncItems.add(syncItem);
        } */
    }

    public void createWest(List<SyncItem> syncItems) {
 /*       for (int y = 100; y < 500; y += 50) {
            SyncItem syncItem = new SyncItem(RADIUS, new Index(100, y), "undefined");
            syncItem.setTargetPosition(new Index(600, 300));
            syncItems.add(syncItem);
        }   */
    }

    private void cleanOldItems(List<SyncItem> syncItems) {
 /*       for (Iterator<SyncItem> iterator = syncItems.iterator(); iterator.hasNext(); ) {
            SyncItem syncItem = iterator.next();
            if (syncItem.isMoving()) {
                if(syncItem.getPosition().getDistance(syncItem.getNextWayPosition()) < 100) {
                    iterator.remove();
                }
            } else {
                iterator.remove();
            }
        } */
    }

}
