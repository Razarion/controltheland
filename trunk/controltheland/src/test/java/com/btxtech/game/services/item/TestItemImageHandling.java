package com.btxtech.game.services.item;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.DemolitionStepSpriteMap;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemClipPosition;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeImageInfo;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.ImageHolder;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import com.btxtech.game.services.item.itemType.DbWeaponType;
import com.btxtech.game.wicket.pages.mgmt.Html5ImagesUploadConverter;
import org.apache.wicket.util.io.IOUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: beat
 * Date: 09.01.2012
 * Time: 23:36:24
 */
public class TestItemImageHandling extends AbstractServiceTest {
    public static final String INLINE_IMAGE_1 = "data:image/png;base64,xxxx";
    public static final String MIME_IMAGE_1 = "image/png";
    public static final byte[] IMAGE_DATA_1 = new byte[]{-57, 28, 113};
    public static final String INLINE_IMAGE_2 = "data:image/loeli;base64,iVBORw0KGgoAAAANS";
    public static final String MIME_IMAGE_2 = "image/loeli";
    public static final byte[] IMAGE_DATA_2 = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13};
    public static final String INLINE_IMAGE_3 = "data:image/jpg;base64,iVBOdseedc";
    public static final String MIME_IMAGE_3 = "image/jpg";
    public static final byte[] IMAGE_DATA_3 = new byte[]{-119, 80, 78, 118, -57, -98, 117};
    public static final String INLINE_IMAGE_4 = "data:image/gif;base64,wrefdfs";
    public static final String MIME_IMAGE_4 = "image/gif";
    public static final byte[] IMAGE_DATA_4 = new byte[]{-62, -73, -97, 117, -5};
    public static final String INLINE_IMAGE_5 = "data:image/png;base64," +
            "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAMAAAAoLQ9TAAAABGdBTUEAANbY1E9YMgAAABl0" +
            "RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAGAUExURcDYv1C8WdPrtIDAeWqk" +
            "Oqnd2VLWaOX0x7ftwZzZlIjGxr7gxanLkdfu1FumybHSsJ3Cutbyx4S9fm2paMrq0Lfo1L/a" +
            "o6PHiozL11aZJpnW2oG0ZLTcm5zjvovbvM3qtcror2q5vUaIRHLGZWWkR0aQGSR/BYy5baHY" +
            "imrbd/z+/HO9bPb69m29xHy8c2OeMUGJPZDThZPPi3ivdGazzZbBgCuCEd3x2jaICs7ksqLZ" +
            "mTp/N6zwtWu/YLDWvZHHeqLS0affzKfvsWDYpn7CvJHJh0+cSGWbY6PQka/Xkvj894W2a4DQ" +
            "qIrDhF+hQ8fexJDN24PYgYGzWnXJy6zXpp3Fm5jCg37NdI++dWejWmCwVnSrT6nuyTuLELvh" +
            "pV2gO6DSwI3qnd3wv6rQxK/fz1OZxrTvtaLim3rRr+bv5TuCN+jw557FiK7NlWzRkHDGhHLd" +
            "fnHWmm2zx7Xe1m21zW65z9Ts0HTHaHrGb3vJb3yvVbTaxVGUTITLtV6w0////94DUpYAAACA" +
            "dFJOU///////////////////////////////////////////////////////////////////" +
            "////////////////////////////////////////////////////////////////////////" +
            "//////////////////////////////8AOAVLZwAAAQxJREFUeNpiqAcCc87wcuVKTl4Qm6G+" +
            "XstKOU6eu9ouxNbICySgZajBzluWUuzgkOyqpwMUsNJIEuQVYWUNkOCysxeqZzBXZk+z4RCV" +
            "kiopquVUcPNn4JSRd4oRkZAwUSwsKPAQN2YIZ0pPFBUpNQnWZGNj0xOrYShPCtSUdeTTlXNm" +
            "0WcUtjRgUE4Kz5Plq0vVzWAJkoy0zGKokDeU4+JjYWHRr+KJVhVTYjDylMn34WPJzeXhiRBX" +
            "sXBnKIti0tZW1M9VV/eWlg6LV2Oo943wZHaR5BEXz/EOUzHTYajXEfZWSJBUVY2NN1UxywR5" +
            "TsfYIFRAQIDfws8sG+xbICGsZG2tpKamBWQDBBgAU049f2kd9aAAAAAASUVORK5CYII=";
    private static final String MIME_IMAGE_5 = "image/png";
    private static final int MIME_IMAGE_5_WIDTH = 16;
    private static final int MIME_IMAGE_5_HEIGHT = 16;


    @Autowired
    private ServerItemTypeService serverItemTypeService;

    @Test
    @DirtiesContext
    public void convertInlineImage1() {
        Html5ImagesUploadConverter.Package aPackage = Html5ImagesUploadConverter.convertInlineImage(INLINE_IMAGE_1);
        Assert.assertEquals(MIME_IMAGE_1, aPackage.getMime());
        Assert.assertEquals("xxxx", aPackage.getBase64Data());
        byte[] array = aPackage.convertBase64ToBytes();
        Assert.assertArrayEquals(IMAGE_DATA_1, array);
    }

    @Test
    @DirtiesContext
    public void convertInlineImage2() {
        Html5ImagesUploadConverter.Package aPackage = Html5ImagesUploadConverter.convertInlineImage(INLINE_IMAGE_2);
        Assert.assertEquals(MIME_IMAGE_2, aPackage.getMime());
        Assert.assertEquals("iVBORw0KGgoAAAANS", aPackage.getBase64Data());
        byte[] array = aPackage.convertBase64ToBytes();
        Assert.assertArrayEquals(IMAGE_DATA_2, array);
    }

    @Test
    @DirtiesContext
    public void overall() throws Exception {
        CrudRootServiceHelper<DbItemType> itemCrud = serverItemTypeService.getDbItemTypeCrud();

        // Create BaseItemType
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType dbBaseItemType = (DbBaseItemType) itemCrud.createDbChild(DbBaseItemType.class);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activated
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify Activated
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Collection<ItemType> itemTypes = serverItemTypeService.getItemTypes();
        Assert.assertEquals(1, itemTypes.size());
        BaseItemType baseItemType = (BaseItemType) CommonJava.getFirst(itemTypes);
        BoundingBox boundingBox = baseItemType.getBoundingBox();
        Assert.assertEquals(0, boundingBox.getRadius());
        Assert.assertEquals(1, boundingBox.getAngelCount());
        ItemTypeSpriteMap itemTypeSpriteMap = baseItemType.getItemTypeSpriteMap();
        Assert.assertEquals(0, itemTypeSpriteMap.getImageWidth());
        Assert.assertEquals(0, itemTypeSpriteMap.getImageHeight());
        Assert.assertEquals(0, itemTypeSpriteMap.getBuildupSteps());
        Assert.assertEquals(0, itemTypeSpriteMap.getBuildupAnimationFrames());
        Assert.assertEquals(0, itemTypeSpriteMap.getBuildupAnimationDuration());
        Assert.assertEquals(0, itemTypeSpriteMap.getRuntimeAnimationFrames());
        Assert.assertEquals(0, itemTypeSpriteMap.getRuntimeAnimationDuration());
        Assert.assertNull(itemTypeSpriteMap.getDemolitionSteps());
        Assert.assertEquals(0, itemTypeSpriteMap.getDemolitionStepCount());
        Assert.assertEquals(new Index(0, 0), itemTypeSpriteMap.getCosmeticImageOffset());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Add first Runtime image
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        boundingBox = new BoundingBox(10, new double[]{0.0});
        itemTypeSpriteMap = new ItemTypeSpriteMap(boundingBox, MIME_IMAGE_5_WIDTH, MIME_IMAGE_5_HEIGHT, 0, 0, 0, 1, 0, null);
        ItemTypeImageInfo runtimeImageInf = new ItemTypeImageInfo(0, 0, 0, INLINE_IMAGE_5);
        serverItemTypeService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                null,
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>singletonList(runtimeImageInf),
                Collections.<ItemTypeImageInfo>emptyList(),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activated
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        itemTypes = serverItemTypeService.getItemTypes();
        Assert.assertEquals(1, itemTypes.size());
        baseItemType = (BaseItemType) CommonJava.getFirst(itemTypes);
        boundingBox = baseItemType.getBoundingBox();
        Assert.assertEquals(10, boundingBox.getRadius());
        Assert.assertEquals(1, boundingBox.getAngelCount());
        Assert.assertArrayEquals(new double[]{0}, boundingBox.getAngels(), 0.0001);
        itemTypeSpriteMap = baseItemType.getItemTypeSpriteMap();
        Assert.assertEquals(16, itemTypeSpriteMap.getImageWidth());
        Assert.assertEquals(16, itemTypeSpriteMap.getImageHeight());
        Assert.assertEquals(0, itemTypeSpriteMap.getBuildupSteps());
        Assert.assertEquals(0, itemTypeSpriteMap.getBuildupAnimationFrames());
        Assert.assertEquals(0, itemTypeSpriteMap.getBuildupAnimationDuration());
        Assert.assertEquals(1, itemTypeSpriteMap.getRuntimeAnimationFrames());
        Assert.assertEquals(0, itemTypeSpriteMap.getRuntimeAnimationDuration());
        Assert.assertNull(itemTypeSpriteMap.getDemolitionSteps());
        Assert.assertEquals(0, itemTypeSpriteMap.getDemolitionStepCount());
        Assert.assertEquals(new Index(0, 0), itemTypeSpriteMap.getCosmeticImageOffset());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Add second Runtime image
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        boundingBox = new BoundingBox(10, new double[]{0.0, 0.1});
        itemTypeSpriteMap = new ItemTypeSpriteMap(boundingBox, MIME_IMAGE_5_WIDTH, MIME_IMAGE_5_HEIGHT, 0, 0, 0, 1, 0, null);
        runtimeImageInf = new ItemTypeImageInfo(1, 0, 0, INLINE_IMAGE_5);
        serverItemTypeService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                null,
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>singletonList(runtimeImageInf),
                Collections.<ItemTypeImageInfo>emptyList(),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activated
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        itemTypes = serverItemTypeService.getItemTypes();
        Assert.assertEquals(1, itemTypes.size());
        baseItemType = (BaseItemType) CommonJava.getFirst(itemTypes);
        boundingBox = baseItemType.getBoundingBox();
        Assert.assertEquals(10, boundingBox.getRadius());
        Assert.assertEquals(2, boundingBox.getAngelCount());
        Assert.assertArrayEquals(new double[]{0, 0.1}, boundingBox.getAngels(), 0.0001);
        itemTypeSpriteMap = baseItemType.getItemTypeSpriteMap();
        Assert.assertEquals(16, itemTypeSpriteMap.getImageWidth());
        Assert.assertEquals(16, itemTypeSpriteMap.getImageHeight());
        Assert.assertEquals(0, itemTypeSpriteMap.getBuildupSteps());
        Assert.assertEquals(0, itemTypeSpriteMap.getBuildupAnimationFrames());
        Assert.assertEquals(0, itemTypeSpriteMap.getBuildupAnimationDuration());
        Assert.assertEquals(1, itemTypeSpriteMap.getRuntimeAnimationFrames());
        Assert.assertEquals(0, itemTypeSpriteMap.getRuntimeAnimationDuration());
        Assert.assertNull(itemTypeSpriteMap.getDemolitionSteps());
        Assert.assertEquals(0, itemTypeSpriteMap.getDemolitionStepCount());
        Assert.assertEquals(new Index(0, 0), itemTypeSpriteMap.getCosmeticImageOffset());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Add first startup image
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        boundingBox = new BoundingBox(10, new double[]{0.0, 0.1});
        itemTypeSpriteMap = new ItemTypeSpriteMap(boundingBox, MIME_IMAGE_5_WIDTH, MIME_IMAGE_5_HEIGHT, 1, 1, 0, 1, 0, null);
        ItemTypeImageInfo buildupImage = new ItemTypeImageInfo(1, 0, 0, INLINE_IMAGE_5);
        serverItemTypeService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                null,
                Collections.<ItemTypeImageInfo>singletonList(buildupImage),
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activated
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        itemTypes = serverItemTypeService.getItemTypes();
        Assert.assertEquals(1, itemTypes.size());
        baseItemType = (BaseItemType) CommonJava.getFirst(itemTypes);
        boundingBox = baseItemType.getBoundingBox();
        Assert.assertEquals(10, boundingBox.getRadius());
        Assert.assertEquals(2, boundingBox.getAngelCount());
        Assert.assertArrayEquals(new double[]{0, 0.1}, boundingBox.getAngels(), 0.0001);
        itemTypeSpriteMap = baseItemType.getItemTypeSpriteMap();
        Assert.assertEquals(16, itemTypeSpriteMap.getImageWidth());
        Assert.assertEquals(16, itemTypeSpriteMap.getImageHeight());
        Assert.assertEquals(1, itemTypeSpriteMap.getBuildupSteps());
        Assert.assertEquals(1, itemTypeSpriteMap.getBuildupAnimationFrames());
        Assert.assertEquals(0, itemTypeSpriteMap.getBuildupAnimationDuration());
        Assert.assertEquals(1, itemTypeSpriteMap.getRuntimeAnimationFrames());
        Assert.assertEquals(0, itemTypeSpriteMap.getRuntimeAnimationDuration());
        Assert.assertNull(itemTypeSpriteMap.getDemolitionSteps());
        Assert.assertEquals(0, itemTypeSpriteMap.getDemolitionStepCount());
        Assert.assertEquals(new Index(16, 0), itemTypeSpriteMap.getCosmeticImageOffset());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Add two demolition image
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        boundingBox = new BoundingBox(10, new double[]{0.0, 0.1});
        itemTypeSpriteMap = new ItemTypeSpriteMap(boundingBox, MIME_IMAGE_5_WIDTH, MIME_IMAGE_5_HEIGHT, 1, 1, 10, 1, 101, new DemolitionStepSpriteMap[]{new DemolitionStepSpriteMap(1, 123, null)});
        ItemTypeImageInfo demolition1 = new ItemTypeImageInfo(0, 0, 0, INLINE_IMAGE_5);
        ItemTypeImageInfo demolition2 = new ItemTypeImageInfo(1, 0, 0, INLINE_IMAGE_5);
        serverItemTypeService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                null,
                Collections.<ItemTypeImageInfo>emptyList(),
                Collections.<ItemTypeImageInfo>emptyList(),
                Arrays.asList(demolition1, demolition2),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activated
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        itemTypes = serverItemTypeService.getItemTypes();
        Assert.assertEquals(1, itemTypes.size());
        baseItemType = (BaseItemType) CommonJava.getFirst(itemTypes);
        boundingBox = baseItemType.getBoundingBox();
        Assert.assertEquals(10, boundingBox.getRadius());
        Assert.assertEquals(2, boundingBox.getAngelCount());
        Assert.assertArrayEquals(new double[]{0, 0.1}, boundingBox.getAngels(), 0.0001);
        itemTypeSpriteMap = baseItemType.getItemTypeSpriteMap();
        Assert.assertEquals(16, itemTypeSpriteMap.getImageWidth());
        Assert.assertEquals(16, itemTypeSpriteMap.getImageHeight());
        Assert.assertEquals(1, itemTypeSpriteMap.getBuildupSteps());
        Assert.assertEquals(1, itemTypeSpriteMap.getBuildupAnimationFrames());
        Assert.assertEquals(10, itemTypeSpriteMap.getBuildupAnimationDuration());
        Assert.assertEquals(1, itemTypeSpriteMap.getRuntimeAnimationFrames());
        Assert.assertEquals(101, itemTypeSpriteMap.getRuntimeAnimationDuration());
        Assert.assertEquals(1, itemTypeSpriteMap.getDemolitionStepCount());
        Assert.assertEquals(1, itemTypeSpriteMap.getDemolitionSteps()[0].getAnimationFrames());
        Assert.assertEquals(123, itemTypeSpriteMap.getDemolitionSteps()[0].getAnimationDuration());
        Assert.assertEquals(new Index(16, 0), itemTypeSpriteMap.getCosmeticImageOffset());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void modifyImages() throws Exception {
        // Create BaseItemType
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbItemTypeImage> dbImages = loadAllDbItemTypeImage();
        Assert.assertTrue(dbImages.isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BoundingBox boundingBox = new BoundingBox(10, new double[]{0.0, 0.1});
        ItemTypeSpriteMap itemTypeSpriteMap = new ItemTypeSpriteMap(boundingBox, 100, 100, 0, 0, 0, 1, 0, null);
        serverItemTypeService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                null,
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.asList(new ItemTypeImageInfo(0, 0, 0, INLINE_IMAGE_1), new ItemTypeImageInfo(1, 0, 0, INLINE_IMAGE_2)),
                Arrays.<ItemTypeImageInfo>asList(),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbImages = loadAllDbItemTypeImage();
        Assert.assertEquals(2, dbImages.size());
        assertDbItemTypeImage(dbImages, 0, 0, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, IMAGE_DATA_1, MIME_IMAGE_1);
        assertDbItemTypeImage(dbImages, 1, 1, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, IMAGE_DATA_2, MIME_IMAGE_2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        boundingBox = new BoundingBox(10, new double[]{0.0, 0.1, 0.2, 0.3});
        itemTypeSpriteMap = new ItemTypeSpriteMap(boundingBox, 100, 100, 0, 0, 0, 1, 0, null);
        serverItemTypeService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                null,
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.asList(new ItemTypeImageInfo(2, 0, 0, INLINE_IMAGE_3), new ItemTypeImageInfo(3, 0, 0, INLINE_IMAGE_4)),
                Arrays.<ItemTypeImageInfo>asList(),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbImages = loadAllDbItemTypeImage();
        Assert.assertEquals(4, dbImages.size());
        assertDbItemTypeImage(dbImages, 0, 0, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, IMAGE_DATA_1, MIME_IMAGE_1);
        assertDbItemTypeImage(dbImages, 1, 1, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, IMAGE_DATA_2, MIME_IMAGE_2);
        assertDbItemTypeImage(dbImages, 2, 2, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, IMAGE_DATA_3, MIME_IMAGE_3);
        assertDbItemTypeImage(dbImages, 3, 3, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, IMAGE_DATA_4, MIME_IMAGE_4);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        boundingBox = new BoundingBox(10, new double[]{0.0, 0.1});
        itemTypeSpriteMap = new ItemTypeSpriteMap(boundingBox, 100, 100, 0, 0, 0, 1, 0, null);
        serverItemTypeService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                null,
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbImages = loadAllDbItemTypeImage();
        Assert.assertEquals(2, dbImages.size());
        assertDbItemTypeImage(dbImages, 0, 0, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, IMAGE_DATA_1, MIME_IMAGE_1);
        assertDbItemTypeImage(dbImages, 1, 1, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, IMAGE_DATA_2, MIME_IMAGE_2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        boundingBox = new BoundingBox(10, new double[]{0.0, 0.1});
        itemTypeSpriteMap = new ItemTypeSpriteMap(boundingBox, 100, 100, 0, 0, 0, 1, 0, null);
        serverItemTypeService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                null,
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.asList(new ItemTypeImageInfo(0, 0, 0, INLINE_IMAGE_3)),
                Arrays.<ItemTypeImageInfo>asList(),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbImages = loadAllDbItemTypeImage();
        Assert.assertEquals(2, dbImages.size());
        assertDbItemTypeImage(dbImages, 0, 0, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, IMAGE_DATA_3, MIME_IMAGE_3);
        assertDbItemTypeImage(dbImages, 1, 1, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, IMAGE_DATA_2, MIME_IMAGE_2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        boundingBox = new BoundingBox(10, new double[]{0.0, 0.1});
        itemTypeSpriteMap = new ItemTypeSpriteMap(boundingBox, 100, 100, 1, 1, 0, 1, 0, null);
        serverItemTypeService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                null,
                Arrays.asList(new ItemTypeImageInfo(0, 0, 0, INLINE_IMAGE_4)),
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbImages = loadAllDbItemTypeImage();
        Assert.assertEquals(3, dbImages.size());
        assertDbItemTypeImage(dbImages, 0, 0, 0, 0, ItemTypeSpriteMap.SyncObjectState.BUILD_UP, IMAGE_DATA_4, MIME_IMAGE_4);
        assertDbItemTypeImage(dbImages, 1, 0, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, IMAGE_DATA_3, MIME_IMAGE_3);
        assertDbItemTypeImage(dbImages, 2, 1, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, IMAGE_DATA_2, MIME_IMAGE_2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        boundingBox = new BoundingBox(10, new double[]{0.0, 0.1});
        itemTypeSpriteMap = new ItemTypeSpriteMap(boundingBox, 100, 100, 1, 1, 0, 1, 0, new DemolitionStepSpriteMap[]{new DemolitionStepSpriteMap(2, 0, null)});
        serverItemTypeService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                null,
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.asList(new ItemTypeImageInfo(0, 0, 0, INLINE_IMAGE_1), new ItemTypeImageInfo(0, 0, 1, INLINE_IMAGE_2), new ItemTypeImageInfo(1, 0, 0, INLINE_IMAGE_3), new ItemTypeImageInfo(1, 0, 1, INLINE_IMAGE_4)),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbImages = loadAllDbItemTypeImage();
        Assert.assertEquals(7, dbImages.size());
        assertDbItemTypeImage(dbImages, 0, 0, 0, 0, ItemTypeSpriteMap.SyncObjectState.BUILD_UP, IMAGE_DATA_4, MIME_IMAGE_4);
        assertDbItemTypeImage(dbImages, 1, 0, 0, 0, ItemTypeSpriteMap.SyncObjectState.DEMOLITION, IMAGE_DATA_1, MIME_IMAGE_1);
        assertDbItemTypeImage(dbImages, 2, 0, 0, 1, ItemTypeSpriteMap.SyncObjectState.DEMOLITION, IMAGE_DATA_2, MIME_IMAGE_2);
        assertDbItemTypeImage(dbImages, 3, 1, 0, 0, ItemTypeSpriteMap.SyncObjectState.DEMOLITION, IMAGE_DATA_3, MIME_IMAGE_3);
        assertDbItemTypeImage(dbImages, 4, 1, 0, 1, ItemTypeSpriteMap.SyncObjectState.DEMOLITION, IMAGE_DATA_4, MIME_IMAGE_4);
        assertDbItemTypeImage(dbImages, 5, 0, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, IMAGE_DATA_3, MIME_IMAGE_3);
        assertDbItemTypeImage(dbImages, 6, 1, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, IMAGE_DATA_2, MIME_IMAGE_2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        boundingBox = new BoundingBox(10, new double[]{0.0});
        itemTypeSpriteMap = new ItemTypeSpriteMap(boundingBox, 100, 100, 0, 0, 0, 1, 0, null);
        serverItemTypeService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                null,
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbImages = loadAllDbItemTypeImage();
        Assert.assertEquals(1, dbImages.size());
        assertDbItemTypeImage(dbImages, 0, 0, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, IMAGE_DATA_3, MIME_IMAGE_3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void assertDbItemTypeImage(List<DbItemTypeImage> dbImages, int index, int angelIndex, int step, int frame, ItemTypeSpriteMap.SyncObjectState objectState, byte[] data, String contentType) {
        DbItemTypeImage dbItemTypeImage = dbImages.get(index);
        Assert.assertEquals(angelIndex, dbItemTypeImage.getAngelIndex());
        Assert.assertEquals(step, dbItemTypeImage.getStep());
        Assert.assertEquals(frame, dbItemTypeImage.getFrame());
        Assert.assertEquals(objectState, dbItemTypeImage.getType());
        Assert.assertArrayEquals(data, dbItemTypeImage.getData());
        Assert.assertEquals(contentType, dbItemTypeImage.getContentType());
    }

    @SuppressWarnings("unchecked")
    private List<DbItemTypeImage> loadAllDbItemTypeImage() {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(DbItemTypeImage.class);
        criteria.addOrder(Order.asc("type"));
        criteria.addOrder(Order.asc("angelIndex"));
        criteria.addOrder(Order.asc("step"));
        criteria.addOrder(Order.asc("frame"));
        return criteria.list();
    }

    @Test
    @DirtiesContext
    public void spriteMapMulti() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        // Buildup
        createDbItemTypeImage(dbBaseItemType, 0, 0, 0, ItemTypeSpriteMap.SyncObjectState.BUILD_UP, "hoover_bagger_0001.png");
        createDbItemTypeImage(dbBaseItemType, 0, 0, 1, ItemTypeSpriteMap.SyncObjectState.BUILD_UP, "hoover_bagger_0002.png");
        createDbItemTypeImage(dbBaseItemType, 0, 1, 0, ItemTypeSpriteMap.SyncObjectState.BUILD_UP, "hoover_bagger_0003.png");
        createDbItemTypeImage(dbBaseItemType, 0, 1, 1, ItemTypeSpriteMap.SyncObjectState.BUILD_UP, "hoover_bagger_0004.png");
        // Runtime
        createDbItemTypeImage(dbBaseItemType, 0, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, "hoover_bagger_0005.png");
        createDbItemTypeImage(dbBaseItemType, 0, 0, 1, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, "hoover_bagger_0006.png");
        createDbItemTypeImage(dbBaseItemType, 1, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, "hoover_bagger_0001.png");
        createDbItemTypeImage(dbBaseItemType, 1, 0, 1, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, "hoover_bagger_0002.png");
        // Demolition
        createDbItemTypeImage(dbBaseItemType, 0, 0, 0, ItemTypeSpriteMap.SyncObjectState.DEMOLITION, "hoover_bagger_0001.png");
        createDbItemTypeImage(dbBaseItemType, 0, 0, 1, ItemTypeSpriteMap.SyncObjectState.DEMOLITION, "hoover_bagger_0002.png");
        createDbItemTypeImage(dbBaseItemType, 0, 1, 0, ItemTypeSpriteMap.SyncObjectState.DEMOLITION, "hoover_bagger_0003.png");
        createDbItemTypeImage(dbBaseItemType, 0, 1, 1, ItemTypeSpriteMap.SyncObjectState.DEMOLITION, "hoover_bagger_0004.png");
        createDbItemTypeImage(dbBaseItemType, 1, 0, 0, ItemTypeSpriteMap.SyncObjectState.DEMOLITION, "hoover_bagger_0005.png");
        createDbItemTypeImage(dbBaseItemType, 1, 0, 1, ItemTypeSpriteMap.SyncObjectState.DEMOLITION, "hoover_bagger_0006.png");
        createDbItemTypeImage(dbBaseItemType, 1, 1, 0, ItemTypeSpriteMap.SyncObjectState.DEMOLITION, "hoover_bagger_0001.png");
        createDbItemTypeImage(dbBaseItemType, 1, 1, 1, ItemTypeSpriteMap.SyncObjectState.DEMOLITION, "hoover_bagger_0002.png");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BoundingBox boundingBox = new BoundingBox(10, new double[]{0.0, 0.1});
        ItemTypeSpriteMap itemTypeSpriteMap = new ItemTypeSpriteMap(boundingBox, 64, 64, 2, 2, 0, 2, 0, new DemolitionStepSpriteMap[]{new DemolitionStepSpriteMap(2, 0, null), new DemolitionStepSpriteMap(2, 0, null)});
        serverItemTypeService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                null,
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        serverItemTypeService.activate();
        int itemTypeId = dbBaseItemType.getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        ImageHolder imageHolder = serverItemTypeService.getItemTypeSpriteMap(itemTypeId);
        Assert.assertNotNull(imageHolder);
        BufferedImage spriteMap = ImageIO.read(new ByteArrayInputStream(imageHolder.getData()));
        // Buildup
        assertBufferedImage("/images/hoover_bagger_0001.png", spriteMap, 0);
        assertBufferedImage("/images/hoover_bagger_0002.png", spriteMap, 64);
        assertBufferedImage("/images/hoover_bagger_0003.png", spriteMap, 128);
        assertBufferedImage("/images/hoover_bagger_0004.png", spriteMap, 192);
        // Runtime
        assertBufferedImage("/images/hoover_bagger_0005.png", spriteMap, 256);
        assertBufferedImage("/images/hoover_bagger_0006.png", spriteMap, 320);
        assertBufferedImage("/images/hoover_bagger_0001.png", spriteMap, 384);
        assertBufferedImage("/images/hoover_bagger_0002.png", spriteMap, 448);
        // Demolition
        assertBufferedImage("/images/hoover_bagger_0001.png", spriteMap, 512);
        assertBufferedImage("/images/hoover_bagger_0002.png", spriteMap, 576);
        assertBufferedImage("/images/hoover_bagger_0003.png", spriteMap, 640);
        assertBufferedImage("/images/hoover_bagger_0004.png", spriteMap, 704);
        assertBufferedImage("/images/hoover_bagger_0005.png", spriteMap, 768);
        assertBufferedImage("/images/hoover_bagger_0006.png", spriteMap, 832);
        assertBufferedImage("/images/hoover_bagger_0001.png", spriteMap, 896);
        assertBufferedImage("/images/hoover_bagger_0002.png", spriteMap, 960);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void createDbItemTypeImage(DbBaseItemType dbBaseItemType, int angelIndex, int step, int frame, ItemTypeSpriteMap.SyncObjectState type, String imageName) throws IOException {
        DbItemTypeImage dbItemTypeImage = new DbItemTypeImage();
        dbItemTypeImage.setParent(dbBaseItemType);
        dbItemTypeImage.setAngelIndex(angelIndex);
        dbItemTypeImage.setStep(step);
        dbItemTypeImage.setFrame(frame);
        dbItemTypeImage.setType(type);
        dbItemTypeImage.setData(IOUtils.toByteArray(getClass().getResource("/images/" + imageName).openStream()));
        dbItemTypeImage.setContentType("png");
        getSessionFactory().getCurrentSession().save(dbItemTypeImage);
    }

    @Test
    @DirtiesContext
    public void spriteMapSingle() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        createDbItemTypeImage(dbBaseItemType, 0, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, "hoover_bagger_0001.png");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BoundingBox boundingBox = new BoundingBox(10, new double[]{0.0});
        ItemTypeSpriteMap itemTypeSpriteMap = new ItemTypeSpriteMap(boundingBox, 64, 64, 0, 0, 0, 1, 0, null);
        serverItemTypeService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                null,
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        serverItemTypeService.activate();
        int itemTypeId = dbBaseItemType.getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        ImageHolder imageHolder = serverItemTypeService.getItemTypeSpriteMap(itemTypeId);
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

        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        // Buildup
        createDbItemTypeImage(dbBaseItemType, 0, 0, 0, ItemTypeSpriteMap.SyncObjectState.BUILD_UP, "hoover_bagger_0001.png");
        createDbItemTypeImage(dbBaseItemType, 0, 0, 1, ItemTypeSpriteMap.SyncObjectState.BUILD_UP, "hoover_bagger_0002.png");
        createDbItemTypeImage(dbBaseItemType, 0, 1, 0, ItemTypeSpriteMap.SyncObjectState.BUILD_UP, "hoover_bagger_0003.png");
        createDbItemTypeImage(dbBaseItemType, 0, 1, 1, ItemTypeSpriteMap.SyncObjectState.BUILD_UP, "hoover_bagger_0004.png");
        // Runtime
        createDbItemTypeImage(dbBaseItemType, 0, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, "hoover_bagger_0005.png");
        createDbItemTypeImage(dbBaseItemType, 0, 0, 1, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, "hoover_bagger_0006.png");
        createDbItemTypeImage(dbBaseItemType, 1, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, "hoover_bagger_0001.png");
        createDbItemTypeImage(dbBaseItemType, 1, 0, 1, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, "hoover_bagger_0002.png");
        // Demolition
        createDbItemTypeImage(dbBaseItemType, 0, 0, 0, ItemTypeSpriteMap.SyncObjectState.DEMOLITION, "hoover_bagger_0001.png");
        createDbItemTypeImage(dbBaseItemType, 0, 0, 1, ItemTypeSpriteMap.SyncObjectState.DEMOLITION, "hoover_bagger_0002.png");
        createDbItemTypeImage(dbBaseItemType, 0, 1, 0, ItemTypeSpriteMap.SyncObjectState.DEMOLITION, "hoover_bagger_0003.png");
        createDbItemTypeImage(dbBaseItemType, 0, 1, 1, ItemTypeSpriteMap.SyncObjectState.DEMOLITION, "hoover_bagger_0004.png");
        createDbItemTypeImage(dbBaseItemType, 1, 0, 0, ItemTypeSpriteMap.SyncObjectState.DEMOLITION, "hoover_bagger_0005.png");
        createDbItemTypeImage(dbBaseItemType, 1, 0, 1, ItemTypeSpriteMap.SyncObjectState.DEMOLITION, "hoover_bagger_0006.png");
        createDbItemTypeImage(dbBaseItemType, 1, 1, 0, ItemTypeSpriteMap.SyncObjectState.DEMOLITION, "hoover_bagger_0001.png");
        createDbItemTypeImage(dbBaseItemType, 1, 1, 1, ItemTypeSpriteMap.SyncObjectState.DEMOLITION, "hoover_bagger_0002.png");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BoundingBox boundingBox = new BoundingBox(10, new double[]{0.0, 0.1});
        ItemTypeSpriteMap itemTypeSpriteMap = new ItemTypeSpriteMap(boundingBox, 64, 64, 2, 2, 0, 2, 0, new DemolitionStepSpriteMap[]{new DemolitionStepSpriteMap(2, 0, null), new DemolitionStepSpriteMap(2, 0, null)});
        serverItemTypeService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                null,
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        serverItemTypeService.activate();
        int itemTypeId = dbBaseItemType.getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        DbItemTypeImage resultDbItemTypeImage = serverItemTypeService.getCmsDbItemTypeImage(itemTypeId);
        Assert.assertNotNull(resultDbItemTypeImage);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(resultDbItemTypeImage.getData()));
        assertBufferedImage("/images/hoover_bagger_0005.png", image, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getCmsDbItemTypeImageSingle() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        createDbItemTypeImage(dbBaseItemType, 0, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, "hoover_bagger_0001.png");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BoundingBox boundingBox = new BoundingBox(10, new double[]{0.0});
        ItemTypeSpriteMap itemTypeSpriteMap = new ItemTypeSpriteMap(boundingBox, 64, 64, 0, 0, 0, 1, 0, null);
        serverItemTypeService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                null,
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        serverItemTypeService.activate();
        int itemTypeId = dbBaseItemType.getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbItemTypeImage resultDbItemTypeImage = serverItemTypeService.getCmsDbItemTypeImage(itemTypeId);
        Assert.assertNotNull(resultDbItemTypeImage);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(resultDbItemTypeImage.getData()));
        assertBufferedImage("/images/hoover_bagger_0001.png", image, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    public static void assertBufferedImage(String expectedFileName, BufferedImage spriteMap, int xOffset) throws IOException {
        final double MAX_DIFF = 1.0;
        BufferedImage expected = ImageIO.read(TestItemImageHandling.class.getResource(expectedFileName).openStream());
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

    @Test
    @DirtiesContext
    public void muzzleFlashPosition() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType dbBaseItemType = serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        DbWeaponType dbWeaponType = dbBaseItemType.getDbWeaponType();
        WeaponType weaponType = dbWeaponType.createWeaponType(3);
        BoundingBox boundingBox = dbBaseItemType.createBoundingBox();
        ItemTypeSpriteMap itemTypeSpriteMap = dbBaseItemType.createItemTypeSpriteMap(boundingBox);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        weaponType.setMuzzleFlashPosition(0, 0, new Index(9, 10));
        weaponType.setMuzzleFlashPosition(0, 1, new Index(11, 12));
        weaponType.setMuzzleFlashPosition(0, 2, new Index(13, 14));

        serverItemTypeService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                weaponType,
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        dbWeaponType = dbBaseItemType.getDbWeaponType();
        weaponType = dbWeaponType.createWeaponType(3);
        Assert.assertEquals(1, weaponType.getMuzzleFlashCount());
        Assert.assertEquals(new Index(9, 10), weaponType.getMuzzleFlashPosition(0, 0));
        Assert.assertEquals(new Index(11, 12), weaponType.getMuzzleFlashPosition(0, 1));
        Assert.assertEquals(new Index(13, 14), weaponType.getMuzzleFlashPosition(0, 2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        serverItemTypeService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                weaponType,
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        dbWeaponType = dbBaseItemType.getDbWeaponType();
        weaponType = dbWeaponType.createWeaponType(3);
        Assert.assertEquals(1, weaponType.getMuzzleFlashCount());
        Assert.assertEquals(new Index(9, 10), weaponType.getMuzzleFlashPosition(0, 0));
        Assert.assertEquals(new Index(11, 12), weaponType.getMuzzleFlashPosition(0, 1));
        Assert.assertEquals(new Index(13, 14), weaponType.getMuzzleFlashPosition(0, 2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        weaponType.changeMuzzleFlashCount(3);
        weaponType.setMuzzleFlashPosition(1, 0, new Index(10, 11));
        weaponType.setMuzzleFlashPosition(1, 1, new Index(10, 12));
        weaponType.setMuzzleFlashPosition(1, 2, new Index(10, 13));
        weaponType.setMuzzleFlashPosition(2, 0, new Index(20, 11));
        weaponType.setMuzzleFlashPosition(2, 1, new Index(20, 12));
        weaponType.setMuzzleFlashPosition(2, 2, new Index(20, 13));
        serverItemTypeService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                weaponType,
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        dbWeaponType = dbBaseItemType.getDbWeaponType();
        weaponType = dbWeaponType.createWeaponType(3);
        Assert.assertEquals(3, weaponType.getMuzzleFlashCount());
        Assert.assertEquals(new Index(9, 10), weaponType.getMuzzleFlashPosition(0, 0));
        Assert.assertEquals(new Index(11, 12), weaponType.getMuzzleFlashPosition(0, 1));
        Assert.assertEquals(new Index(13, 14), weaponType.getMuzzleFlashPosition(0, 2));
        Assert.assertEquals(new Index(10, 11), weaponType.getMuzzleFlashPosition(1, 0));
        Assert.assertEquals(new Index(10, 12), weaponType.getMuzzleFlashPosition(1, 1));
        Assert.assertEquals(new Index(10, 13), weaponType.getMuzzleFlashPosition(1, 2));
        Assert.assertEquals(new Index(20, 11), weaponType.getMuzzleFlashPosition(2, 0));
        Assert.assertEquals(new Index(20, 12), weaponType.getMuzzleFlashPosition(2, 1));
        Assert.assertEquals(new Index(20, 13), weaponType.getMuzzleFlashPosition(2, 2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        weaponType.changeMuzzleFlashCount(1);
        weaponType.setMuzzleFlashPosition(0, 0, new Index(10, 11));
        weaponType.setMuzzleFlashPosition(0, 1, new Index(10, 12));
        weaponType.setMuzzleFlashPosition(0, 2, new Index(10, 13));
        serverItemTypeService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                weaponType,
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        dbWeaponType = dbBaseItemType.getDbWeaponType();
        weaponType = dbWeaponType.createWeaponType(3);
        Assert.assertEquals(1, weaponType.getMuzzleFlashCount());
        Assert.assertEquals(new Index(10, 11), weaponType.getMuzzleFlashPosition(0, 0));
        Assert.assertEquals(new Index(10, 12), weaponType.getMuzzleFlashPosition(0, 1));
        Assert.assertEquals(new Index(10, 13), weaponType.getMuzzleFlashPosition(0, 2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        dbWeaponType = dbBaseItemType.getDbWeaponType();
        weaponType = dbWeaponType.createWeaponType(2);
        Assert.assertEquals(1, weaponType.getMuzzleFlashCount());
        Assert.assertEquals(new Index(10, 11), weaponType.getMuzzleFlashPosition(0, 0));
        Assert.assertEquals(new Index(10, 12), weaponType.getMuzzleFlashPosition(0, 1));
        serverItemTypeService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                weaponType,
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                new ItemClipPosition(0, new Index[0]),
                new ItemClipPosition(0, new Index[0]));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        dbWeaponType = dbBaseItemType.getDbWeaponType();
        weaponType = dbWeaponType.createWeaponType(3);
        Assert.assertEquals(1, weaponType.getMuzzleFlashCount());
        Assert.assertEquals(new Index(10, 11), weaponType.getMuzzleFlashPosition(0, 0));
        Assert.assertEquals(new Index(10, 12), weaponType.getMuzzleFlashPosition(0, 1));
        Assert.assertEquals(new Index(0, 0), weaponType.getMuzzleFlashPosition(0, 2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }


}
