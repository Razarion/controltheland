package com.btxtech.game.jsre.client.effects;

import com.btxtech.game.jsre.client.ClientClipHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.info.ClipInfo;
import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.btxtech.game.jsre.client.common.info.PreloadedImageSpriteMapInfo;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.common.gameengine.itemType.BuilderType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemClipPosition;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBuilder;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;
import com.btxtech.game.services.AbstractServiceTest;
import junit.framework.Assert;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 09.05.14
 * Time: 22:38
 */
public class TestItemEffectHandler {
    private static final int CLIP_1 = 1;
    private static final int CLIP_2 = 2;
    private static final int CLIP_3 = 3;
    private static final int SPRITE_MAP_1 = 1;
    private static final int SPRITE_MAP_2 = 2;
    private static final int SPRITE_MAP_3 = 3;

    private void setupClipHandler() {
        RealGameInfo gameInfo = new RealGameInfo();
        Collection<ClipInfo> clipLibrary = new ArrayList<>();
        Collection<ImageSpriteMapInfo> imageSpriteMapLibrary = new ArrayList<>();
        addClip(clipLibrary, imageSpriteMapLibrary, CLIP_1, SPRITE_MAP_1, 2, 10);
        addClip(clipLibrary, imageSpriteMapLibrary, CLIP_2, SPRITE_MAP_2, 3, 15);
        addClip(clipLibrary, imageSpriteMapLibrary, CLIP_3, SPRITE_MAP_3, 4, 20);
        gameInfo.setClipLibrary(clipLibrary);
        gameInfo.setPreloadedImageSpriteMapInfo(new PreloadedImageSpriteMapInfo());
        gameInfo.setImageSpriteMapLibrary(imageSpriteMapLibrary);
        ClientClipHandler.getInstance().init(gameInfo);
    }

    private void addClip(Collection<ClipInfo> clipLibrary, Collection<ImageSpriteMapInfo> imageSpriteMapLibrary, int clipId, int spriteMapId, int frameCount, int frameTime) {
        ClipInfo clipInfo = new ClipInfo(clipId);
        clipInfo.setSpriteMapId(spriteMapId);
        clipLibrary.add(clipInfo);
        ImageSpriteMapInfo imageSpriteMapInfo = new ImageSpriteMapInfo(spriteMapId);
        imageSpriteMapInfo.setFrameCount(frameCount);
        imageSpriteMapInfo.setFrameTime(frameTime);
        imageSpriteMapLibrary.add(imageSpriteMapInfo);
    }

    @Before
    public void setup() {
        ItemEffectHandler.getInstance().getClips(1, new Rectangle(0, 0, 100, 100), new ArrayList<SyncItem>());
        setupClipHandler();
    }

