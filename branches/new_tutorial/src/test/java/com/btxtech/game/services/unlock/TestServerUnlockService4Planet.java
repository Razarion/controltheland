package com.btxtech.game.services.unlock;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockContainer;
import com.btxtech.game.jsre.common.packets.LevelPacket;
import com.btxtech.game.jsre.common.packets.UnlockContainerPacket;
import com.btxtech.game.jsre.common.packets.XpPacket;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.history.DbHistoryElement;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.XpService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 15.02.13
 * Time: 17:33
 */
public class TestServerUnlockService4Planet extends AbstractServiceTest {
    @Autowired
    private ServerUnlockService unlockService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private XpService xpService;

    @Test
    @DirtiesContext
    public void testReachUnlockPlanet() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Preparation
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_2_ID);
        dbPlanet.setUnlockRazarion(13);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.deactivatePlanet(TEST_PLANET_2_ID);
        planetSystemService.activatePlanet(TEST_PLANET_2_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(getUserState(), TEST_LEVEL_4_REAL);
        getOrCreateBase();
        xpService.onReward(getUserState(), 100001);
        getMovableService().surrenderBase();
        Assert.assertEquals(TEST_PLANET_1_ID, getMovableService().getRealGameInfo(START_UID_1, null).getPlanetInfo().getPlanetId());
        getUserState().setRazarion(100);
        UnlockContainer unlockContainer = unlockService.unlockPlanet(TEST_PLANET_2_ID);
        Assert.assertEquals(87, getUserState().getRazarion());
        getMovableService().surrenderBase();
        Assert.assertEquals(TEST_PLANET_2_ID, getMovableService().getRealGameInfo(START_UID_1, null).getPlanetInfo().getPlanetId());
        Assert.assertTrue(unlockContainer.containsPlanetId(TEST_PLANET_2_ID));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testUnlockReachPlanet() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Preparation
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_2_ID);
        dbPlanet.setUnlockRazarion(15);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.deactivatePlanet(TEST_PLANET_2_ID);
        planetSystemService.activatePlanet(TEST_PLANET_2_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(getUserState(), TEST_LEVEL_4_REAL);
        getOrCreateBase();
        getUserState().setRazarion(100);
        UnlockContainer unlockContainer = unlockService.unlockPlanet(TEST_PLANET_2_ID);
        Assert.assertEquals(85, getUserState().getRazarion());
        Assert.assertTrue(unlockContainer.containsPlanetId(TEST_PLANET_2_ID));
        xpService.onReward(getUserState(), 100001);
        getMovableService().surrenderBase();
        Assert.assertEquals(TEST_PLANET_2_ID, getMovableService().getRealGameInfo(START_UID_1, null).getPlanetInfo().getPlanetId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testBacktrack1() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Preparation
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet3 = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_3_ID);
        dbPlanet3.setUnlockRazarion(15);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet3);
        planetSystemService.deactivatePlanet(TEST_PLANET_3_ID);
        planetSystemService.activatePlanet(TEST_PLANET_3_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(getUserState(), TEST_LEVEL_6_REAL);
        createBase(new Index(500, 500));
        getUserState().setRazarion(100);
        Assert.assertEquals(TEST_PLANET_2_ID, getMovableService().getRealGameInfo(START_UID_1, null).getPlanetInfo().getPlanetId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testBacktrack2() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet2 = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_2_ID);
        dbPlanet2.setUnlockRazarion(15);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet2);
        planetSystemService.deactivatePlanet(TEST_PLANET_2_ID);
        planetSystemService.activatePlanet(TEST_PLANET_2_ID);
        DbPlanet dbPlanet3 = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_3_ID);
        dbPlanet3.setUnlockRazarion(15);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet3);
        planetSystemService.deactivatePlanet(TEST_PLANET_3_ID);
        planetSystemService.activatePlanet(TEST_PLANET_3_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(getUserState(), TEST_LEVEL_6_REAL);
        getOrCreateBase();
        getUserState().setRazarion(100);
        Assert.assertEquals(TEST_PLANET_1_ID, getMovableService().getRealGameInfo(START_UID_1, null).getPlanetInfo().getPlanetId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testReachPlanetPacketSent() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Preparation
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_2_ID);
        dbPlanet.setUnlockRazarion(15);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.deactivatePlanet(TEST_PLANET_2_ID);
        planetSystemService.activatePlanet(TEST_PLANET_2_ID);
        userGuidanceService.activateLevels();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(getUserState(), TEST_LEVEL_2_REAL);
        getOrCreateBase();
        Thread.sleep(1000);
        clearPackets();
        userGuidanceService.promote(getUserState(), TEST_LEVEL_5_REAL);
        XpPacket xpPacket = new XpPacket();
        xpPacket.setXp(0);
        xpPacket.setXp2LevelUp(220);
        LevelPacket levelPacket = new LevelPacket();
        Map<Integer, Integer> itemTypeLimitation = new HashMap<>();
        itemTypeLimitation.put(TEST_START_BUILDER_ITEM_ID, 10);
        itemTypeLimitation.put(TEST_FACTORY_ITEM_ID, 10);
        itemTypeLimitation.put(TEST_ATTACK_ITEM_ID, 10);
        itemTypeLimitation.put(TEST_HARVESTER_ITEM_ID, 10);
        itemTypeLimitation.put(TEST_CONTAINER_ITEM_ID, 10);
        itemTypeLimitation.put(TEST_SIMPLE_BUILDING_ID, 10);
        levelPacket.setLevel(new LevelScope(new PlanetLiteInfo(TEST_PLANET_2_ID, TEST_PLANET_2, 15), TEST_LEVEL_5_REAL_ID, TEST_LEVEL_5_REAL, itemTypeLimitation, 220));
        assertPackagesIgnoreSyncItemInfoAndClear(xpPacket, levelPacket);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void unlockNoRazarion() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_2_ID);
        dbPlanet.setUnlockRazarion(15);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.deactivatePlanet(TEST_PLANET_2_ID);
        planetSystemService.activatePlanet(TEST_PLANET_2_ID);
        PlanetInfo planetInfo = dbPlanet.createPlanetInfo();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getUserState().setRazarion(0);
        try {
            unlockService.unlockPlanet(TEST_PLANET_2_ID);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Not enough razarion to unlock: PlanetLiteInfo{planetId=2, name='TEST_PLANET_2', unlockRazarion=15} user: UserState: user=null", e.getMessage());
        }
        Assert.assertEquals(0, getUserState().getRazarion());
        Assert.assertTrue(unlockService.isPlanetLocked(planetInfo.getPlanetLiteInfo(), getUserState()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void unlockNotLocked() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getUserState().setRazarion(0);
        try {
            unlockService.unlockPlanet(TEST_PLANET_2_ID);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Planet can not be unlocked: PlanetLiteInfo{planetId=2, name='TEST_PLANET_2', unlockRazarion=null}", e.getMessage());
        }
        Assert.assertEquals(0, getUserState().getRazarion());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void isPlanetLocked() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        PlanetLiteInfo planetInfo1 = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID).createPlanetInfo().getPlanetLiteInfo();
        DbPlanet dbPlanet2 = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_2_ID);
        dbPlanet2.setUnlockRazarion(15);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet2);
        planetSystemService.deactivatePlanet(TEST_PLANET_2_ID);
        planetSystemService.activatePlanet(TEST_PLANET_2_ID);
        PlanetLiteInfo planetInfo2 = dbPlanet2.createPlanetInfo().getPlanetLiteInfo();
        DbPlanet dbPlanet3 = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_3_ID);
        dbPlanet3.setUnlockRazarion(15);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet3);
        planetSystemService.deactivatePlanet(TEST_PLANET_3_ID);
        planetSystemService.activatePlanet(TEST_PLANET_3_ID);
        PlanetLiteInfo planetInfo3 = dbPlanet3.createPlanetInfo().getPlanetLiteInfo();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(getUserState(), TEST_LEVEL_4_REAL);
        getOrCreateBase(); // Create Base
        getUserState().setRazarion(100);
        unlockService.unlockPlanet(TEST_PLANET_2_ID);
        Assert.assertFalse(unlockService.isPlanetLocked(planetInfo1, getUserState()));
        Assert.assertFalse(unlockService.isPlanetLocked(planetInfo2, getUserState()));
        Assert.assertTrue(unlockService.isPlanetLocked(planetInfo3, getUserState()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testHistory() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet2 = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_2_ID);
        dbPlanet2.setUnlockRazarion(15);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet2);
        planetSystemService.deactivatePlanet(TEST_PLANET_2_ID);
        planetSystemService.activatePlanet(TEST_PLANET_2_ID);
        DbPlanet dbPlanet3 = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_3_ID);
        dbPlanet3.setUnlockRazarion(20);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet3);
        planetSystemService.deactivatePlanet(TEST_PLANET_3_ID);
        planetSystemService.activatePlanet(TEST_PLANET_3_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Execute
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userGuidanceService.promote(getUserState(), TEST_LEVEL_4_REAL);
        getUserState().setRazarion(100);
        getOrCreateBase(); // Create Base
        unlockService.unlockPlanet(TEST_PLANET_2_ID);
        unlockService.unlockPlanet(TEST_PLANET_3_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbHistoryElement> history = HibernateUtil.loadAll(getSessionFactory(), DbHistoryElement.class);
        DbHistoryElement dbHistoryElement = history.get(5);
        Assert.assertEquals(DbHistoryElement.Type.UNLOCKED_PLANET, dbHistoryElement.getType());
        Assert.assertEquals(TEST_PLANET_2, dbHistoryElement.getPlanetName());
        Assert.assertEquals(TEST_PLANET_2_ID, (int) dbHistoryElement.getPlanetId());
        Assert.assertEquals(85, (int) dbHistoryElement.getRazarion());
        Assert.assertEquals(15, (int) dbHistoryElement.getDeltaRazarion());
        dbHistoryElement = history.get(6);
        Assert.assertEquals(DbHistoryElement.Type.UNLOCKED_PLANET, dbHistoryElement.getType());
        Assert.assertEquals(TEST_PLANET_3, dbHistoryElement.getPlanetName());
        Assert.assertEquals(TEST_PLANET_3_ID, (int) dbHistoryElement.getPlanetId());
        Assert.assertEquals(65, (int) dbHistoryElement.getRazarion());
        Assert.assertEquals(20, (int) dbHistoryElement.getDeltaRazarion());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testBackend() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet2 = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_2_ID);
        dbPlanet2.setUnlockRazarion(15);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet2);
        planetSystemService.deactivatePlanet(TEST_PLANET_2_ID);
        planetSystemService.activatePlanet(TEST_PLANET_2_ID);
        PlanetLiteInfo planetInfo2 = dbPlanet2.createPlanetInfo().getPlanetLiteInfo();
        DbPlanet dbPlanet3 = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_3_ID);
        dbPlanet3.setUnlockRazarion(15);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet3);
        planetSystemService.deactivatePlanet(TEST_PLANET_3_ID);
        planetSystemService.activatePlanet(TEST_PLANET_3_ID);
        PlanetLiteInfo planetInfo3 = dbPlanet3.createPlanetInfo().getPlanetLiteInfo();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(getUserState(), TEST_LEVEL_4_REAL);
        getOrCreateBase(); // Create Base
        // Assert empty
        Collection<DbPlanet> unlockedPlanets = unlockService.getUnlockPlanets(getUserState());
        Assert.assertTrue(unlockedPlanets.isEmpty());
        // Add item
        Thread.sleep(1000); // wait for AccountBalancePacket
        clearPackets();
        unlockService.setUnlockedPlanetsBackend(Arrays.asList(planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet2.getId())), getUserState());
        // Verify
        UnlockContainerPacket unlockContainerPacket = new UnlockContainerPacket();
        UnlockContainer unlockContainer = new UnlockContainer();
        unlockContainer.setPlanets(Arrays.asList(dbPlanet2.getId()));
        unlockContainerPacket.setUnlockContainer(unlockContainer);
        assertPackagesIgnoreSyncItemInfoAndClear(unlockContainerPacket);
        unlockedPlanets = unlockService.getUnlockPlanets(getUserState());
        Assert.assertEquals(1, unlockedPlanets.size());
        Assert.assertTrue(unlockedPlanets.contains(planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet2.getId())));
        Assert.assertFalse(unlockService.isPlanetLocked(planetInfo2, getUserState()));
        // Add item
        clearPackets();
        unlockService.setUnlockedPlanetsBackend(Arrays.asList(planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet2.getId()), planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet3.getId())), getUserState());
        // Verify
        unlockContainerPacket = new UnlockContainerPacket();
        unlockContainer = new UnlockContainer();
        unlockContainer.setPlanets(Arrays.asList(dbPlanet2.getId(), dbPlanet3.getId()));
        unlockContainerPacket.setUnlockContainer(unlockContainer);
        assertPackagesIgnoreSyncItemInfoAndClear(unlockContainerPacket);
        unlockedPlanets = unlockService.getUnlockPlanets(getUserState());
        Assert.assertEquals(2, unlockedPlanets.size());
        Assert.assertTrue(unlockedPlanets.contains(planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet2.getId())));
        Assert.assertTrue(unlockedPlanets.contains(planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet3.getId())));
        Assert.assertFalse(unlockService.isPlanetLocked(planetInfo2, getUserState()));
        Assert.assertFalse(unlockService.isPlanetLocked(planetInfo3, getUserState()));
        // Set no items
        clearPackets();
        unlockService.setUnlockedPlanetsBackend(new ArrayList<DbPlanet>(), getUserState());
        // Verify
        unlockContainerPacket = new UnlockContainerPacket();
        unlockContainer = new UnlockContainer();
        unlockContainer.setPlanets(new ArrayList<Integer>());
        unlockContainerPacket.setUnlockContainer(unlockContainer);
        assertPackagesIgnoreSyncItemInfoAndClear(unlockContainerPacket);
        unlockedPlanets = unlockService.getUnlockPlanets(getUserState());
        Assert.assertEquals(0, unlockedPlanets.size());
        Assert.assertTrue(unlockService.isPlanetLocked(planetInfo2, getUserState()));
        Assert.assertTrue(unlockService.isPlanetLocked(planetInfo3, getUserState()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
