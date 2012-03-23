package com.btxtech.game.controllers;

import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
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
        Assert.assertEquals(19494, mockHttpServletResponse.getContentAsByteArray().length);
    }
}
