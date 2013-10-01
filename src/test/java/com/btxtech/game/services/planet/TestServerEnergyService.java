package com.btxtech.game.services.planet;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.packets.EnergyPacket;
import com.btxtech.game.services.AbstractServiceTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 21.02.13
 * Time: 15:10
 */
public class TestServerEnergyService extends AbstractServiceTest {
    @Autowired
    private PlanetSystemService planetSystemService;

    @Test
    @DirtiesContext
    public void testConsumingGenerating() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getOrCreateBase(); // Create Base
        ServerEnergyService serverEnergyService = planetSystemService.getServerPlanetServices().getEnergyService();
        Assert.assertEquals(0, serverEnergyService.getConsuming());
        Assert.assertEquals(0, serverEnergyService.getGenerating());
        // Build consumer
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_CONSUMER_TYPE_ID);
        waitForActionServiceDone();
        Assert.assertEquals(20, serverEnergyService.getConsuming());
        Assert.assertEquals(0, serverEnergyService.getGenerating());
        // Build generator
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(500, 500), TEST_GENERATOR_TYPE_ID);
        waitForActionServiceDone();
        Assert.assertEquals(20, serverEnergyService.getConsuming());
        Assert.assertEquals(30, serverEnergyService.getGenerating());
        // Sell consumer
        getMovableService().sellItem(START_UID_1, getFirstSynItemId(getOrCreateBase(), TEST_CONSUMER_TYPE_ID));
        Assert.assertEquals(0, serverEnergyService.getConsuming());
        Assert.assertEquals(30, serverEnergyService.getGenerating());
        // Sell generator
        getMovableService().sellItem(START_UID_1, getFirstSynItemId(getOrCreateBase(), TEST_GENERATOR_TYPE_ID));
        Assert.assertEquals(0, serverEnergyService.getConsuming());
        Assert.assertEquals(0, serverEnergyService.getGenerating());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testMultiple() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Build consumer
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_CONSUMER_TYPE_ID);
        waitForActionServiceDone();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 2000), TEST_CONSUMER_TYPE_ID);
        waitForActionServiceDone();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 3000), TEST_CONSUMER_TYPE_ID);
        waitForActionServiceDone();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 4000), TEST_CONSUMER_TYPE_ID);
        waitForActionServiceDone();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(2000, 1000), TEST_GENERATOR_TYPE_ID);
        waitForActionServiceDone();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(2000, 2000), TEST_GENERATOR_TYPE_ID);
        waitForActionServiceDone();
        ServerEnergyService serverEnergyService = planetSystemService.getServerPlanetServices().getEnergyService();
        Assert.assertEquals(80, serverEnergyService.getConsuming());
        Assert.assertEquals(60, serverEnergyService.getGenerating());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testRealGameInfo() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Build consumer
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_CONSUMER_TYPE_ID);
        waitForActionServiceDone();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 2000), TEST_CONSUMER_TYPE_ID);
        waitForActionServiceDone();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 3000), TEST_CONSUMER_TYPE_ID);
        waitForActionServiceDone();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 4000), TEST_CONSUMER_TYPE_ID);
        waitForActionServiceDone();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(2000, 1000), TEST_GENERATOR_TYPE_ID);
        waitForActionServiceDone();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(2000, 2000), TEST_GENERATOR_TYPE_ID);
        waitForActionServiceDone();
        RealGameInfo realGameInfo = getMovableService().getRealGameInfo(START_UID_1, null);
        Assert.assertEquals(80, realGameInfo.getEnergyConsuming());
        Assert.assertEquals(60, realGameInfo.getEnergyGenerating());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testPacketSent() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Build consumer
        getOrCreateBase();
        Thread.sleep(1000); // Wait for money package
        clearPackets();
        // Build consumer
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_CONSUMER_TYPE_ID);
        waitForActionServiceDone();
        EnergyPacket energyPacket = new EnergyPacket();
        energyPacket.setConsuming(20);
        energyPacket.setGenerating(0);
        assertPackagesIgnoreSyncItemInfoAndClear(true, energyPacket);
        // Build generator
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(2000, 2000), TEST_GENERATOR_TYPE_ID);
        waitForActionServiceDone();
        energyPacket = new EnergyPacket();
        energyPacket.setConsuming(20);
        energyPacket.setGenerating(30);
        assertPackagesIgnoreSyncItemInfoAndClear(true, energyPacket);
        // Sell consumer
        getMovableService().sellItem(START_UID_1, getFirstSynItemId(getOrCreateBase(), TEST_CONSUMER_TYPE_ID));
        energyPacket = new EnergyPacket();
        energyPacket.setConsuming(0);
        energyPacket.setGenerating(30);
        // Sell consumer
        getMovableService().sellItem(START_UID_1, getFirstSynItemId(getOrCreateBase(), TEST_GENERATOR_TYPE_ID));
        energyPacket = new EnergyPacket();
        energyPacket.setConsuming(0);
        energyPacket.setGenerating(0);
        assertPackagesIgnoreSyncItemInfoAndClear(true, energyPacket);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testConsumerOperationState() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Build consumer
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_CONSUMER_TYPE_ID);
        waitForActionServiceDone();
        // Verify
        SyncBaseItem syncBaseItem = (SyncBaseItem) planetSystemService.getServerPlanetServices().getItemService().getItem(getFirstSynItemId(TEST_CONSUMER_TYPE_ID));
        Assert.assertFalse(syncBaseItem.getSyncConsumer().isOperating());
        // Build generator
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(2000, 2000), TEST_GENERATOR_TYPE_ID);
        waitForActionServiceDone();
        // Verify
        Assert.assertTrue(syncBaseItem.getSyncConsumer().isOperating());
        // Sell generator
        getMovableService().sellItem(START_UID_1, getFirstSynItemId(getOrCreateBase(), TEST_GENERATOR_TYPE_ID));
        // Verify
        Assert.assertFalse(syncBaseItem.getSyncConsumer().isOperating());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testConsumerOperationStateAndMovable() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(2000, 2000), TEST_GENERATOR_TYPE_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_CONSUMER_ATTACK_MOVABLE_TYPE_ID);
        waitForActionServiceDone();
        SyncBaseItem consumerMovableAttacker = (SyncBaseItem) planetSystemService.getServerPlanetServices().getItemService().getItem(getFirstSynItemId(TEST_CONSUMER_ATTACK_MOVABLE_TYPE_ID));
        // Movable and energy
        sendMoveCommand(consumerMovableAttacker.getId(), new Index(500, 500));
        waitForActionServiceDone();
        // Verify
        Assert.assertEquals(new Index(500, 500), consumerMovableAttacker.getSyncItemArea().getPosition());
        sendMoveCommand(consumerMovableAttacker.getId(), new Index(3000, 3000));
        waitForActionServiceDone();
        Assert.assertEquals(new Index(3000,3000), consumerMovableAttacker.getSyncItemArea().getPosition());
        // Sell generator & Verify
        getMovableService().sellItem(START_UID_1, getFirstSynItemId(getOrCreateBase(), TEST_GENERATOR_TYPE_ID));
        sendMoveCommand(consumerMovableAttacker.getId(), new Index(500, 500));
        waitForActionServiceDone();
        Assert.assertEquals(new Index(3000,3000), consumerMovableAttacker.getSyncItemArea().getPosition());
        // Build generator move & Verify
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(2000, 2000), TEST_GENERATOR_TYPE_ID);
        waitForActionServiceDone();
        sendMoveCommand(consumerMovableAttacker.getId(), new Index(500, 500));
        waitForActionServiceDone();
        Assert.assertEquals(new Index(500,500), consumerMovableAttacker.getSyncItemArea().getPosition());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testConsumerOperationStateAndAttack() throws Exception {
        configureSimplePlanetNoResources();

        // create target
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SyncBaseItem target = (SyncBaseItem) planetSystemService.getServerPlanetServices().getItemService().getItem(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID));
        sendMoveCommand(target.getId(), new Index(10000, 10000));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_CONSUMER_ATTACK_MOVABLE_TYPE_ID);
        waitForActionServiceDone();
        SyncBaseItem consumerMovableAttacker = (SyncBaseItem) planetSystemService.getServerPlanetServices().getItemService().getItem(getFirstSynItemId(TEST_CONSUMER_ATTACK_MOVABLE_TYPE_ID));
        // Attack and verify
        sendAttackCommand(consumerMovableAttacker.getId(), target.getId());
        waitForActionServiceDone();
        Assert.assertTrue(target.isAlive());
        // Attack and verify
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(2000, 2000), TEST_GENERATOR_TYPE_ID);
        waitForActionServiceDone();
        sendAttackCommand(consumerMovableAttacker.getId(), target.getId());
        waitForActionServiceDone();
        Assert.assertFalse(target.isAlive());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
