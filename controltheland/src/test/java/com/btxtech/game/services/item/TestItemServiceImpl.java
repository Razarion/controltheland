package com.btxtech.game.services.item;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.ImageHolder;
import com.btxtech.game.services.item.impl.ItemServiceImpl;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import org.apache.wicket.util.io.IOUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * User: beat
 * Date: 26.08.2011
 * Time: 20:29:24
 */
public class TestItemServiceImpl extends AbstractServiceTest {
    @Autowired
    private ItemService itemService;

    @Test
    @DirtiesContext
    public void isSyncItemOverlapping() throws Exception {
        configureRealGame();

        SimpleBase base1 = new SimpleBase(1);
        SimpleBase base2 = new SimpleBase(2);


        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isAlive(EasyMock.<SimpleBase>anyObject())).andReturn(true).anyTimes();
        setPrivateField(ItemServiceImpl.class, itemService, "baseService", baseService);

        ActionService actionService = EasyMock.createNiceMock(ActionService.class);
        setPrivateField(ItemServiceImpl.class, itemService, "actionService", actionService);

        EasyMock.replay(baseService, actionService);


        ItemType itemType1 = itemService.getItemType(TEST_HARVESTER_ITEM_ID);
        itemType1.setBoundingBox(new BoundingBox(100, 100, 80, 80, ANGELS_24));
        itemService.createSyncObject(itemType1, new Index(4486, 1279), null, base1, 0);

        ItemType itemType2 = itemService.getItemType(TEST_ATTACK_ITEM_ID);
        itemType2.setBoundingBox(new BoundingBox(70, 70, 36, 56, ANGELS_24));
        SyncItem syncItem2 = itemService.createSyncObject(itemType2, new Index(1396, 2225), null, base2, 0);


