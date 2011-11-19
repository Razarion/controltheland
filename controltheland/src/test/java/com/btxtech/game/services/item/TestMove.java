package com.btxtech.game.services.item;

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.itemType.MovableType;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.connection.ConnectionService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncMovable;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.action.ActionService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 27.05.2011
 * Time: 14:17:18
 */
public class TestMove extends AbstractServiceTest {
    @Autowired
    private MovableService movableService;
    @Autowired
    private ActionService actionService;
    @Autowired
    private ItemService itemService;

    private SyncBaseItem createSyncBaseItem() throws Exception {
        configureMinimalGame();

        // Mock
        ItemService itemServiceMock = EasyMock.createNiceMock(ItemService.class);
        EasyMock.expect(itemServiceMock.hasItemsInRectangle(EasyMock.<Rectangle>anyObject())).andReturn(false).anyTimes();
        EasyMock.replay(itemServiceMock);

        ConnectionService connectionService = EasyMock.createNiceMock(ConnectionService.class);
        EasyMock.expect(connectionService.getGameEngineMode()).andReturn(GameEngineMode.MASTER).anyTimes();
        EasyMock.replay(connectionService);

        AbstractTerrainService mockTerrainService = EasyMock.createNiceMock(AbstractTerrainService.class);

        Services services = EasyMock.createNiceMock(Services.class);
        EasyMock.expect(services.getItemService()).andReturn(itemServiceMock).anyTimes();
        EasyMock.expect(services.getTerrainService()).andReturn(mockTerrainService).anyTimes();
        EasyMock.expect(services.getConnectionService()).andReturn(connectionService).anyTimes();
        EasyMock.replay(services);

        Id id = new Id(1, 1, 1);
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(2000, 2000), id, services);
        SyncMovable syncMovable = syncBaseItem.getSyncMovable();
        // Set speed to 100
        syncMovable.getMovableType().changeTo(new MovableType(100, syncMovable.getMovableType().getTerrainType()));
        Assert.assertFalse(syncMovable.tick(1.0));

