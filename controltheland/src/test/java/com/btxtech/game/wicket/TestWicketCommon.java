package com.btxtech.game.wicket;

import com.btxtech.game.controllers.TestItemImageController;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeImageInfo;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.DbCmsImage;
import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.inventory.GlobalInventoryService;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.socialnet.facebook.FacebookAge;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.services.socialnet.facebook.FacebookUser;
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.pages.FacebookAppNickName;
import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.pages.mgmt.MgmtPage;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.protocol.http.mock.MockHttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;

/**
 * User: beat
 * Date: 13.07.13
 * Time: 09:51
 */
public class TestWicketCommon extends AbstractServiceTest {
    public static final String LSC_SCRIPT = "<script type=\"text/javascript\" >\n" +
            "/*<![CDATA[*/\n" +
            "function isNotChromeCommand(toTest) {\n" +
            "    if (toTest instanceof String || typeof toTest == 'string') {\n" +
            "        return !(toTest.substring(0, 9) == 'chrome://');\n" +
            "    } else {\n" +
            "        return true;\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "window.onerror = function (message, file, lineNumber) {\n" +
            "    if (message != 'Script error' && lineNumber != 0 && isNotChromeCommand(file)) {\n" +
            "        var errorMessage = encodeURI(\"FacebookAppNickName\\nMessage: \" + message + \"\\nFile: \" + file + \"\\nLinenumber: \" + lineNumber);\n" +
            "        var pathName = encodeURI(window.location.pathname);\n" +
            "        var img = document.createElement('img');\n" +
            "        img.src = '/spring/lsc?e=' + errorMessage + '&t=' + new Date().getTime() + '&p=' + pathName;\n" +
            "        document.body.appendChild(img);\n" +
            "    }\n" +
            "    return true;\n" +
            "};\n" +
            "/*]]>*/\n" +
            "</script>";
    public static final String HTML5_DETECT = "<script type=\"text/javascript\" >\n" +
            "/*<![CDATA[*/\n" +
            "Wicket.Event.add(window, \"load\", function(event) { \n" +
            "try {\n" +
            "    var value = '/spring/statJS?html5=';\n" +
            "    if (window.HTMLCanvasElement) {\n" +
            "        value += 'y';\n" +
            "    } else {\n" +
            "        value += 'n';\n" +
            "    }\n" +
            "    var f = document.createElement('img');\n" +
            "    f.setAttribute('src', value);\n" +
            "    f.style.position = 'absolute';\n" +
            "    f.style.top = '0';\n" +
            "    f.style.left = '0';\n" +
            "    document.body.appendChild(f);\n" +
            "} catch (e) {\n" +
            "    errorMessage = encodeURI('JSDetection exception:' + e);\n" +
            "    pathname = encodeURI(window.location.pathname);\n" +
            "    var img = document.createElement('img');\n" +
            "    img.src = '/spring/lsc?e=' + errorMessage + '&t=' + new Date().getTime() + '&p=' + pathname;\n" +
            "    document.body.appendChild(img);\n" +
            "};\n" +
            ";});\n" +
            "/*]]>*/\n" +
            "</script>";

    @Autowired
    private CmsService cmsService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private GlobalInventoryService globalInventoryService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;

    @Test
    @DirtiesContext
    public void testMountings() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Setup admin user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Admin", "admin");
        User user = userService.getUser();
        user.setRoles(Collections.singleton(SecurityRoles.ROLE_ADMINISTRATOR));
        userService.save(user);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        pageCrud.updateDbChild(dbPage);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().executeUrl("game_cms");
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().executeUrl("game_run");
        getWicketTester().assertRenderedPage(Game.class);
        try {
            getWicketTester().executeUrl("game_cms_facebook_app");
            Assert.fail("WicketRuntimeException expected");
        } catch (WicketRuntimeException wicketRuntimeException) {
            Assert.assertEquals("Empty signed_request received", (((InvocationTargetException) wicketRuntimeException.getCause()).getTargetException()).getMessage());
        }
        try {
            getWicketTester().executeUrl("game_cms_facebook_auto_login");
            Assert.fail("WicketRuntimeException expected");
        } catch (WicketRuntimeException wicketRuntimeException) {
            Assert.assertEquals("Empty signed_request received", (((InvocationTargetException) wicketRuntimeException.getCause()).getTargetException()).getMessage());
        }
        // Login as administrator
        loginUser("Admin", "admin");
        try {
            // First call crashes. Wickets need to set up wicket-session first
            getWicketTester().startPage(MgmtPage.class);
        } catch (Exception e) {
            // Ignore
        }
        ((WicketAuthenticatedWebSession) AuthenticatedWebSession.get()).setSignIn(true);
        getWicketTester().executeUrl("game_mgmt");
        getWicketTester().assertRenderedPage(MgmtPage.class);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testLscErrorHandler() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        FacebookAge facebookAge = new FacebookAge(20);
        FacebookUser facebookUser = new FacebookUser("", "", facebookAge);
        FacebookSignedRequest facebookSignedRequest = new FacebookSignedRequest("", 0, facebookUser, "", "1234");
        facebookSignedRequest.setEmail("muu@ggg.com");
        getWicketTester().startPage(new FacebookAppNickName(facebookSignedRequest));
        assertStringInHeader(LSC_SCRIPT);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testHtml5Detection() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        FacebookAge facebookAge = new FacebookAge(20);
        FacebookUser facebookUser = new FacebookUser("", "", facebookAge);
        FacebookSignedRequest facebookSignedRequest = new FacebookSignedRequest("", 0, facebookUser, "", "1234");
        facebookSignedRequest.setEmail("muu@ggg.com");
        getWicketTester().startPage(new FacebookAppNickName(facebookSignedRequest));
        assertStringInHeader(HTML5_DETECT);
        getWicketTester().startPage(new FacebookAppNickName(facebookSignedRequest));
        assertStringInHeader(HTML5_DETECT);
        userTrackingService.onJavaScriptDetected(true);
        getWicketTester().startPage(new FacebookAppNickName(facebookSignedRequest));
        assertStringNotInHeader(HTML5_DETECT);
        getWicketTester().assertRenderedPage(FacebookAppNickName.class);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testHtml5DetectionNoHtml5() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        FacebookAge facebookAge = new FacebookAge(20);
        FacebookUser facebookUser = new FacebookUser("", "", facebookAge);
        FacebookSignedRequest facebookSignedRequest = new FacebookSignedRequest("", 0, facebookUser, "", "1234");
        facebookSignedRequest.setEmail("muu@ggg.com");
        getWicketTester().startPage(new FacebookAppNickName(facebookSignedRequest));
        assertStringInHeader(HTML5_DETECT);
        getWicketTester().startPage(new FacebookAppNickName(facebookSignedRequest));
        assertStringInHeader(HTML5_DETECT);
        userTrackingService.onJavaScriptDetected(false);
        getWicketTester().startPage(new FacebookAppNickName(facebookSignedRequest));
        assertStringNotInHeader(HTML5_DETECT);
        getWicketTester().assertRenderedPage(FacebookAppNickName.class);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testInventoryItemImages() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryItem dbInventoryItem = globalInventoryService.getItemCrud().createDbChild();
        dbInventoryItem.setImageData(new byte[]{0, 1, 2, 3, 4});
        dbInventoryItem.setImageContentType("img/bmp");
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem);
        Assert.assertEquals(1, (int) dbInventoryItem.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test generating
        Assert.assertEquals("/inventoryImage/type/item/id/1", ImageHandler.getInventoryItemUrl(1));
        // Normal Parameter
/*        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().executeUrl("inventoryImage?type=item&id=1");
        MockHttpServletResponse mockHttpServletResponse = getWicketTester().getLastResponse();
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("img/bmp", mockHttpServletResponse.getContentType());
        Assert.assertEquals(5, mockHttpServletResponse.getBufferSize());
        Assert.assertArrayEquals(new byte[]{0, 1, 2, 3, 4}, mockHttpServletResponse.getBinaryContent());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Normal Parameter with session
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().executeUrl("inventoryImage?type=item&id=1;jsessionid=4F3860F226027C271226CB572332C2DD");
        mockHttpServletResponse = getWicketTester().getLastResponse();
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("img/bmp", mockHttpServletResponse.getContentType());
        Assert.assertEquals(5, mockHttpServletResponse.getBufferSize());
        Assert.assertArrayEquals(new byte[]{0, 1, 2, 3, 4}, mockHttpServletResponse.getBinaryContent());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();*/
        // Pretty url
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().executeUrl("inventoryImage/type/item/id/1");
        MockHttpServletResponse mockHttpServletResponse = getWicketTester().getLastResponse();
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("img/bmp", mockHttpServletResponse.getContentType());
        Assert.assertEquals(5, mockHttpServletResponse.getBufferSize());
        Assert.assertArrayEquals(new byte[]{0, 1, 2, 3, 4}, mockHttpServletResponse.getBinaryContent());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Pretty url with session
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().executeUrl("inventoryImage/type/item/id/1;jsessionid=4F3860F226027C271226CB572332C2DD");
        mockHttpServletResponse = getWicketTester().getLastResponse();
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("img/bmp", mockHttpServletResponse.getContentType());
        Assert.assertEquals(5, mockHttpServletResponse.getBufferSize());
        Assert.assertArrayEquals(new byte[]{0, 1, 2, 3, 4}, mockHttpServletResponse.getBinaryContent());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testInventoryArtifactImages() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryArtifact dbInventoryArtifact = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact.setImageData(new byte[]{10, 11, 12, 13, 14, 15});
        dbInventoryArtifact.setImageContentType("img/gegel");
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact);
        Assert.assertEquals(1, (int) dbInventoryArtifact.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test generating
        Assert.assertEquals("/inventoryImage/type/art/id/1", ImageHandler.getInventoryArtifactUrl(1));
        // Normal Parameter
/*        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals("/inventoryImage?type=art&id=1", ImageHandler.getInventoryArtifactUrl(1));
        getWicketTester().executeUrl("inventoryImage?type=art&id=1");
        MockHttpServletResponse mockHttpServletResponse = getWicketTester().getLastResponse();
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("img/gegel", mockHttpServletResponse.getContentType());
        Assert.assertEquals(6, mockHttpServletResponse.getBufferSize());
        Assert.assertArrayEquals(new byte[]{10, 11, 12, 13, 14, 15}, mockHttpServletResponse.getBinaryContent());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Normal Parameter with session
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals("/inventoryImage?type=art&id=1", ImageHandler.getInventoryArtifactUrl(1));
        getWicketTester().executeUrl("inventoryImage?type=art&id=1;jsessionid=4F3860F226027C271226CB572332C2DD");
        mockHttpServletResponse = getWicketTester().getLastResponse();
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("img/gegel", mockHttpServletResponse.getContentType());
        Assert.assertEquals(6, mockHttpServletResponse.getBufferSize());
        Assert.assertArrayEquals(new byte[]{10, 11, 12, 13, 14, 15}, mockHttpServletResponse.getBinaryContent());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession(); */
        // Pretty url
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().executeUrl("inventoryImage/type/art/id/1");
        MockHttpServletResponse mockHttpServletResponse = getWicketTester().getLastResponse();
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("img/gegel", mockHttpServletResponse.getContentType());
        Assert.assertEquals(6, mockHttpServletResponse.getBufferSize());
        Assert.assertArrayEquals(new byte[]{10, 11, 12, 13, 14, 15}, mockHttpServletResponse.getBinaryContent());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Pretty url with session
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().executeUrl("inventoryImage/type/art/id/1;jsessionid=4F3860F226027C271226CB572332C2DD");
        mockHttpServletResponse = getWicketTester().getLastResponse();
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("img/gegel", mockHttpServletResponse.getContentType());
        Assert.assertEquals(6, mockHttpServletResponse.getBufferSize());
        Assert.assertArrayEquals(new byte[]{10, 11, 12, 13, 14, 15}, mockHttpServletResponse.getBinaryContent());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testItemTypeImages() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        TestItemImageController.createDbItemTypeImage(getSessionFactory(), dbBaseItemType, 0, 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME, "hoover_bagger_0001.png");
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
                Arrays.<ItemTypeImageInfo>asList());
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Normal Parameter
/*        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().executeUrl("cmsitemimg?id=1");
        MockHttpServletResponse mockHttpServletResponse = getWicketTester().getLastResponse();
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("image/png", mockHttpServletResponse.getContentType());
        Assert.assertEquals(2799, mockHttpServletResponse.getBufferSize());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Normal Parameter with session
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().executeUrl("cmsitemimg?id=1;jsessionid=4F3860F226027C271226CB572332C2DD");
        mockHttpServletResponse = getWicketTester().getLastResponse();
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("image/png", mockHttpServletResponse.getContentType());
        Assert.assertEquals(2799, mockHttpServletResponse.getBufferSize());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession(); */
        // Pretty url
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().executeUrl("cmsitemimg/id/1");
        MockHttpServletResponse mockHttpServletResponse = getWicketTester().getLastResponse();
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("image/png", mockHttpServletResponse.getContentType());
        Assert.assertEquals(2799, mockHttpServletResponse.getBufferSize());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Pretty url with session
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().executeUrl("cmsitemimg/id/1;jsessionid=4F3860F226027C271226CB572332C2DD");
        mockHttpServletResponse = getWicketTester().getLastResponse();
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("image/png", mockHttpServletResponse.getContentType());
        Assert.assertEquals(2799, mockHttpServletResponse.getBufferSize());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