    @Test
    public void empty() {
        Collection<ItemEffect> result = ItemEffectHandler.getInstance().getClips(1, new Rectangle(0, 0, 100, 100), new ArrayList<SyncItem>());
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testNoImages() {
        // Prepare
        SyncBaseItem syncBaseItem = EasyMock.createNiceMock(SyncBaseItem.class);
        ItemType itemType = EasyMock.createNiceMock(ItemType.class);
        ItemTypeSpriteMap itemTypeSpriteMap = EasyMock.createNiceMock(ItemTypeSpriteMap.class);
        EasyMock.expect(syncBaseItem.getItemType()).andReturn(itemType);
        EasyMock.expect(itemType.getItemTypeSpriteMap()).andReturn(itemTypeSpriteMap);
        EasyMock.expect(itemTypeSpriteMap.getDemolitionClipIds(syncBaseItem)).andReturn(null);
        EasyMock.replay(syncBaseItem, itemType, itemTypeSpriteMap);

        // Run test
        Collection<SyncItem> syncItems = new ArrayList<>();
        syncItems.add(syncBaseItem);
        Collection<ItemEffect> result = ItemEffectHandler.getInstance().getClips(1, new Rectangle(0, 0, 100, 100), syncItems);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void test1FireDemolitionStep() {
        // Prepare
        SyncBaseItem syncBaseItem = EasyMock.createNiceMock(SyncBaseItem.class);
        ItemType itemType = EasyMock.createNiceMock(ItemType.class);
        ItemTypeSpriteMap itemTypeSpriteMap = EasyMock.createNiceMock(ItemTypeSpriteMap.class);
        SyncItemArea syncItemArea = EasyMock.createNiceMock(SyncItemArea.class);
        EasyMock.expect(syncBaseItem.getItemType()).andReturn(itemType).anyTimes();
        EasyMock.expect(syncBaseItem.getSyncItemArea()).andReturn(syncItemArea).anyTimes();
        EasyMock.expect(itemType.getItemTypeSpriteMap()).andReturn(itemTypeSpriteMap).anyTimes();
        EasyMock.expect(syncItemArea.getPosition()).andReturn(new Index(100, 100)).anyTimes();
        EasyMock.expect(syncItemArea.getAngelIndex()).andReturn(0).anyTimes();
        Collection<ItemClipPosition> itemClipPositions = new ArrayList<>();
        itemClipPositions.add(new ItemClipPosition(CLIP_1, AbstractServiceTest.INDEX_24));
        EasyMock.expect(itemTypeSpriteMap.getDemolitionClipIds(syncBaseItem)).andReturn(itemClipPositions).anyTimes();
        EasyMock.replay(syncBaseItem, itemType, itemTypeSpriteMap, syncItemArea);
        // Run test
        Collection<SyncItem> syncItems = new ArrayList<>();
        syncItems.add(syncBaseItem);
        long timeStamp = System.currentTimeMillis();
        Collection<ItemEffect> result = ItemEffectHandler.getInstance().getClips(timeStamp, new Rectangle(0, 0, 100, 100), syncItems);
        Assert.assertEquals(1, result.size());
        verifyItemEffect(result, SPRITE_MAP_1, 0);
        result = ItemEffectHandler.getInstance().getClips(timeStamp + 10, new Rectangle(0, 0, 100, 100), syncItems);
        Assert.assertEquals(1, result.size());
        verifyItemEffect(result, SPRITE_MAP_1, 1);
        result = ItemEffectHandler.getInstance().getClips(timeStamp + 20, new Rectangle(0, 0, 100, 100), syncItems);
        Assert.assertEquals(1, result.size());
        verifyItemEffect(result, SPRITE_MAP_1, 0);
    }

    @Test
    public void test1Buildup() {
        // Prepare
        SyncBaseItem syncBaseItem = EasyMock.createNiceMock(SyncBaseItem.class);
        SyncItemArea syncItemArea = EasyMock.createNiceMock(SyncItemArea.class);
        SyncBuilder syncBuilder = EasyMock.createNiceMock(SyncBuilder.class);
        BuilderType builderType = EasyMock.createNiceMock(BuilderType.class);
        EasyMock.expect(syncBaseItem.isHealthy()).andReturn(true).anyTimes();
        EasyMock.expect(syncBaseItem.hasSyncBuilder()).andReturn(true).anyTimes();
        EasyMock.expect(syncBuilder.getBuilderType()).andReturn(builderType).anyTimes();
        EasyMock.expect(syncBuilder.isBuilding()).andReturn(true).anyTimes();
        EasyMock.expect(builderType.getBuildupClip()).andReturn(new ItemClipPosition(CLIP_2, AbstractServiceTest.INDEX_24)).anyTimes();

        EasyMock.expect(syncBaseItem.getSyncBuilder()).andReturn(syncBuilder).anyTimes();
        EasyMock.expect(syncBaseItem.getSyncItemArea()).andReturn(syncItemArea).anyTimes();
        EasyMock.expect(syncItemArea.getPosition()).andReturn(new Index(100, 100)).anyTimes();
        EasyMock.expect(syncItemArea.getAngelIndex()).andReturn(0).anyTimes();
        EasyMock.replay(syncBaseItem, syncBuilder, builderType, syncItemArea);
        // Run test
        Collection<SyncItem> syncItems = new ArrayList<>();
        syncItems.add(syncBaseItem);
        long timeStamp = System.currentTimeMillis();
        Collection<ItemEffect> result = ItemEffectHandler.getInstance().getClips(timeStamp, new Rectangle(0, 0, 100, 100), syncItems);
        Assert.assertEquals(1, result.size());
        verifyItemEffect(result, SPRITE_MAP_2, 0);
        result = ItemEffectHandler.getInstance().getClips(timeStamp + 15, new Rectangle(0, 0, 100, 100), syncItems);
        Assert.assertEquals(1, result.size());
        verifyItemEffect(result, SPRITE_MAP_2, 1);
        result = ItemEffectHandler.getInstance().getClips(timeStamp + 30, new Rectangle(0, 0, 100, 100), syncItems);
        Assert.assertEquals(1, result.size());
        verifyItemEffect(result, SPRITE_MAP_2, 2);
    }

/*
    // TODO can not be tested due to ClientPlanetServices.getInstance().getItemService()
    @Test
    public void test1Harvest() {
        // Prepare
        SyncBaseItem syncBaseItem = EasyMock.createNiceMock(SyncBaseItem.class);
        SyncItemArea syncItemArea = EasyMock.createNiceMock(SyncItemArea.class);
        SyncHarvester syncHarvester = EasyMock.createNiceMock(SyncHarvester.class);
        HarvesterType harvesterType = EasyMock.createNiceMock(HarvesterType.class);
        EasyMock.expect(syncBaseItem.isHealthy()).andReturn(true).anyTimes();
        EasyMock.expect(syncBaseItem.hasSyncHarvester()).andReturn(true).anyTimes();
        EasyMock.expect(syncHarvester.getHarvesterType()).andReturn(harvesterType).anyTimes();
        EasyMock.expect(syncHarvester.isHarvesting()).andReturn(true).anyTimes();
        EasyMock.expect(harvesterType.getHarvesterClip()).andReturn(new ItemClipPosition(CLIP_3, AbstractServiceTest.INDEX_24)).anyTimes();
        EasyMock.expect(syncBaseItem.getSyncHarvester()).andReturn(syncHarvester).anyTimes();
        EasyMock.expect(syncBaseItem.getSyncItemArea()).andReturn(syncItemArea).anyTimes();
        EasyMock.expect(syncItemArea.getPosition()).andReturn(new Index(100, 100)).anyTimes();
        EasyMock.expect(syncItemArea.getAngelIndex()).andReturn(0).anyTimes();
        ClientPlanetServices.getInstance().set
        EasyMock.replay(syncBaseItem, syncHarvester, harvesterType, syncItemArea);
        // Run test
        Collection<SyncItem> syncItems = new ArrayList<>();
        syncItems.add(syncBaseItem);
        long timeStamp = System.currentTimeMillis();
        Collection<ItemEffect> result = ItemEffectHandler.getInstance().getClips(timeStamp, new Rectangle(0, 0, 100, 100), syncItems);
        Assert.assertEquals(1, result.size());
        verifyItemEffect(result, SPRITE_MAP_3, 0);
        result = ItemEffectHandler.getInstance().getClips(timeStamp + 15, new Rectangle(0, 0, 100, 100), syncItems);
        Assert.assertEquals(1, result.size());
        verifyItemEffect(result, SPRITE_MAP_3, 1);
        result = ItemEffectHandler.getInstance().getClips(timeStamp + 30, new Rectangle(0, 0, 100, 100), syncItems);
        Assert.assertEquals(1, result.size());
        verifyItemEffect(result, SPRITE_MAP_3, 2);
    }      */

    private void verifyItemEffect(Collection<ItemEffect> itemEffects, int spriteMapId, int frameNumber) {
        for (ItemEffect itemEffect : itemEffects) {
            if (itemEffect.getImageSpriteMapInfo().getId() == spriteMapId && itemEffect.getFrame() == frameNumber) {
                return;
            }
        }
        Assert.fail("No such image. spriteMapId=" + spriteMapId + " frameNumber=" + frameNumber);
    }
}
