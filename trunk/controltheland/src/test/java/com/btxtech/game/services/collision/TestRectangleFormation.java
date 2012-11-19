package com.btxtech.game.services.collision;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.RectangleFormation;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ServerItemTypeService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 23.08.2011
 * Time: 14:01:59
 */
public class TestRectangleFormation extends AbstractServiceTest {
    //@Autowired
    //private DebugService debugService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;

    @Test
    @DirtiesContext
    public void testRectangleFormation() throws Throwable {
        configureSimplePlanet();
        List<SyncBaseItem> items = new ArrayList<>();

        AbstractTerrainService abstractTerrainService = EasyMock.createStrictMock(AbstractTerrainService.class);
        // Start
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(500, 500))).andReturn(new Index(500, 500));
        // Normal run
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(581, 500))).andReturn(new Index(581, 500));
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(581, 581))).andReturn(new Index(581, 581));
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(500, 581))).andReturn(new Index(500, 581));
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(419, 581))).andReturn(new Index(419, 581));
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(419, 500))).andReturn(new Index(419, 500));
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(419, 419))).andReturn(new Index(419, 419));
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(500, 419))).andReturn(new Index(500, 419));
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(581, 419))).andReturn(new Index(581, 419));
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(662, 419))).andReturn(new Index(662, 419));
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(662, 500))).andReturn(new Index(662, 500));
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(662, 581))).andReturn(new Index(662, 581));
        // fails
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(662, 662))).andReturn(new Index(1, 1));
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(581, 662))).andReturn(new Index(581, 66));
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(500, 662))).andReturn(new Index(500, 662));
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(419, 662))).andReturn(new Index(419, 662));
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(338, 662))).andReturn(new Index(338, 662));
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(338, 581))).andReturn(new Index(338, 581));
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(338, 500))).andReturn(new Index(338, 500));
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(338, 419))).andReturn(new Index(338, 419));
        EasyMock.expect(abstractTerrainService.correctPosition(40, new Index(338, 338))).andReturn(new Index(338, 338));
        EasyMock.replay(abstractTerrainService);

        ItemType targetItemType = serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID);
        targetItemType.setBoundingBox(new BoundingBox(40, ANGELS_24));

        Collection<SyncBaseItem> syncBaseItems = new ArrayList<>();
        for (int i = 1; i < 20; i++) {
            syncBaseItems.add(createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(1500, 1500), new Id(i, -100)));
        }

        RectangleFormation rectangleFormation = new RectangleFormation(new Index(500, 500), syncBaseItems, abstractTerrainService);
        for (int i = 1; i < 20; i++) {
            Index pos = null;
            while (pos == null) {
                pos = rectangleFormation.calculateNextEntry();
            }
            SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, pos, new Id(100, -100));
            items.add(syncBaseItem);
        }
        assertOverlapping(items);
        EasyMock.verify(abstractTerrainService);
    }

    private void assertOverlapping(List<SyncBaseItem> attackers) {
        for (int i = 0, attackersSize = attackers.size(); i < attackersSize; i++) {
            SyncBaseItem attackerToCheck = attackers.get(i);
            for (int j = i + 1; j < attackersSize; j++) {
                SyncBaseItem attacker = attackers.get(j);
                if (attackerToCheck.getSyncItemArea().contains(attacker)) {
                    Assert.fail("Item do overlap| " + attackerToCheck + " | " + attacker);
                }
            }
        }
    }

}