        return syncBaseItem;
    }

    @Test
    @DirtiesContext
    public void testMoveHorizontallyLine1() throws Exception {
        SyncBaseItem syncBaseItem = createSyncBaseItem();

        List<Index> path = new ArrayList<Index>();
        path.add(new Index(3000, 2000));
        syncBaseItem.getSyncMovable().setPathToDestination(path);

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(1.0));
        Assert.assertEquals(new Index(2100, 2000), syncBaseItem.getSyncItemArea().getPosition());

        Assert.assertFalse(syncBaseItem.getSyncMovable().tick(9.0));
        Assert.assertEquals(new Index(3000, 2000), syncBaseItem.getSyncItemArea().getPosition());
    }

    @Test
    @DirtiesContext
    public void testMoveHorizontallyLine2() throws Exception {
        SyncBaseItem syncBaseItem = createSyncBaseItem();

        List<Index> path = new ArrayList<Index>();
        path.add(new Index(3000, 2000));
        syncBaseItem.getSyncMovable().setPathToDestination(path);

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(0.3));
        Assert.assertEquals(new Index(2030, 2000), syncBaseItem.getSyncItemArea().getPosition());

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(4.7));
        Assert.assertEquals(new Index(2500, 2000), syncBaseItem.getSyncItemArea().getPosition());

        Assert.assertFalse(syncBaseItem.getSyncMovable().tick(5.0));
        Assert.assertEquals(new Index(3000, 2000), syncBaseItem.getSyncItemArea().getPosition());
    }

    @Test
    @DirtiesContext
    public void testMoveHorizontallyLine3() throws Exception {
        SyncBaseItem syncBaseItem = createSyncBaseItem();

        List<Index> path = new ArrayList<Index>();
        path.add(new Index(2100, 2000));
        syncBaseItem.getSyncMovable().setPathToDestination(path);

        for (int i = 0; i < 499; i++) {
            Assert.assertTrue(syncBaseItem.getSyncMovable().tick(0.002));
        }

        Assert.assertFalse(syncBaseItem.getSyncMovable().tick(0.002));
        Assert.assertEquals(new Index(2100, 2000), syncBaseItem.getSyncItemArea().getPosition());
    }

    @Test
    @DirtiesContext
    public void testMoveVerticallyLine1() throws Exception {
        SyncBaseItem syncBaseItem = createSyncBaseItem();

        List<Index> path = new ArrayList<Index>();
        path.add(new Index(2000, 3000));
        syncBaseItem.getSyncMovable().setPathToDestination(path);

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(1.0));
        Assert.assertEquals(new Index(2000, 2100), syncBaseItem.getSyncItemArea().getPosition());

        Assert.assertFalse(syncBaseItem.getSyncMovable().tick(9.0));
        Assert.assertEquals(new Index(2000, 3000), syncBaseItem.getSyncItemArea().getPosition());
    }

    @Test
    @DirtiesContext
    public void testMoveVerticallyLine2() throws Exception {
        SyncBaseItem syncBaseItem = createSyncBaseItem();

        List<Index> path = new ArrayList<Index>();
        path.add(new Index(2000, 3000));
        syncBaseItem.getSyncMovable().setPathToDestination(path);

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(0.5));
        Assert.assertEquals(new Index(2000, 2050), syncBaseItem.getSyncItemArea().getPosition());

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(4.5));
        Assert.assertEquals(new Index(2000, 2500), syncBaseItem.getSyncItemArea().getPosition());

        Assert.assertFalse(syncBaseItem.getSyncMovable().tick(5.0));
        Assert.assertEquals(new Index(2000, 3000), syncBaseItem.getSyncItemArea().getPosition());
    }

    @Test
    @DirtiesContext
    public void testMoveVerticallyLine3() throws Exception {
        SyncBaseItem syncBaseItem = createSyncBaseItem();

        List<Index> path = new ArrayList<Index>();
        path.add(new Index(2000, 2050));
        syncBaseItem.getSyncMovable().setPathToDestination(path);

        for (int i = 0; i < 499; i++) {
            Assert.assertTrue(syncBaseItem.getSyncMovable().tick(0.001));
        }

        Assert.assertFalse(syncBaseItem.getSyncMovable().tick(0.001));
        Assert.assertEquals(new Index(2000, 2050), syncBaseItem.getSyncItemArea().getPosition());
    }

    @Test
    @DirtiesContext
    public void testMoveDiagonallyLine1() throws Exception {
        SyncBaseItem syncBaseItem = createSyncBaseItem();

        List<Index> path = new ArrayList<Index>();
        path.add(new Index(3000, 3000));
        syncBaseItem.getSyncMovable().setPathToDestination(path);

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(1.0));
        Assert.assertEquals(new Index(2071, 2071), syncBaseItem.getSyncItemArea().getPosition());

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(13.14));
        Assert.assertEquals(new Index(3000, 3000), syncBaseItem.getSyncItemArea().getPosition());
    }

    @Test
    @DirtiesContext
    public void testMoveDiagonallyLine2() throws Exception {
        SyncBaseItem syncBaseItem = createSyncBaseItem();

        List<Index> path = new ArrayList<Index>();
        path.add(new Index(2100, 2100));
        syncBaseItem.getSyncMovable().setPathToDestination(path);

        for (int i = 0; i < 471; i++) {
            Assert.assertTrue(syncBaseItem.getSyncMovable().tick(0.003));
        }

        Assert.assertFalse(syncBaseItem.getSyncMovable().tick(0.003));
        Assert.assertEquals(new Index(2100, 2100), syncBaseItem.getSyncItemArea().getPosition());
    }

    @Test
    @DirtiesContext
    public void testMoveDiagonallyLine3() throws Exception {
        SyncBaseItem syncBaseItem = createSyncBaseItem();

        List<Index> path = new ArrayList<Index>();
        path.add(new Index(2500, 3000));
        syncBaseItem.getSyncMovable().setPathToDestination(path);

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(0.4));
        Assert.assertEquals(new Index(2018, 2036), syncBaseItem.getSyncItemArea().getPosition());

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(10.78));
        Assert.assertEquals(new Index(2500, 3000), syncBaseItem.getSyncItemArea().getPosition());
    }

    @Test
    @DirtiesContext
    public void testMoveDiagonallyLine4() throws Exception {
        SyncBaseItem syncBaseItem = createSyncBaseItem();

        List<Index> path = new ArrayList<Index>();
        path.add(new Index(2700, 2400));
        syncBaseItem.getSyncMovable().setPathToDestination(path);

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(1.06));
        System.out.println(syncBaseItem.getSyncItemArea().getPosition());
        Assert.assertEquals(new Index(2092, 2053), syncBaseItem.getSyncItemArea().getPosition());

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(7.0));
        Assert.assertEquals(new Index(2700, 2400), syncBaseItem.getSyncItemArea().getPosition());
    }

    @Test
    @DirtiesContext
    public void testMovePath1() throws Exception {
        SyncBaseItem syncBaseItem = createSyncBaseItem();

        List<Index> path = new ArrayList<Index>();
        path.add(new Index(2100, 2000));
        path.add(new Index(2100, 2100));
        syncBaseItem.getSyncMovable().setPathToDestination(path);

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(0.8));
        Assert.assertEquals(new Index(2080, 2000), syncBaseItem.getSyncItemArea().getPosition());

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(0.4));
        Assert.assertEquals(new Index(2100, 2020), syncBaseItem.getSyncItemArea().getPosition());

        Assert.assertFalse(syncBaseItem.getSyncMovable().tick(0.8));
        Assert.assertEquals(new Index(2100, 2100), syncBaseItem.getSyncItemArea().getPosition());
    }

    @Test
    @DirtiesContext
    public void testMovePath2() throws Exception {
        SyncBaseItem syncBaseItem = createSyncBaseItem();

        List<Index> path = new ArrayList<Index>();
        path.add(new Index(2100, 2000));
        path.add(new Index(2100, 2050));
        path.add(new Index(2100, 2010));
        path.add(new Index(2200, 2010));
        path.add(new Index(2300, 2010));
        syncBaseItem.getSyncMovable().setPathToDestination(path);

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(0.5));
        Assert.assertEquals(new Index(2050, 2000), syncBaseItem.getSyncItemArea().getPosition());
        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(0.5));
        Assert.assertEquals(new Index(2100, 2000), syncBaseItem.getSyncItemArea().getPosition());
        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(0.5));
        Assert.assertEquals(new Index(2100, 2050), syncBaseItem.getSyncItemArea().getPosition());
        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(0.5));
        Assert.assertEquals(new Index(2110, 2010), syncBaseItem.getSyncItemArea().getPosition());
        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(0.5));
        Assert.assertEquals(new Index(2160, 2010), syncBaseItem.getSyncItemArea().getPosition());
        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(0.5));
        Assert.assertEquals(new Index(2210, 2010), syncBaseItem.getSyncItemArea().getPosition());
        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(0.5));
        Assert.assertEquals(new Index(2260, 2010), syncBaseItem.getSyncItemArea().getPosition());
        Assert.assertFalse(syncBaseItem.getSyncMovable().tick(0.5));
        Assert.assertEquals(new Index(2300, 2010), syncBaseItem.getSyncItemArea().getPosition());
    }
}
