package com.btxtech.game.jsre.common.gameengine.itemType;

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.ObjectHolder;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.ActiveProjectile;
import com.btxtech.game.jsre.common.gameengine.syncObjects.ActiveProjectileGroup;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.connection.ServerConnectionService;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.ServerItemService;
import com.btxtech.game.services.planet.impl.ServerPlanetServicesImpl;
import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import org.easymock.EasyMock;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: beat
 * Date: 18.10.12
 * Time: 15:45
 */
public class TestSyncWeaponNoSpring {

    @Test
    public void testMultipleProjectilesOneMuzzleFlash() throws Exception {
        // Setup target base item type
        BaseItemType targetBaseItemType = new BaseItemType();
        targetBaseItemType.setBoundingBox(new BoundingBox(100, new double[]{0.0, MathHelper.QUARTER_RADIANT, MathHelper.HALF_RADIANT, MathHelper.THREE_QUARTER_RADIANT}));
        targetBaseItemType.setHealth(1000);
        targetBaseItemType.setId(1);
        // Setup attacker base item type
        BaseItemType attackBaseItemType = new BaseItemType();
        attackBaseItemType.setId(2);
        attackBaseItemType.setBoundingBox(new BoundingBox(100, new double[]{0.0, MathHelper.QUARTER_RADIANT, MathHelper.HALF_RADIANT, MathHelper.THREE_QUARTER_RADIANT}));
        attackBaseItemType.setHealth(1000);
        Index[][] muzzlePositions = new Index[][]{{new Index(0, -10), new Index(-10, 0), new Index(0, 10), new Index(10, 0)}};
        WeaponType weaponType = new WeaponType(300, 100, 10, 0.5, null, null, null, Collections.singletonList(1), muzzlePositions);
        attackBaseItemType.setWeaponType(weaponType);
        // Create planet services & items
        Id targetId = new Id(1, 0);
        Id attackerId = new Id(2, 0);
        ServerPlanetServicesImpl planetServices = AbstractServiceTest.createMockPlanetServices();
        SyncBaseItem target = AbstractServiceTest.createSyncBaseItem(targetBaseItemType, new Index(300, 100), targetId, AbstractServiceTest.createMockGlobalServices(), planetServices, new SimpleBase(1, 1));
        SyncBaseItem attacker = AbstractServiceTest.createSyncBaseItem(attackBaseItemType, new Index(100, 100), attackerId, AbstractServiceTest.createMockGlobalServices(), planetServices, new SimpleBase(2, 1));
        ServerItemService serverItemServiceMock = EasyMock.createNiceMock(ServerItemService.class);
        EasyMock.expect(serverItemServiceMock.getItem(targetId)).andReturn(target).anyTimes();
        EasyMock.expect(serverItemServiceMock.getItem(attackerId)).andReturn(attacker).anyTimes();
        EasyMock.replay(serverItemServiceMock);
        planetServices.setServerItemService(serverItemServiceMock);
        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isEnemy(target, attacker)).andReturn(true).anyTimes();
        EasyMock.expect(baseService.isEnemy(attacker, target)).andReturn(true).anyTimes();
        EasyMock.replay(baseService);
        planetServices.setBaseService(baseService);
        ServerConnectionService connectionService = EasyMock.createNiceMock(ServerConnectionService.class);
        EasyMock.expect(connectionService.getGameEngineMode()).andReturn(GameEngineMode.MASTER).anyTimes();
        planetServices.setServerConnectionService(connectionService);
        EasyMock.replay(connectionService);
        // Check if idle
        Assert.assertTrue(target.isIdle());
        Assert.assertTrue(attacker.isIdle());
        Assert.assertFalse(target.tick(1));
        Assert.assertFalse(attacker.tick(1));
        // Check SyncItemListener
        SyncItemListener syncItemListenerMock = EasyMock.createStrictMock(SyncItemListener.class);
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.ANGEL), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.isNull());
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_LAUNCHED), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_LAUNCHED), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_LAUNCHED), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_LAUNCHED), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_DETONATION), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_LAUNCHED), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_DETONATION), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_LAUNCHED), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        EasyMock.replay(syncItemListenerMock);
        attacker.addSyncItemListener(syncItemListenerMock);
        final List<ActiveProjectileGroup> activeProjectileGroups = new ArrayList<>();
        final ObjectHolder<Index> projectileTarget = new ObjectHolder<>();
        attacker.addSyncItemListener(new SyncItemListener() {
            @Override
            public void onItemChanged(Change change, SyncItem syncItem, Object additionalCustomInfo) {
                if (change == Change.PROJECTILE_LAUNCHED) {
                    activeProjectileGroups.add((ActiveProjectileGroup) additionalCustomInfo);
                    projectileTarget.setObject(((SyncBaseItem) syncItem).getSyncWeapon().getProjectileTarget());
                }
            }
        });
        // Attack
        AttackCommand attackCommand = new AttackCommand();
        attackCommand.setId(attackerId);
        attackCommand.setTimeStamp();
        attackCommand.setTarget(targetId);
        attacker.executeCommand(attackCommand);
        Assert.assertTrue(target.isIdle());
        Assert.assertFalse(attacker.isIdle());
        // Run tests
        Assert.assertEquals(0, activeProjectileGroups.size());
        Assert.assertNull(projectileTarget.getObject());
        // Launch projectile
        Assert.assertTrue(attacker.tick(0.01));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(1, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 1, 0, true, new Index(110, 100));
        // Move projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(1, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 1, 0, true, new Index(135, 100));
        // Launch projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(2, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 1, 0, true, new Index(160, 100));
        assertProjectile(activeProjectileGroups, 1, 1, 0, true, new Index(110, 100));
        // Move projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(2, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 1, 0, true, new Index(185, 100));
        assertProjectile(activeProjectileGroups, 1, 1, 0, true, new Index(135, 100));
        // Launch projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(3, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 1, 0, true, new Index(210, 100));
        assertProjectile(activeProjectileGroups, 1, 1, 0, true, new Index(160, 100));
        assertProjectile(activeProjectileGroups, 2, 1, 0, true, new Index(110, 100));
        // Move projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(3, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 1, 0, true, new Index(235, 100));
        assertProjectile(activeProjectileGroups, 1, 1, 0, true, new Index(185, 100));
        assertProjectile(activeProjectileGroups, 2, 1, 0, true, new Index(135, 100));
        // Launch projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(4, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 1, 0, true, new Index(260, 100));
        assertProjectile(activeProjectileGroups, 1, 1, 0, true, new Index(210, 100));
        assertProjectile(activeProjectileGroups, 2, 1, 0, true, new Index(160, 100));
        assertProjectile(activeProjectileGroups, 3, 1, 0, true, new Index(110, 100));
        // Move projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(4, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 1, 0, true, new Index(285, 100));
        assertProjectile(activeProjectileGroups, 1, 1, 0, true, new Index(235, 100));
        assertProjectile(activeProjectileGroups, 2, 1, 0, true, new Index(185, 100));
        assertProjectile(activeProjectileGroups, 3, 1, 0, true, new Index(135, 100));
        // Launch projectile, first projectile hits target
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(5, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 1, 0, false, new Index(300, 100));
        assertProjectile(activeProjectileGroups, 1, 1, 0, true, new Index(260, 100));
        assertProjectile(activeProjectileGroups, 2, 1, 0, true, new Index(210, 100));
        assertProjectile(activeProjectileGroups, 3, 1, 0, true, new Index(160, 100));
        assertProjectile(activeProjectileGroups, 4, 1, 0, true, new Index(110, 100));
        activeProjectileGroups.remove(0);
        // Move projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(4, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 1, 0, true, new Index(285, 100));
        assertProjectile(activeProjectileGroups, 1, 1, 0, true, new Index(235, 100));
        assertProjectile(activeProjectileGroups, 2, 1, 0, true, new Index(185, 100));
        assertProjectile(activeProjectileGroups, 3, 1, 0, true, new Index(135, 100));
        // Launch projectile, first projectile hits target
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(5, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 1, 0, false, new Index(300, 100));
        assertProjectile(activeProjectileGroups, 1, 1, 0, true, new Index(260, 100));
        assertProjectile(activeProjectileGroups, 2, 1, 0, true, new Index(210, 100));
        assertProjectile(activeProjectileGroups, 3, 1, 0, true, new Index(160, 100));
        assertProjectile(activeProjectileGroups, 4, 1, 0, true, new Index(110, 100));
        activeProjectileGroups.remove(0);
        // Move projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(4, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 1, 0, true, new Index(285, 100));
        assertProjectile(activeProjectileGroups, 1, 1, 0, true, new Index(235, 100));
        assertProjectile(activeProjectileGroups, 2, 1, 0, true, new Index(185, 100));
        assertProjectile(activeProjectileGroups, 3, 1, 0, true, new Index(135, 100));
        EasyMock.verify(syncItemListenerMock);
    }

    @Test
    public void testMultipleProjectilesTwoMuzzleFlash() throws Exception {
        // Setup target base item type
        BaseItemType targetBaseItemType = new BaseItemType();
        targetBaseItemType.setBoundingBox(new BoundingBox(100, new double[]{0.0, MathHelper.QUARTER_RADIANT, MathHelper.HALF_RADIANT, MathHelper.THREE_QUARTER_RADIANT}));
        targetBaseItemType.setHealth(1000);
        targetBaseItemType.setId(1);
        // Setup attacker base item type
        BaseItemType attackBaseItemType = new BaseItemType();
        attackBaseItemType.setId(2);
        attackBaseItemType.setBoundingBox(new BoundingBox(100, new double[]{0.0, MathHelper.QUARTER_RADIANT, MathHelper.HALF_RADIANT, MathHelper.THREE_QUARTER_RADIANT}));
        attackBaseItemType.setHealth(1000);
        Index[][] muzzlePositions = new Index[][]{{new Index(-5, -10), new Index(-10, -5), new Index(5, 10), new Index(10, -5)}, {new Index(5, -10), new Index(-10, 5), new Index(-5, 10), new Index(10, 5)}};
        WeaponType weaponType = new WeaponType(300, 100, 10, 0.5, null, null, null, Collections.singletonList(1), muzzlePositions);
        attackBaseItemType.setWeaponType(weaponType);
        // Create planet services & items
        Id targetId = new Id(1, 0);
        Id attackerId = new Id(2, 0);
        ServerPlanetServicesImpl planetServices = AbstractServiceTest.createMockPlanetServices();
        SyncBaseItem target = AbstractServiceTest.createSyncBaseItem(targetBaseItemType, new Index(300, 100), targetId, AbstractServiceTest.createMockGlobalServices(), planetServices, new SimpleBase(1, 1));
        SyncBaseItem attacker = AbstractServiceTest.createSyncBaseItem(attackBaseItemType, new Index(100, 100), attackerId, AbstractServiceTest.createMockGlobalServices(), planetServices, new SimpleBase(2, 1));
        ServerItemService serverItemServiceMock = EasyMock.createNiceMock(ServerItemService.class);
        EasyMock.expect(serverItemServiceMock.getItem(targetId)).andReturn(target).anyTimes();
        EasyMock.expect(serverItemServiceMock.getItem(attackerId)).andReturn(attacker).anyTimes();
        EasyMock.replay(serverItemServiceMock);
        planetServices.setServerItemService(serverItemServiceMock);
        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isEnemy(target, attacker)).andReturn(true).anyTimes();
        EasyMock.expect(baseService.isEnemy(attacker, target)).andReturn(true).anyTimes();
        EasyMock.replay(baseService);
        planetServices.setBaseService(baseService);
        ServerConnectionService connectionService = EasyMock.createNiceMock(ServerConnectionService.class);
        EasyMock.expect(connectionService.getGameEngineMode()).andReturn(GameEngineMode.MASTER).anyTimes();
        planetServices.setServerConnectionService(connectionService);
        EasyMock.replay(connectionService);
        // Check if idle
        Assert.assertTrue(target.isIdle());
        Assert.assertTrue(attacker.isIdle());
        Assert.assertFalse(target.tick(1));
        Assert.assertFalse(attacker.tick(1));
        // Check SyncItemListener
        SyncItemListener syncItemListenerMock = EasyMock.createStrictMock(SyncItemListener.class);
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.ANGEL), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.isNull());
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_LAUNCHED), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_LAUNCHED), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_LAUNCHED), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_LAUNCHED), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_DETONATION), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_LAUNCHED), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_DETONATION), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_LAUNCHED), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        EasyMock.replay(syncItemListenerMock);
        attacker.addSyncItemListener(syncItemListenerMock);
        final List<ActiveProjectileGroup> activeProjectileGroups = new ArrayList<>();
        final ObjectHolder<Index> projectileTarget = new ObjectHolder<>();
        attacker.addSyncItemListener(new SyncItemListener() {
            @Override
            public void onItemChanged(Change change, SyncItem syncItem, Object additionalCustomInfo) {
                if (change == Change.PROJECTILE_LAUNCHED) {
                    activeProjectileGroups.add((ActiveProjectileGroup) additionalCustomInfo);
                    projectileTarget.setObject(((SyncBaseItem) syncItem).getSyncWeapon().getProjectileTarget());
                }
            }
        });
        // Attack
        AttackCommand attackCommand = new AttackCommand();
        attackCommand.setId(attackerId);
        attackCommand.setTimeStamp();
        attackCommand.setTarget(targetId);
        attacker.executeCommand(attackCommand);
        Assert.assertTrue(target.isIdle());
        Assert.assertFalse(attacker.isIdle());
        // Run tests
        Assert.assertEquals(0, activeProjectileGroups.size());
        Assert.assertNull(projectileTarget.getObject());
        // Launch projectile
        Assert.assertTrue(attacker.tick(0.01));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(1, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 0, 2, 1, true, new Index(110, 105));
        // Move projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(1, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 2, 0, true, new Index(135, 96));
        assertProjectile(activeProjectileGroups, 0, 2, 1, true, new Index(135, 104));
        // Launch projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(2, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 2, 0, true, new Index(160, 96));
        assertProjectile(activeProjectileGroups, 0, 2, 1, true, new Index(160, 104));
        assertProjectile(activeProjectileGroups, 1, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 1, 2, 1, true, new Index(110, 105));
        // Move projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(2, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 2, 0, true, new Index(185, 97));
        assertProjectile(activeProjectileGroups, 0, 2, 1, true, new Index(185, 103));
        assertProjectile(activeProjectileGroups, 1, 2, 0, true, new Index(135, 96));
        assertProjectile(activeProjectileGroups, 1, 2, 1, true, new Index(135, 104));
        // Launch projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(3, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 2, 0, true, new Index(210, 98));
        assertProjectile(activeProjectileGroups, 0, 2, 1, true, new Index(210, 102));
        assertProjectile(activeProjectileGroups, 1, 2, 0, true, new Index(160, 96));
        assertProjectile(activeProjectileGroups, 1, 2, 1, true, new Index(160, 104));
        assertProjectile(activeProjectileGroups, 2, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 2, 2, 1, true, new Index(110, 105));
        // Move projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(3, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 2, 0, true, new Index(235, 98));
        assertProjectile(activeProjectileGroups, 0, 2, 1, true, new Index(235, 102));
        assertProjectile(activeProjectileGroups, 1, 2, 0, true, new Index(185, 97));
        assertProjectile(activeProjectileGroups, 1, 2, 1, true, new Index(185, 103));
        assertProjectile(activeProjectileGroups, 2, 2, 0, true, new Index(135, 96));
        assertProjectile(activeProjectileGroups, 2, 2, 1, true, new Index(135, 104));
        // Launch projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(4, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 2, 0, true, new Index(260, 99));
        assertProjectile(activeProjectileGroups, 0, 2, 1, true, new Index(260, 101));
        assertProjectile(activeProjectileGroups, 1, 2, 0, true, new Index(210, 98));
        assertProjectile(activeProjectileGroups, 1, 2, 1, true, new Index(210, 102));
        assertProjectile(activeProjectileGroups, 2, 2, 0, true, new Index(160, 96));
        assertProjectile(activeProjectileGroups, 2, 2, 1, true, new Index(160, 104));
        assertProjectile(activeProjectileGroups, 3, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 3, 2, 1, true, new Index(110, 105));
        // Move projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(4, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 2, 0, true, new Index(285, 100));
        assertProjectile(activeProjectileGroups, 0, 2, 1, true, new Index(285, 100));
        assertProjectile(activeProjectileGroups, 1, 2, 0, true, new Index(235, 98));
        assertProjectile(activeProjectileGroups, 1, 2, 1, true, new Index(235, 102));
        assertProjectile(activeProjectileGroups, 2, 2, 0, true, new Index(185, 97));
        assertProjectile(activeProjectileGroups, 2, 2, 1, true, new Index(185, 103));
        assertProjectile(activeProjectileGroups, 3, 2, 0, true, new Index(135, 96));
        assertProjectile(activeProjectileGroups, 3, 2, 1, true, new Index(135, 104));
        // Launch projectile, first projectile hits target
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(5, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 2, 0, false, new Index(300, 100));
        assertProjectile(activeProjectileGroups, 0, 2, 1, false, new Index(300, 100));
        assertProjectile(activeProjectileGroups, 1, 2, 0, true, new Index(260, 99));
        assertProjectile(activeProjectileGroups, 1, 2, 1, true, new Index(260, 101));
        assertProjectile(activeProjectileGroups, 2, 2, 0, true, new Index(210, 98));
        assertProjectile(activeProjectileGroups, 2, 2, 1, true, new Index(210, 102));
        assertProjectile(activeProjectileGroups, 3, 2, 0, true, new Index(160, 96));
        assertProjectile(activeProjectileGroups, 3, 2, 1, true, new Index(160, 104));
        assertProjectile(activeProjectileGroups, 4, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 4, 2, 1, true, new Index(110, 105));
        activeProjectileGroups.remove(0);
        // Move projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(4, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 2, 0, true, new Index(285, 100));
        assertProjectile(activeProjectileGroups, 0, 2, 1, true, new Index(285, 100));
        assertProjectile(activeProjectileGroups, 1, 2, 0, true, new Index(235, 98));
        assertProjectile(activeProjectileGroups, 1, 2, 1, true, new Index(235, 102));
        assertProjectile(activeProjectileGroups, 2, 2, 0, true, new Index(185, 97));
        assertProjectile(activeProjectileGroups, 2, 2, 1, true, new Index(185, 103));
        assertProjectile(activeProjectileGroups, 3, 2, 0, true, new Index(135, 96));
        assertProjectile(activeProjectileGroups, 3, 2, 1, true, new Index(135, 104));
        // Launch projectile, first projectile hits target
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(5, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 2, 0, false, new Index(300, 100));
        assertProjectile(activeProjectileGroups, 0, 2, 1, false, new Index(300, 100));
        assertProjectile(activeProjectileGroups, 1, 2, 0, true, new Index(260, 99));
        assertProjectile(activeProjectileGroups, 1, 2, 1, true, new Index(260, 101));
        assertProjectile(activeProjectileGroups, 2, 2, 0, true, new Index(210, 98));
        assertProjectile(activeProjectileGroups, 2, 2, 1, true, new Index(210, 102));
        assertProjectile(activeProjectileGroups, 3, 2, 0, true, new Index(160, 96));
        assertProjectile(activeProjectileGroups, 3, 2, 1, true, new Index(160, 104));
        assertProjectile(activeProjectileGroups, 4, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 4, 2, 1, true, new Index(110, 105));
        activeProjectileGroups.remove(0);
        // Move projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(4, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 2, 0, true, new Index(285, 100));
        assertProjectile(activeProjectileGroups, 0, 2, 1, true, new Index(285, 100));
        assertProjectile(activeProjectileGroups, 1, 2, 0, true, new Index(235, 98));
        assertProjectile(activeProjectileGroups, 1, 2, 1, true, new Index(235, 102));
        assertProjectile(activeProjectileGroups, 2, 2, 0, true, new Index(185, 97));
        assertProjectile(activeProjectileGroups, 2, 2, 1, true, new Index(185, 103));
        assertProjectile(activeProjectileGroups, 3, 2, 0, true, new Index(135, 96));
        assertProjectile(activeProjectileGroups, 3, 2, 1, true, new Index(135, 104));
        EasyMock.verify(syncItemListenerMock);
    }

    @Test
    public void testNoProjectileSpeedOneMuzzleFlash() throws Exception {
        // Setup target base item type
        BaseItemType targetBaseItemType = new BaseItemType();
        targetBaseItemType.setBoundingBox(new BoundingBox(100, new double[]{0.0, MathHelper.QUARTER_RADIANT, MathHelper.HALF_RADIANT, MathHelper.THREE_QUARTER_RADIANT}));
        targetBaseItemType.setHealth(1000);
        targetBaseItemType.setId(1);
        // Setup attacker base item type
        BaseItemType attackBaseItemType = new BaseItemType();
        attackBaseItemType.setId(2);
        attackBaseItemType.setBoundingBox(new BoundingBox(100, new double[]{0.0, MathHelper.QUARTER_RADIANT, MathHelper.HALF_RADIANT, MathHelper.THREE_QUARTER_RADIANT}));
        attackBaseItemType.setHealth(1000);
        Index[][] muzzlePositions = new Index[][]{{new Index(0, -10), new Index(-10, 0), new Index(0, 10), new Index(10, 0)}};
        WeaponType weaponType = new WeaponType(300, null, 10, 0.5, null, null, null, Collections.singletonList(1), muzzlePositions);
        attackBaseItemType.setWeaponType(weaponType);
        // Create planet services & items
        Id targetId = new Id(1, 0);
        Id attackerId = new Id(2, 0);
        ServerPlanetServicesImpl planetServices = AbstractServiceTest.createMockPlanetServices();
        SyncBaseItem target = AbstractServiceTest.createSyncBaseItem(targetBaseItemType, new Index(300, 100), targetId, AbstractServiceTest.createMockGlobalServices(), planetServices, new SimpleBase(1, 1));
        SyncBaseItem attacker = AbstractServiceTest.createSyncBaseItem(attackBaseItemType, new Index(100, 100), attackerId, AbstractServiceTest.createMockGlobalServices(), planetServices, new SimpleBase(2, 1));
        ServerItemService serverItemServiceMock = EasyMock.createNiceMock(ServerItemService.class);
        EasyMock.expect(serverItemServiceMock.getItem(targetId)).andReturn(target).anyTimes();
        EasyMock.expect(serverItemServiceMock.getItem(attackerId)).andReturn(attacker).anyTimes();
        EasyMock.replay(serverItemServiceMock);
        planetServices.setServerItemService(serverItemServiceMock);
        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isEnemy(target, attacker)).andReturn(true).anyTimes();
        EasyMock.expect(baseService.isEnemy(attacker, target)).andReturn(true).anyTimes();
        EasyMock.replay(baseService);
        planetServices.setBaseService(baseService);
        ServerConnectionService connectionService = EasyMock.createNiceMock(ServerConnectionService.class);
        EasyMock.expect(connectionService.getGameEngineMode()).andReturn(GameEngineMode.MASTER).anyTimes();
        planetServices.setServerConnectionService(connectionService);
        EasyMock.replay(connectionService);
        // Check if idle
        Assert.assertTrue(target.isIdle());
        Assert.assertTrue(attacker.isIdle());
        Assert.assertFalse(target.tick(1));
        Assert.assertFalse(attacker.tick(1));
        // Check SyncItemListener
        SyncItemListener syncItemListenerMock = EasyMock.createStrictMock(SyncItemListener.class);
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.ANGEL), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.isNull());
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_LAUNCHED), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_DETONATION), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_LAUNCHED), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_DETONATION), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_LAUNCHED), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_DETONATION), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_LAUNCHED), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_DETONATION), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        EasyMock.replay(syncItemListenerMock);
        attacker.addSyncItemListener(syncItemListenerMock);
        final List<ActiveProjectileGroup> activeProjectileGroups = new ArrayList<>();
        final ObjectHolder<Index> projectileTarget = new ObjectHolder<>();
        attacker.addSyncItemListener(new SyncItemListener() {
            @Override
            public void onItemChanged(Change change, SyncItem syncItem, Object additionalCustomInfo) {
                if (change == Change.PROJECTILE_LAUNCHED) {
                    activeProjectileGroups.add((ActiveProjectileGroup) additionalCustomInfo);
                    projectileTarget.setObject(((SyncBaseItem) syncItem).getSyncWeapon().getProjectileTarget());
                }
            }
        });
        // Attack
        AttackCommand attackCommand = new AttackCommand();
        attackCommand.setId(attackerId);
        attackCommand.setTimeStamp();
        attackCommand.setTarget(targetId);
        attacker.executeCommand(attackCommand);
        Assert.assertTrue(target.isIdle());
        Assert.assertFalse(attacker.isIdle());
        // Run tests
        Assert.assertEquals(0, activeProjectileGroups.size());
        Assert.assertNull(projectileTarget.getObject());
        // Launch projectile
        Assert.assertTrue(attacker.tick(0.01));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(1, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 1, 0, true, new Index(110, 100));
        // No changes
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(1, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 1, 0, true, new Index(110, 100));
        // Launch projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(2, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 1, 0, true, new Index(110, 100));
        assertProjectile(activeProjectileGroups, 1, 1, 0, true, new Index(110, 100));
        // No changes
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(2, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 1, 0, true, new Index(110, 100));
        assertProjectile(activeProjectileGroups, 1, 1, 0, true, new Index(110, 100));
        // Launch projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(3, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 1, 0, true, new Index(110, 100));
        assertProjectile(activeProjectileGroups, 1, 1, 0, true, new Index(110, 100));
        assertProjectile(activeProjectileGroups, 2, 1, 0, true, new Index(110, 100));
        // No changes
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(3, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 1, 0, true, new Index(110, 100));
        assertProjectile(activeProjectileGroups, 1, 1, 0, true, new Index(110, 100));
        assertProjectile(activeProjectileGroups, 2, 1, 0, true, new Index(110, 100));
        // Launch projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(4, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 1, 0, true, new Index(110, 100));
        assertProjectile(activeProjectileGroups, 1, 1, 0, true, new Index(110, 100));
        assertProjectile(activeProjectileGroups, 2, 1, 0, true, new Index(110, 100));
        assertProjectile(activeProjectileGroups, 3, 1, 0, true, new Index(110, 100));
        // No changes
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(4, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 1, 0, true, new Index(110, 100));
        assertProjectile(activeProjectileGroups, 1, 1, 0, true, new Index(110, 100));
        assertProjectile(activeProjectileGroups, 2, 1, 0, true, new Index(110, 100));
        assertProjectile(activeProjectileGroups, 3, 1, 0, true, new Index(110, 100));
        EasyMock.verify(syncItemListenerMock);
    }

    @Test
    public void testNoProjectileSpeedTwoMuzzleFlash() throws Exception {
        // Setup target base item type
        BaseItemType targetBaseItemType = new BaseItemType();
        targetBaseItemType.setBoundingBox(new BoundingBox(100, new double[]{0.0, MathHelper.QUARTER_RADIANT, MathHelper.HALF_RADIANT, MathHelper.THREE_QUARTER_RADIANT}));
        targetBaseItemType.setHealth(1000);
        targetBaseItemType.setId(1);
        // Setup attacker base item type
        BaseItemType attackBaseItemType = new BaseItemType();
        attackBaseItemType.setId(2);
        attackBaseItemType.setBoundingBox(new BoundingBox(100, new double[]{0.0, MathHelper.QUARTER_RADIANT, MathHelper.HALF_RADIANT, MathHelper.THREE_QUARTER_RADIANT}));
        attackBaseItemType.setHealth(1000);
        Index[][] muzzlePositions = new Index[][]{{new Index(-5, -10), new Index(-10, -5), new Index(5, 10), new Index(10, -5)}, {new Index(5, -10), new Index(-10, 5), new Index(-5, 10), new Index(10, 5)}};
        WeaponType weaponType = new WeaponType(300, null, 10, 0.5, null, null, null, Collections.singletonList(1), muzzlePositions);
        attackBaseItemType.setWeaponType(weaponType);
        // Create planet services & items
        Id targetId = new Id(1, 0);
        Id attackerId = new Id(2, 0);
        ServerPlanetServicesImpl planetServices = AbstractServiceTest.createMockPlanetServices();
        SyncBaseItem target = AbstractServiceTest.createSyncBaseItem(targetBaseItemType, new Index(300, 100), targetId, AbstractServiceTest.createMockGlobalServices(), planetServices, new SimpleBase(1, 1));
        SyncBaseItem attacker = AbstractServiceTest.createSyncBaseItem(attackBaseItemType, new Index(100, 100), attackerId, AbstractServiceTest.createMockGlobalServices(), planetServices, new SimpleBase(2, 1));
        ServerItemService serverItemServiceMock = EasyMock.createNiceMock(ServerItemService.class);
        EasyMock.expect(serverItemServiceMock.getItem(targetId)).andReturn(target).anyTimes();
        EasyMock.expect(serverItemServiceMock.getItem(attackerId)).andReturn(attacker).anyTimes();
        EasyMock.replay(serverItemServiceMock);
        planetServices.setServerItemService(serverItemServiceMock);
        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isEnemy(target, attacker)).andReturn(true).anyTimes();
        EasyMock.expect(baseService.isEnemy(attacker, target)).andReturn(true).anyTimes();
        EasyMock.replay(baseService);
        planetServices.setBaseService(baseService);
        ServerConnectionService connectionService = EasyMock.createNiceMock(ServerConnectionService.class);
        EasyMock.expect(connectionService.getGameEngineMode()).andReturn(GameEngineMode.MASTER).anyTimes();
        planetServices.setServerConnectionService(connectionService);
        EasyMock.replay(connectionService);
        // Check if idle
        Assert.assertTrue(target.isIdle());
        Assert.assertTrue(attacker.isIdle());
        Assert.assertFalse(target.tick(1));
        Assert.assertFalse(attacker.tick(1));
        // Check SyncItemListener
        SyncItemListener syncItemListenerMock = EasyMock.createStrictMock(SyncItemListener.class);
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.ANGEL), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.isNull());
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_LAUNCHED), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_DETONATION), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_LAUNCHED), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_DETONATION), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_LAUNCHED), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_DETONATION), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_LAUNCHED), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        syncItemListenerMock.onItemChanged(EasyMock.eq(SyncItemListener.Change.PROJECTILE_DETONATION), AbstractServiceTest.createSyncItemMatcher(attackerId), EasyMock.anyObject(ActiveProjectileGroup.class));
        EasyMock.replay(syncItemListenerMock);
        attacker.addSyncItemListener(syncItemListenerMock);
        final List<ActiveProjectileGroup> activeProjectileGroups = new ArrayList<>();
        final ObjectHolder<Index> projectileTarget = new ObjectHolder<>();
        attacker.addSyncItemListener(new SyncItemListener() {
            @Override
            public void onItemChanged(Change change, SyncItem syncItem, Object additionalCustomInfo) {
                if (change == Change.PROJECTILE_LAUNCHED) {
                    activeProjectileGroups.add((ActiveProjectileGroup) additionalCustomInfo);
                    projectileTarget.setObject(((SyncBaseItem) syncItem).getSyncWeapon().getProjectileTarget());
                }
            }
        });
        // Attack
        AttackCommand attackCommand = new AttackCommand();
        attackCommand.setId(attackerId);
        attackCommand.setTimeStamp();
        attackCommand.setTarget(targetId);
        attacker.executeCommand(attackCommand);
        Assert.assertTrue(target.isIdle());
        Assert.assertFalse(attacker.isIdle());
        // Run tests
        Assert.assertEquals(0, activeProjectileGroups.size());
        Assert.assertNull(projectileTarget.getObject());
        // Launch projectile
        Assert.assertTrue(attacker.tick(0.01));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(1, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 0, 2, 1, true, new Index(110, 105));
        // No changes
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(1, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 0, 2, 1, true, new Index(110, 105));
        // Launch projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(2, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 0, 2, 1, true, new Index(110, 105));
        assertProjectile(activeProjectileGroups, 1, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 1, 2, 1, true, new Index(110, 105));
        // No changes
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(2, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 0, 2, 1, true, new Index(110, 105));
        assertProjectile(activeProjectileGroups, 1, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 1, 2, 1, true, new Index(110, 105));
        // Launch projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(3, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 0, 2, 1, true, new Index(110, 105));
        assertProjectile(activeProjectileGroups, 1, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 1, 2, 1, true, new Index(110, 105));
        assertProjectile(activeProjectileGroups, 2, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 2, 2, 1, true, new Index(110, 105));
        // No changes
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(3, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 0, 2, 1, true, new Index(110, 105));
        assertProjectile(activeProjectileGroups, 1, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 1, 2, 1, true, new Index(110, 105));
        assertProjectile(activeProjectileGroups, 2, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 2, 2, 1, true, new Index(110, 105));
        // Launch projectile
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(4, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 0, 2, 1, true, new Index(110, 105));
        assertProjectile(activeProjectileGroups, 1, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 1, 2, 1, true, new Index(110, 105));
        assertProjectile(activeProjectileGroups, 2, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 2, 2, 1, true, new Index(110, 105));
        assertProjectile(activeProjectileGroups, 3, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 3, 2, 1, true, new Index(110, 105));
        // No changes
        Assert.assertTrue(attacker.tick(0.25));
        Assert.assertNotNull(projectileTarget.getObject());
        Assert.assertEquals(4, activeProjectileGroups.size());
        assertProjectile(activeProjectileGroups, 0, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 0, 2, 1, true, new Index(110, 105));
        assertProjectile(activeProjectileGroups, 1, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 1, 2, 1, true, new Index(110, 105));
        assertProjectile(activeProjectileGroups, 2, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 2, 2, 1, true, new Index(110, 105));
        assertProjectile(activeProjectileGroups, 3, 2, 0, true, new Index(110, 95));
        assertProjectile(activeProjectileGroups, 3, 2, 1, true, new Index(110, 105));
        EasyMock.verify(syncItemListenerMock);
    }

    private void assertProjectile(List<ActiveProjectileGroup> activeProjectileGroups, int index, int projectileCount, int muzzleFlash, boolean alive, Index position) {
        Assert.assertEquals(projectileCount, activeProjectileGroups.get(index).getProjectiles().size());
        ActiveProjectile activeProjectile = findActiveProjectile(activeProjectileGroups.get(index), muzzleFlash);
        Assert.assertEquals(alive, activeProjectile.isAlive());
        Assert.assertEquals(position, activeProjectile.getPosition());
    }

    private ActiveProjectile findActiveProjectile(ActiveProjectileGroup projectileGroup, int muzzleFlash) {
        for (ActiveProjectile activeProjectile : projectileGroup.getProjectiles()) {
            if (activeProjectile.getMuzzleNr() == muzzleFlash) {
                return activeProjectile;
            }
        }
        throw new AssertionFailedError("Can not find muzzle flash number in projectile: " + muzzleFlash);
    }
}
