package com.btxtech.game.services.item;

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.itemType.MovableType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncMovable;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.TestGlobalServices;
import com.btxtech.game.services.connection.ServerConnectionService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.ServerItemService;
import com.btxtech.game.services.planet.ServerTerrainService;
import com.btxtech.game.services.planet.impl.ServerPlanetServicesImpl;
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
    private PlanetSystemService planetSystemService;

    private SyncBaseItem createSyncBaseItem() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServicesImpl serverPlanetServices = (ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        // Mock
        ServerItemService serverItemServiceMock = EasyMock.createNiceMock(ServerItemService.class);
        EasyMock.expect(serverItemServiceMock.hasItemsInRectangle(EasyMock.<Rectangle>anyObject())).andReturn(false).anyTimes();
        EasyMock.replay(serverItemServiceMock);

        ServerConnectionService connectionService = EasyMock.createNiceMock(ServerConnectionService.class);
        EasyMock.expect(connectionService.getGameEngineMode()).andReturn(GameEngineMode.MASTER).anyTimes();
        EasyMock.replay(connectionService);

        ServerTerrainService mockTerrainService = EasyMock.createNiceMock(ServerTerrainService.class);
        EasyMock.replay(mockTerrainService);

        TestGlobalServices testGlobalServices = new TestGlobalServices();

        serverPlanetServices.setServerItemService(serverItemServiceMock);
        serverPlanetServices.setTerrainService(mockTerrainService);

        Id id = new Id(1, 1);
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(2000, 2000), id, testGlobalServices, serverPlanetServices);
        SyncMovable syncMovable = syncBaseItem.getSyncMovable();
        // Set speed to 100
        syncMovable.getMovableType().changeTo(new MovableType(100));
        Assert.assertFalse(syncMovable.tick(1.0));

        return syncBaseItem;
    }

    @Test
    @DirtiesContext
    public void testMoveHorizontallyLine1() throws Exception {
        SyncBaseItem syncBaseItem = createSyncBaseItem();

        List<Index> path = new ArrayList<>();
        path.add(new Index(3000, 2000));
        syncBaseItem.getSyncMovable().setPathToDestination(path, MathHelper.WEST);

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(1.0));
        Assert.assertEquals(new Index(2100, 2000), syncBaseItem.getSyncItemArea().getPosition());

        Assert.assertFalse(syncBaseItem.getSyncMovable().tick(9.0));
        Assert.assertEquals(new Index(3000, 2000), syncBaseItem.getSyncItemArea().getPosition());
    }

    @Test
    @DirtiesContext
    public void testMoveHorizontallyLine2() throws Exception {
        SyncBaseItem syncBaseItem = createSyncBaseItem();

        List<Index> path = new ArrayList<>();
        path.add(new Index(3000, 2000));
        syncBaseItem.getSyncMovable().setPathToDestination(path, MathHelper.WEST);

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

        List<Index> path = new ArrayList<>();
        path.add(new Index(2100, 2000));
        syncBaseItem.getSyncMovable().setPathToDestination(path, MathHelper.WEST);

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

        List<Index> path = new ArrayList<>();
        path.add(new Index(2000, 3000));
        syncBaseItem.getSyncMovable().setPathToDestination(path, MathHelper.WEST);

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(1.0));
        Assert.assertEquals(new Index(2000, 2100), syncBaseItem.getSyncItemArea().getPosition());

        Assert.assertFalse(syncBaseItem.getSyncMovable().tick(9.0));
        Assert.assertEquals(new Index(2000, 3000), syncBaseItem.getSyncItemArea().getPosition());
    }

    @Test
    @DirtiesContext
    public void testMoveVerticallyLine2() throws Exception {
        SyncBaseItem syncBaseItem = createSyncBaseItem();

        List<Index> path = new ArrayList<>();
        path.add(new Index(2000, 3000));
        syncBaseItem.getSyncMovable().setPathToDestination(path, MathHelper.WEST);

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

        List<Index> path = new ArrayList<>();
        path.add(new Index(2000, 2050));
        syncBaseItem.getSyncMovable().setPathToDestination(path, MathHelper.WEST);

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

        List<Index> path = new ArrayList<>();
        path.add(new Index(3000, 3000));
        syncBaseItem.getSyncMovable().setPathToDestination(path, MathHelper.WEST);

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(1.0));
        Assert.assertEquals(new Index(2071, 2071), syncBaseItem.getSyncItemArea().getPosition());

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(13.14));
        Assert.assertEquals(new Index(3000, 3000), syncBaseItem.getSyncItemArea().getPosition());
    }

    @Test
    @DirtiesContext
    public void testMoveDiagonallyLine2() throws Exception {
        SyncBaseItem syncBaseItem = createSyncBaseItem();

        List<Index> path = new ArrayList<>();
        path.add(new Index(2100, 2100));
        syncBaseItem.getSyncMovable().setPathToDestination(path, MathHelper.WEST);

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

        List<Index> path = new ArrayList<>();
        path.add(new Index(2500, 3000));
        syncBaseItem.getSyncMovable().setPathToDestination(path, MathHelper.WEST);

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(0.4));
        Assert.assertEquals(new Index(2018, 2036), syncBaseItem.getSyncItemArea().getPosition());

        Assert.assertTrue(syncBaseItem.getSyncMovable().tick(10.78));
        Assert.assertEquals(new Index(2500, 3000), syncBaseItem.getSyncItemArea().getPosition());
    }

    @Test
    @DirtiesContext
    public void testMoveDiagonallyLine4() throws Exception {
        SyncBaseItem syncBaseItem = createSyncBaseItem();

        List<Index> path = new ArrayList<>();
        path.add(new Index(2700, 2400));
        syncBaseItem.getSyncMovable().setPathToDestination(path, MathHelper.WEST);

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

        List<Index> path = new ArrayList<>();
        path.add(new Index(2100, 2000));
        path.add(new Index(2100, 2100));
        syncBaseItem.getSyncMovable().setPathToDestination(path, MathHelper.WEST);

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

        List<Index> path = new ArrayList<>();
        path.add(new Index(2100, 2000));
        path.add(new Index(2100, 2050));
        path.add(new Index(2100, 2010));
        path.add(new Index(2200, 2010));
        path.add(new Index(2300, 2010));
        syncBaseItem.getSyncMovable().setPathToDestination(path, MathHelper.WEST);

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