    @Test
    @DirtiesContext
    public void testCmsImages() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbCmsImage dbCmsImage = cmsService.getImageCrudRootServiceHelper().createDbChild();
        dbCmsImage.setData(new byte[]{10, 12, 13, 14, 15});
        dbCmsImage.setContentType("ich/gegel");
        cmsService.getImageCrudRootServiceHelper().updateDbChild(dbCmsImage);
        cmsService.activateCms();
        Assert.assertEquals(1, (int)dbCmsImage.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
 /*       // Normal Parameter
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().executeUrl("cmsimg?id=1");
        MockHttpServletResponse mockHttpServletResponse = getWicketTester().getLastResponse();
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("ich/gegel", mockHttpServletResponse.getContentType());
        Assert.assertEquals(5, mockHttpServletResponse.getBufferSize());
        Assert.assertArrayEquals(new byte[]{10, 12, 13, 14, 15}, mockHttpServletResponse.getBinaryContent());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Normal Parameter with session
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().executeUrl("cmsimg?id=1");
        mockHttpServletResponse = getWicketTester().getLastResponse();
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("ich/gegel", mockHttpServletResponse.getContentType());
        Assert.assertEquals(5, mockHttpServletResponse.getBufferSize());
        Assert.assertArrayEquals(new byte[]{10, 12, 13, 14, 15}, mockHttpServletResponse.getBinaryContent());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();  */
        // Pretty url
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().executeUrl("cmsimg/id/1");
        MockHttpServletResponse mockHttpServletResponse = getWicketTester().getLastResponse();
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("ich/gegel", mockHttpServletResponse.getContentType());
        Assert.assertEquals(5, mockHttpServletResponse.getBufferSize());
        Assert.assertArrayEquals(new byte[]{10, 12, 13, 14, 15}, mockHttpServletResponse.getBinaryContent());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Pretty url with session
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().executeUrl("cmsimg/id/1;jsessionid=4F3860F226027C271226CB572332C2DD");
        mockHttpServletResponse = getWicketTester().getLastResponse();
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("ich/gegel", mockHttpServletResponse.getContentType());
        Assert.assertEquals(5, mockHttpServletResponse.getBufferSize());
        Assert.assertArrayEquals(new byte[]{10, 12, 13, 14, 15}, mockHttpServletResponse.getBinaryContent());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }
}