        Assert.assertFalse(itemService.isSyncItemOverlapping(syncItem2, new Index(1425, 2331), null, null));
    }

    @Test
    @DirtiesContext
    public void isSyncItemOverlappingAngel() throws Exception {
        configureRealGame();

        SimpleBase base1 = new SimpleBase(1);
        SimpleBase base2 = new SimpleBase(2);


        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isAlive(EasyMock.<SimpleBase>anyObject())).andReturn(true).anyTimes();
        setPrivateField(ItemServiceImpl.class, itemService, "baseService", baseService);

        ActionService actionService = EasyMock.createNiceMock(ActionService.class);
        setPrivateField(ItemServiceImpl.class, itemService, "actionService", actionService);

        EasyMock.replay(baseService, actionService);


        ItemType itemType1 = itemService.getItemType(TEST_HARVESTER_ITEM_ID);
        itemType1.setBoundingBox(new BoundingBox(180, 130, 182, 120, ANGELS_24));
        itemService.createSyncObject(itemType1, new Index(2820, 2626), null, base1, 0);

        ItemType itemType2 = itemService.getItemType(TEST_ATTACK_ITEM_ID);
        itemType2.setBoundingBox(new BoundingBox(80, 80, 54, 60, ANGELS_24));
        SyncItem syncItem2 = itemService.createSyncObject(itemType2, new Index(2940, 2609), null, base2, 0);

        Assert.assertFalse(itemService.isSyncItemOverlapping(syncItem2, new Index(2940, 2609), null, null));
        Assert.assertTrue(itemService.isSyncItemOverlapping(syncItem2, new Index(2940, 2609), 0.2053953891897674, null));
    }

    @Test
    @DirtiesContext
    public void spriteMapMulti() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        DbBaseItemType dbBaseItemType = (DbBaseItemType) itemService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        CrudChildServiceHelper<DbItemTypeImage> crud = dbBaseItemType.getItemTypeImageCrud();

        DbItemTypeImage dbItemTypeImage = crud.createDbChild();
        dbItemTypeImage.setNumber(1);
        dbItemTypeImage.setData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0001.png").openStream()));
        dbItemTypeImage.setContentType("png");

        dbItemTypeImage = crud.createDbChild();
        dbItemTypeImage.setNumber(2);
        dbItemTypeImage.setData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0002.png").openStream()));
        dbItemTypeImage.setContentType("png");

        dbItemTypeImage = crud.createDbChild();
        dbItemTypeImage.setNumber(3);
        dbItemTypeImage.setData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0003.png").openStream()));
        dbItemTypeImage.setContentType("png");

        dbItemTypeImage = crud.createDbChild();
        dbItemTypeImage.setNumber(4);
        dbItemTypeImage.setData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0004.png").openStream()));
        dbItemTypeImage.setContentType("png");

        dbItemTypeImage = crud.createDbChild();
        dbItemTypeImage.setNumber(5);
        dbItemTypeImage.setData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0005.png").openStream()));
        dbItemTypeImage.setContentType("png");

        dbItemTypeImage = crud.createDbChild();
        dbItemTypeImage.setNumber(6);
        dbItemTypeImage.setData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0006.png").openStream()));
        dbItemTypeImage.setContentType("png");

        dbBaseItemType.setName(TEST_SIMPLE_BUILDING);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(64, 64, 80, 80, new double[]{0.1, 0.2, 0.3, 0.4, 0.5, 0.6}));

        itemService.saveDbItemType(dbBaseItemType);
        itemService.activate();
        int itemTypeId = dbBaseItemType.getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        ImageHolder imageHolder = itemService.getItemTypeSpriteMap(itemTypeId);
        Assert.assertNotNull(imageHolder);

        BufferedImage spriteMap = ImageIO.read(new ByteArrayInputStream(imageHolder.getData()));

        assertBufferedImage("/images/hoover_bagger_0001.png", spriteMap, 0);
        assertBufferedImage("/images/hoover_bagger_0002.png", spriteMap, 64);
        assertBufferedImage("/images/hoover_bagger_0003.png", spriteMap, 128);
        assertBufferedImage("/images/hoover_bagger_0004.png", spriteMap, 192);
        assertBufferedImage("/images/hoover_bagger_0005.png", spriteMap, 256);
        assertBufferedImage("/images/hoover_bagger_0006.png", spriteMap, 320);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void spriteMapSingle() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        DbBaseItemType dbBaseItemType = (DbBaseItemType) itemService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        CrudChildServiceHelper<DbItemTypeImage> crud = dbBaseItemType.getItemTypeImageCrud();

        DbItemTypeImage dbItemTypeImage = crud.createDbChild();
        dbItemTypeImage.setNumber(1);
        dbItemTypeImage.setData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0001.png").openStream()));
        dbItemTypeImage.setContentType("png");

        dbBaseItemType.setName(TEST_SIMPLE_BUILDING);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(64, 64, 80, 80, new double[]{0}));

        itemService.saveDbItemType(dbBaseItemType);
        itemService.activate();
        int itemTypeId = dbBaseItemType.getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        ImageHolder imageHolder = itemService.getItemTypeSpriteMap(itemTypeId);
        Assert.assertNotNull(imageHolder);

        BufferedImage spriteMap = ImageIO.read(new ByteArrayInputStream(imageHolder.getData()));

        assertBufferedImage("/images/hoover_bagger_0001.png", spriteMap, 0);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getCmsDbItemTypeImageMulti() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        DbBaseItemType dbBaseItemType = (DbBaseItemType) itemService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        CrudChildServiceHelper<DbItemTypeImage> crud = dbBaseItemType.getItemTypeImageCrud();

        DbItemTypeImage dbItemTypeImage = crud.createDbChild();
        dbItemTypeImage.setNumber(1);
        dbItemTypeImage.setData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0001.png").openStream()));
        dbItemTypeImage.setContentType("image/png");

        dbItemTypeImage = crud.createDbChild();
        dbItemTypeImage.setNumber(2);
        dbItemTypeImage.setData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0002.png").openStream()));
        dbItemTypeImage.setContentType("image/png");

        dbItemTypeImage = crud.createDbChild();
        dbItemTypeImage.setNumber(3);
        dbItemTypeImage.setData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0003.png").openStream()));
        dbItemTypeImage.setContentType("image/png");

        dbItemTypeImage = crud.createDbChild();
        dbItemTypeImage.setNumber(4);
        dbItemTypeImage.setData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0004.png").openStream()));
        dbItemTypeImage.setContentType("image/png");

        dbItemTypeImage = crud.createDbChild();
        dbItemTypeImage.setNumber(5);
        dbItemTypeImage.setData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0005.png").openStream()));
        dbItemTypeImage.setContentType("image/png");

        dbItemTypeImage = crud.createDbChild();
        dbItemTypeImage.setNumber(6);
        dbItemTypeImage.setData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0006.png").openStream()));
        dbItemTypeImage.setContentType("image/png");

        dbBaseItemType.setName(TEST_SIMPLE_BUILDING);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(64, 64, 80, 80, new double[]{0.1, 0.2, 0.3, 0.4, 0.5, 0.6}));

        itemService.saveDbItemType(dbBaseItemType);
        itemService.activate();
        int itemTypeId = dbBaseItemType.getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        DbItemTypeImage resultDbItemTypeImage = itemService.getCmsDbItemTypeImage(itemTypeId);
        Assert.assertNotNull(resultDbItemTypeImage);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(resultDbItemTypeImage.getData()));

        assertBufferedImage("/images/hoover_bagger_0001.png", image, 0);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getCmsDbItemTypeImageSingle() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        DbBaseItemType dbBaseItemType = (DbBaseItemType) itemService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        CrudChildServiceHelper<DbItemTypeImage> crud = dbBaseItemType.getItemTypeImageCrud();

        DbItemTypeImage dbItemTypeImage = crud.createDbChild();
        dbItemTypeImage.setNumber(1);
        dbItemTypeImage.setData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0001.png").openStream()));
        dbItemTypeImage.setContentType("image/png");

        dbBaseItemType.setName(TEST_SIMPLE_BUILDING);
        dbBaseItemType.setTerrainType(TerrainType.LAND);
        dbBaseItemType.setBounding(new BoundingBox(64, 64, 80, 80, new double[]{0.1}));

        itemService.saveDbItemType(dbBaseItemType);
        itemService.activate();
        int itemTypeId = dbBaseItemType.getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        DbItemTypeImage resultDbItemTypeImage = itemService.getCmsDbItemTypeImage(itemTypeId);
        Assert.assertNotNull(resultDbItemTypeImage);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(resultDbItemTypeImage.getData()));

        assertBufferedImage("/images/hoover_bagger_0001.png", image, 0);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void assertBufferedImage(String expectedFileName, BufferedImage spriteMap, int xOffset) throws IOException {
        final double MAX_DIFF = 1.0;
        BufferedImage expected = ImageIO.read(getClass().getResource(expectedFileName).openStream());
        BufferedImage actual = spriteMap.getSubimage(xOffset, 0, expected.getWidth(), expected.getHeight());
        Assert.assertEquals("Image width is not same", expected.getWidth(), actual.getWidth());
        Assert.assertEquals("Image height is not same", expected.getHeight(), actual.getHeight());
        Assert.assertEquals("Image color model is not same", expected.getColorModel(), actual.getColorModel());
        Assert.assertEquals("Image type is not same", expected.getType(), actual.getType());
        int width = expected.getWidth();
        int height = expected.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int expectedPixel = expected.getRGB(x, y);
                int expectedAlpha = 0xFF & (expectedPixel >> 24);
                int expectedRed = 0xFF & (expectedPixel >> 16);
                int expectedGreen = 0xFF & (expectedPixel >> 8);
                int expectedBlue = 0xFF & expectedPixel;
                int actualPixel = actual.getRGB(x, y);
                int actualAlpha = 0xFF & (actualPixel >> 24);
                int actualRed = 0xFF & (actualPixel >> 16);
                int actualGreen = 0xFF & (actualPixel >> 8);
                int actualBlue = 0xFF & actualPixel;
                double alpha = (double) expectedAlpha / 255.0;
                double redDiff = Math.abs(expectedRed - actualRed) * alpha;
                double greenDiff = Math.abs(expectedGreen - actualGreen) * alpha;
                double blueDiff = Math.abs(expectedBlue - actualBlue) * alpha;

                if (redDiff > MAX_DIFF || greenDiff > MAX_DIFF || blueDiff > MAX_DIFF) {
                    System.out.println("x: " + x + " y: " + y);
                    System.out.println("Alpha expected: " + expectedAlpha + " actual: " + actualAlpha);
                    System.out.println("Red   expected: " + expectedRed + " actual: " + actualRed + " diff: " + redDiff);
                    System.out.println("Green expected: " + expectedGreen + " actual: " + actualGreen + " diff: " + greenDiff);
                    System.out.println("Blue  expected: " + expectedBlue + " actual: " + actualBlue + " diff: " + blueDiff);
                    Assert.assertEquals("Image pixel is not the same x:" + x + " y:" + y, expectedPixel, actualPixel);
                }
            }
        }
    }

}
