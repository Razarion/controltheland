package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.ContentService;
import com.btxtech.game.services.cms.DbCmsImage;
import com.btxtech.game.services.cms.content.DbBlogEntry;
import com.btxtech.game.services.cms.content.DbWikiSection;
import com.btxtech.game.services.cms.impl.CmsServiceImpl;
import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.cms.layout.DbContentBook;
import com.btxtech.game.services.cms.layout.DbContentContainer;
import com.btxtech.game.services.cms.layout.DbContentCreateEdit;
import com.btxtech.game.services.cms.layout.DbContentDetailLink;
import com.btxtech.game.services.cms.layout.DbContentDynamicHtml;
import com.btxtech.game.services.cms.layout.DbContentGameLink;
import com.btxtech.game.services.cms.layout.DbContentList;
import com.btxtech.game.services.cms.layout.DbContentPageLink;
import com.btxtech.game.services.cms.layout.DbContentPlugin;
import com.btxtech.game.services.cms.layout.DbContentRow;
import com.btxtech.game.services.cms.layout.DbContentStaticHtml;
import com.btxtech.game.services.cms.layout.DbExpressionProperty;
import com.btxtech.game.services.cms.page.DbMenu;
import com.btxtech.game.services.cms.page.DbMenuItem;
import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.cms.page.DbPageStyle;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.DateUtil;
import com.btxtech.game.services.forum.DbCategory;
import com.btxtech.game.services.forum.DbForumThread;
import com.btxtech.game.services.forum.DbPost;
import com.btxtech.game.services.forum.DbSubForum;
import com.btxtech.game.services.forum.ForumService;
import com.btxtech.game.services.forum.TestForum;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.user.AlreadyLoggedInException;
import com.btxtech.game.services.user.DbContentAccessControl;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.pages.cms.content.plugin.PluginEnum;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import com.btxtech.game.wicket.uiservices.cms.SecurityCmsUiService;
import com.btxtech.game.wicket.uiservices.cms.impl.CmsUiServiceImpl;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.FormTester;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * User: beat
 * Date: 04.06.2011
 * Time: 12:37:56
 */
public class TestCmsService extends AbstractServiceTest {
    @Autowired
    private CmsService cmsService;
    @Autowired
    private CmsUiService cmsUiService;
    @Autowired
    private ContentService contentService;
    @Autowired
    private UserService userService;
    @Autowired
    private ForumService forumService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;

    @Test
    @DirtiesContext
    public void testImages() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbCmsImage> crud = cmsService.getImageCrudRootServiceHelper();
        DbCmsImage dbCmsImage1 = crud.createDbChild();
        dbCmsImage1.setData(new byte[50000]);
        dbCmsImage1.setContentType("image");
        crud.updateDbChild(dbCmsImage1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Collection<DbCmsImage> collection = crud.readDbChildren();
        Assert.assertEquals(1, collection.size());
        Assert.assertEquals("image", collection.iterator().next().getContentType());
        Assert.assertEquals(50000, collection.iterator().next().getData().length);
        int id1 = collection.iterator().next().getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbCmsImage cachedImage = cmsService.getDbCmsImage(id1);
        Assert.assertEquals("image", cachedImage.getContentType());
        Assert.assertEquals(50000, cachedImage.getData().length);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbCmsImage dbCmsImage2 = crud.createDbChild();
        dbCmsImage2.setData(new byte[40000]);
        dbCmsImage2.setContentType("image2");
        crud.updateDbChild(dbCmsImage1);
        int id2 = dbCmsImage2.getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cachedImage = cmsService.getDbCmsImage(id1);
        Assert.assertEquals("image", cachedImage.getContentType());
        Assert.assertEquals(50000, cachedImage.getData().length);
        cachedImage = cmsService.getDbCmsImage(id2);
        Assert.assertEquals("image2", cachedImage.getContentType());
        Assert.assertEquals(40000, cachedImage.getData().length);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testPages() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage1.setName("Home");
        DbPage dbPage2 = pageCrud.createDbChild();
        dbPage2.setName("Market");
        DbPage dbPage3 = pageCrud.createDbChild();
        dbPage3.setName("Rank");

        CrudRootServiceHelper<DbMenu> menuCrud = cmsService.getMenuCrudRootServiceHelper();
        DbMenu dbMenu = menuCrud.createDbChild();
        dbMenu.setName("MainMenu");
        DbMenuItem dbMenuItem1 = dbMenu.getMenuItemCrudChildServiceHelper().createDbChild();
        dbMenuItem1.setName("Home Menu");
        dbMenuItem1.setPage(dbPage1);
        DbMenuItem dbMenuItem2 = dbMenu.getMenuItemCrudChildServiceHelper().createDbChild();
        dbMenuItem2.setName("Market Menu");
        dbMenuItem2.setPage(dbPage2);
        DbMenuItem dbMenuItem3 = dbMenu.getMenuItemCrudChildServiceHelper().createDbChild();
        dbMenuItem3.setName("Rank Menu");
        dbMenuItem3.setPage(dbPage3);
        menuCrud.updateDbChild(dbMenu);

        CrudRootServiceHelper<DbPageStyle> styleCrud = cmsService.getPageStyleCrudRootServiceHelper();
        DbPageStyle dbPageStyle = styleCrud.createDbChild();
        dbPageStyle.setCss("CSS STRING");
        dbPageStyle.setName("Main Style");
        styleCrud.updateDbChild(dbPageStyle);

        dbPage1.setMenu(dbMenu);
        dbPage1.setStyle(dbPageStyle);
        dbPage2.setMenu(dbMenu);
        dbPage2.setStyle(dbPageStyle);
        dbPage3.setMenu(dbMenu);
        dbPage3.setStyle(dbPageStyle);
        pageCrud.updateDbChild(dbPage1);
        pageCrud.updateDbChild(dbPage2);
        pageCrud.updateDbChild(dbPage3);

        int id2 = dbPage2.getId();
        int id3 = dbPage3.getId();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPage cachePageHome = cmsService.getPredefinedDbPage(CmsUtil.CmsPredefinedPage.HOME);
        Assert.assertEquals("Home", cachePageHome.getName());
        DbMenu cacheMenu = cachePageHome.getMenu();
        Assert.assertEquals("MainMenu", cacheMenu.getName());
        List<DbMenuItem> cachedMenuItems = cacheMenu.getMenuItemCrudChildServiceHelper().readDbChildren();
        Assert.assertEquals("Home Menu", cachedMenuItems.get(0).getName());
        Assert.assertEquals("Home", cachedMenuItems.get(0).getPage().getName());
        Assert.assertEquals("Market Menu", cachedMenuItems.get(1).getName());
        Assert.assertEquals("Market", cachedMenuItems.get(1).getPage().getName());
        Assert.assertEquals("Rank Menu", cachedMenuItems.get(2).getName());
        Assert.assertEquals("Rank", cachedMenuItems.get(2).getPage().getName());
        Assert.assertEquals("Main Style", cachePageHome.getStyle().getName());
        Assert.assertEquals("CSS STRING", cachePageHome.getStyle().getCss());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPage cachePage = cmsService.getPage(id2);
        Assert.assertEquals("Market", cachePage.getName());
        cacheMenu = cachePage.getMenu();
        Assert.assertEquals("MainMenu", cacheMenu.getName());
        Assert.assertEquals("Main Style", cachePage.getStyle().getName());
        Assert.assertEquals("CSS STRING", cachePage.getStyle().getCss());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbMenu = menuCrud.readDbChild(dbMenu.getId());
        Collections.swap(dbMenu.getMenuItemCrudChildServiceHelper().readDbChildren(), 0, 2);
        menuCrud.updateDbChild(dbMenu);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cachePage = cmsService.getPage(id3);
        Assert.assertEquals("Rank", cachePage.getName());
        cacheMenu = cachePage.getMenu();
        Assert.assertEquals("MainMenu", cacheMenu.getName());
        cachedMenuItems = cacheMenu.getMenuItemCrudChildServiceHelper().readDbChildren();
        Assert.assertEquals("Rank Menu", cachedMenuItems.get(0).getName());
        Assert.assertEquals("Rank", cachedMenuItems.get(0).getPage().getName());
        Assert.assertEquals("Market Menu", cachedMenuItems.get(1).getName());
        Assert.assertEquals("Market", cachedMenuItems.get(1).getPage().getName());
        Assert.assertEquals("Home Menu", cachedMenuItems.get(2).getName());
        Assert.assertEquals("Home", cachedMenuItems.get(2).getPage().getName());
        Assert.assertEquals("Main Style", cachePage.getStyle().getName());
        Assert.assertEquals("CSS STRING", cachePage.getStyle().getCss());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        ((CmsServiceImpl) cmsService).init();
    }

    @Test
    @DirtiesContext
    public void testSubMenu() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage1.setName("Home");
        DbPage dbPage2 = pageCrud.createDbChild();
        dbPage2.setName("Market");
        DbPage dbPage3 = pageCrud.createDbChild();
        dbPage3.setName("Rank");
        DbPage dbSubPage31 = pageCrud.createDbChild();
        dbSubPage31.setName("SubRank1");
        DbPage dbSubPage32 = pageCrud.createDbChild();
        dbSubPage32.setName("SubRank2");
        DbPage dbSubPage33 = pageCrud.createDbChild();
        dbSubPage33.setName("SubRank3");
        DbPage dbPage4 = pageCrud.createDbChild();
        dbPage4.setName("Page4");
        DbPage dbPage5 = pageCrud.createDbChild();
        dbPage5.setName("Page5");
        DbPage dbSubPage51 = pageCrud.createDbChild();
        dbSubPage51.setName("SubPage51");
        DbPage dbSubPage52 = pageCrud.createDbChild();
        dbSubPage52.setName("SubPage52");


        CrudRootServiceHelper<DbMenu> menuCrud = cmsService.getMenuCrudRootServiceHelper();
        DbMenu dbMenu = menuCrud.createDbChild();
        dbMenu.setName("MainMenu");
        createMenuItem(dbPage1, dbMenu, "Home Menu");
        createMenuItem(dbPage2, dbMenu, "Market Menu");
        DbMenuItem dbMenuItem3 = createMenuItem(dbPage3, dbMenu, "Rank Menu");
        createMenuItem(dbPage4, dbMenu, "Page4");
        DbMenuItem dbMenuItem5 = createMenuItem(dbPage5, dbMenu, "Page5");


        DbMenu dbSubMenu3 = new DbMenu();
        dbSubMenu3.init(userService);
        dbMenuItem3.setSubMenu(dbSubMenu3);
        createMenuItem(dbSubPage31, dbSubMenu3, "SubMenu1");
        createMenuItem(dbSubPage32, dbSubMenu3, "SubMenu2");
        createMenuItem(dbSubPage33, dbSubMenu3, "SubMenu3");

        DbMenu dbSubMenu5 = new DbMenu();
        dbSubMenu5.init(userService);
        dbMenuItem5.setSubMenu(dbSubMenu5);
        createMenuItem(dbSubPage51, dbSubMenu5, "SubMenu51");
        createMenuItem(dbSubPage51, dbSubMenu5, "SubMenu52");

