package com.btxtech.game.services.item;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.HarvesterType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemClipPosition;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeImageInfo;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.media.ClipService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;

/**
 * User: beat
 * Date: 06.05.14
 * Time: 18:31
 */
public class TestHarvesterClip extends AbstractServiceTest {
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private ClipService clipService;

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
        BaseItemType attacker = (BaseItemType) serverItemTypeService.getItemType(TEST_HARVESTER_ITEM_ID);
        serverItemTypeService.saveItemTypeProperties(attacker.getId(),
                attacker.getBoundingBox(),
                attacker.getItemTypeSpriteMap(),
                attacker.getWeaponType(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        HarvesterType harvesterType = ((BaseItemType) serverItemTypeService.getItemType(TEST_HARVESTER_ITEM_ID)).getHarvesterType();
        Assert.assertNull(harvesterType.getHarvesterClip());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void crud() throws NoSuchItemTypeException {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItemTypes();
        int clipId1 = clipService.getClipLibraryCrud().createDbChild().getId();
        int clipId2 = clipService.getClipLibraryCrud().createDbChild().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Change 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BaseItemType attacker = (BaseItemType) serverItemTypeService.getItemType(TEST_HARVESTER_ITEM_ID);
        serverItemTypeService.saveItemTypeProperties(attacker.getId(),
                attacker.getBoundingBox(),
                attacker.getItemTypeSpriteMap(),
                attacker.getWeaponType(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                new ItemClipPosition(clipId1, INDEX_24),
                new ItemClipPosition(0, new Index[0]));
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        HarvesterType harvesterType = ((BaseItemType) serverItemTypeService.getItemType(TEST_HARVESTER_ITEM_ID)).getHarvesterType();
        Assert.assertNotNull(harvesterType.getHarvesterClip());
        Assert.assertEquals(clipId1, harvesterType.getHarvesterClip().getClipId());
        Assert.assertArrayEquals(INDEX_24, harvesterType.getHarvesterClip().getPositions());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Change 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        attacker = (BaseItemType) serverItemTypeService.getItemType(TEST_HARVESTER_ITEM_ID);
        serverItemTypeService.saveItemTypeProperties(attacker.getId(),
                attacker.getBoundingBox(),
                attacker.getItemTypeSpriteMap(),
                attacker.getWeaponType(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                new ItemClipPosition(clipId2, INDEX_05),
                new ItemClipPosition(0, new Index[0]));
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        harvesterType = ((BaseItemType) serverItemTypeService.getItemType(TEST_HARVESTER_ITEM_ID)).getHarvesterType();
        Assert.assertNotNull(harvesterType.getHarvesterClip());
        Assert.assertEquals(clipId2, harvesterType.getHarvesterClip().getClipId());
        Assert.assertArrayEquals(INDEX_05, harvesterType.getHarvesterClip().getPositions());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Change 3
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        attacker = (BaseItemType) serverItemTypeService.getItemType(TEST_HARVESTER_ITEM_ID);
        serverItemTypeService.saveItemTypeProperties(attacker.getId(),
                attacker.getBoundingBox(),
                attacker.getItemTypeSpriteMap(),
                attacker.getWeaponType(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        harvesterType = ((BaseItemType) serverItemTypeService.getItemType(TEST_HARVESTER_ITEM_ID)).getHarvesterType();
        Assert.assertNull(harvesterType.getHarvesterClip());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
