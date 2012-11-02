package com.btxtech.game.services.item;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemClipPosition;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeImageInfo;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.item.itemType.DbItemTypeDemolitionClip;
import com.btxtech.game.services.item.itemType.DbItemTypeDemolitionClips;
import com.btxtech.game.services.media.ClipService;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 02.11.12
 * Time: 14:05
 */
public class TestItemTypeDemolitionClip extends AbstractServiceTest {
    private static final Index[] POSITION_1 = {new Index(0, 0), new Index(0, 1), new Index(0, 2), new Index(0, 3), new Index(0, 4), new Index(0, 5),
            new Index(0, 6), new Index(0, 7), new Index(0, 8), new Index(0, 9), new Index(0, 10), new Index(0, 11),
            new Index(0, 12), new Index(0, 13), new Index(0, 14), new Index(0, 15), new Index(0, 16), new Index(0, 17),
            new Index(0, 18), new Index(0, 19), new Index(0, 20), new Index(0, 21), new Index(0, 22), new Index(0, 23)};
    private static final Index[] POSITION_2 = {new Index(1, 0), new Index(1, 1), new Index(1, 2), new Index(1, 3), new Index(1, 4), new Index(1, 5),
            new Index(1, 6), new Index(1, 7), new Index(1, 8), new Index(1, 9), new Index(1, 10), new Index(1, 11),
            new Index(1, 12), new Index(1, 13), new Index(1, 14), new Index(1, 15), new Index(1, 16), new Index(1, 17),
            new Index(1, 18), new Index(1, 19), new Index(1, 20), new Index(1, 21), new Index(1, 22), new Index(1, 23)};
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private ClipService clipService;
    @Autowired
    private SessionFactory sessionFactory;
    private int clipId1;
    private int clipId2;
    private int clipId3;

