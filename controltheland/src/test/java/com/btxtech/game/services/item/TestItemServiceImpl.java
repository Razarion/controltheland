package com.btxtech.game.services.item;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.planet.ActionService;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.impl.ServerPlanetServicesImpl;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 26.08.2011
 * Time: 20:29:24
 */
public class TestItemServiceImpl extends AbstractServiceTest {
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private PlanetSystemService planetSystemService;

    @Test
    @DirtiesContext
    public void isSyncItemOverlapping() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServicesImpl serverPlanetServices = (ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        SimpleBase base1 = new SimpleBase(1, 1);
        SimpleBase base2 = new SimpleBase(2, 1);

        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isAlive(EasyMock.<SimpleBase>anyObject())).andReturn(true).anyTimes();
        serverPlanetServices.setBaseService(baseService);

        ActionService actionService = EasyMock.createNiceMock(ActionService.class);
        serverPlanetServices.setActionService(actionService);

        EasyMock.replay(baseService, actionService);


        ItemType itemType1 = serverItemTypeService.getItemType(TEST_HARVESTER_ITEM_ID);
        itemType1.setBoundingBox(new BoundingBox(80, ANGELS_24));
        serverPlanetServices.getItemService().createSyncObject(itemType1, new Index(4486, 1279), null, base1);

        ItemType itemType2 = serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        itemType2.setBoundingBox(new BoundingBox(36, ANGELS_24));
        SyncItem syncItem2 = serverPlanetServices.getItemService().createSyncObject(itemType2, new Index(1396, 2225), null, base2);


        Assert.assertFalse(serverPlanetServices.getItemService().isSyncItemOverlapping(syncItem2, new Index(1425, 2331), null, null));
    }

    @Test
    @DirtiesContext
    public void isSyncItemOverlappingAngel() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServicesImpl serverPlanetServices = (ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        SimpleBase base1 = new SimpleBase(1, 1);
        SimpleBase base2 = new SimpleBase(2, 1);

        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isAlive(EasyMock.<SimpleBase>anyObject())).andReturn(true).anyTimes();
        serverPlanetServices.setBaseService(baseService);

        ActionService actionService = EasyMock.createNiceMock(ActionService.class);
        serverPlanetServices.setActionService(actionService);

        EasyMock.replay(baseService, actionService);


        ItemType itemType1 = serverItemTypeService.getItemType(TEST_HARVESTER_ITEM_ID);
        itemType1.setBoundingBox(new BoundingBox(100, ANGELS_24));
        serverPlanetServices.getItemService().createSyncObject(itemType1, new Index(2800, 2600), null, base1);

        ItemType itemType2 = serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        itemType2.setBoundingBox(new BoundingBox(200, ANGELS_24));
        SyncItem syncItem2 = serverPlanetServices.getItemService().createSyncObject(itemType2, new Index(2940, 2609), null, base2);

        Assert.assertTrue(serverPlanetServices.getItemService().isSyncItemOverlapping(syncItem2, new Index(2500, 2600), null, null));
        Assert.assertFalse(serverPlanetServices.getItemService().isSyncItemOverlapping(syncItem2, new Index(2499, 2600), 0.2053953891897674, null));
    }
}
