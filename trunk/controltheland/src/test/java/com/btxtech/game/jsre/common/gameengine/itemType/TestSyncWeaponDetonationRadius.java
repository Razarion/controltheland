package com.btxtech.game.jsre.common.gameengine.itemType;

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.connection.ServerConnectionService;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.ServerItemService;
import com.btxtech.game.services.planet.impl.ServerPlanetServicesImpl;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * User: beat
 * Date: 18.10.12
 * Time: 15:45
 */
public class TestSyncWeaponDetonationRadius {
    @Test
    public void testDetonationRadius() throws Exception {
        // Setup target base item types
        BaseItemType targetBaseItemType1 = new BaseItemType();
        targetBaseItemType1.setBoundingBox(new BoundingBox(100, new double[]{0.0, MathHelper.QUARTER_RADIANT, MathHelper.HALF_RADIANT, MathHelper.THREE_QUARTER_RADIANT}));
        targetBaseItemType1.setHealth(1000);
        targetBaseItemType1.setId(1);
        BaseItemType targetBaseItemType2 = new BaseItemType();
        targetBaseItemType2.setBoundingBox(new BoundingBox(100, new double[]{0.0, MathHelper.QUARTER_RADIANT, MathHelper.HALF_RADIANT, MathHelper.THREE_QUARTER_RADIANT}));
        targetBaseItemType2.setHealth(1000);
        targetBaseItemType2.setId(2);
        BaseItemType targetBaseItemType3 = new BaseItemType();
        targetBaseItemType3.setBoundingBox(new BoundingBox(100, new double[]{0.0, MathHelper.QUARTER_RADIANT, MathHelper.HALF_RADIANT, MathHelper.THREE_QUARTER_RADIANT}));
        targetBaseItemType3.setHealth(1000);
        targetBaseItemType3.setId(3);
        // Setup attacker base item type
        Index[][] muzzlePositions = new Index[][]{{new Index(0, -10), new Index(-10, 0), new Index(0, 10), new Index(10, 0)}};
        BaseItemType attackBaseItemType1 = new BaseItemType();
        attackBaseItemType1.setId(4);
        attackBaseItemType1.setBoundingBox(new BoundingBox(100, new double[]{0.0, MathHelper.QUARTER_RADIANT, MathHelper.HALF_RADIANT, MathHelper.THREE_QUARTER_RADIANT}));
        attackBaseItemType1.setHealth(1000);
        attackBaseItemType1.setWeaponType(new WeaponType(300, null, 10, null, 1, null, null, null, Collections.<Integer>emptyList(), Collections.<Integer, Double>emptyMap(), muzzlePositions));
        BaseItemType attackBaseItemType2 = new BaseItemType();
        attackBaseItemType2.setId(5);
        attackBaseItemType2.setBoundingBox(new BoundingBox(100, new double[]{0.0, MathHelper.QUARTER_RADIANT, MathHelper.HALF_RADIANT, MathHelper.THREE_QUARTER_RADIANT}));
        attackBaseItemType2.setHealth(1000);
        attackBaseItemType2.setWeaponType(new WeaponType(300, null, 15, 200, 1, null, null, null, Collections.<Integer>emptyList(), Collections.<Integer, Double>emptyMap(), muzzlePositions));
        BaseItemType attackBaseItemType3 = new BaseItemType();
        attackBaseItemType3.setId(6);
        attackBaseItemType3.setBoundingBox(new BoundingBox(100, new double[]{0.0, MathHelper.QUARTER_RADIANT, MathHelper.HALF_RADIANT, MathHelper.THREE_QUARTER_RADIANT}));
        attackBaseItemType3.setHealth(1000);
        attackBaseItemType3.setWeaponType(new WeaponType(300, 100, 20, null, 10, null, null, null, Collections.<Integer>emptyList(), Collections.<Integer, Double>emptyMap(), muzzlePositions));
        BaseItemType attackBaseItemType4 = new BaseItemType();
        attackBaseItemType4.setId(7);
        attackBaseItemType4.setBoundingBox(new BoundingBox(100, new double[]{0.0, MathHelper.QUARTER_RADIANT, MathHelper.HALF_RADIANT, MathHelper.THREE_QUARTER_RADIANT}));
        attackBaseItemType4.setHealth(1000);
        attackBaseItemType4.setWeaponType(new WeaponType(300, 100, 30, 200, 1, null, null, null, Collections.<Integer>emptyList(), Collections.<Integer, Double>emptyMap(), muzzlePositions));
        // Create planet services & items
        Id target1Id = new Id(1, 0);
        Id attacker1Id = new Id(2, 0);
        Id target2Id = new Id(3, 0);
        Id attacker2Id = new Id(4, 0);
        Id target3Id = new Id(5, 0);
        Id attacker3Id = new Id(6, 0);
        Id attacker4Id = new Id(7, 0);
        ServerPlanetServicesImpl planetServices = AbstractServiceTest.createMockPlanetServices();
        SyncBaseItem target1 = AbstractServiceTest.createSyncBaseItem(targetBaseItemType1, new Index(300, 100), target1Id, AbstractServiceTest.createMockGlobalServices(), planetServices, new SimpleBase(1, 1));
        SyncBaseItem attacker1 = AbstractServiceTest.createSyncBaseItem(attackBaseItemType1, new Index(100, 100), attacker1Id, AbstractServiceTest.createMockGlobalServices(), planetServices, new SimpleBase(2, 1));
        SyncBaseItem target2 = AbstractServiceTest.createSyncBaseItem(targetBaseItemType2, new Index(300, 300), target2Id, AbstractServiceTest.createMockGlobalServices(), planetServices, new SimpleBase(3, 1));
        SyncBaseItem attacker2 = AbstractServiceTest.createSyncBaseItem(attackBaseItemType2, new Index(100, 300), attacker2Id, AbstractServiceTest.createMockGlobalServices(), planetServices, new SimpleBase(4, 1));
        SyncBaseItem target3 = AbstractServiceTest.createSyncBaseItem(targetBaseItemType3, new Index(300, 500), target3Id, AbstractServiceTest.createMockGlobalServices(), planetServices, new SimpleBase(5, 1));
        SyncBaseItem attacker3 = AbstractServiceTest.createSyncBaseItem(attackBaseItemType3, new Index(100, 500), attacker3Id, AbstractServiceTest.createMockGlobalServices(), planetServices, new SimpleBase(6, 1));
        SyncBaseItem attacker4 = AbstractServiceTest.createSyncBaseItem(attackBaseItemType4, new Index(100, 700), attacker4Id, AbstractServiceTest.createMockGlobalServices(), planetServices, new SimpleBase(7, 1));
        ServerItemService serverItemServiceMock = EasyMock.createNiceMock(ServerItemService.class);
        EasyMock.expect(serverItemServiceMock.getItem(target1Id)).andReturn(target1).anyTimes();
        EasyMock.expect(serverItemServiceMock.getItem(target2Id)).andReturn(target2).anyTimes();
        EasyMock.expect(serverItemServiceMock.getItem(target3Id)).andReturn(target3).anyTimes();
        EasyMock.expect(serverItemServiceMock.getItem(attacker1Id)).andReturn(attacker1).anyTimes();
        EasyMock.expect(serverItemServiceMock.getItem(attacker2Id)).andReturn(attacker2).anyTimes();
        EasyMock.expect(serverItemServiceMock.getItem(attacker3Id)).andReturn(attacker3).anyTimes();
        EasyMock.expect(serverItemServiceMock.getItem(attacker4Id)).andReturn(attacker4).anyTimes();
        EasyMock.expect(serverItemServiceMock.getBaseItemsInRadius(new Index(300, 100), 200, null, null)).andReturn(Arrays.asList(target1, target2));
        EasyMock.expect(serverItemServiceMock.getBaseItemsInRadius(new Index(300, 500), 200, null, null)).andReturn(Arrays.asList(target1, target2, target3));
        EasyMock.replay(serverItemServiceMock);
        planetServices.setServerItemService(serverItemServiceMock);
        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isEnemy(EasyMock.<SyncBaseItem>anyObject(), EasyMock.<SyncBaseItem>anyObject())).andReturn(true).anyTimes();
        EasyMock.replay(baseService);
        planetServices.setBaseService(baseService);
        ServerConnectionService connectionService = EasyMock.createNiceMock(ServerConnectionService.class);
        EasyMock.expect(connectionService.getGameEngineMode()).andReturn(GameEngineMode.MASTER).anyTimes();
        planetServices.setServerConnectionService(connectionService);
        EasyMock.replay(connectionService);
        // Attacker 1
        AttackCommand attackCommand = new AttackCommand();
        attackCommand.setId(attacker1Id);
        attackCommand.setTimeStamp();
        attackCommand.setTarget(target1Id);
        attacker1.executeCommand(attackCommand);
        Assert.assertTrue(attacker1.tick(0.5));
        Assert.assertEquals(990.0, target1.getHealth(), 0.001);
        Assert.assertEquals(1000.0, target2.getHealth(), 0.001);
        Assert.assertEquals(1000.0, target3.getHealth(), 0.001);