    private void setupClips() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        clipId1 = clipService.getClipLibraryCrud().createDbChild().getId();
        clipId2 = clipService.getClipLibraryCrud().createDbChild().getId();
        clipId3 = clipService.getClipLibraryCrud().createDbChild().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void saveEmpty() throws NoSuchItemTypeException {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BaseItemType attacker = (BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        serverItemTypeService.saveItemTypeProperties(attacker.getId(),
                attacker.getBoundingBox(),
                attacker.getItemTypeSpriteMap(),
                attacker.getWeaponType(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                null);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        ItemTypeSpriteMap itemTypeSpriteMap = serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID).getItemTypeSpriteMap();
        Assert.assertNull(itemTypeSpriteMap.getDemolitionStepClips());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void saveOneDemolitionStep() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        setupClips();

        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BaseItemType attacker = (BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        Map<Integer, Collection<ItemClipPosition>> demolitionStepClips = new HashMap<>();
        Collection<ItemClipPosition> itemClipPositions = new ArrayList<>();
        itemClipPositions.add(new ItemClipPosition(clipId1, POSITION_1));
        demolitionStepClips.put(0, itemClipPositions);
        attacker.getItemTypeSpriteMap().setDemolitionSteps(3);
        serverItemTypeService.saveItemTypeProperties(attacker.getId(),
                attacker.getBoundingBox(),
                attacker.getItemTypeSpriteMap(),
                attacker.getWeaponType(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                demolitionStepClips);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        ItemTypeSpriteMap itemTypeSpriteMap = serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID).getItemTypeSpriteMap();
        Assert.assertEquals(1, itemTypeSpriteMap.getDemolitionStepClips().size());
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(500, 500), new Id(-1, -1, -1));
        syncBaseItem.setHealth(0.8 * (double) syncBaseItem.getBaseItemType().getHealth());
        Collection<ItemClipPosition> itemClipPositionsDemolition1 = itemTypeSpriteMap.getDemolitionClipIds(syncBaseItem);
        Assert.assertEquals(1, itemClipPositionsDemolition1.size());
        ItemClipPosition itemClipPosition = CommonJava.getFirst(itemClipPositionsDemolition1);
        Assert.assertEquals(clipId1, itemClipPosition.getClipId());
        Assert.assertArrayEquals(POSITION_1, itemClipPosition.getPositions());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }


    @Test
    @DirtiesContext
    public void saveMultipleDemolitionStep() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        setupClips();

        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BaseItemType attacker = (BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        Map<Integer, Collection<ItemClipPosition>> demolitionStepClips = new HashMap<>();
        Collection<ItemClipPosition> itemClipPositions = new ArrayList<>();
        itemClipPositions.add(new ItemClipPosition(clipId1, POSITION_1));
        itemClipPositions.add(new ItemClipPosition(clipId2, POSITION_2));
        itemClipPositions.add(new ItemClipPosition(clipId3, POSITION_1));
        demolitionStepClips.put(0, itemClipPositions);
        itemClipPositions = new ArrayList<>();
        itemClipPositions.add(new ItemClipPosition(clipId3, POSITION_2));
        itemClipPositions.add(new ItemClipPosition(clipId1, POSITION_1));
        demolitionStepClips.put(1, itemClipPositions);
        itemClipPositions = new ArrayList<>();
        itemClipPositions.add(new ItemClipPosition(clipId1, POSITION_1));
        itemClipPositions.add(new ItemClipPosition(clipId2, POSITION_2));
        demolitionStepClips.put(2, itemClipPositions);
        attacker.getItemTypeSpriteMap().setDemolitionSteps(3);
        serverItemTypeService.saveItemTypeProperties(attacker.getId(),
                attacker.getBoundingBox(),
                attacker.getItemTypeSpriteMap(),
                attacker.getWeaponType(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                demolitionStepClips);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        ItemTypeSpriteMap itemTypeSpriteMap = serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID).getItemTypeSpriteMap();
        Assert.assertEquals(3, itemTypeSpriteMap.getDemolitionStepClips().size());
        // Test step 0
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(500, 500), new Id(-1, -1, -1));
        syncBaseItem.setHealth(0.75 * (double) syncBaseItem.getBaseItemType().getHealth());
        Collection<ItemClipPosition> itemClipPositionsDemolition1 = itemTypeSpriteMap.getDemolitionClipIds(syncBaseItem);
        Assert.assertEquals(3, itemClipPositionsDemolition1.size());
        ItemClipPosition itemClipPosition1 = getItemClipPosition(clipId1, itemClipPositionsDemolition1);
        Assert.assertArrayEquals(POSITION_1, itemClipPosition1.getPositions());
        ItemClipPosition itemClipPosition2 = getItemClipPosition(clipId2, itemClipPositionsDemolition1);
        Assert.assertArrayEquals(POSITION_2, itemClipPosition2.getPositions());
        ItemClipPosition itemClipPosition3 = getItemClipPosition(clipId3, itemClipPositionsDemolition1);
        Assert.assertArrayEquals(POSITION_1, itemClipPosition3.getPositions());
        // Test step 1
        syncBaseItem.setHealth(0.5 * (double) syncBaseItem.getBaseItemType().getHealth());
        Collection<ItemClipPosition> itemClipPositionsDemolition2 = itemTypeSpriteMap.getDemolitionClipIds(syncBaseItem);
        Assert.assertEquals(2, itemClipPositionsDemolition2.size());
        itemClipPosition1 = getItemClipPosition(clipId3, itemClipPositionsDemolition2);
        Assert.assertArrayEquals(POSITION_2, itemClipPosition1.getPositions());
        itemClipPosition2 = getItemClipPosition(clipId1, itemClipPositionsDemolition2);
        Assert.assertArrayEquals(POSITION_1, itemClipPosition2.getPositions());
        // Test step 2
        syncBaseItem.setHealth(0.25 * (double) syncBaseItem.getBaseItemType().getHealth());
        Collection<ItemClipPosition> itemClipPositionsDemolition3 = itemTypeSpriteMap.getDemolitionClipIds(syncBaseItem);
        Assert.assertEquals(2, itemClipPositionsDemolition3.size());
        itemClipPosition1 = getItemClipPosition(clipId1, itemClipPositionsDemolition3);
        Assert.assertArrayEquals(POSITION_1, itemClipPosition1.getPositions());
        itemClipPosition2 = getItemClipPosition(clipId2, itemClipPositionsDemolition3);
        Assert.assertArrayEquals(POSITION_2, itemClipPosition2.getPositions());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void editDeleteMultipleDemolitionStep() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        setupClips();
        // Setup 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BaseItemType attacker = (BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        Map<Integer, Collection<ItemClipPosition>> demolitionStepClips = new HashMap<>();
        Collection<ItemClipPosition> itemClipPositions = new ArrayList<>();
        itemClipPositions.add(new ItemClipPosition(clipId1, POSITION_1));
        itemClipPositions.add(new ItemClipPosition(clipId2, POSITION_2));
        itemClipPositions.add(new ItemClipPosition(clipId3, POSITION_1));
        demolitionStepClips.put(0, itemClipPositions);
        itemClipPositions = new ArrayList<>();
        itemClipPositions.add(new ItemClipPosition(clipId3, POSITION_2));
        itemClipPositions.add(new ItemClipPosition(clipId1, POSITION_1));
        demolitionStepClips.put(1, itemClipPositions);
        itemClipPositions = new ArrayList<>();
        itemClipPositions.add(new ItemClipPosition(clipId1, POSITION_1));
        itemClipPositions.add(new ItemClipPosition(clipId2, POSITION_2));
        demolitionStepClips.put(2, itemClipPositions);
        attacker.getItemTypeSpriteMap().setDemolitionSteps(3);
        serverItemTypeService.saveItemTypeProperties(attacker.getId(),
                attacker.getBoundingBox(),
                attacker.getItemTypeSpriteMap(),
                attacker.getWeaponType(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                demolitionStepClips);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(3, HibernateUtil.loadAll(sessionFactory, DbItemTypeDemolitionClips.class).size());
        Assert.assertEquals(7, HibernateUtil.loadAll(sessionFactory, DbItemTypeDemolitionClip.class).size());
        assertQueryDb("SELECT COUNT(*) FROM ITEM_TYPE_DEMOLITION_CLIP_POSITION", "168");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Setup 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        attacker = (BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        serverItemTypeService.saveItemTypeProperties(attacker.getId(),
                attacker.getBoundingBox(),
                attacker.getItemTypeSpriteMap(),
                attacker.getWeaponType(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                null);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(3, HibernateUtil.loadAll(sessionFactory, DbItemTypeDemolitionClips.class).size());
        Assert.assertEquals(7, HibernateUtil.loadAll(sessionFactory, DbItemTypeDemolitionClip.class).size());
        assertQueryDb("SELECT COUNT(*) FROM ITEM_TYPE_DEMOLITION_CLIP_POSITION", "168");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Setup 3
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        attacker = (BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        demolitionStepClips = new HashMap<>();
        itemClipPositions = new ArrayList<>();
        itemClipPositions.add(new ItemClipPosition(clipId3, POSITION_2));
        itemClipPositions.add(new ItemClipPosition(clipId1, POSITION_2));
        demolitionStepClips.put(0, itemClipPositions);
        itemClipPositions = new ArrayList<>();
        itemClipPositions.add(new ItemClipPosition(clipId3, POSITION_1));
        itemClipPositions.add(new ItemClipPosition(clipId2, POSITION_1));
        itemClipPositions.add(new ItemClipPosition(clipId1, POSITION_2));
        demolitionStepClips.put(2, itemClipPositions);
        attacker.getItemTypeSpriteMap().setDemolitionSteps(3);
        serverItemTypeService.saveItemTypeProperties(attacker.getId(),
                attacker.getBoundingBox(),
                attacker.getItemTypeSpriteMap(),
                attacker.getWeaponType(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                demolitionStepClips);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(2, HibernateUtil.loadAll(sessionFactory, DbItemTypeDemolitionClips.class).size());
        Assert.assertEquals(5, HibernateUtil.loadAll(sessionFactory, DbItemTypeDemolitionClip.class).size());
        assertQueryDb("SELECT COUNT(*) FROM ITEM_TYPE_DEMOLITION_CLIP_POSITION", "120");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        ItemTypeSpriteMap itemTypeSpriteMap = serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID).getItemTypeSpriteMap();
        Assert.assertEquals(2, itemTypeSpriteMap.getDemolitionStepClips().size());
        // Test step 0
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(500, 500), new Id(-1, -1, -1));
        syncBaseItem.setHealth(0.75 * (double) syncBaseItem.getBaseItemType().getHealth());
        Collection<ItemClipPosition> itemClipPositionsDemolition1 = itemTypeSpriteMap.getDemolitionClipIds(syncBaseItem);
        Assert.assertEquals(2, itemClipPositionsDemolition1.size());
        ItemClipPosition itemClipPosition1 = getItemClipPosition(clipId3, itemClipPositionsDemolition1);
        Assert.assertArrayEquals(POSITION_2, itemClipPosition1.getPositions());
        ItemClipPosition itemClipPosition2 = getItemClipPosition(clipId1, itemClipPositionsDemolition1);
        Assert.assertArrayEquals(POSITION_2, itemClipPosition2.getPositions());
        // Test step 1
        syncBaseItem.setHealth(0.5 * (double) syncBaseItem.getBaseItemType().getHealth());
        Collection<ItemClipPosition> itemClipPositionsDemolition2 = itemTypeSpriteMap.getDemolitionClipIds(syncBaseItem);
        Assert.assertNull(itemClipPositionsDemolition2);
        // Test step 2
        syncBaseItem.setHealth(0.25 * (double) syncBaseItem.getBaseItemType().getHealth());
        Collection<ItemClipPosition> itemClipPositionsDemolition3 = itemTypeSpriteMap.getDemolitionClipIds(syncBaseItem);
        Assert.assertEquals(3, itemClipPositionsDemolition3.size());
        itemClipPosition1 = getItemClipPosition(clipId3, itemClipPositionsDemolition3);
        Assert.assertArrayEquals(POSITION_1, itemClipPosition1.getPositions());
        itemClipPosition2 = getItemClipPosition(clipId2, itemClipPositionsDemolition3);
        Assert.assertArrayEquals(POSITION_1, itemClipPosition2.getPositions());
        ItemClipPosition itemClipPosition3 = getItemClipPosition(clipId1, itemClipPositionsDemolition1);
        Assert.assertArrayEquals(POSITION_2, itemClipPosition3.getPositions());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Setup 4
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        attacker = (BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        demolitionStepClips = new HashMap<>();
        attacker.getItemTypeSpriteMap().setDemolitionSteps(3);
        serverItemTypeService.saveItemTypeProperties(attacker.getId(),
                attacker.getBoundingBox(),
                attacker.getItemTypeSpriteMap(),
                attacker.getWeaponType(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                demolitionStepClips);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, HibernateUtil.loadAll(sessionFactory, DbItemTypeDemolitionClips.class).size());
        Assert.assertEquals(0, HibernateUtil.loadAll(sessionFactory, DbItemTypeDemolitionClip.class).size());
        assertQueryDb("SELECT COUNT(*) FROM ITEM_TYPE_DEMOLITION_CLIP_POSITION", "0");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        itemTypeSpriteMap = serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID).getItemTypeSpriteMap();
        Assert.assertNull(itemTypeSpriteMap.getDemolitionStepClips());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private ItemClipPosition getItemClipPosition(int clipId, Collection<ItemClipPosition> itemClipPositionsDemolition1) {
        for (ItemClipPosition itemClipPosition : itemClipPositionsDemolition1) {
            if (itemClipPosition.getClipId() == clipId) {
                return itemClipPosition;
            }
        }
        Assert.fail("No clip for id " + clipId + " found");
        return null; // Will never be reached
    }
}