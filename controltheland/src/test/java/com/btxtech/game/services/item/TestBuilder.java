package com.btxtech.game.services.item;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BuilderType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.MovableType;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.connection.ConnectionService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBuilder;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncMovable;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.terrain.TerrainService;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
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
public class TestBuilder extends AbstractServiceTest {
    @Autowired
    private ActionService actionService;
    @Autowired
    private ItemService itemService;
    private Services servicesMock;

    private SyncBaseItem createSyncBuilderItem() throws Exception {
        configureRealGame();

        // Mock
        ItemService itemServiceMock = EasyMock.createNiceMock(ItemService.class);
        EasyMock.expect(itemServiceMock.hasItemsInRectangle(EasyMock.<Rectangle>anyObject())).andReturn(false).anyTimes();
        EasyMock.expect(itemServiceMock.baseObjectExists(EasyMock.<SyncItem>anyObject())).andReturn(true).anyTimes();
        EasyMock.expect(itemServiceMock.createSyncObject(EasyMock.<ItemType>anyObject(),
                EasyMock.<Index>anyObject(),
                EasyMock.<SyncBaseItem>anyObject(),
                EasyMock.<SimpleBase>anyObject(),
                EasyMock.anyInt())).andAnswer(new IAnswer<SyncItem>() {
            @Override
            public SyncItem answer() throws Throwable {
                Id id2 = new Id(2, 1, 1);
                return createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(5000, 5350), id2, servicesMock);
            }
        }).once();
        EasyMock.replay(itemServiceMock);

        TerrainService terrainServiceMock = EasyMock.createNiceMock(TerrainService.class);
        EasyMock.expect(terrainServiceMock.correctPosition(EasyMock.<SyncItem>anyObject(), EasyMock.<Index>anyObject())).andReturn(new Index(5000, 5000));
        EasyMock.replay(terrainServiceMock);

        ConnectionService connectionServiceMock = EasyMock.createNiceMock(ConnectionService.class);
        EasyMock.replay(connectionServiceMock);

        AbstractBaseService abstractBaseServiceMock = EasyMock.createNiceMock(AbstractBaseService.class);
        EasyMock.replay(abstractBaseServiceMock);

        servicesMock = EasyMock.createNiceMock(Services.class);
        EasyMock.expect(servicesMock.getItemService()).andReturn(itemServiceMock).anyTimes();
        EasyMock.expect(servicesMock.getTerrainService()).andReturn(terrainServiceMock).anyTimes();
        EasyMock.expect(servicesMock.getConnectionService()).andReturn(connectionServiceMock).anyTimes();
        EasyMock.expect(servicesMock.getBaseService()).andReturn(abstractBaseServiceMock).anyTimes();
        EasyMock.replay(servicesMock);