        // Attacker 2
        attackCommand = new AttackCommand();
        attackCommand.setId(attacker2Id);
        attackCommand.setTimeStamp();
        attackCommand.setTarget(target1Id);
        attacker2.executeCommand(attackCommand);
        Assert.assertTrue(attacker2.tick(0.5));
        Assert.assertEquals(975.0, target1.getHealth(), 0.001);
        Assert.assertEquals(985.0, target2.getHealth(), 0.001);
        Assert.assertEquals(1000.0, target3.getHealth(), 0.001);

        // Attacker 3
        attackCommand = new AttackCommand();
        attackCommand.setId(attacker3Id);
        attackCommand.setTimeStamp();
        attackCommand.setTarget(target1Id);
        attacker3.executeCommand(attackCommand);
        Assert.assertTrue(attacker3.tick(0.1)); // Fire
        Assert.assertTrue(attacker3.tick(6)); // Move projectile
        Assert.assertEquals(955.0, target1.getHealth(), 0.001);
        Assert.assertEquals(985.0, target2.getHealth(), 0.001);
        Assert.assertEquals(1000.0, target3.getHealth(), 0.001);

        // Attacker 3
        attackCommand = new AttackCommand();
        attackCommand.setId(attacker4Id);
        attackCommand.setTimeStamp();
        attackCommand.setTarget(target3Id);
        attacker4.executeCommand(attackCommand);
        Assert.assertTrue(attacker4.tick(0.1)); // Fire
        Assert.assertTrue(attacker4.tick(6)); // Move projectile
        Assert.assertEquals(925.0, target1.getHealth(), 0.001);
        Assert.assertEquals(955.0, target2.getHealth(), 0.001);
        Assert.assertEquals(970.0, target3.getHealth(), 0.001);
    }


}
