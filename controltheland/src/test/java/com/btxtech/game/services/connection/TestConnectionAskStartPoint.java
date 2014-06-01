package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.packets.AccountBalancePacket;
import com.btxtech.game.jsre.common.packets.BaseChangedPacket;
import com.btxtech.game.jsre.common.packets.BaseLostPacket;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.services.AbstractServiceTest;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Iterator;
import java.util.List;

/**
 * User: beat
 * Date: 16.04.2011
 * Time: 12:33:58
 */
public class TestConnectionAskStartPoint extends AbstractServiceTest {

    @Test
    @DirtiesContext
    public void newSellCreateBase() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        RealGameInfo realGameInfo = getMovableService().getRealGameInfo(START_UID_1, null);
        Assert.assertNotNull(realGameInfo.getStartPointInfo());
        clearPackets();
        SimpleBase simpleBase = getMovableService().createBase(START_UID_1, new Index(1000, 1000)).getBase();
        List<Packet> packets = getMovableService().getSyncInfo(START_UID_1, false);
        Assert.assertEquals(BaseChangedPacket.Type.CREATED, ((BaseChangedPacket) packets.get(0)).getType());
        Assert.assertEquals(simpleBase, ((BaseChangedPacket) packets.get(0)).getBaseAttributes().getSimpleBase());
        Assert.assertEquals(1000.0, ((AccountBalancePacket) packets.get(2)).getAccountBalance(), 0.001);
        // TODO failed (ClassCastException): 23.11.2013, 06.12.2013
        Assert.assertEquals(new Index(1000, 1000), ((SyncItemInfo) packets.get(3)).getPosition());
        Assert.assertEquals(TEST_START_BUILDER_ITEM_ID, ((SyncItemInfo) packets.get(3)).getItemTypeId());
        Assert.assertEquals(simpleBase, ((SyncItemInfo) packets.get(3)).getBase());
        // Sell
        getMovableService().sellItem(START_UID_1, getFirstSynItemId(TEST_START_BUILDER_ITEM_ID));
        Thread.sleep(200);
        packets = getMovableService().getSyncInfo(START_UID_1, false);
        Assert.assertEquals(BaseChangedPacket.Type.REMOVED, ((BaseChangedPacket) packets.get(0)).getType());
        Assert.assertEquals(simpleBase, ((BaseChangedPacket) packets.get(0)).getBaseAttributes().getSimpleBase());
        realGameInfo = ((BaseLostPacket) packets.get(1)).getRealGameInfo();
        Assert.assertEquals(-3, realGameInfo.getBase().getBaseId());
        Assert.assertEquals(TEST_PLANET_1_ID, realGameInfo.getBase().getPlanetId());
        Assert.assertEquals(0.0, realGameInfo.getAccountBalance(), 0.001);
        Assert.assertEquals(0, realGameInfo.getEnergyConsuming());
        Assert.assertEquals(0, realGameInfo.getEnergyGenerating());
        Assert.assertEquals(0, realGameInfo.getHouseSpace());
        Assert.assertEquals(1, realGameInfo.getAllBase().size());
        Assert.assertEquals(TEST_START_BUILDER_ITEM_ID, realGameInfo.getStartPointInfo().getBaseItemTypeId());
        Assert.assertEquals(300, realGameInfo.getStartPointInfo().getItemFreeRange());
        Assert.assertNull(realGameInfo.getStartPointInfo().getSuggestedPosition());
        // Create Base
        simpleBase = getMovableService().createBase(START_UID_1, new Index(1000, 1000)).getBase();
        packets = getMovableService().getSyncInfo(START_UID_1, false);
        Assert.assertEquals(BaseChangedPacket.Type.CREATED, ((BaseChangedPacket) packets.get(0)).getType());
        Assert.assertEquals(simpleBase, ((BaseChangedPacket) packets.get(0)).getBaseAttributes().getSimpleBase());
        // TODO failed: 20.01.2014
        Assert.assertEquals(1000.0, ((AccountBalancePacket) packets.get(2)).getAccountBalance(), 0.001);
        Assert.assertEquals(new Index(1000, 1000), ((SyncItemInfo) packets.get(3)).getPosition());
        Assert.assertEquals(TEST_START_BUILDER_ITEM_ID, ((SyncItemInfo) packets.get(3)).getItemTypeId());
        Assert.assertEquals(simpleBase, ((SyncItemInfo) packets.get(3)).getBase());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void newSurrenderCreateBase() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        RealGameInfo realGameInfo = getMovableService().getRealGameInfo(START_UID_1, null);
        Assert.assertNotNull(realGameInfo.getStartPointInfo());
        SimpleBase simpleBase = getMovableService().createBase(START_UID_1, new Index(1000, 1000)).getBase();
        clearPackets();
        // Surrender
        getMovableService().surrenderBase();
        Thread.sleep(200);
        List<Packet> packets = getMovableService().getSyncInfo(START_UID_1, false);
        // Remove account balance package
        for (Iterator<Packet> iterator = packets.iterator(); iterator.hasNext(); ) {
            Packet packet = iterator.next();
            if (packet instanceof AccountBalancePacket) {
                iterator.remove();
            }
        }
        Assert.assertEquals(BaseChangedPacket.Type.REMOVED, ((BaseChangedPacket) packets.get(0)).getType());
        Assert.assertEquals(simpleBase, ((BaseChangedPacket) packets.get(0)).getBaseAttributes().getSimpleBase());
        realGameInfo = ((BaseLostPacket) packets.get(1)).getRealGameInfo();
        Assert.assertEquals(-3, realGameInfo.getBase().getBaseId());
        Assert.assertEquals(TEST_PLANET_1_ID, realGameInfo.getBase().getPlanetId());
        Assert.assertEquals(0.0, realGameInfo.getAccountBalance(), 0.001);
        Assert.assertEquals(0, realGameInfo.getEnergyConsuming());
        Assert.assertEquals(0, realGameInfo.getEnergyGenerating());
        Assert.assertEquals(0, realGameInfo.getHouseSpace());
        Assert.assertEquals(1, realGameInfo.getAllBase().size());
        Assert.assertEquals(TEST_START_BUILDER_ITEM_ID, realGameInfo.getStartPointInfo().getBaseItemTypeId());
        Assert.assertEquals(300, realGameInfo.getStartPointInfo().getItemFreeRange());
        Assert.assertNull(realGameInfo.getStartPointInfo().getSuggestedPosition());
        // Create Base
        simpleBase = getMovableService().createBase(START_UID_1, new Index(2000, 2000)).getBase();
        packets = getMovableService().getSyncInfo(START_UID_1, false);
        Assert.assertEquals(BaseChangedPacket.Type.CREATED, ((BaseChangedPacket) packets.get(0)).getType());
        Assert.assertEquals(simpleBase, ((BaseChangedPacket) packets.get(0)).getBaseAttributes().getSimpleBase());
        Assert.assertEquals(1000.0, ((AccountBalancePacket) packets.get(2)).getAccountBalance(), 0.001);
        Assert.assertEquals(new Index(2000, 2000), ((SyncItemInfo) packets.get(3)).getPosition());
        Assert.assertEquals(TEST_START_BUILDER_ITEM_ID, ((SyncItemInfo) packets.get(3)).getItemTypeId());
        Assert.assertEquals(simpleBase, ((SyncItemInfo) packets.get(3)).getBase());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void newKillCreateBase() throws Exception {
        configureSimplePlanetNoResources();

        // Create Attacker
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().getRealGameInfo(START_UID_1, null); // Make connection
        getMovableService().createBase(START_UID_1, new Index(5000, 5000)).getBase();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(5200, 5200), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendMoveCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), new Index(4000, 4000));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        RealGameInfo realGameInfo = getMovableService().getRealGameInfo(START_UID_1, null);
        Assert.assertNotNull(realGameInfo.getStartPointInfo());
        SimpleBase simpleBase = getMovableService().createBase(START_UID_1, new Index(1000, 1000)).getBase();
        sendMoveCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(3800, 4000));
        clearPackets();
        waitForActionServiceDone();
        // Get Killed
        Thread.sleep(200);
        List<Packet> packets = getMovableService().getSyncInfo(START_UID_1, false);
        Assert.assertEquals(BaseChangedPacket.Type.REMOVED, ((BaseChangedPacket) packets.get(1)).getType());
        Assert.assertEquals(simpleBase, ((BaseChangedPacket) packets.get(1)).getBaseAttributes().getSimpleBase());
        realGameInfo = ((BaseLostPacket) packets.get(2)).getRealGameInfo();
        Assert.assertEquals(-3, realGameInfo.getBase().getBaseId());
        Assert.assertEquals(TEST_PLANET_1_ID, realGameInfo.getBase().getPlanetId());
        Assert.assertEquals(0.0, realGameInfo.getAccountBalance(), 0.001);
        Assert.assertEquals(0, realGameInfo.getEnergyConsuming());
        Assert.assertEquals(0, realGameInfo.getEnergyGenerating());
        Assert.assertEquals(0, realGameInfo.getHouseSpace());
        Assert.assertEquals(2, realGameInfo.getAllBase().size());
        Assert.assertEquals(TEST_START_BUILDER_ITEM_ID, realGameInfo.getStartPointInfo().getBaseItemTypeId());
        Assert.assertEquals(300, realGameInfo.getStartPointInfo().getItemFreeRange());
        Assert.assertNull(realGameInfo.getStartPointInfo().getSuggestedPosition());
        // Create Base
        simpleBase = getMovableService().createBase(START_UID_1, new Index(1000, 1000)).getBase();
        packets = getMovableService().getSyncInfo(START_UID_1, false);
        Assert.assertEquals(BaseChangedPacket.Type.CREATED, ((BaseChangedPacket) packets.get(0)).getType());
        Assert.assertEquals(simpleBase, ((BaseChangedPacket) packets.get(0)).getBaseAttributes().getSimpleBase());
        // TODO failed on: 28.05.2014, 01.06.2014
        Assert.assertEquals(1000.0, ((AccountBalancePacket) packets.get(2)).getAccountBalance(), 0.001);
        Assert.assertEquals(new Index(1000, 1000), ((SyncItemInfo) packets.get(3)).getPosition());
        Assert.assertEquals(TEST_START_BUILDER_ITEM_ID, ((SyncItemInfo) packets.get(3)).getItemTypeId());
        Assert.assertEquals(simpleBase, ((SyncItemInfo) packets.get(3)).getBase());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
