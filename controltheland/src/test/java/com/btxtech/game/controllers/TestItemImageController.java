package com.btxtech.game.controllers;

import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeImageInfo;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import org.apache.wicket.util.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.Arrays;

/**
 * User: beat
 * Date: 21.03.2012
 * Time: 14:11:49
 */
public class TestItemImageController extends AbstractServiceTest {
    @Autowired
    private ItemImageController itemImageController;
    @Autowired
    private ItemService itemService;

    @Test
    public void test1() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType dbBaseItemType = (DbBaseItemType) itemService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        createDbItemTypeImage(dbBaseItemType, 0, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, "hoover_bagger_0001.png");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BoundingBox boundingBox = new BoundingBox(10, 12, new double[]{0.0});
        ItemTypeSpriteMap itemTypeSpriteMap = new ItemTypeSpriteMap(boundingBox, 64, 64, 0, 0, 0, 1, 0, 0, 0, 0);
        itemService.saveItemTypeProperties(dbBaseItemType.getId(),
                boundingBox,
                itemTypeSpriteMap,
                null,
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList(),
                Arrays.<ItemTypeImageInfo>asList());
        itemService.activate();
        int itemTypeId = dbBaseItemType.getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        itemImageController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertEquals("idsm or id must be given.", mockHttpServletResponse.getErrorMessage());
        Assert.assertEquals(400, mockHttpServletResponse.getStatus());

        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("idsm", Integer.toString(itemTypeId));
        mockHttpServletResponse = new MockHttpServletResponse();
        itemImageController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertNull(mockHttpServletResponse.getErrorMessage());
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("image/png", mockHttpServletResponse.getContentType());
        Assert.assertEquals(2533, mockHttpServletResponse.getContentAsByteArray().length);
    }

    private void createDbItemTypeImage(DbBaseItemType dbBaseItemType, int angelIndex, int step, int frame, ItemTypeSpriteMap.SyncObjectState type, String imageName) throws IOException {
        DbItemTypeImage dbItemTypeImage = new DbItemTypeImage();
        dbItemTypeImage.setParent(dbBaseItemType);
        dbItemTypeImage.setAngelIndex(angelIndex);
        dbItemTypeImage.setStep(step);
        dbItemTypeImage.setFrame(frame);
        dbItemTypeImage.setType(type);
        dbItemTypeImage.setData(IOUtils.toByteArray(getClass().getResource("/images/" + imageName).openStream()));
        dbItemTypeImage.setContentType("image/png");
        getSessionFactory().getCurrentSession().save(dbItemTypeImage);
    }

}