        Id id = new Id(1, 1, 1);
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(5000, 5000), id, servicesMock);
        SyncMovable syncMovable = syncBaseItem.getSyncMovable();
        // Set speed to 100
        syncMovable.getMovableType().changeTo(new MovableType(100, syncMovable.getMovableType().getTerrainType()));
        Assert.assertFalse(syncMovable.tick(1.0));
        // Set buildup
        SyncBuilder syncBuilder = syncBaseItem.getSyncBuilder();
        syncBuilder.getBuilderType().changeTo(new BuilderType(11, 2, syncBuilder.getBuilderType().getAbleToBuild()));


        return syncBaseItem;
    }

    @Test
    @DirtiesContext
    public void testBuildup() throws Exception {
        SyncBaseItem syncBaseItem = createSyncBuilderItem();

        List<Index> path = new ArrayList<Index>();
        path.add(new Index(5000, 5000));
        path.add(new Index(5000, 5200));
        syncBaseItem.getSyncMovable().setPathToDestination(path);
        syncBaseItem.getSyncBuilder().setToBeBuildPosition(new Index(5000, 5290));
        syncBaseItem.getSyncBuilder().setToBeBuiltType((BaseItemType) itemService.getItemType(TEST_SIMPLE_BUILDING_ID));

        Assert.assertEquals(new Index(5000, 5000), syncBaseItem.getSyncItemArea().getPosition());
        Assert.assertNull(syncBaseItem.getSyncBuilder().getCurrentBuildup());

        Assert.assertTrue(syncBaseItem.tick(1));
        Assert.assertEquals(new Index(5000, 5100), syncBaseItem.getSyncItemArea().getPosition());
        Assert.assertNull(syncBaseItem.getSyncBuilder().getCurrentBuildup());

        Assert.assertTrue(syncBaseItem.tick(1));
        Assert.assertEquals(new Index(5000, 5200), syncBaseItem.getSyncItemArea().getPosition());
        Assert.assertNotNull(syncBaseItem.getSyncBuilder().getCurrentBuildup());
        Assert.assertEquals(0.2, syncBaseItem.getSyncBuilder().getCurrentBuildup().getBuildup(), 0.001);
        Assert.assertFalse(syncBaseItem.getSyncBuilder().getCurrentBuildup().isReady());

        Assert.assertTrue(syncBaseItem.tick(1));
        Assert.assertEquals(new Index(5000, 5200), syncBaseItem.getSyncItemArea().getPosition());
        Assert.assertNotNull(syncBaseItem.getSyncBuilder().getCurrentBuildup());
        Assert.assertEquals(0.4, syncBaseItem.getSyncBuilder().getCurrentBuildup().getBuildup(), 0.001);
        Assert.assertFalse(syncBaseItem.getSyncBuilder().getCurrentBuildup().isReady());

        Assert.assertTrue(syncBaseItem.tick(1));
        Assert.assertEquals(new Index(5000, 5200), syncBaseItem.getSyncItemArea().getPosition());
        Assert.assertNotNull(syncBaseItem.getSyncBuilder().getCurrentBuildup());
        Assert.assertEquals(0.6, syncBaseItem.getSyncBuilder().getCurrentBuildup().getBuildup(), 0.001);
        Assert.assertFalse(syncBaseItem.getSyncBuilder().getCurrentBuildup().isReady());

        Assert.assertTrue(syncBaseItem.tick(1));
        Assert.assertEquals(new Index(5000, 5200), syncBaseItem.getSyncItemArea().getPosition());
        Assert.assertNotNull(syncBaseItem.getSyncBuilder().getCurrentBuildup());
        Assert.assertEquals(0.8, syncBaseItem.getSyncBuilder().getCurrentBuildup().getBuildup(), 0.001);
        Assert.assertFalse(syncBaseItem.getSyncBuilder().getCurrentBuildup().isReady());

        SyncBaseItem buildup = syncBaseItem.getSyncBuilder().getCurrentBuildup();
        Assert.assertFalse(syncBaseItem.tick(1));
        Assert.assertEquals(new Index(5000, 5200), syncBaseItem.getSyncItemArea().getPosition());
        Assert.assertNull(syncBaseItem.getSyncBuilder().getCurrentBuildup());
        Assert.assertEquals(1.0, buildup.getBuildup(), 0.001);
        Assert.assertTrue(buildup.isReady());
    }

    @Test
    @DirtiesContext
    public void testFinalizeBuild() throws Exception {
        SyncBaseItem syncBaseItem = createSyncBuilderItem();

        Id id2 = new Id(2, 1, 1);
        SyncBaseItem buildupBaseItem = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(5000, 5290), id2, servicesMock);
        buildupBaseItem.setBuildup(0.5);
        List<Index> path = new ArrayList<Index>();
        path.add(new Index(5000, 5000));
        path.add(new Index(5000, 5200));
        syncBaseItem.getSyncMovable().setPathToDestination(path);
        syncBaseItem.getSyncBuilder().setCurrentBuildup(buildupBaseItem);
        syncBaseItem.getSyncBuilder().setToBeBuildPosition(buildupBaseItem.getSyncItemArea().getPosition());
        syncBaseItem.getSyncBuilder().setToBeBuiltType(buildupBaseItem.getBaseItemType());

        Assert.assertEquals(new Index(5000, 5000), syncBaseItem.getSyncItemArea().getPosition());
        Assert.assertNotNull(syncBaseItem.getSyncBuilder().getCurrentBuildup());
        Assert.assertEquals(0.5, syncBaseItem.getSyncBuilder().getCurrentBuildup().getBuildup(), 0.001);
        Assert.assertFalse(syncBaseItem.getSyncBuilder().getCurrentBuildup().isReady());

        Assert.assertTrue(syncBaseItem.tick(1));
        Assert.assertEquals(new Index(5000, 5100), syncBaseItem.getSyncItemArea().getPosition());
        Assert.assertNotNull(syncBaseItem.getSyncBuilder().getCurrentBuildup());
        Assert.assertEquals(0.5, syncBaseItem.getSyncBuilder().getCurrentBuildup().getBuildup(), 0.001);
        Assert.assertFalse(syncBaseItem.getSyncBuilder().getCurrentBuildup().isReady());

        Assert.assertTrue(syncBaseItem.tick(1));
        Assert.assertEquals(new Index(5000, 5200), syncBaseItem.getSyncItemArea().getPosition());
        Assert.assertNotNull(syncBaseItem.getSyncBuilder().getCurrentBuildup());
        Assert.assertEquals(0.7, syncBaseItem.getSyncBuilder().getCurrentBuildup().getBuildup(), 0.001);
        Assert.assertFalse(syncBaseItem.getSyncBuilder().getCurrentBuildup().isReady());

        Assert.assertTrue(syncBaseItem.tick(1));
        Assert.assertEquals(new Index(5000, 5200), syncBaseItem.getSyncItemArea().getPosition());
        Assert.assertNotNull(syncBaseItem.getSyncBuilder().getCurrentBuildup());
        Assert.assertEquals(0.9, syncBaseItem.getSyncBuilder().getCurrentBuildup().getBuildup(), 0.001);
        Assert.assertFalse(syncBaseItem.getSyncBuilder().getCurrentBuildup().isReady());

        Assert.assertFalse(syncBaseItem.tick(1));
        Assert.assertEquals(new Index(5000, 5200), syncBaseItem.getSyncItemArea().getPosition());
        Assert.assertNull(syncBaseItem.getSyncBuilder().getCurrentBuildup());
        Assert.assertEquals(1.0, buildupBaseItem.getBuildup(), 0.001);
        Assert.assertTrue(buildupBaseItem.isReady());
    }
}
