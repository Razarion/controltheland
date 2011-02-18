package com.btxtech.game.services.overall;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.services.SimpleMapScope;
import com.btxtech.game.services.TestWebSessionContextLoader;
import com.btxtech.game.services.gwt.MovableServiceImpl;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.overall.helpers.BaseHelper;
import com.btxtech.game.services.overall.helpers.GameTestHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: beat
 * Date: Oct 3, 2009
 * Time: 10:39:24 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:war/WEB-INF/applicationContext.xml"}, loader = TestWebSessionContextLoader.class)
@Transactional
@TransactionConfiguration()
public class TestItemAttack {
    public static final String BASE_SCOPE_1 = "baseScope1";
    public static final String BASE_SCOPE_2 = "baseScope2";
    public static final int X_TILE_COUNT = 50;
    public static final int Y_TILE_COUNT = 50;
    public static final int BASE_TANKE_COUNT = 100;
    @Autowired
    private GameTestHelper gameTestHelper;
    @Autowired
    private ItemService itemService;
    @Autowired
    private MovableServiceImpl movableService;

    @Test
    public void testItemMove() throws Exception {
        SimpleMapScope.getInstance().setScopeType(SimpleMapScope.ScopeType.MANUEL);
        gameTestHelper.emptyGame();
        //gameTestHelper.emptyTerrain(X_TILE_COUNT, Y_TILE_COUNT);
        SimpleMapScope.getInstance().setManuelScopeId(BASE_SCOPE_1);
        BaseHelper baseHelper1 = gameTestHelper.createBase(0);
        SimpleMapScope.getInstance().setManuelScopeId(BASE_SCOPE_2);
        BaseHelper baseHelper2 = gameTestHelper.createBase(1);

        // Put CV away
        SimpleMapScope.getInstance().setManuelScopeId(BASE_SCOPE_1);
        itemService.getItem(baseHelper1.getConstructionVehicleSyncInfo().getId()).setAbsolutePosition(new Index(X_TILE_COUNT * Constants.TILE_WIDTH, Y_TILE_COUNT * Constants.TILE_HEIGHT), false);
        SimpleMapScope.getInstance().setManuelScopeId(BASE_SCOPE_2);
        itemService.getItem(baseHelper2.getConstructionVehicleSyncInfo().getId()).setAbsolutePosition(new Index(X_TILE_COUNT * Constants.TILE_WIDTH, Y_TILE_COUNT * Constants.TILE_HEIGHT), false);

        // Make Tanks
        TankSyncItem[][] tankArray = new TankSyncItem[BASE_TANKE_COUNT][];
        for (int i = 0; i < BASE_TANKE_COUNT; i++) {
            SimpleMapScope.getInstance().setManuelScopeId(BASE_SCOPE_1);
            TankSyncItem t1 = (TankSyncItem) itemService.createSyncObject(ItemType.TANK, baseHelper1.getGameInfo().getBase(), new Index(i * 20, 0), null, 0, true);
            SimpleMapScope.getInstance().setManuelScopeId(BASE_SCOPE_2);
            TankSyncItem t2 = (TankSyncItem) itemService.createSyncObject(ItemType.TANK, baseHelper2.getGameInfo().getBase(), new Index(i * 20, ItemType.TANK_ATTACK_RANGE + 10), null, 0, true);
            tankArray[i] = new TankSyncItem[]{t1, t2};
        }
        // Attack
        for (TankSyncItem[] tanks : tankArray) {
            SimpleMapScope.getInstance().setManuelScopeId(BASE_SCOPE_1);
            gameTestHelper.sendAttackCommand(tanks[0], tanks[1]);
        }

        // Wait for finish all attacks
        while (true) {
            Thread.sleep(GameTestHelper.THREAD_SLEEP);
            boolean isAttacking = false;
            for (TankSyncItem[] tankSyncItems : tankArray) {
                if (tankSyncItems[0].isAttacking() || tankSyncItems[0].getPathToAbsoluteDestination() != null) {
                    isAttacking = true;
                    break;
                }
                if (tankSyncItems[1].isAttacking() || tankSyncItems[1].getPathToAbsoluteDestination() != null) {
                    isAttacking = true;
                    break;
                }
            }
            if (!isAttacking) {
                break;
            }
        }

        // Verifey with sync infos
        SimpleMapScope.getInstance().setManuelScopeId(BASE_SCOPE_1);
        Collection<Packet> packets1 = movableService.getSyncInfo(baseHelper1.getGameInfo().getBase());
        SimpleMapScope.getInstance().setManuelScopeId(BASE_SCOPE_2);
        Collection<Packet> packets2 = movableService.getSyncInfo(baseHelper2.getGameInfo().getBase());

        ArrayList<TankSyncItem> deathTanks = new ArrayList<TankSyncItem>();
        for (TankSyncItem[] tankSyncItems : tankArray) {
            if (tankSyncItems[0].isDead()) {
                deathTanks.add(tankSyncItems[0]);
            }
            if (tankSyncItems[1].isDead()) {
                deathTanks.add(tankSyncItems[1]);
            }
        }
        verify(packets1, deathTanks);
        verify(packets2, deathTanks);
    }

    private void verify(Collection<Packet> packets, ArrayList<TankSyncItem> deathTanks) {
        ArrayList<TankSyncItem> tmpDeathTanks = new ArrayList<TankSyncItem>(deathTanks);
        for (Packet packet : packets) {
            if (packet instanceof BaseSyncInfo) {
                BaseSyncInfo baseSyncInfo = (BaseSyncInfo) packet;
                if (baseSyncInfo.getHealth() == 0) {
                    boolean found = false;
                    for (Iterator<TankSyncItem> it = tmpDeathTanks.iterator(); it.hasNext();) {
                        TankSyncItem tmpDeathTank = it.next();
                        if (tmpDeathTank.getId().equals(baseSyncInfo.getId())) {
                            it.remove();
                            found = true;
                            break;
                        }
                    }
                    Assert.assertTrue("Death item not found in deathTanks: " + baseSyncInfo, found);
                }
            }
        }
        Assert.assertTrue("Not all death items gathered via movableService", tmpDeathTanks.isEmpty());
    }
}