        menuCrud.updateDbChild(dbMenu);
        pageCrud.updateDbChild(dbPage1);
        pageCrud.updateDbChild(dbPage2);
        pageCrud.updateDbChild(dbPage3);
        pageCrud.updateDbChild(dbSubPage31);
        pageCrud.updateDbChild(dbSubPage32);
        pageCrud.updateDbChild(dbSubPage33);
        pageCrud.updateDbChild(dbSubPage51);
        pageCrud.updateDbChild(dbSubPage52);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertLabel("menu:menuTable:1:menuLink:menuLinkName", "Home Menu");
        getWicketTester().assertLabel("menu:menuTable:2:menuLink:menuLinkName", "Market Menu");
        getWicketTester().assertLabel("menu:menuTable:3:menuLink:menuLinkName", "Rank Menu");
        getWicketTester().assertLabel("menu:menuTable:4:menuLink:menuLinkName", "Page4");
        getWicketTester().assertLabel("menu:menuTable:5:menuLink:menuLinkName", "Page5");
        // Click Home Menu
        getWicketTester().clickLink("menu:menuTable:1:menuLink");
        getWicketTester().assertLabel("menu:menuTable:1:menuLink:menuLinkName", "Home Menu");
        getWicketTester().assertLabel("menu:menuTable:2:menuLink:menuLinkName", "Market Menu");
        getWicketTester().assertLabel("menu:menuTable:3:menuLink:menuLinkName", "Rank Menu");
        getWicketTester().assertLabel("menu:menuTable:4:menuLink:menuLinkName", "Page4");
        getWicketTester().assertLabel("menu:menuTable:5:menuLink:menuLinkName", "Page5");
        // Click Market Menu
        getWicketTester().clickLink("menu:menuTable:2:menuLink");
        getWicketTester().assertLabel("menu:menuTable:1:menuLink:menuLinkName", "Home Menu");
        getWicketTester().assertLabel("menu:menuTable:2:menuLink:menuLinkName", "Market Menu");
        getWicketTester().assertLabel("menu:menuTable:3:menuLink:menuLinkName", "Rank Menu");
        getWicketTester().assertLabel("menu:menuTable:4:menuLink:menuLinkName", "Page4");
        getWicketTester().assertLabel("menu:menuTable:5:menuLink:menuLinkName", "Page5");
        // Click Rank Menu
        getWicketTester().clickLink("menu:menuTable:3:menuLink");
        getWicketTester().assertLabel("menu:menuTable:1:menuLink:menuLinkName", "Home Menu");
        getWicketTester().assertLabel("menu:menuTable:2:menuLink:menuLinkName", "Market Menu");
        getWicketTester().assertLabel("menu:menuTable:3:menuLink:menuLinkName", "Rank Menu");
        getWicketTester().assertLabel("menu:menuTable:4:menuLink:menuLinkName", "SubMenu1");
        getWicketTester().assertLabel("menu:menuTable:5:menuLink:menuLinkName", "SubMenu2");
        getWicketTester().assertLabel("menu:menuTable:6:menuLink:menuLinkName", "SubMenu3");
        getWicketTester().assertLabel("menu:menuTable:7:menuLink:menuLinkName", "Page4");
        getWicketTester().assertLabel("menu:menuTable:8:menuLink:menuLinkName", "Page5");
        // Click Page 4
        getWicketTester().clickLink("menu:menuTable:7:menuLink");
        getWicketTester().assertLabel("menu:menuTable:1:menuLink:menuLinkName", "Home Menu");
        getWicketTester().assertLabel("menu:menuTable:2:menuLink:menuLinkName", "Market Menu");
        getWicketTester().assertLabel("menu:menuTable:3:menuLink:menuLinkName", "Rank Menu");
        getWicketTester().assertLabel("menu:menuTable:4:menuLink:menuLinkName", "Page4");
        getWicketTester().assertLabel("menu:menuTable:5:menuLink:menuLinkName", "Page5");
        // Click Page5
        getWicketTester().clickLink("menu:menuTable:5:menuLink");
        getWicketTester().assertLabel("menu:menuTable:1:menuLink:menuLinkName", "Home Menu");
        getWicketTester().assertLabel("menu:menuTable:2:menuLink:menuLinkName", "Market Menu");
        getWicketTester().assertLabel("menu:menuTable:3:menuLink:menuLinkName", "Rank Menu");
        getWicketTester().assertLabel("menu:menuTable:4:menuLink:menuLinkName", "Page4");
        getWicketTester().assertLabel("menu:menuTable:5:menuLink:menuLinkName", "Page5");
        getWicketTester().assertLabel("menu:menuTable:6:menuLink:menuLinkName", "SubMenu51");
        getWicketTester().assertLabel("menu:menuTable:7:menuLink:menuLinkName", "SubMenu52");
        // Click Home Menu
        getWicketTester().clickLink("menu:menuTable:1:menuLink");
        getWicketTester().assertLabel("menu:menuTable:1:menuLink:menuLinkName", "Home Menu");
        getWicketTester().assertLabel("menu:menuTable:2:menuLink:menuLinkName", "Market Menu");
        getWicketTester().assertLabel("menu:menuTable:3:menuLink:menuLinkName", "Rank Menu");
        getWicketTester().assertLabel("menu:menuTable:4:menuLink:menuLinkName", "Page4");
        getWicketTester().assertLabel("menu:menuTable:5:menuLink:menuLinkName", "Page5");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private DbMenuItem createMenuItem(DbPage dbPage, DbMenu dbMenu, String name) {
        DbMenuItem dbMenuItem = dbMenu.getMenuItemCrudChildServiceHelper().createDbChild();
        dbMenuItem.setName(name);
        dbMenuItem.getDbI18nName().putString(name);
        dbMenuItem.setPage(dbPage);
        dbMenuItem.setCssClass("itemCss");
        dbMenuItem.setCssLinkClass("linkCss");
        dbMenuItem.setCssTrClass("trCss");
        dbMenuItem.setSelectedCssClass("selectedItemCss");
        dbMenuItem.setSelectedCssLinkClass("selectedLinkCss");
        dbMenuItem.setSelectedCssTrClass("selectedTrCss");
        dbPage.setMenu(dbMenu);
        return dbMenuItem;
    }

