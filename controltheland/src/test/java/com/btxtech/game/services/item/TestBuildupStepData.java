package com.btxtech.game.services.item;

import com.btxtech.game.controllers.ItemImageController;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BuildupStep;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBuildupStep;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.wicket.pages.mgmt.Html5ImagesUploadConverter;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 09.01.2012
 * Time: 23:36:24
 */
public class TestBuildupStepData extends AbstractServiceTest {
    private static final String INLINE_IMAGE_1 = "data:image/png;base64,xxxx";
    private static final String MIME_IMAGE_1 = "image/png";
    private static final byte[] IMAGE_DATA_1 = new byte[]{-57, 28, 113};
    private static final String INLINE_IMAGE_2 = "data:image/loeli;base64,iVBORw0KGgoAAAANS";
    private static final String MIME_IMAGE_2 = "image/loeli";
    private static final byte[] IMAGE_DATA_2 = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 0};
    private static final String INLINE_IMAGE_3 = "data:image/jpg;base64,iVBOdseedc";
    private static final String MIME_IMAGE_3 = "image/jpg";
    private static final byte[] IMAGE_DATA_3 = new byte[]{-119, 80, 78, 118, -57, -98, 0, 0};
    private static final String INLINE_IMAGE_4 = "data:image/gif;base64,wrefdfs";
    private static final String MIME_IMAGE_4 = "image/gif";
    private static final byte[] IMAGE_DATA_4 = new byte[]{-62, -73, -97, 0, 0, 0};
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemImageController itemImageController;

    @Test
    public void convertInlineImage1() {
        Html5ImagesUploadConverter.Package aPackage = Html5ImagesUploadConverter.convertInlineImage(INLINE_IMAGE_1);
        Assert.assertEquals(MIME_IMAGE_1, aPackage.getMime());
        Assert.assertEquals("xxxx", aPackage.getBase64Data());
        byte[] array = aPackage.convertBase64ToBytes();
        Assert.assertArrayEquals(IMAGE_DATA_1, array);
    }

    @Test
    public void convertInlineImage2() {
        Html5ImagesUploadConverter.Package aPackage = Html5ImagesUploadConverter.convertInlineImage(INLINE_IMAGE_2);
        Assert.assertEquals(MIME_IMAGE_2, aPackage.getMime());
        Assert.assertEquals("iVBORw0KGgoAAAANS", aPackage.getBase64Data());
        byte[] array = aPackage.convertBase64ToBytes();
        Assert.assertArrayEquals(IMAGE_DATA_2, array);
    }

    @Test
    public void saveBuildupStepData() throws Exception {
        CrudRootServiceHelper<DbItemType> itemCrud = itemService.getDbItemTypeCrud();

        // Create BaseItemType
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType dbBaseItemType = (DbBaseItemType) itemCrud.createDbChild(DbBaseItemType.class);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activated
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        itemService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify Activated
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Collection<ItemType> itemTypes = itemService.getItemTypes();
        Assert.assertEquals(1, itemTypes.size());
        List<BuildupStep> activatedBuildupStep = ((BaseItemType) CommonJava.getFirst(itemTypes)).getBuildupStep();
        Assert.assertNull(activatedBuildupStep);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Create first BuildupStep
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BuildupStep> buildupSteps = new ArrayList<BuildupStep>();
        BuildupStep buildupStep = new BuildupStep(INLINE_IMAGE_1);
        buildupStep.setFrom(0);
        buildupStep.setToExclusive(1.0);
        buildupSteps.add(buildupStep);
        itemService.saveBuildupStepData(dbBaseItemType.getId(), buildupSteps);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = (DbBaseItemType) itemCrud.readDbChild(dbBaseItemType.getId());
        Collection<DbBuildupStep> dbBuildupSteps = dbBaseItemType.getBuildupStepCrud().readDbChildren();
        Assert.assertEquals(1, dbBuildupSteps.size());
        DbBuildupStep dbBuildupStep1 = CommonJava.getFirst(dbBuildupSteps);
        Assert.assertEquals(0, dbBuildupStep1.getFrom(), 0.0001);
        Assert.assertEquals(1.0, dbBuildupStep1.getToExclusive(), 0.0001);
        Assert.assertEquals(MIME_IMAGE_1, dbBuildupStep1.getContentType());
        Assert.assertArrayEquals(IMAGE_DATA_1, dbBuildupStep1.getData());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertActivationAndImageController();

        // Create add second BuildupStep (first is untouched)
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        buildupSteps = new ArrayList<BuildupStep>();
        buildupStep = new BuildupStep(INLINE_IMAGE_2);
        buildupStep.setFrom(0);
        buildupStep.setToExclusive(0.5);
        buildupSteps.add(buildupStep);
        buildupSteps.add(new BuildupStep(dbBuildupStep1.getId(), 0.5, 1.0));
        itemService.saveBuildupStepData(dbBaseItemType.getId(), buildupSteps);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = (DbBaseItemType) itemCrud.readDbChild(dbBaseItemType.getId());
        dbBuildupSteps = dbBaseItemType.getBuildupStepCrud().readDbChildren();
        Assert.assertEquals(2, dbBuildupSteps.size());
        dbBuildupStep1 = dbBaseItemType.getBuildupStepCrud().readDbChild(dbBuildupStep1.getId());
        Assert.assertEquals(0.5, dbBuildupStep1.getFrom(), 0.0001);
        Assert.assertEquals(1.0, dbBuildupStep1.getToExclusive(), 0.0001);
        Assert.assertEquals(MIME_IMAGE_1, dbBuildupStep1.getContentType());
        Assert.assertArrayEquals(IMAGE_DATA_1, dbBuildupStep1.getData());
        dbBuildupSteps.remove(dbBuildupStep1);
        DbBuildupStep dbBuildupStep2 = CommonJava.getFirst(dbBuildupSteps);
        Assert.assertEquals(0.0, dbBuildupStep2.getFrom(), 0.0001);
        Assert.assertEquals(0.5, dbBuildupStep2.getToExclusive(), 0.0001);
        Assert.assertEquals(MIME_IMAGE_2, dbBuildupStep2.getContentType());
        Assert.assertArrayEquals(IMAGE_DATA_2, dbBuildupStep2.getData());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Create two new BuildupStep (override old ones)
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        buildupSteps = new ArrayList<BuildupStep>();
        buildupStep = new BuildupStep(INLINE_IMAGE_3);
        buildupStep.setFrom(0);
        buildupStep.setToExclusive(0.3);
        buildupSteps.add(buildupStep);
        buildupStep = new BuildupStep(INLINE_IMAGE_4);
        buildupStep.setFrom(0.3);
        buildupStep.setToExclusive(1.0);
        buildupSteps.add(buildupStep);
        itemService.saveBuildupStepData(dbBaseItemType.getId(), buildupSteps);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = (DbBaseItemType) itemCrud.readDbChild(dbBaseItemType.getId());
        dbBuildupSteps = dbBaseItemType.getBuildupStepCrud().readDbChildren();
        Assert.assertEquals(2, dbBuildupSteps.size());
        dbBuildupStep1 = getDbBuildupStep(dbBuildupSteps, 0, 0.3);
        Assert.assertEquals(MIME_IMAGE_3, dbBuildupStep1.getContentType());
        Assert.assertArrayEquals(IMAGE_DATA_3, dbBuildupStep1.getData());
        dbBuildupStep2 = getDbBuildupStep(dbBuildupSteps, 0.3, 1.0);
        Assert.assertEquals(MIME_IMAGE_4, dbBuildupStep2.getContentType());
        Assert.assertArrayEquals(IMAGE_DATA_4, dbBuildupStep2.getData());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // No new BuildupStep (user just hits the save button without manipulation the data)
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        buildupSteps = new ArrayList<BuildupStep>();
        buildupSteps.add(dbBuildupStep2.createBuildupStep());
        buildupSteps.add(dbBuildupStep1.createBuildupStep());
        itemService.saveBuildupStepData(dbBaseItemType.getId(), buildupSteps);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = (DbBaseItemType) itemCrud.readDbChild(dbBaseItemType.getId());
        dbBuildupSteps = dbBaseItemType.getBuildupStepCrud().readDbChildren();
        Assert.assertEquals(2, dbBuildupSteps.size());
        dbBuildupStep1 = getDbBuildupStep(dbBuildupSteps, 0, 0.3);
        Assert.assertEquals(MIME_IMAGE_3, dbBuildupStep1.getContentType());
        Assert.assertArrayEquals(IMAGE_DATA_3, dbBuildupStep1.getData());
        dbBuildupStep2 = getDbBuildupStep(dbBuildupSteps, 0.3, 1.0);
        Assert.assertEquals(MIME_IMAGE_4, dbBuildupStep2.getContentType());
        Assert.assertArrayEquals(IMAGE_DATA_4, dbBuildupStep2.getData());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Change one buildupStep
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        buildupSteps = new ArrayList<BuildupStep>();
        buildupSteps.add(dbBuildupStep1.createBuildupStep());
        buildupStep = new BuildupStep(INLINE_IMAGE_1);
        buildupStep.setFrom(0.3);
        buildupStep.setToExclusive(1.0);
        buildupSteps.add(buildupStep);
        itemService.saveBuildupStepData(dbBaseItemType.getId(), buildupSteps);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = (DbBaseItemType) itemCrud.readDbChild(dbBaseItemType.getId());
        dbBuildupSteps = dbBaseItemType.getBuildupStepCrud().readDbChildren();
        Assert.assertEquals(2, dbBuildupSteps.size());
        dbBuildupStep1 = getDbBuildupStep(dbBuildupSteps, 0, 0.3);
        Assert.assertEquals(MIME_IMAGE_3, dbBuildupStep1.getContentType());
        Assert.assertArrayEquals(IMAGE_DATA_3, dbBuildupStep1.getData());
        dbBuildupStep2 = getDbBuildupStep(dbBuildupSteps, 0.3, 1.0);
        Assert.assertEquals(MIME_IMAGE_1, dbBuildupStep2.getContentType());
        Assert.assertArrayEquals(IMAGE_DATA_1, dbBuildupStep2.getData());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Deleting all buildupStep
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        buildupSteps = new ArrayList<BuildupStep>();
        itemService.saveBuildupStepData(dbBaseItemType.getId(), buildupSteps);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = (DbBaseItemType) itemCrud.readDbChild(dbBaseItemType.getId());
        dbBuildupSteps = dbBaseItemType.getBuildupStepCrud().readDbChildren();
        Assert.assertEquals(0, dbBuildupSteps.size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void assertActivationAndImageController() throws Exception {
        // Activated
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        itemService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify Activated
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Collection<ItemType> itemTypes = itemService.getItemTypes();
        Assert.assertEquals(1, itemTypes.size());
        BaseItemType activatedBaseItemType = (BaseItemType) CommonJava.getFirst(itemTypes);
        List<BuildupStep> activatedBuildupStep = activatedBaseItemType.getBuildupStep();
        Assert.assertEquals(1, activatedBuildupStep.size());
        Assert.assertEquals(0, activatedBuildupStep.get(0).getFrom(), 0.0001);
        Assert.assertEquals(1.0, activatedBuildupStep.get(0).getToExclusive(), 0.0001);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify image controller
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addParameter(Constants.TYPE, Constants.TYPE_BUILDUP_STEP);
        mockHttpServletRequest.addParameter(Constants.ITEM_TYPE_ID, Integer.toString(activatedBaseItemType.getId()));
        mockHttpServletRequest.addParameter(Constants.ITEM_IMAGE_BUILDUP_STEP, Integer.toString(activatedBuildupStep.get(0).getImageId()));
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        itemImageController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertEquals(MIME_IMAGE_1, mockHttpServletResponse.getContentType());
        Assert.assertArrayEquals(IMAGE_DATA_1, mockHttpServletResponse.getContentAsByteArray());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private DbBuildupStep getDbBuildupStep(Collection<DbBuildupStep> dbBuildupSteps, double from, double toExclusive) {
        DbBuildupStep result = null;
        for (DbBuildupStep dbBuildupStep : dbBuildupSteps) {
            if (MathHelper.compareWithPrecision(dbBuildupStep.getFrom(), from) && MathHelper.compareWithPrecision(dbBuildupStep.getToExclusive(), toExclusive)) {
                if (result == null) {
                    result = dbBuildupStep;
                } else {
                    throw new IllegalArgumentException("More then one DbBuildupStep found with from: " + from + " toExclusive: " + toExclusive);
                }
            }
        }
        if (result == null) {
            throw new IllegalArgumentException("No DbBuildupStep found with from: " + from + " toExclusive: " + toExclusive);
        }
        return result;
    }
}