    @Test
    @DirtiesContext
    public void testMenuI18n() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        // Menu
        CrudRootServiceHelper<DbMenu> menuCrud = cmsService.getMenuCrudRootServiceHelper();
        DbMenu dbMenu = menuCrud.createDbChild();
        dbMenu.setName("MainMenu");
        DbMenuItem dbMenuItem = dbMenu.getMenuItemCrudChildServiceHelper().createDbChild();
        dbMenuItem.setName("Internal Name");
        dbMenuItem.getDbI18nName().putString(Locale.GERMAN, "German");
        dbMenuItem.getDbI18nName().putString("English");
        dbMenuItem.setPage(dbPage);
        dbMenuItem.setCssClass("itemCss");
        dbMenuItem.setCssLinkClass("linkCss");
        dbMenuItem.setCssTrClass("trCss");
        dbMenuItem.setSelectedCssClass("selectedItemCss");
        dbMenuItem.setSelectedCssLinkClass("selectedLinkCss");
        dbMenuItem.setSelectedCssTrClass("selectedTrCss");
        dbPage.setMenu(dbMenu);
        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("menu:menuTable:1:menuLink:menuLinkName", "English");
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.GERMAN);
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("menu:menuTable:1:menuLink:menuLinkName", "German");
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.CHINESE);
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("menu:menuTable:1:menuLink:menuLinkName", "English");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testBottomMenuInvisible() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");

        CrudRootServiceHelper<DbMenu> menuCrud = cmsService.getMenuCrudRootServiceHelper();
        DbMenu dbMenu = menuCrud.createDbChild();
        dbMenu.setName("MainMenu");
        createMenuItem(dbPage, dbMenu, "Home Menu");

        menuCrud.updateDbChild(dbMenu);
        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertInvisible("menu:bottom");
        getWicketTester().debugComponentTrees();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testBottomMenu() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");

        CrudRootServiceHelper<DbMenu> menuCrud = cmsService.getMenuCrudRootServiceHelper();
        DbMenu dbMenu = menuCrud.createDbChild();
        dbMenu.setName("MainMenu");
        createMenuItem(dbPage, dbMenu, "Home Menu");

        DbContentGameLink dbContentGameLink = (DbContentGameLink) cmsService.getContentCrud().createDbChild(DbContentGameLink.class);
        dbContentGameLink.getDbI18nName().putString("Hallo Galli");
        dbContentGameLink.setReadRestricted(DbContent.Access.ALLOWED);
        dbContentGameLink.setWriteRestricted(DbContent.Access.DENIED);
        dbContentGameLink.setCreateRestricted(DbContent.Access.DENIED);
        dbContentGameLink.setDeleteRestricted(DbContent.Access.DENIED);
        dbMenu.setBottom(dbContentGameLink);

        cmsService.getContentCrud().updateDbChild(dbContentGameLink);
        menuCrud.updateDbChild(dbMenu);
        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.getDbLevel(); // Set level for new user
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("menu:bottom:link:label", "Hallo Galli");
        getWicketTester().assertInvisible("menu:bottom:link:image");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private int setupBlogPage() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage1.setName("Home");

        DbContentList dbContentList = new DbContentList();
        dbContentList.init(userService);
        dbPage1.setContentAndAccessWrites(dbContentList);
        dbContentList.setRowsPerPage(5);
        dbContentList.setSpringBeanName("contentService");
        dbContentList.setContentProviderGetter("blogEntryCrudRootServiceHelper");

        CrudListChildServiceHelper<DbContent> columnCrud = dbContentList.getColumnsCrud();
        DbContentContainer dbContentContentContainer = (DbContentContainer) columnCrud.createDbChild(DbContentContainer.class);

        DbExpressionProperty title = (DbExpressionProperty) dbContentContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        title.setExpression("name");
        DbExpressionProperty date = (DbExpressionProperty) dbContentContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        date.setExpression("timeStamp");
        date.setOptionalType(DbExpressionProperty.Type.DATE_DDMMYYYY_HH_MM_SS);
        DbExpressionProperty html = (DbExpressionProperty) dbContentContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        html.setExpression("html");
        html.setEditorType(DbExpressionProperty.EditorType.HTML_AREA);

        pageCrud.updateDbChild(dbPage1);
        int id = dbPage1.getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        return id;
    }

    @Test
    @DirtiesContext
    public void testBlogRead() throws Exception {
        configureSimplePlanetNoResources();

        // Setup CMS content
        int id = setupBlogPage();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Set blog service
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbBlogEntry> blogCrud = contentService.getBlogEntryCrudRootServiceHelper();
        DbBlogEntry dbBlogEntry1 = blogCrud.createDbChild();
        dbBlogEntry1.setHtml("Blog 1");
        dbBlogEntry1.setName("News 1");
        blogCrud.updateDbChild(dbBlogEntry1);

        Thread.sleep(10); // Make Blog 1 older than blog 2

        DbBlogEntry dbBlogEntry2 = blogCrud.createDbChild();
        dbBlogEntry2.setHtml("Blog 2");
        dbBlogEntry2.setName("News 2");
        blogCrud.updateDbChild(dbBlogEntry2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPage cachePage = cmsService.getPage(id);
        DbContentList cacheContentList = (DbContentList) cachePage.getContent();

        Assert.assertEquals("contentService", cacheContentList.getSpringBeanName());
        Assert.assertEquals("blogEntryCrudRootServiceHelper", cacheContentList.getContentProviderGetter());
        Assert.assertEquals(5, (int) cacheContentList.getRowsPerPage());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell:container:1", "News 2");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell:container:2", DateUtil.formatDateTime(new Date(dbBlogEntry2.getTimeStamp())));
        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell:container:3", "Blog 2");

        getWicketTester().assertLabel("form:content:table:rows:2:cells:1:cell:container:1", "News 1");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:1:cell:container:2", DateUtil.formatDateTime(new Date(dbBlogEntry1.getTimeStamp())));
        getWicketTester().assertLabel("form:content:table:rows:2:cells:1:cell:container:3", "Blog 1");

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testBlogWrite() throws Exception {
        configureSimplePlanetNoResources();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage1.setName("Home");

        DbContentList dbContentList = new DbContentList();
        dbContentList.init(userService);
        dbPage1.setContentAndAccessWrites(dbContentList);
        dbContentList.setWriteRestricted(DbContent.Access.USER);
        dbContentList.setRowsPerPage(5);
        dbContentList.setSpringBeanName("contentService");
        dbContentList.setContentProviderGetter("blogEntryCrudRootServiceHelper");

        CrudListChildServiceHelper<DbContent> columnCrud = dbContentList.getColumnsCrud();
        DbContentContainer dbContentContentContainer = (DbContentContainer) columnCrud.createDbChild(DbContentContainer.class);

        DbExpressionProperty title = (DbExpressionProperty) dbContentContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        title.setExpression("name");
        DbExpressionProperty date = (DbExpressionProperty) dbContentContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        date.setExpression("timeStamp");
        date.setOptionalType(DbExpressionProperty.Type.DATE_DDMMYYYY_HH_MM_SS);
        DbExpressionProperty html = (DbExpressionProperty) dbContentContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        html.setExpression("html");
        html.setEditorType(DbExpressionProperty.EditorType.HTML_AREA);

        pageCrud.updateDbChild(dbPage1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        // Create User
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("test");
        User user = userService.getUser();
        DbContentAccessControl control = user.getContentCrud().createDbChild();
        control.setDbContent(dbContentList);
        control.setCreateAllowed(true);
        control.setDeleteAllowed(true);
        control.setWriteAllowed(true);
        userService.save(user);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Write
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("test", "test");
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertVisible("form:content:edit:edit");
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.submit("content:edit:edit");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        formTester = getWicketTester().newFormTester("form");
        formTester.submit("content:edit:create");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        formTester = getWicketTester().newFormTester("form");
        getWicketTester().debugComponentTrees();
        formTester.setValue("content:table:rows:1:cells:1:cell:container:1:editor:field", "Blog 1");
        formTester.setValue("content:table:rows:1:cells:1:cell:container:3:editor:editor", "Bla Bla");
        formTester.submit("content:edit:save");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Collection<DbBlogEntry> blog = contentService.getBlogEntryCrudRootServiceHelper().readDbChildren();
        Assert.assertEquals(1, blog.size());
        DbBlogEntry dbBlogEntry = blog.iterator().next();
        Assert.assertEquals("Blog 1", dbBlogEntry.getName());
        Assert.assertEquals("Bla Bla", dbBlogEntry.getHtml());
    }

    @Test
    @DirtiesContext
    public void testBlogWriteProtected() throws Exception {
        configureSimplePlanetNoResources();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage1.setName("Home");

        DbContentList dbContentList = new DbContentList();
        dbContentList.init(userService);
        dbPage1.setContentAndAccessWrites(dbContentList);
        dbContentList.setRowsPerPage(5);
        dbContentList.setSpringBeanName("contentService");
        dbContentList.setContentProviderGetter("blogEntryCrudRootServiceHelper");

        CrudListChildServiceHelper<DbContent> columnCrud = dbContentList.getColumnsCrud();
        DbContentContainer dbContentContentContainer = (DbContentContainer) columnCrud.createDbChild(DbContentContainer.class);

        DbExpressionProperty title = (DbExpressionProperty) dbContentContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        title.setExpression("name");
        DbExpressionProperty date = (DbExpressionProperty) dbContentContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        date.setExpression("timeStamp");
        date.setOptionalType(DbExpressionProperty.Type.DATE_DDMMYYYY_HH_MM_SS);
        DbExpressionProperty html = (DbExpressionProperty) dbContentContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        html.setExpression("html");
        html.setEditorType(DbExpressionProperty.EditorType.HTML_AREA);

        pageCrud.updateDbChild(dbPage1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();

        // Create User
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("test");
        User user = userService.getUser();
        DbContentAccessControl control = user.getContentCrud().createDbChild();
        control.setDbContent(dbContentList);
        control.setCreateAllowed(true);
        control.setDeleteAllowed(true);
        control.setWriteAllowed(true);
        control = user.getContentCrud().createDbChild();
        control.setDbContent(title);
        control.setCreateAllowed(true);
        control.setDeleteAllowed(true);
        control.setWriteAllowed(true);
        control = user.getContentCrud().createDbChild();
        control.setDbContent(html);
        control.setCreateAllowed(true);
        control.setDeleteAllowed(true);
        control.setWriteAllowed(true);
        endHttpRequestAndOpenSessionInViewFilter();

        // Write
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertInvisible("form:content:edit:edit");
        endHttpRequestAndOpenSessionInViewFilter();
    }

    @Test
    @DirtiesContext
    public void testWiki() throws Exception {
        configureSimplePlanetNoResources();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage1.setName("Home");

        DbContentList dbContentList = new DbContentList();
        dbContentList.init(userService);
        dbPage1.setContentAndAccessWrites(dbContentList);
        dbContentList.setRowsPerPage(5);
        dbContentList.setSpringBeanName("contentService");
        dbContentList.setContentProviderGetter("wikiSectionCrudRootServiceHelper");
        dbContentList.setWriteRestricted(DbContent.Access.REGISTERED_USER);

        CrudListChildServiceHelper<DbContent> columnCrud = dbContentList.getColumnsCrud();
        DbExpressionProperty nameProperty = (DbExpressionProperty) columnCrud.createDbChild(DbExpressionProperty.class);
        nameProperty.setExpression("name");
        DbContentDetailLink detailLink = (DbContentDetailLink) columnCrud.createDbChild(DbContentDetailLink.class);
        detailLink.setName("Details");

        CrudChildServiceHelper<DbContentBook> contentBookCrud = dbContentList.getContentBookCrud();
        DbContentBook dbContentBook = contentBookCrud.createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.cms.content.DbWikiSection");
        CrudListChildServiceHelper<DbContentRow> rowCrud = dbContentBook.getRowCrud();
        DbContentRow dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("theName");
        DbExpressionProperty expProperty = new DbExpressionProperty();
        expProperty.setParent(dbContentRow);
        expProperty.setExpression("html");
        expProperty.setEditorType(DbExpressionProperty.EditorType.HTML_AREA);
        dbContentRow.setDbContent(expProperty);

        pageCrud.updateDbChild(dbPage1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();

        // Create User
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("test");
        endHttpRequestAndOpenSessionInViewFilter();

        // Write
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertVisible("form:content:edit:edit");
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.submit("content:edit:edit");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        formTester = getWicketTester().newFormTester("form");
        formTester.submit("content:edit:create");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        formTester = getWicketTester().newFormTester("form");
        formTester.setValue("content:table:rows:1:cells:1:cell:editor:field", "TEST 1");
        formTester.submit("content:edit:save");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().clickLink("form:content:table:rows:2:cells:2:cell:link");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().debugComponentTrees();
        formTester = getWicketTester().newFormTester("form");
        formTester.setValue("content:table:rows:1:cells:2:cell:editor:editor", "Content Content Content");
        formTester.submit("content:edit:save");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        formTester = getWicketTester().newFormTester("form");
        formTester.submit("content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();

        Collection<DbWikiSection> wiki = contentService.getWikiSectionCrudRootServiceHelper().readDbChildren();
        Assert.assertEquals(1, wiki.size());
        DbWikiSection dbWikiSection = wiki.iterator().next();
        Assert.assertEquals("TEST 1", dbWikiSection.getName());
        Assert.assertEquals("Content Content Content", dbWikiSection.getHtml());
    }

    @Test
    @DirtiesContext
    public void testDbContentRowI18n() throws Exception {
        configureMultiplePlanetsAndLevels();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        DbContentBook dbContentBook = new DbContentBook();
        dbContentBook.init(userService);
        dbPage.setContentAndAccessWrites(dbContentBook);
        DbContentRow dbContentRow = dbContentBook.getRowCrud().createDbChild();
        dbContentRow.getDbI18nName().putString("english");
        dbContentRow.getDbI18nName().putString(Locale.GERMAN, "german");
        DbContentStaticHtml dbContentStaticHtml = new DbContentStaticHtml();
        dbContentStaticHtml.getDbI18nHtml().putString("xxx");
        dbContentStaticHtml.setParent(dbContentRow);
        dbContentRow.setDbContent(dbContentStaticHtml);
        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell", "english");
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.GERMAN);
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell", "german");
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.CHINESE);
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell", "english");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testDynamicHtmlRead() throws Exception {
        configureSimplePlanetNoResources();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage1.setName("Home");

        DbContentDynamicHtml dbContentDynamicHtml = new DbContentDynamicHtml();
        dbContentDynamicHtml.init(userService);
        dbPage1.setContentAndAccessWrites(dbContentDynamicHtml);
        pageCrud.updateDbChild(dbPage1);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        contentService.setDynamicHtml(dbContentDynamicHtml.getId(), "1234567890");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();

        // Read
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);

        //getWicketTester().assertLabel();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testDynamicHtmlWrite() throws Exception {
        configureSimplePlanetNoResources();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage1.setName("Home");

        DbContentDynamicHtml dbContentDynamicHtml = new DbContentDynamicHtml();
        dbContentDynamicHtml.init(userService);
        dbPage1.setContentAndAccessWrites(dbContentDynamicHtml);
        dbContentDynamicHtml.setWriteRestricted(DbContent.Access.ALLOWED);

        pageCrud.updateDbChild(dbPage1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();

        // Create User
        /*  beginHttpRequestAndOpenSessionInViewFilter();
      userService.createUser("test", "test", "test", "");
      loginUser("test", "test");
      User user = userService.getUserName();
      DbContentAccessControl control = user.getContentCrud().createDbChild();
      control.setDbContent(dbContentDynamicHtml);
      control.setCreateAllowed(true);
      control.setDeleteAllowed(true);
      control.setWriteAllowed(true);
      endHttpRequestAndOpenSessionInViewFilter();  */

        // Write
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertVisible("form:content:edit:edit");
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.submit("content:edit:edit");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        formTester = getWicketTester().newFormTester("form");
        getWicketTester().debugComponentTrees();
        formTester.setValue("content:htmlTextArea:editor", "qaywsxedc");
        formTester.submit("content:edit:save");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertEquals("qaywsxedc", contentService.getDynamicHtml(dbContentDynamicHtml.getId()));
    }

    @Test
    @DirtiesContext
    public void test2DynamicHtml() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");

        DbContentContainer dbContentContainer = new DbContentContainer();
        dbContentContainer.init(userService);
        dbPage.setContentAndAccessWrites(dbContentContainer);
        dbContentContainer.setWriteRestricted(DbContent.Access.ALLOWED);
        DbContentDynamicHtml dynamicHtml1 = (DbContentDynamicHtml) dbContentContainer.getContentCrud().createDbChild(DbContentDynamicHtml.class);
        dynamicHtml1.setEditorType(DbExpressionProperty.EditorType.HTML_AREA);
        DbContentDynamicHtml dynamicHtml2 = (DbContentDynamicHtml) dbContentContainer.getContentCrud().createDbChild(DbContentDynamicHtml.class);
        dynamicHtml2.setEditorType(DbExpressionProperty.EditorType.PLAIN_TEXT_AREA);

        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Write field 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertVisible("form:content:container:1:edit:edit");
        getWicketTester().assertVisible("form:content:container:1:edit:edit");
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.submit("content:container:1:edit:edit");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        formTester = getWicketTester().newFormTester("form");
        getWicketTester().assertInvisible("form:content:container:1:edit:edit");
        // getWicketTester().assertInvisible("form:content:container:2:edit:edit");
        getWicketTester().assertInvisible("form:content:container:2:edit:save");
        getWicketTester().assertVisible("form:content:container:1:htmlTextArea:editor");
        formTester.setValue("content:container:1:htmlTextArea:editor", "qaywsxedc");
        formTester.submit("content:container:1:edit:save");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Write field 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertVisible("form:content:container:1:edit:edit");
        getWicketTester().assertVisible("form:content:container:2:edit:edit");
        formTester = getWicketTester().newFormTester("form");
        formTester.submit("content:container:2:edit:edit");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        formTester = getWicketTester().newFormTester("form");
//        getWicketTester().assertInvisible("form:content:container:1:edit:edit");
        getWicketTester().assertInvisible("form:content:container:2:edit:edit");
        getWicketTester().assertInvisible("form:content:container:1:edit:save");
        getWicketTester().assertVisible("form:content:container:2:htmlTextArea:editor");
        formTester.setValue("content:container:2:htmlTextArea:editor", "qaywsxedc2");
        formTester.submit("content:container:2:edit:save");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        Assert.assertEquals("qaywsxedc", contentService.getDynamicHtml(dynamicHtml1.getId()));
        Assert.assertEquals("qaywsxedc2", contentService.getDynamicHtml(dynamicHtml2.getId()));
    }

    private void fillForum() throws UserAlreadyExistsException, PasswordNotMatchException {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Setup und login user and fill forum
        createAndLoginUser("U1");
        TestForum.fillForum(forumService, userService);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void setupForumStructure() throws UserAlreadyExistsException, PasswordNotMatchException {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Setup CMS content
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage1.setName("Forum");

        DbContentList dbContentList = new DbContentList();
        dbContentList.init(userService);
        dbPage1.setContentAndAccessWrites(dbContentList);
        dbContentList.setWriteRestricted(DbContent.Access.USER);
        dbContentList.setCreateRestricted(DbContent.Access.USER);
        dbContentList.setDeleteRestricted(DbContent.Access.USER);
        dbContentList.setSpringBeanName("forumService");
        dbContentList.setContentProviderGetter("subForumCrud");

        DbContentContainer subForums = (DbContentContainer) dbContentList.getColumnsCrud().createDbChild(DbContentContainer.class);
        DbExpressionProperty name = (DbExpressionProperty) subForums.getContentCrud().createDbChild(DbExpressionProperty.class);
        name.setExpression("name");
        DbExpressionProperty content = (DbExpressionProperty) subForums.getContentCrud().createDbChild(DbExpressionProperty.class);
        content.setExpression("content");
        DbContentList categories = (DbContentList) subForums.getContentCrud().createDbChild(DbContentList.class);
        categories.setName("Categories");
        categories.setContentProviderGetter("categoryCrud");

        DbExpressionProperty nameCat = (DbExpressionProperty) categories.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        nameCat.setExpression("name");
        //DbExpressionProperty lastPostCat = (DbExpressionProperty) categories.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        //lastPostCat.setExpression("lastPost");
        DbContentDetailLink categoryLink = (DbContentDetailLink) categories.getColumnsCrud().createDbChild(DbContentDetailLink.class);
        categoryLink.setName("details");

        DbContentBook categoryContentBook = categories.getContentBookCrud().createDbChild();
        categoryContentBook.setClassName("com.btxtech.game.services.forum.DbCategory");
        DbContentRow categoryNameRow = categoryContentBook.getRowCrud().createDbChild();
        DbExpressionProperty categoryName = new DbExpressionProperty();
        categoryName.setParent(categoryNameRow);
        categoryName.setExpression("name");
        categoryNameRow.setDbContent(categoryName);
        DbContentRow categoryDetailRow = categoryContentBook.getRowCrud().createDbChild();
        DbContentList threadList = new DbContentList();
        threadList.setParent(categoryDetailRow);
        threadList.init(userService);
        threadList.setContentProviderGetter("forumThreadCrud");
        categoryDetailRow.setDbContent(threadList);
        DbExpressionProperty threadColumnName = (DbExpressionProperty) threadList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        threadColumnName.setExpression("name");
        DbContentDetailLink threadLink = (DbContentDetailLink) threadList.getColumnsCrud().createDbChild(DbContentDetailLink.class);
        threadLink.setName("details");

        DbContentBook threadContentBook = threadList.getContentBookCrud().createDbChild();
        threadContentBook.setClassName("com.btxtech.game.services.forum.DbForumThread");
        DbContentRow threadNameRow = threadContentBook.getRowCrud().createDbChild();
        DbExpressionProperty postColumnName = new DbExpressionProperty();
        postColumnName.setParent(threadNameRow);
        postColumnName.setExpression("name");
        threadNameRow.setDbContent(postColumnName);

        DbContentCreateEdit threadCreateEdit = new DbContentCreateEdit();
        threadCreateEdit.init(userService);
        threadCreateEdit.setParent(threadList);
        threadCreateEdit.setName("New Thread");
        threadCreateEdit.setCreateRestricted(DbContent.Access.REGISTERED_USER);
        threadList.setDbContentCreateEdit(threadCreateEdit);
        DbExpressionProperty createName = threadCreateEdit.getValueCrud().createDbChild(DbExpressionProperty.class);
        createName.setExpression("name");
        createName.setWriteRestricted(DbContent.Access.REGISTERED_USER);
        DbExpressionProperty createContent = threadCreateEdit.getValueCrud().createDbChild(DbExpressionProperty.class);
        createContent.setExpression("content");
        createContent.setEditorType(DbExpressionProperty.EditorType.HTML_AREA);
        createContent.setWriteRestricted(DbContent.Access.REGISTERED_USER);

        DbContentRow postsNameRow = threadContentBook.getRowCrud().createDbChild();
        DbContentList postList = new DbContentList();
        postList.setParent(postsNameRow);
        postList.init(userService);
        postList.setContentProviderGetter("postCrud");
        postsNameRow.setDbContent(postList);
        DbExpressionProperty postContent = (DbExpressionProperty) postList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        postContent.setExpression("content");
        postContent.setEditorType(DbExpressionProperty.EditorType.HTML_AREA);

        pageCrud.updateDbChild(dbPage1);
        endHttpRequestAndOpenSessionInViewFilter();

        // Activate
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();

        // Create User with forum rights
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("forum", "forum");
        User user = userService.getUser();
        DbContentAccessControl control = user.getContentCrud().createDbChild();
        control.setDbContent(dbContentList);
        control.setCreateAllowed(true);
        control.setDeleteAllowed(true);
        control.setWriteAllowed(true);
        userService.save(user);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForumView() throws Exception {
        configureSimplePlanetNoResources();

        setupForumStructure();

        fillForum();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell:container:1", "SubForumName1");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell:container:2", "SubForumContent1");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:1:cell", "CategoryName1");
        getWicketTester().assertInvisible("form:content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:create");
        getWicketTester().assertInvisible("form:content:edit:save");
        getWicketTester().assertInvisible("form:content:edit:delete");
        getWicketTester().assertInvisible("form:content:edit:cancelEdit");
        // Click the category link
        getWicketTester().clickLink("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "CategoryName1");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "ForumThreadName1");
        getWicketTester().assertInvisible("form:content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:create");
        getWicketTester().assertInvisible("form:content:edit:save");
        getWicketTester().assertInvisible("form:content:edit:delete");
        getWicketTester().assertInvisible("form:content:edit:cancelEdit");
        // Click the thread link
        getWicketTester().clickLink("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:2:cell:link");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "ForumThreadName1");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "PostContent1");
        getWicketTester().assertInvisible("form:content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:create");
        getWicketTester().assertInvisible("form:content:edit:save");
        getWicketTester().assertInvisible("form:content:edit:delete");
        getWicketTester().assertInvisible("form:content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForumAdmin() throws Exception {
        configureSimplePlanetNoResources();

        setupForumStructure();

        fillForum();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("forum", "forum");

        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell:container:1", "SubForumName1");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell:container:2", "SubForumContent1");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:1:cell", "CategoryName1");
        getWicketTester().assertVisible("form:content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:create");
        getWicketTester().assertInvisible("form:content:edit:save");
        getWicketTester().assertInvisible("form:content:edit:delete");
        getWicketTester().assertInvisible("form:content:edit:cancelEdit");
        // Click the category link
        getWicketTester().clickLink("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "CategoryName1");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "ForumThreadName1");
        getWicketTester().assertVisible("form:content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:create");
        getWicketTester().assertInvisible("form:content:edit:save");
        getWicketTester().assertInvisible("form:content:edit:delete");
        getWicketTester().assertInvisible("form:content:edit:cancelEdit");
        // Click the thread link
        getWicketTester().clickLink("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:2:cell:link");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "ForumThreadName1");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "PostContent1");
        getWicketTester().assertVisible("form:content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:create");
        getWicketTester().assertInvisible("form:content:edit:save");
        getWicketTester().assertInvisible("form:content:edit:delete");
        getWicketTester().assertInvisible("form:content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForumAdminSubForumEdit() throws Exception {
        configureSimplePlanetNoResources();

        setupForumStructure();

        fillForum();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("forum", "forum");

        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell:container:1", "SubForumName1");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell:container:2", "SubForumContent1");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:1:cell", "CategoryName1");
        getWicketTester().assertVisible("form:content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:create");
        getWicketTester().assertInvisible("form:content:edit:save");
        getWicketTester().assertInvisible("form:content:edit:delete");
        getWicketTester().assertInvisible("form:content:edit:cancelEdit");
        // Click the Edit button
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.submit("content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:edit");
        getWicketTester().assertVisible("form:content:edit:create");
        getWicketTester().assertVisible("form:content:edit:save");
        getWicketTester().assertInvisible("form:content:edit:delete");
        getWicketTester().assertVisible("form:content:edit:cancelEdit");
        // Fill invalues and press save
        formTester = getWicketTester().newFormTester("form");
        getWicketTester().debugComponentTrees();
        formTester.setValue("content:table:rows:2:cells:1:cell:container:1:editor:field", "SubForumName2");
        formTester.setValue("content:table:rows:2:cells:1:cell:container:2:editor:field", "SubForumContent2");
        formTester.setValue("content:table:rows:2:cells:1:cell:container:3:table:rows:1:cells:1:cell:editor:field", "CategoryName2");
        formTester.submit("content:edit:save");
        getWicketTester().newFormTester("form").submit("content:edit:cancelEdit");
        getWicketTester().assertVisible("form:content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:create");
        getWicketTester().assertInvisible("form:content:edit:save");
        getWicketTester().assertInvisible("form:content:edit:delete");
        getWicketTester().assertInvisible("form:content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell:container:1", "SubForumName2");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell:container:2", "SubForumContent2");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:1:cell", "CategoryName2");
        getWicketTester().assertInvisible("form:content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:create");
        getWicketTester().assertInvisible("form:content:edit:save");
        getWicketTester().assertInvisible("form:content:edit:delete");
        getWicketTester().assertInvisible("form:content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForumAdminCategoryEdit() throws Exception {
        configureSimplePlanetNoResources();

        setupForumStructure();

        fillForum();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("forum", "forum");

        getWicketTester().startPage(CmsPage.class);
        // Click the Edit button
        getWicketTester().newFormTester("form").submit("content:edit:edit");
        // Click the category link
        getWicketTester().clickLink("form:content:table:rows:2:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        getWicketTester().assertInvisible("form:content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:create");
        getWicketTester().assertVisible("form:content:edit:save");
        getWicketTester().assertInvisible("form:content:edit:delete");
        getWicketTester().assertVisible("form:content:edit:cancelEdit");
        // Fill in values and press save
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.setValue("content:table:rows:1:cells:2:cell:editor:field", "CategoryName2");
        formTester.setValue("content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell:editor:field", "ForumThreadName2");
        formTester.submit("content:edit:save");
        getWicketTester().newFormTester("form").submit("content:edit:cancelEdit");
        getWicketTester().assertVisible("form:content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:create");
        getWicketTester().assertInvisible("form:content:edit:save");
        getWicketTester().assertInvisible("form:content:edit:delete");
        getWicketTester().assertInvisible("form:content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().clickLink("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "CategoryName2");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "ForumThreadName2");
        getWicketTester().assertInvisible("form:content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:create");
        getWicketTester().assertInvisible("form:content:edit:save");
        getWicketTester().assertInvisible("form:content:edit:delete");
        getWicketTester().assertInvisible("form:content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForumAdminCategoryEdit2() throws Exception {
        configureSimplePlanetNoResources();

        setupForumStructure();

        fillForum();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("forum", "forum");

        getWicketTester().startPage(CmsPage.class);
        // Click the category link
        getWicketTester().clickLink("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        // Click the Edit button
        getWicketTester().newFormTester("form").submit("content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:create");
        getWicketTester().assertVisible("form:content:edit:save");
        getWicketTester().assertInvisible("form:content:edit:delete");
        getWicketTester().assertVisible("form:content:edit:cancelEdit");
        // Fill in values and press save
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.setValue("content:table:rows:3:cells:2:cell:editor:field", "CategoryName2");
        formTester.setValue("content:table:rows:4:cells:2:cell:table:rows:1:cells:1:cell:editor:field", "ForumThreadName2");
        formTester.submit("content:edit:save");
        getWicketTester().newFormTester("form").submit("content:edit:cancelEdit");
        getWicketTester().assertVisible("form:content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:create");
        getWicketTester().assertInvisible("form:content:edit:save");
        getWicketTester().assertInvisible("form:content:edit:delete");
        getWicketTester().assertInvisible("form:content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().clickLink("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "CategoryName2");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "ForumThreadName2");
        getWicketTester().assertInvisible("form:content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:create");
        getWicketTester().assertInvisible("form:content:edit:save");
        getWicketTester().assertInvisible("form:content:edit:delete");
        getWicketTester().assertInvisible("form:content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForumAdminThreadEdit2() throws Exception {
        configureSimplePlanetNoResources();

        setupForumStructure();

        fillForum();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("forum", "forum");

        getWicketTester().startPage(CmsPage.class);
        // Click the category link
        getWicketTester().clickLink("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        // Click the thread link
        getWicketTester().clickLink("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:2:cell:link");
        // Click the Edit button
        getWicketTester().newFormTester("form").submit("content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:create");
        getWicketTester().assertVisible("form:content:edit:save");
        getWicketTester().assertInvisible("form:content:edit:delete");
        getWicketTester().assertVisible("form:content:edit:cancelEdit");
        // Fill in values and press save
        FormTester formTester = getWicketTester().newFormTester("form");
        //formTester.setValue("content:table:rows:3:cells:2:cell:textArea", "ForumThreadName5");
        getWicketTester().debugComponentTrees();
        formTester.setValue("content:table:rows:4:cells:2:cell:table:rows:1:cells:1:cell:editor:editor", "PostContent6");
        formTester.submit("content:edit:save");
        getWicketTester().newFormTester("form").submit("content:edit:cancelEdit");
        getWicketTester().assertVisible("form:content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:create");
        getWicketTester().assertInvisible("form:content:edit:save");
        getWicketTester().assertInvisible("form:content:edit:delete");
        getWicketTester().assertInvisible("form:content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().clickLink("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        getWicketTester().clickLink("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:2:cell:link");
        //getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "ForumThreadName5");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "PostContent6");
        getWicketTester().assertInvisible("form:content:edit:edit");
        getWicketTester().assertInvisible("form:content:edit:create");
        getWicketTester().assertInvisible("form:content:edit:save");
        getWicketTester().assertInvisible("form:content:edit:delete");
        getWicketTester().assertInvisible("form:content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForumCreateThreadInvisible() throws Exception {
        configureSimplePlanetNoResources();

        setupForumStructure();

        fillForum();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        // Click the category link
        getWicketTester().clickLink("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        // Click the thread link
        getWicketTester().clickLink("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:2:cell:link");
        getWicketTester().assertInvisible("form:content:table:rows:2:cells:2:cell:edit:createEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForumCreateThreadCancel() throws Exception {
        configureSimplePlanetNoResources();

        setupForumStructure();

        fillForum();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("test");

        getWicketTester().startPage(CmsPage.class);
        // Click the category link
        getWicketTester().clickLink("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "CategoryName1");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "ForumThreadName1");
        // Click the thread link
        getWicketTester().assertVisible("form:content:table:rows:2:cells:2:cell:edit:createEdit");
        // Click the New Thread Button
        getWicketTester().newFormTester("form").submit("content:table:rows:2:cells:2:cell:edit:createEdit");
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.setValue("content:listView:0:content:editor:field", "Title");
        formTester.setValue("content:listView:1:content:editor:editor", "Content Content");
        // Cancel -> back to page before
        formTester.submit("content:cancel");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "CategoryName1");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "ForumThreadName1");
        endHttpRequestAndOpenSessionInViewFilter();

        // Verify        
        // getWicketTester().setupRequestAndResponse(); // Clears the attribute in the request
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(((ServletWebRequest) RequestCycle.get().getRequest()).getContainerRequest().getAttribute(CmsUiServiceImpl.REQUEST_TMP_CREATE_BEAN_ATTRIBUTES));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbSubForum> subForums = (List<DbSubForum>) forumService.getSubForumCrud().readDbChildren();
        Assert.assertEquals(1, subForums.size());
        List<DbCategory> categories = subForums.get(0).getCategoryCrud().readDbChildren();
        Assert.assertEquals(1, categories.size());
        List<DbForumThread> threads = categories.get(0).getForumThreadCrud().readDbChildren();
        Assert.assertEquals(1, threads.size());
        List<DbPost> posts = threads.get(0).getPostCrud().readDbChildren();
        Assert.assertEquals(1, posts.size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForumCreateThreadSubmit() throws Exception {
        configureSimplePlanetNoResources();

        setupForumStructure();

        fillForum();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("test");

        getWicketTester().startPage(CmsPage.class);
        // Click the category link
        getWicketTester().clickLink("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "CategoryName1");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "ForumThreadName1");
        // Click the thread link
        getWicketTester().assertVisible("form:content:table:rows:2:cells:2:cell:edit:createEdit");
        // Click the New Thread Button
        getWicketTester().newFormTester("form").submit("content:table:rows:2:cells:2:cell:edit:createEdit");
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.setValue("content:listView:0:content:editor:field", "ForumThreadName2");
        formTester.setValue("content:listView:1:content:editor:editor", "Content Content");
        // Submit -> back to page before
        formTester.submit("content:submit");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "CategoryName1");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "ForumThreadName2");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:2:cells:1:cell", "ForumThreadName1");
        // Open Thread
        getWicketTester().clickLink("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:2:cell:link");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "ForumThreadName2");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "Content Content");
        endHttpRequestAndOpenSessionInViewFilter();

        // Verify
        // getWicketTester().setupRequestAndResponse(); // Clears the attribute in the request
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(((ServletWebRequest) RequestCycle.get().getRequest()).getContainerRequest().getAttribute(CmsUiServiceImpl.REQUEST_TMP_CREATE_BEAN_ATTRIBUTES));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbSubForum> subForums = (List<DbSubForum>) forumService.getSubForumCrud().readDbChildren();
        Assert.assertEquals(1, subForums.size());
        List<DbCategory> categories = subForums.get(0).getCategoryCrud().readDbChildren();
        Assert.assertEquals(1, categories.size());
        List<DbForumThread> threads = categories.get(0).getForumThreadCrud().readDbChildren();
        Assert.assertEquals(2, threads.size());
        List<DbPost> posts1 = threads.get(0).getPostCrud().readDbChildren();
        Assert.assertEquals(1, posts1.size());
        List<DbPost> posts2 = threads.get(1).getPostCrud().readDbChildren();
        Assert.assertEquals(1, posts2.size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testLevelsContentContainerExpression() throws Exception {
        configureMultiplePlanetsAndLevels();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");

        DbContentList dbContentList = new DbContentList();
        dbContentList.setRowsPerPage(5);
        dbContentList.init(userService);
        dbPage.setContentAndAccessWrites(dbContentList);
        dbContentList.setSpringBeanName("userGuidanceService");
        dbContentList.setContentProviderGetter("dbLevelCrud");

        CrudListChildServiceHelper<DbContent> columnCrud = dbContentList.getColumnsCrud();
        DbExpressionProperty column = (DbExpressionProperty) columnCrud.createDbChild(DbExpressionProperty.class);
        column.setExpression("name");
        DbContentDetailLink detailLink = (DbContentDetailLink) columnCrud.createDbChild(DbContentDetailLink.class);
        detailLink.setName("Details");

        CrudChildServiceHelper<DbContentBook> contentBookCrud = dbContentList.getContentBookCrud();
        DbContentBook dbContentBook = contentBookCrud.createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.utg.DbLevel");
        CrudListChildServiceHelper<DbContentRow> rowCrud = dbContentBook.getRowCrud();

        DbContentRow dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Name");
        DbExpressionProperty expProperty = new DbExpressionProperty();
        expProperty.setExpression("name");
        expProperty.setParent(dbContentRow);
        dbContentRow.setDbContent(expProperty);

        dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Description");
        expProperty = new DbExpressionProperty();
        expProperty.setParent(dbContentRow);
        expProperty.setExpression("html");
        expProperty.setEditorType(DbExpressionProperty.EditorType.HTML_AREA);
        dbContentRow.setDbContent(expProperty);

        dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Allowed Items");
        DbContentList dbContentListItems = new DbContentList();
        dbContentListItems.setParent(dbContentRow);
        dbContentRow.setDbContent(dbContentListItems);
        dbContentListItems.init(userService);
        dbContentListItems.setContentProviderGetter("itemTypeLimitationCrud");
        columnCrud = dbContentListItems.getColumnsCrud();
        DbExpressionProperty count = (DbExpressionProperty) columnCrud.createDbChild(DbExpressionProperty.class);
        count.setExpression("count");

        DbContentContainer itemContainer = (DbContentContainer) columnCrud.createDbChild(DbContentContainer.class);
        itemContainer.setExpression("dbBaseItemType");
        DbExpressionProperty name = (DbExpressionProperty) itemContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        name.setExpression("name");
        DbExpressionProperty img = (DbExpressionProperty) itemContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        img.setExpression(".");

        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell", "1");
        getWicketTester().assertVisible("form:content:table:rows:1:cells:2:cell:link");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:1:cell", "2");
        getWicketTester().assertVisible("form:content:table:rows:2:cells:2:cell:link");
        // Click first Level
        getWicketTester().clickLink("form:content:table:rows:2:cells:2:cell:link");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "2");
        getWicketTester().debugComponentTrees();
        // Item limitations list
        // unpredictable order getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:1:cell", "TestAttackItem");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:1:cell", "10");
        getWicketTester().assertVisible("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:2:cell:container:2:image");

        // unpredictable order getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:2:cells:1:cell", "TEST_HARVESTER_ITEM");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:2:cells:1:cell", "10");
        getWicketTester().assertVisible("form:content:table:rows:3:cells:2:cell:table:rows:2:cells:2:cell:container:2:image");

        // unpredictable order getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:3:cells:1:cell", "TestContainerItem");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:3:cells:1:cell", "10");
        getWicketTester().assertVisible("form:content:table:rows:3:cells:2:cell:table:rows:3:cells:2:cell:container:2:image");

        // unpredictable order getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:3:cells:1:cell", "TestContainerItem");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:4:cells:1:cell", "10");
        getWicketTester().assertVisible("form:content:table:rows:3:cells:2:cell:table:rows:4:cells:2:cell:container:2:image");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
// TODO
/* TODO    @Test
    @DirtiesContext
    public void testItemTypes() throws Exception {
        configureSimplePlanetNoResources();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");

        DbContentList dbContentList = new DbContentList();
        dbContentList.init(userService);
        dbPage.setContentAndAccessWrites(dbContentList);
        dbContentList.setSpringBeanName("serverItemTypeService");
        dbContentList.setContentProviderGetter("dbItemTypeCrud");

        CrudListChildServiceHelper<DbContent> columnCrud = dbContentList.getColumnsCrud();
        DbExpressionProperty column = (DbExpressionProperty) columnCrud.createDbChild(DbExpressionProperty.class);
        column.setExpression("name");
        DbContentDetailLink detailLink = (DbContentDetailLink) columnCrud.createDbChild(DbContentDetailLink.class);
        detailLink.setName("Details");

        CrudChildServiceHelper<DbContentBook> contentBookCrud = dbContentList.getContentBookCrud();
        DbContentBook dbContentBook = contentBookCrud.createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.item.itemType.DbBaseItemType");
        CrudListChildServiceHelper<DbContentRow> rowCrud = dbContentBook.getRowCrud();

        DbContentRow dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Name");
        DbExpressionProperty expProperty = new DbExpressionProperty();
        expProperty.setExpression("name");
        expProperty.setParent(dbContentRow);
        dbContentRow.setDbContent(expProperty);

        dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Progress");
        expProperty = new DbExpressionProperty();
        expProperty.setParent(dbContentRow);
        expProperty.setExpression("dbFactoryType.progress");
        dbContentRow.setDbContent(expProperty);

        dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Able to build");
        DbContentList ableToBuild = new DbContentList();
        ableToBuild.init(userService);
        ableToBuild.setContentProviderGetter("dbFactoryType.ableToBuildCrud");
        ableToBuild.setParent(dbContentRow);
        dbContentRow.setDbContent(ableToBuild);

        CrudListChildServiceHelper<DbContent> ableToBuildColumnCrud = ableToBuild.getColumnsCrud();
        DbExpressionProperty ableToBuildName = (DbExpressionProperty) ableToBuildColumnCrud.createDbChild(DbExpressionProperty.class);
        ableToBuildName.setExpression("name");

        contentBookCrud = dbContentList.getContentBookCrud();
        dbContentBook = contentBookCrud.createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.item.itemType.DbResourceItemType");
        rowCrud = dbContentBook.getRowCrud();

        dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Name");
        expProperty = new DbExpressionProperty();
        expProperty.setExpression("name");
        expProperty.setParent(dbContentRow);
        dbContentRow.setDbContent(expProperty);

        dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("amount");
        expProperty = new DbExpressionProperty();
        expProperty.setExpression("amount");
        expProperty.setParent(dbContentRow);
        dbContentRow.setDbContent(expProperty);

        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:table:rows:9:cells:1:cell", "TEST_WATER_CONTAINER_ITEM");
        getWicketTester().assertLabel("form:content:table:rows:9:cells:2:cell:link:label", "Details");
        getWicketTester().assertLabel("form:content:table:rows:12:cells:1:cell", "TestStartBuilderItem");
        getWicketTester().assertLabel("form:content:table:rows:12:cells:2:cell:link:label", "Details");
        // Click link
        getWicketTester().clickLink("form:content:table:rows:9:cells:2:cell:link");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "TEST_WATER_CONTAINER_ITEM");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell", "-");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:1:cell", "TestFactoryItem");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:2:cells:1:cell", "TEST_HARVESTER_ITEM");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:3:cells:1:cell", "TestAttackItem");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify if null property
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:table:rows:3:cells:1:cell", "TestAttackItem");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:link:label", "Details");
        // Click link
        getWicketTester().clickLink("form:content:table:rows:3:cells:2:cell:link");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "TestAttackItem");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell", "-");
        // Go back to table and click the resource item type
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().clickLink("form:content:table:rows:12:cells:2:cell:link");
        getWicketTester().debugComponentTrees();
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "TestResourceItem");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell", "3");

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testItemTypesColumnCountSingleCell() throws Exception {
        configureSimplePlanetNoResources();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");

        DbContentList dbContentList = new DbContentList();
        dbContentList.setRowsPerPage(9);
        dbContentList.init(userService);
        dbPage.setContentAndAccessWrites(dbContentList);
        dbContentList.setSpringBeanName("serverItemTypeService");
        dbContentList.setContentProviderGetter("dbItemTypeCrud");

        CrudListChildServiceHelper<DbContent> columnCrud = dbContentList.getColumnsCrud();
        DbExpressionProperty column = (DbExpressionProperty) columnCrud.createDbChild(DbExpressionProperty.class);
        column.setExpression("name");
        DbContentDetailLink detailLink = (DbContentDetailLink) columnCrud.createDbChild(DbContentDetailLink.class);
        detailLink.setName("Details");

        CrudChildServiceHelper<DbContentBook> contentBookCrud = dbContentList.getContentBookCrud();
        DbContentBook dbContentBook = contentBookCrud.createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.item.itemType.DbBaseItemType");
        CrudListChildServiceHelper<DbContentRow> rowCrud = dbContentBook.getRowCrud();

        DbContentRow dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Name");
        DbExpressionProperty expProperty = new DbExpressionProperty();
        expProperty.setExpression("name");
        expProperty.setParent(dbContentRow);
        dbContentRow.setDbContent(expProperty);

        dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Progress");
        expProperty = new DbExpressionProperty();
        expProperty.setParent(dbContentRow);
        expProperty.setExpression("dbFactoryType.progress");
        dbContentRow.setDbContent(expProperty);

        dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Able to build");
        DbContentList ableToBuild = new DbContentList();
        ableToBuild.init(userService);
        ableToBuild.setContentProviderGetter("dbFactoryType.ableToBuildCrud");
        ableToBuild.setParent(dbContentRow);
        ableToBuild.setColumnCountSingleCell(5);
        dbContentRow.setDbContent(ableToBuild);

        CrudListChildServiceHelper<DbContent> ableToBuildColumnCrud = ableToBuild.getColumnsCrud();
        DbExpressionProperty ableToBuildName = (DbExpressionProperty) ableToBuildColumnCrud.createDbChild(DbExpressionProperty.class);
        ableToBuildName.setExpression("name");


        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:table:rows:9:cells:1:cell", "TEST_WATER_CONTAINER_ITEM");
        getWicketTester().assertLabel("form:content:table:rows:9:cells:2:cell:link:label", "Details");
        // Click link
        getWicketTester().clickLink("form:content:table:rows:9:cells:2:cell:link");
        getWicketTester().debugComponentTrees();
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "TEST_WATER_CONTAINER_ITEM");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell", "-");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:1:cell", "TEST_HARVESTER_ITEM");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:2:cell", "TestAttackItem");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:3:cell", "TestContainerItem");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:4:cell", "TEST_CONSUMER_ATTACK_MOVABLE_TYPE");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:5:cell", "");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify if null property
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:table:rows:3:cells:1:cell", "TestAttackItem");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:link:label", "Details");
        // Click link
        getWicketTester().clickLink("form:content:table:rows:3:cells:2:cell:link");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "TestAttackItem");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell", "-");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testItemTypeSectionLink() throws Exception {
        configureSimplePlanetNoResources();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");

        DbContentList dbContentList = new DbContentList();
        dbContentList.setRowsPerPage(20);
        dbContentList.init(userService);
        dbPage.setContentAndAccessWrites(dbContentList);
        dbContentList.setSpringBeanName("serverItemTypeService");
        dbContentList.setContentProviderGetter("dbItemTypeCrud");

        CrudListChildServiceHelper<DbContent> columnCrud = dbContentList.getColumnsCrud();
        DbExpressionProperty column = (DbExpressionProperty) columnCrud.createDbChild(DbExpressionProperty.class);
        column.setExpression("name");
        DbContentDetailLink detailLink = (DbContentDetailLink) columnCrud.createDbChild(DbContentDetailLink.class);
        detailLink.setName("Details");

        CrudChildServiceHelper<DbContentBook> contentBookCrud = dbContentList.getContentBookCrud();
        DbContentBook dbContentBook = contentBookCrud.createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.item.itemType.DbBaseItemType");
        CrudListChildServiceHelper<DbContentRow> rowCrud = dbContentBook.getRowCrud();

        DbContentRow dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Name");
        DbExpressionProperty expProperty = new DbExpressionProperty();
        expProperty.setExpression("name");
        expProperty.setParent(dbContentRow);
        dbContentRow.setDbContent(expProperty);

        dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Progress");
        expProperty = new DbExpressionProperty();
        expProperty.setParent(dbContentRow);
        expProperty.setExpression("dbFactoryType.progress");
        dbContentRow.setDbContent(expProperty);

        dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Able to build");
        DbContentList ableToBuild = new DbContentList();
        ableToBuild.init(userService);
        ableToBuild.setContentProviderGetter("dbFactoryType.ableToBuildCrud");
        ableToBuild.setParent(dbContentRow);
        ableToBuild.setColumnCountSingleCell(5);
        dbContentRow.setDbContent(ableToBuild);

        CrudListChildServiceHelper<DbContent> ableToBuildColumnCrud = ableToBuild.getColumnsCrud();
        DbExpressionProperty ableToBuildName = (DbExpressionProperty) ableToBuildColumnCrud.createDbChild(DbExpressionProperty.class);
        ableToBuildName.setExpression("name");
        ableToBuildName.setLink(true);

        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().debugComponentTrees();
        getWicketTester().assertLabel("form:content:table:rows:9:cells:1:cell", "TEST_WATER_CONTAINER_ITEM");
        getWicketTester().assertLabel("form:content:table:rows:9:cells:2:cell:link:label", "Details");
        // Click link
        getWicketTester().clickLink("form:content:table:rows:9:cells:2:cell:link");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "TEST_WATER_CONTAINER_ITEM");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell", "-");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:1:cell:link:label", "TEST_HARVESTER_ITEM");
        PageParameters pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("sec", "units");
        pageParameters.set("childId", 2);
        getWicketTester().assertBookmarkablePageLink("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:1:cell:link", CmsPage.class, pageParameters);

        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:2:cell:link:label", "TestAttackItem");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("sec", "units");
        pageParameters.set("childId", 3);
        getWicketTester().assertBookmarkablePageLink("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:2:cell:link", CmsPage.class, pageParameters);

        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:3:cell:link:label", "TestContainerItem");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("sec", "units");
        pageParameters.set("childId", 4);
        getWicketTester().assertBookmarkablePageLink("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:3:cell:link", CmsPage.class, pageParameters);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify if null property
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:table:rows:3:cells:1:cell", "TestAttackItem");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell:link:label", "Details");
        // Click link
        getWicketTester().clickLink("form:content:table:rows:3:cells:2:cell:link");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "TestAttackItem");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell", "-");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
 */
    @Test
    @DirtiesContext
    public void testLevelSectionLink() throws Exception {
        configureSimplePlanetNoResources();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();

        // Setup Home
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        DbExpressionProperty dbExpressionProperty = new DbExpressionProperty();
        dbExpressionProperty.init(userService);
        dbPage.setContentAndAccessWrites(dbExpressionProperty);
        dbExpressionProperty.setSpringBeanName("userGuidanceService");
        dbExpressionProperty.setExpression("dbLevelCms.name");
        dbExpressionProperty.setLink(true);
        pageCrud.updateDbChild(dbPage);

        // Setup the level page
        DbPage dbLevelPage = pageCrud.createDbChild();
        dbLevelPage.setName("Level");

        // Setup the level page
        DbContentList levelContentList = new DbContentList();
        dbLevelPage.setContentAndAccessWrites(levelContentList);
        levelContentList.setRowsPerPage(5);
        levelContentList.init(userService);
        levelContentList.setSpringBeanName("userGuidanceService");
        levelContentList.setContentProviderGetter("dbLevelCrud");

        DbContentBook dbContentBook = levelContentList.getContentBookCrud().createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.utg.DbLevel");
        CrudListChildServiceHelper<DbContentRow> rowCrud = dbContentBook.getRowCrud();

        DbContentRow dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Name");
        dbContentRow.getDbI18nName().putString("Name");
        DbExpressionProperty expProperty = new DbExpressionProperty();
        expProperty.setParent(dbContentRow);
        expProperty.setExpression("name");
        expProperty.setEditorType(DbExpressionProperty.EditorType.PLAIN_TEXT_AREA);
        dbContentRow.setDbContent(expProperty);
        pageCrud.updateDbChild(dbLevelPage);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertLabel("form:content", "-");
        // Enter game
        getMovableService().getRealGameInfo(START_UID_1, null);
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:link:label", "2");
        // Click Link
        getWicketTester().clickLink("form:content:link");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell", "Name");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "2");

        getWicketTester().debugComponentTrees();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
/* TODO
    @Test
    @DirtiesContext
    public void testItemTypeNavigation() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Remove resource
        serverItemTypeService.getDbItemTypeCrud().deleteDbChild(serverItemTypeService.getDbItemTypeCrud().readDbChild(TEST_RESOURCE_ITEM_ID));
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");

        DbContentList dbContentList = new DbContentList();
        dbContentList.setRowsPerPage(9);
        dbContentList.init(userService);
        dbPage.setContentAndAccessWrites(dbContentList);
        dbContentList.setSpringBeanName("serverItemTypeService");
        dbContentList.setContentProviderGetter("dbItemTypeCrud");

        CrudListChildServiceHelper<DbContent> columnCrud = dbContentList.getColumnsCrud();
        DbExpressionProperty column = (DbExpressionProperty) columnCrud.createDbChild(DbExpressionProperty.class);
        column.setExpression("name");
        DbContentDetailLink detailLink = (DbContentDetailLink) columnCrud.createDbChild(DbContentDetailLink.class);
        detailLink.setName("Details");

        CrudChildServiceHelper<DbContentBook> contentBookCrud = dbContentList.getContentBookCrud();
        DbContentBook dbContentBook = contentBookCrud.createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.item.itemType.DbBaseItemType");
        dbContentBook.setNavigationVisible(true);
        dbContentBook.setNextNavigationName("next");
        dbContentBook.setPreviousNavigationName("previous");
        dbContentBook.setUpNavigationName("up");
        CrudListChildServiceHelper<DbContentRow> rowCrud = dbContentBook.getRowCrud();

        DbContentRow dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Name");
        DbExpressionProperty expProperty = new DbExpressionProperty();
        expProperty.setExpression("name");
        expProperty.setParent(dbContentRow);
        dbContentRow.setDbContent(expProperty);

        dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Progress");
        expProperty = new DbExpressionProperty();
        expProperty.setParent(dbContentRow);
        expProperty.setExpression("dbFactoryType.progress");
        dbContentRow.setDbContent(expProperty);

        dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Able to build");
        DbContentList ableToBuild = new DbContentList();
        ableToBuild.init(userService);
        ableToBuild.setContentProviderGetter("dbFactoryType.ableToBuildCrud");
        ableToBuild.setParent(dbContentRow);
        dbContentRow.setDbContent(ableToBuild);

        CrudListChildServiceHelper<DbContent> ableToBuildColumnCrud = ableToBuild.getColumnsCrud();
        DbExpressionProperty ableToBuildName = (DbExpressionProperty) ableToBuildColumnCrud.createDbChild(DbExpressionProperty.class);
        ableToBuildName.setExpression("name");
        ableToBuildName.setLink(true);

        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        // Click link
        getWicketTester().clickLink("form:content:table:rows:1:cells:2:cell:link");
        getWicketTester().assertLabel("form:content:navigation:previousLink:previousLabel", "previous");
        getWicketTester().assertDisabled("form:content:navigation:previousLink:previousLabel");
        getWicketTester().assertDisabled("form:content:navigation:previousLink");

        getWicketTester().assertLabel("form:content:navigation:upLink:upLabel", "up");
        getWicketTester().assertEnabled("form:content:navigation:upLink:upLabel");
        getWicketTester().assertEnabled("form:content:navigation:upLink");
        PageParameters pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        getWicketTester().assertBookmarkablePageLink("form:content:navigation:upLink", CmsPage.class, pageParameters);

        getWicketTester().assertLabel("form:content:navigation:nextLink:nextLabel", "next");
        getWicketTester().assertEnabled("form:content:navigation:nextLink:nextLabel");
        getWicketTester().assertEnabled("form:content:navigation:nextLink");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("childId", 2);
        pageParameters.set("detailId", "1");
        getWicketTester().assertBookmarkablePageLink("form:content:navigation:nextLink", CmsPage.class, pageParameters);

        // Click next
        getWicketTester().clickLink("form:content:navigation:nextLink");
        getWicketTester().assertLabel("form:content:navigation:previousLink:previousLabel", "previous");
        getWicketTester().assertEnabled("form:content:navigation:previousLink:previousLabel");
        getWicketTester().assertEnabled("form:content:navigation:previousLink");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("childId", 1);
        pageParameters.set("detailId", "1");
        getWicketTester().assertBookmarkablePageLink("form:content:navigation:previousLink", CmsPage.class, pageParameters);

        getWicketTester().assertLabel("form:content:navigation:upLink:upLabel", "up");
        getWicketTester().assertEnabled("form:content:navigation:upLink:upLabel");
        getWicketTester().assertEnabled("form:content:navigation:upLink");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        getWicketTester().assertBookmarkablePageLink("form:content:navigation:upLink", CmsPage.class, pageParameters);

        getWicketTester().assertLabel("form:content:navigation:nextLink:nextLabel", "next");
        getWicketTester().assertEnabled("form:content:navigation:nextLink:nextLabel");
        getWicketTester().assertEnabled("form:content:navigation:nextLink");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("childId", 3);
        pageParameters.set("detailId", "1");
        getWicketTester().assertBookmarkablePageLink("form:content:navigation:nextLink", CmsPage.class, pageParameters);

        // Click next (go to the last)
        getWicketTester().clickLink("form:content:navigation:nextLink");
        getWicketTester().clickLink("form:content:navigation:nextLink");
        getWicketTester().clickLink("form:content:navigation:nextLink");
        getWicketTester().clickLink("form:content:navigation:nextLink");
        getWicketTester().clickLink("form:content:navigation:nextLink");
        getWicketTester().clickLink("form:content:navigation:nextLink");
        getWicketTester().clickLink("form:content:navigation:nextLink");
        getWicketTester().clickLink("form:content:navigation:nextLink");
        getWicketTester().clickLink("form:content:navigation:nextLink");
        getWicketTester().assertLabel("form:content:navigation:previousLink:previousLabel", "previous");
        getWicketTester().assertEnabled("form:content:navigation:previousLink:previousLabel");
        getWicketTester().assertEnabled("form:content:navigation:previousLink");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("childId", 10);
        pageParameters.set("detailId", "1");
        getWicketTester().assertBookmarkablePageLink("form:content:navigation:previousLink", CmsPage.class, pageParameters);

        getWicketTester().assertLabel("form:content:navigation:upLink:upLabel", "up");
        getWicketTester().assertEnabled("form:content:navigation:upLink:upLabel");
        getWicketTester().assertEnabled("form:content:navigation:upLink");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        getWicketTester().assertBookmarkablePageLink("form:content:navigation:upLink", CmsPage.class, pageParameters);

        getWicketTester().assertLabel("form:content:navigation:nextLink:nextLabel", "next");
        getWicketTester().assertDisabled("form:content:navigation:nextLink:nextLabel");
        getWicketTester().assertDisabled("form:content:navigation:nextLink");

        // Click pref
        getWicketTester().clickLink("form:content:navigation:previousLink");
        getWicketTester().assertLabel("form:content:navigation:previousLink:previousLabel", "previous");
        getWicketTester().assertEnabled("form:content:navigation:previousLink:previousLabel");
        getWicketTester().assertEnabled("form:content:navigation:previousLink");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("childId", 9);
        pageParameters.set("detailId", "1");
        getWicketTester().assertBookmarkablePageLink("form:content:navigation:previousLink", CmsPage.class, pageParameters);

        getWicketTester().assertLabel("form:content:navigation:upLink:upLabel", "up");
        getWicketTester().assertEnabled("form:content:navigation:upLink:upLabel");
        getWicketTester().assertEnabled("form:content:navigation:upLink");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        getWicketTester().assertBookmarkablePageLink("form:content:navigation:upLink", CmsPage.class, pageParameters);

        getWicketTester().assertLabel("form:content:navigation:nextLink:nextLabel", "next");
        getWicketTester().assertEnabled("form:content:navigation:nextLink:nextLabel");
        getWicketTester().assertEnabled("form:content:navigation:nextLink");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("childId", 11);
        pageParameters.set("detailId", "1");
        getWicketTester().assertBookmarkablePageLink("form:content:navigation:nextLink", CmsPage.class, pageParameters);

        // Click up
        getWicketTester().clickLink("form:content:navigation:upLink");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
*/

    @Test
    @DirtiesContext
    public void testPageLinkText() throws Exception {
        configureSimplePlanetNoResources();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");

        DbContentPageLink dbContentPageLink = new DbContentPageLink();
        dbContentPageLink.getDbI18nName().putString("PAGE LINK");
        dbContentPageLink.setDbPage(dbPage);
        dbPage.setContentAndAccessWrites(dbContentPageLink);

        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:link:label", "PAGE LINK");
        getWicketTester().assertInvisible("form:content:link:image");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testPageLinkImage() throws Exception {
        configureSimplePlanetNoResources();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");

        // Prepare image
        DbCmsImage dbCmsImage = cmsService.getImageCrudRootServiceHelper().createDbChild();
        dbCmsImage.setData(new byte[]{1, 2, 3});
        cmsService.getImageCrudRootServiceHelper().updateDbChild(dbCmsImage);

        DbContentPageLink dbContentPageLink = new DbContentPageLink();
        dbContentPageLink.getDbI18nName().putString("PAGE LINK");
        dbContentPageLink.setDbPage(dbPage);
        dbContentPageLink.setDbCmsImage(dbCmsImage);
        dbPage.setContentAndAccessWrites(dbContentPageLink);

        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertInvisible("form:content:link:label");
        getWicketTester().assertVisible("form:content:link:image");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void prepare4RegisterCheck() throws Exception {
        configureMultiplePlanetsAndLevels();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        DbPage dbUserPage = pageCrud.createDbChild();
        dbUserPage.setPredefinedType(CmsUtil.CmsPredefinedPage.USER_PAGE);
        dbUserPage.setName("User Page");
        DbPage dbMessagePage = pageCrud.createDbChild();
        dbMessagePage.setPredefinedType(CmsUtil.CmsPredefinedPage.MESSAGE);
        dbMessagePage.setName("Message Page");

        DbContentPlugin registerPlugin = new DbContentPlugin();
        registerPlugin.setPluginEnum(PluginEnum.REGISTER);
        dbPage.setContentAndAccessWrites(registerPlugin);

        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    // @Test
    @DirtiesContext
    public void testRegisterSimulated() throws Exception {
        SecurityCmsUiService securityCmsUiServiceMock = EasyMock.createMock(SecurityCmsUiService.class);
        securityCmsUiServiceMock.signIn("U1", "test");
        EasyMock.replay(securityCmsUiServiceMock);
        ReflectionTestUtils.setField(cmsUiService, "securityCmsUiService", securityCmsUiServiceMock);

        prepare4RegisterCheck();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertVisible("form:content:newUserForm:name");
        getWicketTester().assertVisible("form:content:newUserForm:email");
        getWicketTester().assertVisible("form:content:newUserForm:password");
        getWicketTester().assertVisible("form:content:newUserForm:confirmPassword");
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.setValue("content:newUserForm:name", "U1");
        formTester.setValue("content:newUserForm:email", "u1email");
        formTester.setValue("content:newUserForm:password", "test");
        formTester.setValue("content:newUserForm:confirmPassword", "test");
        formTester.submit();
        getWicketTester().assertRenderedPage(Game.class);
        Page page = getWicketTester().getLastRenderedPage();
        Assert.assertEquals(TEST_LEVEL_TASK_1_1_SIMULATED_ID, page.getPageParameters().get(com.btxtech.game.jsre.client.Game.LEVEL_TASK_ID).toInt());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    // @Test
    @DirtiesContext
    public void testRegisterReal() throws Exception {
        SecurityCmsUiService securityCmsUiServiceMock = EasyMock.createMock(SecurityCmsUiService.class);
        securityCmsUiServiceMock.signIn("U1", "test");
        EasyMock.replay(securityCmsUiServiceMock);
        ReflectionTestUtils.setField(cmsUiService, "securityCmsUiService", securityCmsUiServiceMock);

        prepare4RegisterCheck();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertVisible("form:content:newUserForm:name");
        getWicketTester().assertVisible("form:content:newUserForm:email");
        getWicketTester().assertVisible("form:content:newUserForm:password");
        getWicketTester().assertVisible("form:content:newUserForm:confirmPassword");
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.setValue("content:newUserForm:name", "U1");
        formTester.setValue("content:newUserForm:email", "u1email");
        formTester.setValue("content:newUserForm:password", "test");
        formTester.setValue("content:newUserForm:confirmPassword", "test");
        formTester.submit();
        getWicketTester().assertRenderedPage(Game.class);
        Page page = getWicketTester().getLastRenderedPage();
        Assert.assertTrue(page.getPageParameters().get(com.btxtech.game.jsre.client.Game.LEVEL_TASK_ID).isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    // @Test
    @DirtiesContext
    public void testRegisterFailAlreadyLoggedInException() throws Exception {
        SecurityCmsUiService securityCmsUiServiceMock = EasyMock.createMock(SecurityCmsUiService.class);
        securityCmsUiServiceMock.signIn("U1", "test");
        User user = new User();
        user.registerUser("TestUser", "", "", null);
        EasyMock.expectLastCall().andThrow(new AlreadyLoggedInException(null));
        EasyMock.replay(securityCmsUiServiceMock);

        ReflectionTestUtils.setField(cmsUiService, "securityCmsUiService", securityCmsUiServiceMock);

        prepare4RegisterCheck();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertVisible("form:content:newUserForm:name");
        getWicketTester().assertVisible("form:content:newUserForm:email");
        getWicketTester().assertVisible("form:content:newUserForm:password");
        getWicketTester().assertVisible("form:content:newUserForm:confirmPassword");
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.setValue("content:newUserForm:name", "U1");
        formTester.setValue("content:newUserForm:email", "u1email");
        formTester.setValue("content:newUserForm:password", "test");
        formTester.setValue("content:newUserForm:confirmPassword", "test");
        formTester.submit();
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:border:borderContent:message", "Already logged in as: TestUser");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    // @Test
    @DirtiesContext
    public void testRegisterFailUserAlreadyExistsException() throws Exception {
        SecurityCmsUiService securityCmsUiServiceMock = EasyMock.createMock(SecurityCmsUiService.class);
        securityCmsUiServiceMock.signIn("U1", "test");
        User user = new User();
        user.registerUser("TestUser", "", "", null);
        EasyMock.expectLastCall().andThrow(new UserAlreadyExistsException());
        EasyMock.replay(securityCmsUiServiceMock);

        ReflectionTestUtils.setField(cmsUiService, "securityCmsUiService", securityCmsUiServiceMock);

        prepare4RegisterCheck();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertVisible("form:content:newUserForm:name");
        getWicketTester().assertVisible("form:content:newUserForm:email");
        getWicketTester().assertVisible("form:content:newUserForm:password");
        getWicketTester().assertVisible("form:content:newUserForm:confirmPassword");
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.setValue("content:newUserForm:name", "U1");
        formTester.setValue("content:newUserForm:email", "u1email");
        formTester.setValue("content:newUserForm:password", "test");
        formTester.setValue("content:newUserForm:confirmPassword", "test");
        formTester.submit();
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:border:borderContent:message", "The user already exists");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    // @Test
    @DirtiesContext
    public void testRegisterFailPasswordNotMatchException() throws Exception {
        SecurityCmsUiService securityCmsUiServiceMock = EasyMock.createMock(SecurityCmsUiService.class);
        securityCmsUiServiceMock.signIn("U1", "test");
        User user = new User();
        user.registerUser("TestUser", "", "", null);
        EasyMock.expectLastCall().andThrow(new PasswordNotMatchException());
        EasyMock.replay(securityCmsUiServiceMock);

        ReflectionTestUtils.setField(cmsUiService, "securityCmsUiService", securityCmsUiServiceMock);

        prepare4RegisterCheck();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertVisible("form:content:newUserForm:name");
        getWicketTester().assertVisible("form:content:newUserForm:email");
        getWicketTester().assertVisible("form:content:newUserForm:password");
        getWicketTester().assertVisible("form:content:newUserForm:confirmPassword");
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.setValue("content:newUserForm:name", "U1");
        formTester.setValue("content:newUserForm:email", "u1email");
        formTester.setValue("content:newUserForm:password", "test");
        formTester.setValue("content:newUserForm:confirmPassword", "test");
        formTester.submit();
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:border:borderContent:message", "Password and confirm password do not match");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
