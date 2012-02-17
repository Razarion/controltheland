package com.btxtech.game.services.cms;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.cms.content.DbBlogEntry;
import com.btxtech.game.services.cms.content.DbWikiSection;
import com.btxtech.game.services.cms.impl.CmsServiceImpl;
import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.cms.layout.DbContentBook;
import com.btxtech.game.services.cms.layout.DbContentBooleanExpressionImage;
import com.btxtech.game.services.cms.layout.DbContentContainer;
import com.btxtech.game.services.cms.layout.DbContentCreateEdit;
import com.btxtech.game.services.cms.layout.DbContentDetailLink;
import com.btxtech.game.services.cms.layout.DbContentDynamicHtml;
import com.btxtech.game.services.cms.layout.DbContentGameLink;
import com.btxtech.game.services.cms.layout.DbContentInvoker;
import com.btxtech.game.services.cms.layout.DbContentInvokerButton;
import com.btxtech.game.services.cms.layout.DbContentList;
import com.btxtech.game.services.cms.layout.DbContentPageLink;
import com.btxtech.game.services.cms.layout.DbContentPlugin;
import com.btxtech.game.services.cms.layout.DbContentRow;
import com.btxtech.game.services.cms.layout.DbContentSmartPageLink;
import com.btxtech.game.services.cms.layout.DbContentStartLevelTaskButton;
import com.btxtech.game.services.cms.layout.DbContentStaticHtml;
import com.btxtech.game.services.cms.layout.DbExpressionProperty;
import com.btxtech.game.services.cms.page.DbAds;
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
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.messenger.InvalidFieldException;
import com.btxtech.game.services.messenger.MessengerService;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.statistics.impl.StatisticsServiceImpl;
import com.btxtech.game.services.user.AlreadyLoggedInException;
import com.btxtech.game.services.user.DbContentAccessControl;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.XpService;
import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.pages.cms.CmsStringGenerator;
import com.btxtech.game.wicket.pages.cms.content.plugin.PluginEnum;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import com.btxtech.game.wicket.uiservices.cms.SecurityCmsUiService;
import com.btxtech.game.wicket.uiservices.cms.impl.CmsUiServiceImpl;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.easymock.EasyMock;
import org.hibernate.proxy.HibernateProxy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private ApplicationContext applicationContext;
    @Autowired
    private UserService userService;
    @Autowired
    private MovableService movableService;
    @Autowired
    private XpService xpService;
    @Autowired
    private ForumService forumService;
    @Autowired
    private MessengerService messengerService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private ItemService itemService;

    private WicketTester tester;

    @Before
    public void setUp() {
        tester = new WicketTester();
        tester.getApplication().addComponentInstantiationListener(new SpringComponentInjector(tester.getApplication(), applicationContext, true));
    }

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
    public void testSubMenu() {
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
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertLabel("menu:menuTable:1:menuLink:menuLinkName", "Home Menu");
        tester.assertLabel("menu:menuTable:2:menuLink:menuLinkName", "Market Menu");
        tester.assertLabel("menu:menuTable:3:menuLink:menuLinkName", "Rank Menu");
        tester.assertLabel("menu:menuTable:4:menuLink:menuLinkName", "Page4");
        tester.assertLabel("menu:menuTable:5:menuLink:menuLinkName", "Page5");
        // Click Home Menu
        tester.clickLink("menu:menuTable:1:menuLink");
        tester.assertLabel("menu:menuTable:1:menuLink:menuLinkName", "Home Menu");
        tester.assertLabel("menu:menuTable:2:menuLink:menuLinkName", "Market Menu");
        tester.assertLabel("menu:menuTable:3:menuLink:menuLinkName", "Rank Menu");
        tester.assertLabel("menu:menuTable:4:menuLink:menuLinkName", "Page4");
        tester.assertLabel("menu:menuTable:5:menuLink:menuLinkName", "Page5");
        // Click Market Menu
        tester.clickLink("menu:menuTable:2:menuLink");
        tester.assertLabel("menu:menuTable:1:menuLink:menuLinkName", "Home Menu");
        tester.assertLabel("menu:menuTable:2:menuLink:menuLinkName", "Market Menu");
        tester.assertLabel("menu:menuTable:3:menuLink:menuLinkName", "Rank Menu");
        tester.assertLabel("menu:menuTable:4:menuLink:menuLinkName", "Page4");
        tester.assertLabel("menu:menuTable:5:menuLink:menuLinkName", "Page5");
        // Click Rank Menu
        tester.clickLink("menu:menuTable:3:menuLink");
        tester.assertLabel("menu:menuTable:1:menuLink:menuLinkName", "Home Menu");
        tester.assertLabel("menu:menuTable:2:menuLink:menuLinkName", "Market Menu");
        tester.assertLabel("menu:menuTable:3:menuLink:menuLinkName", "Rank Menu");
        tester.assertLabel("menu:menuTable:4:menuLink:menuLinkName", "SubMenu1");
        tester.assertLabel("menu:menuTable:5:menuLink:menuLinkName", "SubMenu2");
        tester.assertLabel("menu:menuTable:6:menuLink:menuLinkName", "SubMenu3");
        tester.assertLabel("menu:menuTable:7:menuLink:menuLinkName", "Page4");
        tester.assertLabel("menu:menuTable:8:menuLink:menuLinkName", "Page5");
        // Click Page 4
        tester.clickLink("menu:menuTable:7:menuLink");
        tester.assertLabel("menu:menuTable:1:menuLink:menuLinkName", "Home Menu");
        tester.assertLabel("menu:menuTable:2:menuLink:menuLinkName", "Market Menu");
        tester.assertLabel("menu:menuTable:3:menuLink:menuLinkName", "Rank Menu");
        tester.assertLabel("menu:menuTable:4:menuLink:menuLinkName", "Page4");
        tester.assertLabel("menu:menuTable:5:menuLink:menuLinkName", "Page5");
        // Click Page5
        tester.clickLink("menu:menuTable:5:menuLink");
        tester.assertLabel("menu:menuTable:1:menuLink:menuLinkName", "Home Menu");
        tester.assertLabel("menu:menuTable:2:menuLink:menuLinkName", "Market Menu");
        tester.assertLabel("menu:menuTable:3:menuLink:menuLinkName", "Rank Menu");
        tester.assertLabel("menu:menuTable:4:menuLink:menuLinkName", "Page4");
        tester.assertLabel("menu:menuTable:5:menuLink:menuLinkName", "Page5");
        tester.assertLabel("menu:menuTable:6:menuLink:menuLinkName", "SubMenu51");
        tester.assertLabel("menu:menuTable:7:menuLink:menuLinkName", "SubMenu52");
        // Click Home Menu
        tester.clickLink("menu:menuTable:1:menuLink");
        tester.assertLabel("menu:menuTable:1:menuLink:menuLinkName", "Home Menu");
        tester.assertLabel("menu:menuTable:2:menuLink:menuLinkName", "Market Menu");
        tester.assertLabel("menu:menuTable:3:menuLink:menuLinkName", "Rank Menu");
        tester.assertLabel("menu:menuTable:4:menuLink:menuLinkName", "Page4");
        tester.assertLabel("menu:menuTable:5:menuLink:menuLinkName", "Page5");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private DbMenuItem createMenuItem(DbPage dbPage, DbMenu dbMenu, String name) {
        DbMenuItem dbMenuItem = dbMenu.getMenuItemCrudChildServiceHelper().createDbChild();
        dbMenuItem.setName(name);
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
    public void testBottomMenuInvisible() {
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
        tester.startPage(CmsPage.class);
        tester.assertInvisible("menu:bottom");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testBottomMenu() throws Exception {
        configureRealGame();

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
        dbContentGameLink.setLinkText("Hallo Galli");
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
        tester.startPage(CmsPage.class);
        tester.assertLabel("menu:bottom:link:label", "Hallo Galli");
        tester.assertInvisible("menu:bottom:link:image");
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
        html.setEscapeMarkup(false);

        pageCrud.updateDbChild(dbPage1);
        int id = dbPage1.getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        return id;
    }

    @Test
    @DirtiesContext
    public void testBlogRead() throws InterruptedException {
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
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertLabel("form:content:table:rows:1:cells:1:cell:container:1", "News 2");
        tester.assertLabel("form:content:table:rows:1:cells:1:cell:container:2", DateUtil.formatDateTime(new Date(dbBlogEntry2.getTimeStamp())));
        tester.assertLabel("form:content:table:rows:1:cells:1:cell:container:3", "Blog 2");

        tester.assertLabel("form:content:table:rows:2:cells:1:cell:container:1", "News 1");
        tester.assertLabel("form:content:table:rows:2:cells:1:cell:container:2", DateUtil.formatDateTime(new Date(dbBlogEntry1.getTimeStamp())));
        tester.assertLabel("form:content:table:rows:2:cells:1:cell:container:3", "Blog 1");

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testBlogWrite() throws Exception {
        configureRealGame();

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
        html.setEscapeMarkup(false);

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
        userService.createUser("test", "test", "test", "");
        userService.login("test", "test");
        User user = userService.getUser();
        DbContentAccessControl control = user.getContentCrud().createDbChild();
        control.setDbContent(dbContentList);
        control.setCreateAllowed(true);
        control.setDeleteAllowed(true);
        control.setWriteAllowed(true);
        endHttpRequestAndOpenSessionInViewFilter();

        // Write
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.assertVisible("form:content:edit:edit");
        FormTester formTester = tester.newFormTester("form");
        formTester.submit("content:edit:edit");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        formTester = tester.newFormTester("form");
        formTester.submit("content:edit:create");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        formTester = tester.newFormTester("form");
        formTester.setValue("content:table:rows:1:cells:1:cell:container:1:field", "Blog 1");
        formTester.setValue("content:table:rows:1:cells:1:cell:container:3:textArea", "Bla Bla");
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
        configureRealGame();

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
        html.setEscapeMarkup(false);

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
        userService.createUser("test", "test", "test", "");
        userService.login("test", "test");
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
        tester.startPage(CmsPage.class);
        tester.assertInvisible("form:content:edit:edit");
        endHttpRequestAndOpenSessionInViewFilter();
    }

    @Test
    @DirtiesContext
    public void testWiki() throws Exception {
        configureRealGame();

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
        expProperty.setEscapeMarkup(false);
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
        userService.createUser("test", "test", "test", "");
        userService.login("test", "test");
        endHttpRequestAndOpenSessionInViewFilter();

        // Write
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.assertVisible("form:content:edit:edit");
        FormTester formTester = tester.newFormTester("form");
        formTester.submit("content:edit:edit");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        formTester = tester.newFormTester("form");
        formTester.submit("content:edit:create");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        formTester = tester.newFormTester("form");
        formTester.setValue("content:table:rows:1:cells:1:cell:field", "TEST 1");
        formTester.submit("content:edit:save");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        tester.clickLink("form:content:table:rows:2:cells:2:cell:link");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        formTester = tester.newFormTester("form");
        formTester.setValue("content:table:rows:1:cells:2:cell:textArea", "Content Content Content");
        formTester.submit("content:edit:save");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        formTester = tester.newFormTester("form");
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
    public void testDynamicHtmlRead() throws Exception {
        configureRealGame();

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
        tester.startPage(CmsPage.class);

        //tester.assertLabel();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testDynamicHtmlWrite() throws Exception {
        configureRealGame();

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
      userService.login("test", "test");
      User user = userService.getUser();
      DbContentAccessControl control = user.getContentCrud().createDbChild();
      control.setDbContent(dbContentDynamicHtml);
      control.setCreateAllowed(true);
      control.setDeleteAllowed(true);
      control.setWriteAllowed(true);
      endHttpRequestAndOpenSessionInViewFilter();  */

        // Write
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.assertVisible("form:content:edit:edit");
        FormTester formTester = tester.newFormTester("form");
        formTester.submit("content:edit:edit");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        formTester = tester.newFormTester("form");
        formTester.setValue("content:htmlTextArea", "qaywsxedc");
        formTester.submit("content:edit:save");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertEquals("qaywsxedc", contentService.getDynamicHtml(dbContentDynamicHtml.getId()));
    }

    @Test
    @DirtiesContext
    public void test2DynamicHtml() {
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
        dynamicHtml1.setEscapeMarkup(false);
        DbContentDynamicHtml dynamicHtml2 = (DbContentDynamicHtml) dbContentContainer.getContentCrud().createDbChild(DbContentDynamicHtml.class);
        dynamicHtml2.setEscapeMarkup(false);

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
        tester.startPage(CmsPage.class);
        tester.assertVisible("form:content:container:1:edit:edit");
        tester.assertVisible("form:content:container:1:edit:edit");
        FormTester formTester = tester.newFormTester("form");
        formTester.submit("content:container:1:edit:edit");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        formTester = tester.newFormTester("form");
        tester.assertInvisible("form:content:container:1:edit:edit");
        // tester.assertInvisible("form:content:container:2:edit:edit");
        tester.assertInvisible("form:content:container:2:edit:save");
        tester.assertVisible("form:content:container:1:htmlTextArea");
        formTester.setValue("content:container:1:htmlTextArea", "qaywsxedc");
        formTester.submit("content:container:1:edit:save");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Write field 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.assertVisible("form:content:container:1:edit:edit");
        tester.assertVisible("form:content:container:2:edit:edit");
        formTester = tester.newFormTester("form");
        formTester.submit("content:container:2:edit:edit");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        formTester = tester.newFormTester("form");
//        tester.assertInvisible("form:content:container:1:edit:edit");
        tester.assertInvisible("form:content:container:2:edit:edit");
        tester.assertInvisible("form:content:container:1:edit:save");
        tester.assertVisible("form:content:container:2:htmlTextArea");
        formTester.setValue("content:container:2:htmlTextArea", "qaywsxedc2");
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
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
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
        createContent.setEscapeMarkup(false);
        createContent.setWriteRestricted(DbContent.Access.REGISTERED_USER);

        DbContentRow postsNameRow = threadContentBook.getRowCrud().createDbChild();
        DbContentList postList = new DbContentList();
        postList.setParent(postsNameRow);
        postList.init(userService);
        postList.setContentProviderGetter("postCrud");
        postsNameRow.setDbContent(postList);
        DbExpressionProperty postContent = (DbExpressionProperty) postList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        postContent.setExpression("content");
        postContent.setEscapeMarkup(false);

        pageCrud.updateDbChild(dbPage1);
        endHttpRequestAndOpenSessionInViewFilter();

        // Activate
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();

        // Create User with forum rights
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("forum", "forum", "forum", "");
        userService.login("forum", "forum");
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
        configureRealGame();

        setupForumStructure();

        fillForum();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.assertLabel("form:content:table:rows:1:cells:1:cell:container:1", "SubForumName1");
        tester.assertLabel("form:content:table:rows:1:cells:1:cell:container:2", "SubForumContent1");
        tester.assertLabel("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:1:cell", "CategoryName1");
        tester.assertInvisible("form:content:edit:edit");
        tester.assertInvisible("form:content:edit:create");
        tester.assertInvisible("form:content:edit:save");
        tester.assertInvisible("form:content:edit:delete");
        tester.assertInvisible("form:content:edit:cancelEdit");
        // Click the category link
        tester.clickLink("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "CategoryName1");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "ForumThreadName1");
        tester.assertInvisible("form:content:edit:edit");
        tester.assertInvisible("form:content:edit:create");
        tester.assertInvisible("form:content:edit:save");
        tester.assertInvisible("form:content:edit:delete");
        tester.assertInvisible("form:content:edit:cancelEdit");
        // Click the thread link
        tester.clickLink("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "ForumThreadName1");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "PostContent1");
        tester.assertInvisible("form:content:edit:edit");
        tester.assertInvisible("form:content:edit:create");
        tester.assertInvisible("form:content:edit:save");
        tester.assertInvisible("form:content:edit:delete");
        tester.assertInvisible("form:content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForumAdmin() throws Exception {
        configureRealGame();

        setupForumStructure();

        fillForum();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("forum", "forum");

        tester.startPage(CmsPage.class);
        tester.assertLabel("form:content:table:rows:1:cells:1:cell:container:1", "SubForumName1");
        tester.assertLabel("form:content:table:rows:1:cells:1:cell:container:2", "SubForumContent1");
        tester.assertLabel("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:1:cell", "CategoryName1");
        tester.assertVisible("form:content:edit:edit");
        tester.assertInvisible("form:content:edit:create");
        tester.assertInvisible("form:content:edit:save");
        tester.assertInvisible("form:content:edit:delete");
        tester.assertInvisible("form:content:edit:cancelEdit");
        // Click the category link
        tester.clickLink("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "CategoryName1");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "ForumThreadName1");
        tester.assertVisible("form:content:edit:edit");
        tester.assertInvisible("form:content:edit:create");
        tester.assertInvisible("form:content:edit:save");
        tester.assertInvisible("form:content:edit:delete");
        tester.assertInvisible("form:content:edit:cancelEdit");
        // Click the thread link
        tester.clickLink("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "ForumThreadName1");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "PostContent1");
        tester.assertVisible("form:content:edit:edit");
        tester.assertInvisible("form:content:edit:create");
        tester.assertInvisible("form:content:edit:save");
        tester.assertInvisible("form:content:edit:delete");
        tester.assertInvisible("form:content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForumAdminSubForumEdit() throws Exception {
        configureRealGame();

        setupForumStructure();

        fillForum();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("forum", "forum");

        tester.startPage(CmsPage.class);
        tester.assertLabel("form:content:table:rows:1:cells:1:cell:container:1", "SubForumName1");
        tester.assertLabel("form:content:table:rows:1:cells:1:cell:container:2", "SubForumContent1");
        tester.assertLabel("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:1:cell", "CategoryName1");
        tester.assertVisible("form:content:edit:edit");
        tester.assertInvisible("form:content:edit:create");
        tester.assertInvisible("form:content:edit:save");
        tester.assertInvisible("form:content:edit:delete");
        tester.assertInvisible("form:content:edit:cancelEdit");
        // Click the Edit button
        FormTester formTester = tester.newFormTester("form");
        formTester.submit("content:edit:edit");
        tester.assertInvisible("form:content:edit:edit");
        tester.assertVisible("form:content:edit:create");
        tester.assertVisible("form:content:edit:save");
        tester.assertInvisible("form:content:edit:delete");
        tester.assertVisible("form:content:edit:cancelEdit");
        // Fill invalues and press save
        formTester = tester.newFormTester("form");
        formTester.setValue("content:table:rows:2:cells:1:cell:container:1:field", "SubForumName2");
        formTester.setValue("content:table:rows:2:cells:1:cell:container:2:field", "SubForumContent2");
        formTester.setValue("content:table:rows:2:cells:1:cell:container:3:table:rows:1:cells:1:cell:field", "CategoryName2");
        formTester.submit("content:edit:save");
        tester.newFormTester("form").submit("content:edit:cancelEdit");
        tester.assertVisible("form:content:edit:edit");
        tester.assertInvisible("form:content:edit:create");
        tester.assertInvisible("form:content:edit:save");
        tester.assertInvisible("form:content:edit:delete");
        tester.assertInvisible("form:content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.assertLabel("form:content:table:rows:1:cells:1:cell:container:1", "SubForumName2");
        tester.assertLabel("form:content:table:rows:1:cells:1:cell:container:2", "SubForumContent2");
        tester.assertLabel("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:1:cell", "CategoryName2");
        tester.assertInvisible("form:content:edit:edit");
        tester.assertInvisible("form:content:edit:create");
        tester.assertInvisible("form:content:edit:save");
        tester.assertInvisible("form:content:edit:delete");
        tester.assertInvisible("form:content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForumAdminCategoryEdit() throws Exception {
        configureRealGame();

        setupForumStructure();

        fillForum();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("forum", "forum");

        tester.startPage(CmsPage.class);
        // Click the Edit button
        tester.newFormTester("form").submit("content:edit:edit");
        // Click the category link
        tester.clickLink("form:content:table:rows:2:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        tester.assertInvisible("form:content:edit:edit");
        tester.assertInvisible("form:content:edit:create");
        tester.assertVisible("form:content:edit:save");
        tester.assertInvisible("form:content:edit:delete");
        tester.assertVisible("form:content:edit:cancelEdit");
        // Fill in values and press save
        FormTester formTester = tester.newFormTester("form");
        formTester.setValue("content:table:rows:1:cells:2:cell:field", "CategoryName2");
        formTester.setValue("content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell:field", "ForumThreadName2");
        formTester.submit("content:edit:save");
        tester.newFormTester("form").submit("content:edit:cancelEdit");
        tester.assertVisible("form:content:edit:edit");
        tester.assertInvisible("form:content:edit:create");
        tester.assertInvisible("form:content:edit:save");
        tester.assertInvisible("form:content:edit:delete");
        tester.assertInvisible("form:content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.clickLink("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "CategoryName2");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "ForumThreadName2");
        tester.assertInvisible("form:content:edit:edit");
        tester.assertInvisible("form:content:edit:create");
        tester.assertInvisible("form:content:edit:save");
        tester.assertInvisible("form:content:edit:delete");
        tester.assertInvisible("form:content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForumAdminCategoryEdit2() throws Exception {
        configureRealGame();

        setupForumStructure();

        fillForum();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("forum", "forum");

        tester.startPage(CmsPage.class);
        // Click the category link
        tester.clickLink("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        // Click the Edit button
        tester.newFormTester("form").submit("content:edit:edit");
        tester.assertInvisible("form:content:edit:edit");
        tester.assertInvisible("form:content:edit:create");
        tester.assertVisible("form:content:edit:save");
        tester.assertInvisible("form:content:edit:delete");
        tester.assertVisible("form:content:edit:cancelEdit");
        // Fill in values and press save
        FormTester formTester = tester.newFormTester("form");
        formTester.setValue("content:table:rows:3:cells:2:cell:field", "CategoryName2");
        formTester.setValue("content:table:rows:4:cells:2:cell:table:rows:1:cells:1:cell:field", "ForumThreadName2");
        formTester.submit("content:edit:save");
        tester.newFormTester("form").submit("content:edit:cancelEdit");
        tester.assertVisible("form:content:edit:edit");
        tester.assertInvisible("form:content:edit:create");
        tester.assertInvisible("form:content:edit:save");
        tester.assertInvisible("form:content:edit:delete");
        tester.assertInvisible("form:content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.clickLink("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "CategoryName2");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "ForumThreadName2");
        tester.assertInvisible("form:content:edit:edit");
        tester.assertInvisible("form:content:edit:create");
        tester.assertInvisible("form:content:edit:save");
        tester.assertInvisible("form:content:edit:delete");
        tester.assertInvisible("form:content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForumAdminThreadEdit2() throws Exception {
        configureRealGame();

        setupForumStructure();

        fillForum();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("forum", "forum");

        tester.startPage(CmsPage.class);
        // Click the category link
        tester.clickLink("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        // Click the thread link
        tester.clickLink("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:2:cell:link");
        // Click the Edit button
        tester.newFormTester("form").submit("content:edit:edit");
        tester.assertInvisible("form:content:edit:edit");
        tester.assertInvisible("form:content:edit:create");
        tester.assertVisible("form:content:edit:save");
        tester.assertInvisible("form:content:edit:delete");
        tester.assertVisible("form:content:edit:cancelEdit");
        // Fill in values and press save
        FormTester formTester = tester.newFormTester("form");
        //formTester.setValue("content:table:rows:3:cells:2:cell:textArea", "ForumThreadName5");
        formTester.setValue("content:table:rows:4:cells:2:cell:table:rows:1:cells:1:cell:textArea", "PostContent6");
        formTester.submit("content:edit:save");
        tester.newFormTester("form").submit("content:edit:cancelEdit");
        tester.assertVisible("form:content:edit:edit");
        tester.assertInvisible("form:content:edit:create");
        tester.assertInvisible("form:content:edit:save");
        tester.assertInvisible("form:content:edit:delete");
        tester.assertInvisible("form:content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.clickLink("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        tester.clickLink("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:2:cell:link");
        //tester.assertLabel("form:content:table:rows:1:cells:2:cell", "ForumThreadName5");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "PostContent6");
        tester.assertInvisible("form:content:edit:edit");
        tester.assertInvisible("form:content:edit:create");
        tester.assertInvisible("form:content:edit:save");
        tester.assertInvisible("form:content:edit:delete");
        tester.assertInvisible("form:content:edit:cancelEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForumCreateThreadInvisible() throws Exception {
        configureRealGame();

        setupForumStructure();

        fillForum();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        // Click the category link
        tester.clickLink("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        // Click the thread link
        tester.clickLink("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:2:cell:link");
        tester.assertInvisible("form:content:table:rows:2:cells:2:cell:edit:createEdit");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForumCreateThreadCancel() throws Exception {
        configureRealGame();

        setupForumStructure();

        fillForum();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("test", "test", "test", "");
        userService.login("test", "test");

        tester.startPage(CmsPage.class);
        // Click the category link
        tester.clickLink("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "CategoryName1");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "ForumThreadName1");
        // Click the thread link
        tester.assertVisible("form:content:table:rows:2:cells:2:cell:edit:createEdit");
        // Click the New Thread Button
        tester.newFormTester("form").submit("content:table:rows:2:cells:2:cell:edit:createEdit");
        FormTester formTester = tester.newFormTester("form");
        formTester.setValue("content:listView:0:content:field", "Title");
        formTester.setValue("content:listView:1:content:textArea", "Content Content");
        // Cancel -> back to page before
        formTester.submit("content:cancel");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "CategoryName1");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "ForumThreadName1");
        endHttpRequestAndOpenSessionInViewFilter();

        // Verify        
        tester.setupRequestAndResponse(); // Clears the attribute in the request
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(((WebRequest) RequestCycle.get().getRequest()).getHttpServletRequest().getAttribute(CmsUiServiceImpl.REQUEST_TMP_CREATE_BEAN_ATTRIBUTES));
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
        configureRealGame();

        setupForumStructure();

        fillForum();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("test", "test", "test", "");
        userService.login("test", "test");

        tester.startPage(CmsPage.class);
        // Click the category link
        tester.clickLink("form:content:table:rows:1:cells:1:cell:container:3:table:rows:1:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "CategoryName1");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "ForumThreadName1");
        // Click the thread link
        tester.assertVisible("form:content:table:rows:2:cells:2:cell:edit:createEdit");
        // Click the New Thread Button
        tester.newFormTester("form").submit("content:table:rows:2:cells:2:cell:edit:createEdit");
        FormTester formTester = tester.newFormTester("form");
        formTester.setValue("content:listView:0:content:field", "ForumThreadName2");
        formTester.setValue("content:listView:1:content:textArea", "Content Content");
        // Submit -> back to page before
        formTester.submit("content:submit");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "CategoryName1");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "ForumThreadName2");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:2:cells:1:cell", "ForumThreadName1");
        // Open Thread
        tester.clickLink("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "ForumThreadName2");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "Content Content");
        endHttpRequestAndOpenSessionInViewFilter();

        // Verify
        tester.setupRequestAndResponse(); // Clears the attribute in the request
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(((WebRequest) RequestCycle.get().getRequest()).getHttpServletRequest().getAttribute(CmsUiServiceImpl.REQUEST_TMP_CREATE_BEAN_ATTRIBUTES));
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
    public void testLevels() throws Exception {
        configureGameMultipleLevel();

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
        dbContentList.setContentProviderGetter("crudQuestHub");

        CrudListChildServiceHelper<DbContent> columnCrud = dbContentList.getColumnsCrud();
        DbExpressionProperty column = (DbExpressionProperty) columnCrud.createDbChild(DbExpressionProperty.class);
        column.setExpression("name");
        DbContentDetailLink detailLink = (DbContentDetailLink) columnCrud.createDbChild(DbContentDetailLink.class);
        detailLink.setName("Details");

        CrudChildServiceHelper<DbContentBook> contentBookCrud = dbContentList.getContentBookCrud();
        DbContentBook dbContentBook = contentBookCrud.createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.utg.DbQuestHub");
        CrudListChildServiceHelper<DbContentRow> rowCrud = dbContentBook.getRowCrud();

        DbContentRow dbQuestHubRow = rowCrud.createDbChild();
        DbExpressionProperty questHubNameProperty = new DbExpressionProperty();
        questHubNameProperty.setExpression("name");
        dbQuestHubRow.setDbContent(questHubNameProperty);
        questHubNameProperty.init(userService);
        questHubNameProperty.setParent(dbQuestHubRow);

        DbContentRow dbLevelRow = rowCrud.createDbChild();
        DbContentList levelContentList = new DbContentList();
        levelContentList.setRowsPerPage(5);
        dbLevelRow.setDbContent(levelContentList);
        levelContentList.init(userService);
        levelContentList.setParent(dbLevelRow);
        levelContentList.setContentProviderGetter("levelCrud");

        DbExpressionProperty expProperty = (DbExpressionProperty) levelContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        expProperty.setExpression("name");
        DbContentDetailLink dbContentDetailLink = (DbContentDetailLink) levelContentList.getColumnsCrud().createDbChild(DbContentDetailLink.class);
        dbContentDetailLink.setName("Details");
        dbContentBook = levelContentList.getContentBookCrud().createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.utg.DbLevel");
        rowCrud = dbContentBook.getRowCrud();

        DbContentRow dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Name");
        expProperty = new DbExpressionProperty();
        expProperty.setExpression("name");
        expProperty.setParent(dbContentRow);
        dbContentRow.setDbContent(expProperty);

        dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Description");
        expProperty = new DbExpressionProperty();
        expProperty.setParent(dbContentRow);
        expProperty.setExpression("html");
        expProperty.setEscapeMarkup(false);
        dbContentRow.setDbContent(expProperty);

        dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Allowed Items");
        DbContentList dbContentListItems = new DbContentList();
        dbContentListItems.setParent(dbContentRow);
        dbContentRow.setDbContent(dbContentListItems);
        dbContentListItems.init(userService);
        dbContentListItems.setContentProviderGetter("itemTypeLimitationCrud");
        columnCrud = dbContentListItems.getColumnsCrud();
        DbExpressionProperty name = (DbExpressionProperty) columnCrud.createDbChild(DbExpressionProperty.class);
        name.setExpression("dbBaseItemType.name");
        DbExpressionProperty count = (DbExpressionProperty) columnCrud.createDbChild(DbExpressionProperty.class);
        count.setExpression("count");
        DbExpressionProperty img = (DbExpressionProperty) columnCrud.createDbChild(DbExpressionProperty.class);
        img.setExpression("dbBaseItemType");

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
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertLabel("form:content:table:rows:1:cells:1:cell", "TEST_QUEST_HUB_1");
        tester.assertVisible("form:content:table:rows:1:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:2:cells:1:cell", "TEST_QUEST_HUB_2");
        tester.assertVisible("form:content:table:rows:2:cells:2:cell:link");
        // Click first link (RealGame Level)
        tester.clickLink("form:content:table:rows:2:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "TEST_QUEST_HUB_2");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "TEST_LEVEL_2_REAL");
        tester.assertVisible("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:2:cells:1:cell", "TEST_LEVEL_3_REAL");
        tester.assertVisible("form:content:table:rows:2:cells:2:cell:table:rows:2:cells:2:cell:link");
        // Click second link
        tester.clickLink("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "TEST_LEVEL_2_REAL");
        // Item limitations list
        // unpredictable order tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:1:cell", "TestAttackItem");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:2:cell", "10");
        tester.assertVisible("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:3:cell:image");

        // unpredictable order tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:2:cells:1:cell", "TEST_HARVESTER_ITEM");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:2:cells:2:cell", "10");
        tester.assertVisible("form:content:table:rows:3:cells:2:cell:table:rows:2:cells:3:cell:image");

        // unpredictable order tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:3:cells:1:cell", "TestContainerItem");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:3:cells:2:cell", "10");
        tester.assertVisible("form:content:table:rows:3:cells:2:cell:table:rows:3:cells:3:cell:image");

        // unpredictable order tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:3:cells:1:cell", "TestContainerItem");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:3:cells:2:cell", "10");
        tester.assertVisible("form:content:table:rows:3:cells:2:cell:table:rows:3:cells:3:cell:image");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Test that a hibernate proxy as a ContentBook also works
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Go to start again
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        DbLevel dbLevel = userGuidanceService.getDbLevel(); // User level is now in UserState (HTTP session)
        Assert.assertFalse(dbLevel.getParent() instanceof HibernateProxy);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbLevel = userGuidanceService.getDbLevel(); // User level is just be read from DB
        Assert.assertTrue(dbLevel.getParent() instanceof HibernateProxy);
        // Click first link (First Quest. This DBQuestHub is now in the Hibernate-Session as Hibernate-Proxy object)
        tester.clickLink("form:content:table:rows:1:cells:2:cell:link");
        tester.debugComponentTrees();
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "TEST_QUEST_HUB_1");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:1:cell", "TEST_LEVEL_1_SIMULATED");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testLevelsContentContainerExpression() throws Exception {
        configureGameMultipleLevel();

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
        dbContentList.setContentProviderGetter("crudQuestHub");

        CrudListChildServiceHelper<DbContent> columnCrud = dbContentList.getColumnsCrud();
        DbExpressionProperty column = (DbExpressionProperty) columnCrud.createDbChild(DbExpressionProperty.class);
        column.setExpression("name");
        DbContentDetailLink detailLink = (DbContentDetailLink) columnCrud.createDbChild(DbContentDetailLink.class);
        detailLink.setName("Details");

        CrudChildServiceHelper<DbContentBook> contentBookCrud = dbContentList.getContentBookCrud();
        DbContentBook dbContentBook = contentBookCrud.createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.utg.DbQuestHub");
        CrudListChildServiceHelper<DbContentRow> rowCrud = dbContentBook.getRowCrud();

        DbContentRow dbQuestHubRow = rowCrud.createDbChild();
        DbExpressionProperty questHubNameProperty = new DbExpressionProperty();
        questHubNameProperty.setExpression("name");
        dbQuestHubRow.setDbContent(questHubNameProperty);
        questHubNameProperty.init(userService);
        questHubNameProperty.setParent(dbQuestHubRow);

        DbContentRow dbLevelRow = rowCrud.createDbChild();
        DbContentList levelContentList = new DbContentList();
        levelContentList.setRowsPerPage(5);
        dbLevelRow.setDbContent(levelContentList);
        levelContentList.init(userService);
        levelContentList.setParent(dbLevelRow);
        levelContentList.setContentProviderGetter("levelCrud");

        DbExpressionProperty expProperty = (DbExpressionProperty) levelContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        expProperty.setExpression("name");
        DbContentDetailLink dbContentDetailLink = (DbContentDetailLink) levelContentList.getColumnsCrud().createDbChild(DbContentDetailLink.class);
        dbContentDetailLink.setName("Details");
        dbContentBook = levelContentList.getContentBookCrud().createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.utg.DbLevel");
        rowCrud = dbContentBook.getRowCrud();

        DbContentRow dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Name");
        expProperty = new DbExpressionProperty();
        expProperty.setExpression("name");
        expProperty.setParent(dbContentRow);
        dbContentRow.setDbContent(expProperty);

        dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Description");
        expProperty = new DbExpressionProperty();
        expProperty.setParent(dbContentRow);
        expProperty.setExpression("html");
        expProperty.setEscapeMarkup(false);
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
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertLabel("form:content:table:rows:1:cells:1:cell", "TEST_QUEST_HUB_1");
        tester.assertVisible("form:content:table:rows:1:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:2:cells:1:cell", "TEST_QUEST_HUB_2");
        tester.assertVisible("form:content:table:rows:2:cells:2:cell:link");
        // Click QuestHub
        tester.clickLink("form:content:table:rows:2:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "TEST_QUEST_HUB_2");
        tester.debugComponentTrees();
        // Click first Level
        tester.clickLink("form:content:table:rows:2:cells:2:cell:table:rows:1:cells:2:cell:link");
        // Item limitations list
        // unpredictable order tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:1:cell", "TestAttackItem");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:1:cell", "10");
        tester.assertVisible("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:2:cell:container:2:image");

        // unpredictable order tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:2:cells:1:cell", "TEST_HARVESTER_ITEM");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:2:cells:1:cell", "10");
        tester.assertVisible("form:content:table:rows:3:cells:2:cell:table:rows:2:cells:2:cell:container:2:image");

        // unpredictable order tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:3:cells:1:cell", "TestContainerItem");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:3:cells:1:cell", "10");
        tester.assertVisible("form:content:table:rows:3:cells:2:cell:table:rows:3:cells:2:cell:container:2:image");

        // unpredictable order tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:3:cells:1:cell", "TestContainerItem");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:4:cells:1:cell", "10");
        tester.assertVisible("form:content:table:rows:3:cells:2:cell:table:rows:4:cells:2:cell:container:2:image");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testItemTypes() throws Exception {
        configureRealGame();

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
        dbContentList.setSpringBeanName("itemService");
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
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertLabel("form:content:table:rows:5:cells:1:cell", "TestFactoryItem");
        tester.assertLabel("form:content:table:rows:5:cells:2:cell:link:label", "Details");
        tester.assertLabel("form:content:table:rows:8:cells:1:cell", "TestResourceItem");
        tester.assertLabel("form:content:table:rows:8:cells:2:cell:link:label", "Details");
        // Click link
        tester.clickLink("form:content:table:rows:5:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "TestFactoryItem");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell", "1000.0");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:1:cell", "TEST_HARVESTER_ITEM");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:2:cells:1:cell", "TestAttackItem");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:3:cells:1:cell", "TestContainerItem");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify if null property
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertLabel("form:content:table:rows:3:cells:1:cell", "TestAttackItem");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell:link:label", "Details");
        // Click link
        tester.clickLink("form:content:table:rows:3:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "TestAttackItem");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell", "-");
        // Go back to table and click the resource item type
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.debugComponentTrees();
        tester.clickLink("form:content:table:rows:8:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "TestResourceItem");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell", "3");

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testItemTypesColumnCountSingleCell() throws Exception {
        configureRealGame();

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
        dbContentList.setSpringBeanName("itemService");
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
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertLabel("form:content:table:rows:5:cells:1:cell", "TestFactoryItem");
        tester.assertLabel("form:content:table:rows:5:cells:2:cell:link:label", "Details");
        // Click link
        tester.clickLink("form:content:table:rows:5:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "TestFactoryItem");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell", "1000.0");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:1:cell", "TEST_HARVESTER_ITEM");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:2:cell", "TestAttackItem");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:3:cell", "TestContainerItem");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:4:cell", "");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:5:cell", "");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify if null property
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertLabel("form:content:table:rows:3:cells:1:cell", "TestAttackItem");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell:link:label", "Details");
        // Click link
        tester.clickLink("form:content:table:rows:3:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "TestAttackItem");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell", "-");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testItemTypeSectionLink() throws Exception {
        configureRealGame();

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
        dbContentList.setSpringBeanName("itemService");
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
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertLabel("form:content:table:rows:5:cells:1:cell", "TestFactoryItem");
        tester.assertLabel("form:content:table:rows:5:cells:2:cell:link:label", "Details");
        // Click link
        tester.clickLink("form:content:table:rows:5:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "TestFactoryItem");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell", "1000.0");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:1:cell:link:label", "TEST_HARVESTER_ITEM");
        PageParameters pageParameters = new PageParameters("page=1,sec=units");
        pageParameters.put("childId", 2);
        assertBookmarkablePageLink(tester, "form:content:table:rows:3:cells:2:cell:table:rows:1:cells:1:cell:link", CmsPage.class, pageParameters);

        tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:2:cell:link:label", "TestAttackItem");
        pageParameters = new PageParameters("page=1,sec=units");
        pageParameters.put("childId", 3);
        assertBookmarkablePageLink(tester, "form:content:table:rows:3:cells:2:cell:table:rows:1:cells:2:cell:link", CmsPage.class, pageParameters);

        tester.assertLabel("form:content:table:rows:3:cells:2:cell:table:rows:1:cells:3:cell:link:label", "TestContainerItem");
        pageParameters = new PageParameters("page=1,sec=units");
        pageParameters.put("childId", 4);
        assertBookmarkablePageLink(tester, "form:content:table:rows:3:cells:2:cell:table:rows:1:cells:3:cell:link", CmsPage.class, pageParameters);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify if null property
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertLabel("form:content:table:rows:3:cells:1:cell", "TestAttackItem");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell:link:label", "Details");
        // Click link
        tester.clickLink("form:content:table:rows:3:cells:2:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "TestAttackItem");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell", "-");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testLevelSectionLink() throws Exception {
        configureRealGame();

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

        DbContentList dbContentList = new DbContentList();
        dbContentList.init(userService);
        dbLevelPage.setContentAndAccessWrites(dbContentList);
        dbContentList.setSpringBeanName("userGuidanceService");
        dbContentList.setContentProviderGetter("crudQuestHub");
        dbLevelPage.setContentAndAccessWrites(dbContentList);

        CrudChildServiceHelper<DbContentBook> contentBookCrud = dbContentList.getContentBookCrud();
        DbContentBook dbContentBook = contentBookCrud.createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.utg.DbQuestHub");
        CrudListChildServiceHelper<DbContentRow> rowCrud = dbContentBook.getRowCrud();

        DbContentRow dbLevelRow = rowCrud.createDbChild();
        DbContentList levelContentList = new DbContentList();
        levelContentList.setRowsPerPage(5);
        dbLevelRow.setDbContent(levelContentList);
        levelContentList.init(userService);
        levelContentList.setParent(dbLevelRow);
        levelContentList.setContentProviderGetter("levelCrud");

        dbContentBook = levelContentList.getContentBookCrud().createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.utg.DbLevel");
        rowCrud = dbContentBook.getRowCrud();

        DbContentRow dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Name");
        DbExpressionProperty expProperty = new DbExpressionProperty();
        expProperty.setParent(dbContentRow);
        expProperty.setExpression("name");
        expProperty.setEscapeMarkup(false);
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
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertLabel("form:content", "-");
        // Enter game
        movableService.getRealGameInfo();
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertLabel("form:content:link:label", "TEST_LEVEL_2_REAL");
        // Click Link
        tester.clickLink("form:content:link");
        tester.assertLabel("form:content:table:rows:1:cells:1:cell", "Name");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "TEST_LEVEL_2_REAL");

        tester.debugComponentTrees();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testTaskLevelSectionLink() throws Exception {
        configureGameMultipleLevel();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();

        // Setup Home
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        DbContentList taskList = new DbContentList();
        taskList.init(userService);
        dbPage.setContentAndAccessWrites(taskList);
        taskList.setSpringBeanName("userGuidanceService");
        taskList.setContentProviderGetter("mercenaryMissionCms");
        DbExpressionProperty dbExpressionProperty = (DbExpressionProperty) taskList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        dbExpressionProperty.setExpression("dbLevelTask.name");
        dbExpressionProperty.setLink(true);
        pageCrud.updateDbChild(dbPage);

        // Setup the level page
        DbPage dbLevelPage = pageCrud.createDbChild();
        dbLevelPage.setName("Level");

        DbContentList dbContentList = new DbContentList();
        dbContentList.init(userService);
        dbLevelPage.setContentAndAccessWrites(dbContentList);
        dbContentList.setSpringBeanName("userGuidanceService");
        dbContentList.setContentProviderGetter("crudQuestHub");
        dbLevelPage.setContentAndAccessWrites(dbContentList);

        CrudChildServiceHelper<DbContentBook> contentBookCrud = dbContentList.getContentBookCrud();
        DbContentBook dbContentBook = contentBookCrud.createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.utg.DbQuestHub");
        CrudListChildServiceHelper<DbContentRow> rowCrud = dbContentBook.getRowCrud();

        DbContentRow dbLevelRow = rowCrud.createDbChild();
        DbContentList levelContentList = new DbContentList();
        levelContentList.setRowsPerPage(5);
        dbLevelRow.setDbContent(levelContentList);
        levelContentList.init(userService);
        levelContentList.setParent(dbLevelRow);
        levelContentList.setContentProviderGetter("levelCrud");

        dbContentBook = levelContentList.getContentBookCrud().createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.utg.DbLevel");
        rowCrud = dbContentBook.getRowCrud();

        DbContentRow dbTaskRow = rowCrud.createDbChild();
        DbContentList taskContentList = new DbContentList();
        dbTaskRow.setDbContent(taskContentList);
        taskContentList.init(userService);
        taskContentList.setParent(dbTaskRow);
        taskContentList.setContentProviderGetter("levelTaskCrud");

        dbContentBook = taskContentList.getContentBookCrud().createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.utg.DbLevelTask");
        rowCrud = dbContentBook.getRowCrud();

        DbContentRow dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Name");
        DbExpressionProperty expProperty = new DbExpressionProperty();
        expProperty.setParent(dbContentRow);
        expProperty.setExpression("name");
        expProperty.setEscapeMarkup(false);
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
        userGuidanceService.getDbLevel(); // set level for new user
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertLabel("form:content:table:rows:1:cells:1:cell:link:label", "TEST_LEVEL_TASK_1_SIMULATED_NAME");
        // Click Link
        tester.clickLink("form:content:table:rows:1:cells:1:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:1:cell", "Name");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "TEST_LEVEL_TASK_1_SIMULATED_NAME");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testStartLevelTaskButton() throws Exception {
        configureGameMultipleLevel();

        // Add cms image
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbCmsImage> crud = cmsService.getImageCrudRootServiceHelper();
        DbCmsImage dbCmsImage1 = crud.createDbChild();
        dbCmsImage1.setData(new byte[50000]);
        dbCmsImage1.setContentType("image1");
        crud.updateDbChild(dbCmsImage1);
        DbCmsImage dbCmsImage2 = crud.createDbChild();
        dbCmsImage2.setData(new byte[10000]);
        dbCmsImage2.setContentType("image2");
        crud.updateDbChild(dbCmsImage2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        DbContentList taskList = new DbContentList();
        taskList.init(userService);
        dbPage.setContentAndAccessWrites(taskList);
        taskList.setSpringBeanName("userGuidanceService");
        taskList.setContentProviderGetter("mercenaryMissionCms");
        DbExpressionProperty dbExpressionProperty = (DbExpressionProperty) taskList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        dbExpressionProperty.setExpression("dbLevelTask.name");
        DbContentStartLevelTaskButton taskButton = (DbContentStartLevelTaskButton) taskList.getColumnsCrud().createDbChild(DbContentStartLevelTaskButton.class);
        taskButton.setExpression("dbLevelTask");
        taskButton.setDoneExpression("done");
        taskButton.setStartImage(dbCmsImage1);
        taskButton.setDoneImage(dbCmsImage2);
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
        userGuidanceService.getDbLevel(); // set level for new user
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertLabel("form:content:table:rows:1:cells:1:cell", "TEST_LEVEL_TASK_1_SIMULATED_NAME");
        tester.assertVisible("form:content:table:rows:1:cells:2:cell:link:linkImage");
        tester.assertBookmarkablePageLink("form:content:table:rows:1:cells:2:cell:link", Game.class, "taskId = 1");
        tester.assertInvisible("form:content:table:rows:1:cells:2:cell:doneImage");
        // go to level 3
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_3_REAL_ID);
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertVisible("form:content:table:rows:1:cells:2:cell:link:linkImage");
        tester.assertBookmarkablePageLink("form:content:table:rows:1:cells:2:cell:link", Game.class, "taskId = " + TEST_LEVEL_TASK_3_3_SIM_ID);
        tester.assertInvisible("form:content:table:rows:1:cells:2:cell:doneImage");
        tester.assertVisible("form:content:table:rows:2:cells:2:cell:link:linkImage");
        tester.assertBookmarkablePageLink("form:content:table:rows:2:cells:2:cell:link", Game.class, "taskId = " + TEST_LEVEL_TASK_4_3_SIM_ID);
        tester.assertInvisible("form:content:table:rows:2:cells:2:cell:doneImage");
        // Click first level task
        tester.clickLink("form:content:table:rows:1:cells:2:cell:link");
        tester.assertRenderedPage(Game.class);
        tester.debugComponentTrees();
        // Finish first tutorial
        userGuidanceService.onTutorialFinished(TEST_LEVEL_TASK_3_3_SIM_ID);
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertInvisible("form:content:table:rows:1:cells:2:cell:link");
        tester.assertVisible("form:content:table:rows:1:cells:2:cell:doneImage");
        tester.assertVisible("form:content:table:rows:2:cells:2:cell:link:linkImage");
        tester.assertBookmarkablePageLink("form:content:table:rows:2:cells:2:cell:link", Game.class, "taskId = " + TEST_LEVEL_TASK_4_3_SIM_ID);
        tester.assertInvisible("form:content:table:rows:2:cells:2:cell:doneImage");
        // Finish second tutorial
        userGuidanceService.onTutorialFinished(TEST_LEVEL_TASK_4_3_SIM_ID);
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertInvisible("form:content:table:rows:1:cells:2:cell:link");
        tester.assertVisible("form:content:table:rows:1:cells:2:cell:doneImage");
        tester.assertInvisible("form:content:table:rows:2:cells:2:cell:link");
        tester.assertVisible("form:content:table:rows:2:cells:2:cell:doneImage");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testItemTypeNavigation() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Remove resource
        itemService.getDbItemTypeCrud().deleteDbChild(itemService.getDbItemTypeCrud().readDbChild(TEST_RESOURCE_ITEM_ID));
        itemService.activate();
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
        dbContentList.setRowsPerPage(5);
        dbContentList.init(userService);
        dbPage.setContentAndAccessWrites(dbContentList);
        dbContentList.setSpringBeanName("itemService");
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
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        // Click link
        tester.clickLink("form:content:table:rows:1:cells:2:cell:link");
        tester.assertLabel("form:content:navigation:previousLink:previousLabel", "previous");
        tester.assertDisabled("form:content:navigation:previousLink:previousLabel");
        tester.assertDisabled("form:content:navigation:previousLink");

        tester.assertLabel("form:content:navigation:upLink:upLabel", "up");
        tester.assertEnabled("form:content:navigation:upLink:upLabel");
        tester.assertEnabled("form:content:navigation:upLink");
        tester.assertBookmarkablePageLink("form:content:navigation:upLink", CmsPage.class, "page = 1");

        tester.assertLabel("form:content:navigation:nextLink:nextLabel", "next");
        tester.assertEnabled("form:content:navigation:nextLink:nextLabel");
        tester.assertEnabled("form:content:navigation:nextLink");
        PageParameters pageParameters = new PageParameters("page=1");
        pageParameters.put("childId", 2);
        pageParameters.put("detailId", "1");
        assertBookmarkablePageLink(tester, "form:content:navigation:nextLink", CmsPage.class, pageParameters);

        // Click next
        tester.clickLink("form:content:navigation:nextLink");
        tester.assertLabel("form:content:navigation:previousLink:previousLabel", "previous");
        tester.assertEnabled("form:content:navigation:previousLink:previousLabel");
        tester.assertEnabled("form:content:navigation:previousLink");
        pageParameters = new PageParameters("page=1");
        pageParameters.put("childId", 1);
        pageParameters.put("detailId", "1");
        assertBookmarkablePageLink(tester, "form:content:navigation:previousLink", CmsPage.class, pageParameters);

        tester.assertLabel("form:content:navigation:upLink:upLabel", "up");
        tester.assertEnabled("form:content:navigation:upLink:upLabel");
        tester.assertEnabled("form:content:navigation:upLink");
        tester.assertBookmarkablePageLink("form:content:navigation:upLink", CmsPage.class, "page = 1");

        tester.assertLabel("form:content:navigation:nextLink:nextLabel", "next");
        tester.assertEnabled("form:content:navigation:nextLink:nextLabel");
        tester.assertEnabled("form:content:navigation:nextLink");
        pageParameters = new PageParameters("page=1");
        pageParameters.put("childId", 3);
        pageParameters.put("detailId", "1");
        assertBookmarkablePageLink(tester, "form:content:navigation:nextLink", CmsPage.class, pageParameters);

        // Click next (go to the last)
        tester.clickLink("form:content:navigation:nextLink");
        tester.clickLink("form:content:navigation:nextLink");
        tester.clickLink("form:content:navigation:nextLink");
        tester.clickLink("form:content:navigation:nextLink");
        tester.clickLink("form:content:navigation:nextLink");
        tester.assertLabel("form:content:navigation:previousLink:previousLabel", "previous");
        tester.assertEnabled("form:content:navigation:previousLink:previousLabel");
        tester.assertEnabled("form:content:navigation:previousLink");
        pageParameters = new PageParameters("page=1");
        pageParameters.put("childId", 6);
        pageParameters.put("detailId", "1");
        assertBookmarkablePageLink(tester, "form:content:navigation:previousLink", CmsPage.class, pageParameters);

        tester.assertLabel("form:content:navigation:upLink:upLabel", "up");
        tester.assertEnabled("form:content:navigation:upLink:upLabel");
        tester.assertEnabled("form:content:navigation:upLink");
        tester.assertBookmarkablePageLink("form:content:navigation:upLink", CmsPage.class, "page = 1");

        tester.assertLabel("form:content:navigation:nextLink:nextLabel", "next");
        tester.assertDisabled("form:content:navigation:nextLink:nextLabel");
        tester.assertDisabled("form:content:navigation:nextLink");

        // Click pref
        tester.clickLink("form:content:navigation:previousLink");
        tester.assertLabel("form:content:navigation:previousLink:previousLabel", "previous");
        tester.assertEnabled("form:content:navigation:previousLink:previousLabel");
        tester.assertEnabled("form:content:navigation:previousLink");
        pageParameters = new PageParameters("page=1");
        pageParameters.put("childId", 5);
        pageParameters.put("detailId", "1");
        assertBookmarkablePageLink(tester, "form:content:navigation:previousLink", CmsPage.class, pageParameters);

        tester.assertLabel("form:content:navigation:upLink:upLabel", "up");
        tester.assertEnabled("form:content:navigation:upLink:upLabel");
        tester.assertEnabled("form:content:navigation:upLink");
        tester.assertBookmarkablePageLink("form:content:navigation:upLink", CmsPage.class, "page = 1");

        tester.assertLabel("form:content:navigation:nextLink:nextLabel", "next");
        tester.assertEnabled("form:content:navigation:nextLink:nextLabel");
        tester.assertEnabled("form:content:navigation:nextLink");
        pageParameters = new PageParameters("page=1");
        pageParameters.put("childId", 7);
        pageParameters.put("detailId", "1");
        assertBookmarkablePageLink(tester, "form:content:navigation:nextLink", CmsPage.class, pageParameters);

        // Click up
        tester.clickLink("form:content:navigation:upLink");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }


    @Test
    @DirtiesContext
    public void testPageLinkText() throws Exception {
        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");

        DbContentPageLink dbContentPageLink = new DbContentPageLink();
        dbContentPageLink.setName("PAGE LINK");
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
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertLabel("form:content:link:label", "PAGE LINK");
        tester.assertInvisible("form:content:link:image");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testPageLinkImage() throws Exception {
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
        dbContentPageLink.setName("PAGE LINK");
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
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertInvisible("form:content:link:label");
        tester.assertVisible("form:content:link:image");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void prepare4RegisterCheck() throws Exception {
        configureRealGame();

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

    @Test
    @DirtiesContext
    public void testRegister() throws Exception {
        SecurityCmsUiService securityCmsUiServiceMock = EasyMock.createMock(SecurityCmsUiService.class);
        securityCmsUiServiceMock.signIn("U1", "test");
        EasyMock.replay(securityCmsUiServiceMock);
        ReflectionTestUtils.setField(cmsUiService, "securityCmsUiService", securityCmsUiServiceMock);

        prepare4RegisterCheck();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertVisible("form:content:newUserForm:name");
        tester.assertVisible("form:content:newUserForm:email");
        tester.assertVisible("form:content:newUserForm:password");
        tester.assertVisible("form:content:newUserForm:confirmPassword");
        FormTester formTester = tester.newFormTester("form");
        formTester.setValue("content:newUserForm:name", "U1");
        formTester.setValue("content:newUserForm:email", "u1email");
        formTester.setValue("content:newUserForm:password", "test");
        formTester.setValue("content:newUserForm:confirmPassword", "test");
        formTester.submit();
        tester.assertRenderedPage(CmsPage.class);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testRegisterFailAlreadyLoggedInException() throws Exception {
        SecurityCmsUiService securityCmsUiServiceMock = EasyMock.createMock(SecurityCmsUiService.class);
        securityCmsUiServiceMock.signIn("U1", "test");
        User user = new User();
        user.registerUser("TestUser", "", "");
        EasyMock.expectLastCall().andThrow(new AlreadyLoggedInException(user));
        EasyMock.replay(securityCmsUiServiceMock);

        ReflectionTestUtils.setField(cmsUiService, "securityCmsUiService", securityCmsUiServiceMock);

        prepare4RegisterCheck();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertVisible("form:content:newUserForm:name");
        tester.assertVisible("form:content:newUserForm:email");
        tester.assertVisible("form:content:newUserForm:password");
        tester.assertVisible("form:content:newUserForm:confirmPassword");
        FormTester formTester = tester.newFormTester("form");
        formTester.setValue("content:newUserForm:name", "U1");
        formTester.setValue("content:newUserForm:email", "u1email");
        formTester.setValue("content:newUserForm:password", "test");
        formTester.setValue("content:newUserForm:confirmPassword", "test");
        formTester.submit();
        tester.assertRenderedPage(CmsPage.class);
        tester.assertLabel("form:content:message", "Already logged in as: TestUser");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testRegisterFailUserAlreadyExistsException() throws Exception {
        SecurityCmsUiService securityCmsUiServiceMock = EasyMock.createMock(SecurityCmsUiService.class);
        securityCmsUiServiceMock.signIn("U1", "test");
        User user = new User();
        user.registerUser("TestUser", "", "");
        EasyMock.expectLastCall().andThrow(new UserAlreadyExistsException());
        EasyMock.replay(securityCmsUiServiceMock);

        ReflectionTestUtils.setField(cmsUiService, "securityCmsUiService", securityCmsUiServiceMock);

        prepare4RegisterCheck();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertVisible("form:content:newUserForm:name");
        tester.assertVisible("form:content:newUserForm:email");
        tester.assertVisible("form:content:newUserForm:password");
        tester.assertVisible("form:content:newUserForm:confirmPassword");
        FormTester formTester = tester.newFormTester("form");
        formTester.setValue("content:newUserForm:name", "U1");
        formTester.setValue("content:newUserForm:email", "u1email");
        formTester.setValue("content:newUserForm:password", "test");
        formTester.setValue("content:newUserForm:confirmPassword", "test");
        formTester.submit();
        tester.assertRenderedPage(CmsPage.class);
        tester.assertLabel("form:content:message", "The user already exists");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testRegisterFailPasswordNotMatchException() throws Exception {
        SecurityCmsUiService securityCmsUiServiceMock = EasyMock.createMock(SecurityCmsUiService.class);
        securityCmsUiServiceMock.signIn("U1", "test");
        User user = new User();
        user.registerUser("TestUser", "", "");
        EasyMock.expectLastCall().andThrow(new PasswordNotMatchException());
        EasyMock.replay(securityCmsUiServiceMock);

        ReflectionTestUtils.setField(cmsUiService, "securityCmsUiService", securityCmsUiServiceMock);

        prepare4RegisterCheck();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertVisible("form:content:newUserForm:name");
        tester.assertVisible("form:content:newUserForm:email");
        tester.assertVisible("form:content:newUserForm:password");
        tester.assertVisible("form:content:newUserForm:confirmPassword");
        FormTester formTester = tester.newFormTester("form");
        formTester.setValue("content:newUserForm:name", "U1");
        formTester.setValue("content:newUserForm:email", "u1email");
        formTester.setValue("content:newUserForm:password", "test");
        formTester.setValue("content:newUserForm:confirmPassword", "test");
        formTester.submit();
        tester.assertRenderedPage(CmsPage.class);
        tester.assertLabel("form:content:message", "Password and confirm password do not match");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testCmsStringGenerator() throws Exception {
        Assert.assertEquals("Hallo 0", CmsStringGenerator.createNumberString(0, "Hallo 0", "Hallo 1", "Hallo $"));
        Assert.assertEquals("Hallo 1", CmsStringGenerator.createNumberString(1, "Hallo 0", "Hallo 1", "Hallo $"));
        Assert.assertEquals("Hallo 2", CmsStringGenerator.createNumberString(2, "Hallo 0", "Hallo 1", "Hallo $"));
        Assert.assertEquals("Hallo 5", CmsStringGenerator.createNumberString(5, "Hallo 0", "Hallo 1", "Hallo $"));
        Assert.assertEquals("Hallo 15", CmsStringGenerator.createNumberString(15, "Hallo 0", "Hallo 1", "Hallo $"));
    }

    @Test
    @DirtiesContext
    public void testGetValue() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("test", "test", "test", "");
        userService.login("test", "test");

        Assert.assertEquals(0, cmsUiService.getValue("messengerService", "unreadMails"));
        // Add mail
        messengerService.sendMail("test", "subject", "body");
        Assert.assertEquals(1, cmsUiService.getValue("messengerService", "unreadMails"));

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSmartPageLink() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        DbPage dbPage2 = pageCrud.createDbChild();
        dbPage2.setName("Page 2");
        DbContentStaticHtml dbContentStaticHtml = new DbContentStaticHtml();
        dbContentStaticHtml.setHtml("This is page two");
        dbPage2.setContentAndAccessWrites(dbContentStaticHtml);

        // Smart link
        DbContentSmartPageLink smartPageLink = new DbContentSmartPageLink();
        dbPage.setContentAndAccessWrites(smartPageLink);
        smartPageLink.init(userService);
        smartPageLink.setAccessDeniedString("No access");
        smartPageLink.setButtonName("Button Name");
        smartPageLink.setDbPage(dbPage2);
        smartPageLink.setEnableAccess(DbContent.Access.REGISTERED_USER);
        smartPageLink.setSpringBeanName("messengerService");
        smartPageLink.setPropertyExpression("unreadMails");
        smartPageLink.setString0("Nothing");
        smartPageLink.setString1("Single");
        smartPageLink.setStringN("Multi $");
        pageCrud.updateDbChild(dbPage);
        pageCrud.updateDbChild(dbPage2);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify not logged in
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.assertLabel("form:content:label", "No access");
        tester.assertDisabled("form:content:button");
        Button button = (Button) tester.getComponentFromLastRenderedPage("form:content:button");
        Assert.assertEquals("Button Name", button.getValue());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify logged in 0 mail
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("test", "test", "test", "");
        userService.login("test", "test");
        tester.startPage(CmsPage.class);
        tester.assertLabel("form:content:label", "Nothing");
        tester.assertEnabled("form:content:button");
        button = (Button) tester.getComponentFromLastRenderedPage("form:content:button");
        Assert.assertEquals("Button Name", button.getValue());
        // Click mail button
        tester.newFormTester("form").submit("content:button");
        tester.assertLabel("form:content", "This is page two");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify logged in 1 mail
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("test", "test");
        messengerService.sendMail("test", "subject", "body");
        tester.startPage(CmsPage.class);
        tester.assertLabel("form:content:label", "Single");
        tester.assertEnabled("form:content:button");
        button = (Button) tester.getComponentFromLastRenderedPage("form:content:button");
        Assert.assertEquals("Button Name", button.getValue());
        // Click mail button
        tester.newFormTester("form").submit("content:button");
        tester.assertLabel("form:content", "This is page two");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify logged in 2 mails
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("test", "test");
        messengerService.sendMail("test", "subject", "body");
        tester.startPage(CmsPage.class);
        tester.assertLabel("form:content:label", "Multi 2");
        tester.assertEnabled("form:content:button");
        button = (Button) tester.getComponentFromLastRenderedPage("form:content:button");
        Assert.assertEquals("Button Name", button.getValue());
        // Click mail button
        tester.newFormTester("form").submit("content:button");
        tester.assertLabel("form:content", "This is page two");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void createUserAndSendEmails() throws UserAlreadyExistsException, PasswordNotMatchException, InvalidFieldException {
        // Fill mails
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "");
        userService.login("U1", "test");
        userService.logout();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U2", "test", "test", "");
        userService.login("U2", "test");
        userService.logout();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Send mail 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        messengerService.sendMail("U2", "subject1", "body1");
        userService.logout();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Send mail 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U2", "test");
        messengerService.sendMail("U1", "subject2", "body2");
        userService.logout();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Send mail 3
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        messengerService.sendMail("U2", "subject3", "body3");
        userService.logout();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Send mail 4
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U2", "test");
        messengerService.sendMail("U1", "subject4", "body4");
        userService.logout();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Send mail 5
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        messengerService.sendMail("U2", "subject5", "body5");
        userService.logout();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Send mail 6
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U2", "test");
        messengerService.sendMail("U1", "subject6", "body6");
        userService.logout();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testMail() throws Exception {
        configureRealGame();
        // Add cms image
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbCmsImage> crud = cmsService.getImageCrudRootServiceHelper();
        DbCmsImage dbCmsImage1 = crud.createDbChild();
        dbCmsImage1.setData(new byte[50000]);
        dbCmsImage1.setContentType("image1");
        crud.updateDbChild(dbCmsImage1);
        DbCmsImage dbCmsImage2 = crud.createDbChild();
        dbCmsImage2.setData(new byte[10000]);
        dbCmsImage2.setContentType("image2");
        crud.updateDbChild(dbCmsImage2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");

        // Mail List
        DbContentContainer dbContentContainer = new DbContentContainer();
        dbPage.setContentAndAccessWrites(dbContentContainer);
        dbContentContainer.init(userService);
        DbContentList mailList = (DbContentList) dbContentContainer.getContentCrud().createDbChild(DbContentList.class);
        mailList.setSpringBeanName("messengerService");
        mailList.setContentProviderGetter("userMailCrud");
        DbContentBooleanExpressionImage readImage = (DbContentBooleanExpressionImage) mailList.getColumnsCrud().createDbChild(DbContentBooleanExpressionImage.class);
        readImage.setExpression("read");
        readImage.setTrueImage(dbCmsImage1);
        readImage.setFalseImage(dbCmsImage2);
        DbExpressionProperty date = (DbExpressionProperty) mailList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        date.setExpression("sent");
        date.setOptionalType(DbExpressionProperty.Type.DATE_DDMMYYYY_HH_MM_SS);
        DbExpressionProperty from = (DbExpressionProperty) mailList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        from.setExpression("fromUser");
        DbExpressionProperty subject = (DbExpressionProperty) mailList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        subject.setExpression("subject");
        DbContentDetailLink details = (DbContentDetailLink) mailList.getColumnsCrud().createDbChild(DbContentDetailLink.class);
        details.setName("read");

        DbContentBook dbContentBook = mailList.getContentBookCrud().createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.messenger.DbMail");
        dbContentBook.setHiddenMethodName("setMailRead");
        DbContentRow dbContentRow = dbContentBook.getRowCrud().createDbChild();
        DbExpressionProperty mailSubject = new DbExpressionProperty();
        mailSubject.init(userService);
        mailSubject.setParent(dbContentRow);
        dbContentRow.setDbContent(mailSubject);
        mailSubject.setExpression("subject");

        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        createUserAndSendEmails();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        tester.startPage(CmsPage.class);
        tester.assertLabel("form:content:container:1:table:rows:1:cells:4:cell", "subject6");
        tester.assertLabel("form:content:container:1:table:rows:1:cells:3:cell", "U2");
        tester.assertLabel("form:content:container:1:table:rows:2:cells:4:cell", "subject4");
        tester.assertLabel("form:content:container:1:table:rows:2:cells:3:cell", "U2");
        tester.assertLabel("form:content:container:1:table:rows:3:cells:4:cell", "subject2");
        tester.assertLabel("form:content:container:1:table:rows:3:cells:3:cell", "U2");
        Assert.assertFalse(messengerService.getMails().get(0).isRead());
        // click the read more link
        tester.clickLink("form:content:container:1:table:rows:1:cells:5:cell:link");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "subject6");
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue(messengerService.getMails().get(0).isRead());
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }

    private void setupNewMailTest() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        DbPage dbMessagePage = pageCrud.createDbChild();
        dbMessagePage.setPredefinedType(CmsUtil.CmsPredefinedPage.MESSAGE);
        dbMessagePage.setName("Message Page");

        // Mail List
        DbContentContainer dbContentContainer = new DbContentContainer();
        dbPage.setContentAndAccessWrites(dbContentContainer);
        dbContentContainer.init(userService);
        DbContentInvokerButton newMailButton = (DbContentInvokerButton) dbContentContainer.getContentCrud().createDbChild(DbContentInvokerButton.class);
        newMailButton.setName("New Mail");
        DbContentInvoker dbContentInvoker = new DbContentInvoker();
        newMailButton.setDbContentInvoker(dbContentInvoker);
        dbContentInvoker.setParent(newMailButton);
        dbContentInvoker.init(userService);
        dbContentInvoker.setSpringBeanName("messengerService");
        dbContentInvoker.setMethodName("sendMail");
        DbExpressionProperty to = dbContentInvoker.getValueCrud().createDbChild();
        to.setName("To");
        to.setExpression("toUser");
        DbExpressionProperty subject = dbContentInvoker.getValueCrud().createDbChild();
        subject.setName("subject");
        subject.setExpression("subject");
        DbExpressionProperty body = dbContentInvoker.getValueCrud().createDbChild();
        body.setName("Message");
        body.setEscapeMarkup(false);
        body.setExpression("body");

        pageCrud.updateDbChild(dbPage);
        pageCrud.updateDbChild(dbMessagePage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testNewMail() throws Exception {
        setupNewMailTest();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U2", "test", "test", "");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "");
        userService.login("U1", "test");
        tester.startPage(CmsPage.class);
        tester.assertVisible("form:content:container:1:button");
        tester.newFormTester("form").submit("content:container:1:button");
        tester.assertVisible("form:content:listView:0:field");
        tester.assertVisible("form:content:listView:1:field");
        tester.assertVisible("form:content:listView:2:textArea");
        FormTester formTester = tester.newFormTester("form");
        formTester.setValue("content:listView:0:field", "U2");
        formTester.setValue("content:listView:1:field", "subject2");
        formTester.setValue("content:listView:2:textArea", "message message");
        formTester.submit("content:invoke");
        tester.assertVisible("form:content:container:1:button");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify U2 got mail
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U2", "test");
        Assert.assertEquals(1, messengerService.getMails().size());
        Assert.assertEquals("U1", messengerService.getMails().get(0).getFromUser());
        Assert.assertEquals("subject2", messengerService.getMails().get(0).getSubject());
        Assert.assertEquals("message message", messengerService.getMails().get(0).getBody());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testNewMailFailWrongUser() throws Exception {
        setupNewMailTest();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "");
        userService.login("U1", "test");
        tester.startPage(CmsPage.class);
        tester.assertVisible("form:content:container:1:button");
        tester.newFormTester("form").submit("content:container:1:button");
        tester.assertVisible("form:content:listView:0:field");
        tester.assertVisible("form:content:listView:1:field");
        tester.assertVisible("form:content:listView:2:textArea");
        FormTester formTester = tester.newFormTester("form");
        formTester.setValue("content:listView:0:field", "U5");
        formTester.setValue("content:listView:1:field", "subject2");
        formTester.setValue("content:listView:2:textArea", "message message");
        formTester.submit("content:invoke");
        tester.assertLabel("form:content:message", "Unknown user: U5");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testNewMailPressCancel() throws Exception {
        setupNewMailTest();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "");
        userService.login("U1", "test");
        tester.startPage(CmsPage.class);
        tester.assertVisible("form:content:container:1:button");
        tester.newFormTester("form").submit("content:container:1:button");
        tester.assertVisible("form:content:listView:0:field");
        tester.assertVisible("form:content:listView:1:field");
        tester.assertVisible("form:content:listView:2:textArea");
        FormTester formTester = tester.newFormTester("form");
        formTester.setValue("content:listView:0:field", "U5");
        formTester.setValue("content:listView:1:field", "subject2");
        formTester.setValue("content:listView:2:textArea", "message message");
        formTester.submit("content:cancel");
        tester.assertVisible("form:content:container:1:button");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testAds() throws Exception {
        configureRealGame();

        // Setup ads
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbAds dbAds = cmsService.getAdsCrud().createDbChild();
        dbAds.setActive(true);
        dbAds.setCode("THIS IS THE CODE");
        cmsService.getAdsCrud().updateDbChild(dbAds);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setName("Home");
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setAdsVisible(true);
        DbContentStaticHtml dbContentStaticHtml = new DbContentStaticHtml();
        dbContentStaticHtml.setHtml("This is a page");
        dbPage.setContentAndAccessWrites(dbContentStaticHtml);

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
        tester.startPage(CmsPage.class);
        tester.assertLabel("form:content", "This is a page");
        tester.assertLabel("contentRight:label", "THIS IS THE CODE");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testGameLinkText() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setName("Home");
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setAdsVisible(true);
        DbContentGameLink gameLink = new DbContentGameLink();
        gameLink.setLinkText("GAME LINK");
        dbPage.setContentAndAccessWrites(gameLink);

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
        tester.startPage(CmsPage.class);
        tester.assertLabel("form:content:link:label", "GAME LINK");
        tester.assertInvisible("form:content:link:image");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testGameLinkImage() throws Exception {
        configureRealGame();

        // Setup image
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbCmsImage> crud = cmsService.getImageCrudRootServiceHelper();
        DbCmsImage dbCmsImage = crud.createDbChild();
        dbCmsImage.setData(new byte[50000]);
        dbCmsImage.setContentType("image");
        crud.updateDbChild(dbCmsImage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setName("Home");
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setAdsVisible(true);
        DbContentGameLink gameLink = new DbContentGameLink();
        gameLink.setDbCmsImage(dbCmsImage);
        dbPage.setContentAndAccessWrites(gameLink);

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
        tester.startPage(CmsPage.class);
        tester.assertVisible("form:content:link:image");
        tester.assertInvisible("form:content:link:label");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testExpressionProperty() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setName("Home");
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setAdsVisible(true);
        DbContentContainer dbContentContainer = new DbContentContainer();
        dbPage.setContentAndAccessWrites(dbContentContainer);
        dbContentContainer.init(userService);

        DbExpressionProperty level = (DbExpressionProperty) dbContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        level.setSpringBeanName("userGuidanceService");
        level.setExpression("dbLevelCms.name");

        DbExpressionProperty xp = (DbExpressionProperty) dbContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        xp.setSpringBeanName("userService");
        xp.setExpression("userState.xp");

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
        // Set Level and XP
        movableService.getRealGameInfo(); // Connection is created here. Don't call movableService.getGameInfo() again!

        tester.startPage(CmsPage.class);
        tester.assertLabel("form:content:container:1", "TEST_LEVEL_2_REAL");
        tester.assertLabel("form:content:container:2", "0");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testUrlGenerating() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        pageCrud.updateDbChild(dbPage);

        dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.MESSAGE);
        pageCrud.updateDbChild(dbPage);

        dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.REGISTER);
        pageCrud.updateDbChild(dbPage);

        dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.USER_PAGE);
        pageCrud.updateDbChild(dbPage);

        dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HIGH_SCORE);
        pageCrud.updateDbChild(dbPage);

        dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.INFO);
        pageCrud.updateDbChild(dbPage);

        dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.NO_HTML5_BROWSER);
        pageCrud.updateDbChild(dbPage);

        dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.NOT_FOUND);
        pageCrud.updateDbChild(dbPage);

        dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.LEVEL_TASK_DONE);
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
        Map<CmsUtil.CmsPredefinedPage, String> urls = cmsUiService.getPredefinedUrls();
        Assert.assertEquals(CmsUtil.CmsPredefinedPage.values().length, urls.size());

        for (String url : urls.values()) {
            Assert.assertNotNull(url);
            Assert.assertTrue(url.length() > 10);
        }

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void noHtml5Browser() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.NO_HTML5_BROWSER);
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

        tester.startPage(CmsPage.class, new PageParameters("page=NoHtml5Browser"));

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void notFound() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();

        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.NOT_FOUND);
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
        Assert.assertNotNull(cmsUiService.getPredefinedNotFound());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSorting() throws Exception {
        configureGameMultipleLevel();
        // Mock statistics service
        List<UserState> userStates = new ArrayList<UserState>();

        UserState userState = new UserState();
        userState.setDbLevelId(TEST_LEVEL_1_SIMULATED_ID);
        userStates.add(userState);

        User user = new User();
        user.registerUser("aaa", "2", "");
        userState = new UserState();
        userState.setUser(user);
        Base base1 = new Base(userState, 1);
        base1.setAccountBalance(1234);
        setPrivateField(Base.class, base1, "startTime", new Date(System.currentTimeMillis() - DateUtil.MILLIS_IN_HOUR));
        base1.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(1, 1, 1)));
        base1.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(2, 1, 1)));
        userState.setBase(base1);
        userState.setDbLevelId(TEST_LEVEL_2_REAL_ID);
        userStates.add(userState);

        user = new User();
        user.registerUser("xxx", "1", "");
        userState = new UserState();
        userState.setUser(user);
        Base base2 = new Base(userState, 2);
        base2.setAccountBalance(90);
        setPrivateField(Base.class, base2, "startTime", new Date(System.currentTimeMillis() - DateUtil.MILLIS_IN_MINUTE));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(1, 1, 1)));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(2, 1, 1)));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(3, 1, 1)));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(4, 1, 1)));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(5, 1, 1)));
        userState.setBase(base2);
        userState.setDbLevelId(TEST_LEVEL_2_REAL_ID);
        userStates.add(userState);

        UserService userServiceMock = EasyMock.createMock(UserService.class);
        EasyMock.expect(userServiceMock.getAllUserStates()).andReturn(userStates).times(3);
        EasyMock.replay(userServiceMock);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "userService", userServiceMock);

        BaseService baseService = EasyMock.createMock(BaseService.class);
        EasyMock.expect(baseService.getBaseName(base1.getSimpleBase())).andReturn("Base 1").times(3);
        EasyMock.expect(baseService.getBaseName(base2.getSimpleBase())).andReturn("RegUser").times(3);
        EasyMock.replay(baseService);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "baseService", baseService);

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
        dbContentList.setShowHead(true);
        dbContentList.setSpringBeanName("statisticsService");
        dbContentList.setContentProviderGetter("currentStatistics");

        DbExpressionProperty level = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        level.setExpression("level.name");
        level.setName("Level");
        level.setSortable(true);
        DbExpressionProperty userColumn = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        userColumn.setExpression("user.username");
        userColumn.setName("User");
        userColumn.setSortable(true);
        userColumn.setSortHintExpression("user.password");
        DbExpressionProperty baseUpTime = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        baseUpTime.setExpression("baseUpTime");
        baseUpTime.setOptionalType(DbExpressionProperty.Type.DURATION_HH_MM_SS);
        baseUpTime.setName("Time");
        baseUpTime.setSortable(true);
        DbExpressionProperty baseName = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        baseName.setExpression("baseName");
        baseName.setName("Base Name");
        baseName.setSortable(true);
        DbExpressionProperty itemCount = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        itemCount.setExpression("itemCount");
        itemCount.setName("Items");
        itemCount.setSortable(true);
        itemCount.setDefaultSortable(true);
        itemCount.setDefaultSortableAsc(false);
        DbExpressionProperty money = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        money.setExpression("money");
        money.setName("Money");
        money.setSortable(false);

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
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.assertBookmarkablePageLink("form:content:table:tHead:cell:1:link", CmsPage.class, "page=1,sort1=dLevel");
        tester.assertLabel("form:content:table:tHead:cell:1:link:label", "Level");
        tester.assertBookmarkablePageLink("form:content:table:tHead:cell:2:link", CmsPage.class, "page=1,sort1=dUser");
        tester.assertLabel("form:content:table:tHead:cell:2:link:label", "User");
        tester.assertBookmarkablePageLink("form:content:table:tHead:cell:3:link", CmsPage.class, "page=1,sort1=dTime");
        tester.assertLabel("form:content:table:tHead:cell:3:link:label", "Time");
        tester.assertBookmarkablePageLink("form:content:table:tHead:cell:4:link", CmsPage.class, "page=1,sort1=dBase Name");
        tester.assertLabel("form:content:table:tHead:cell:4:link:label", "Base Name");
        tester.assertBookmarkablePageLink("form:content:table:tHead:cell:5:link", CmsPage.class, "page=1,sort1=dItems");
        tester.assertLabel("form:content:table:tHead:cell:5:link:label", "Items");
        tester.assertLabel("form:content:table:tHead:cell:6", "Money");

        tester.assertLabel("form:content:table:rows:1:cells:1:cell", "TEST_LEVEL_2_REAL");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "xxx");
        tester.assertLabel("form:content:table:rows:1:cells:3:cell", "0:01:00");
        tester.assertLabel("form:content:table:rows:1:cells:4:cell", "RegUser");
        tester.assertLabel("form:content:table:rows:1:cells:5:cell", "5");
        tester.assertLabel("form:content:table:rows:1:cells:6:cell", "90");

        tester.assertLabel("form:content:table:rows:2:cells:1:cell", "TEST_LEVEL_2_REAL");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell", "aaa");
        tester.assertLabel("form:content:table:rows:2:cells:3:cell", "1:00:00");
        tester.assertLabel("form:content:table:rows:2:cells:4:cell", "Base 1");
        tester.assertLabel("form:content:table:rows:2:cells:5:cell", "2");
        tester.assertLabel("form:content:table:rows:2:cells:6:cell", "1234");

        tester.assertLabel("form:content:table:rows:3:cells:1:cell", "TEST_LEVEL_1_SIMULATED");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell", "-");
        tester.assertLabel("form:content:table:rows:3:cells:3:cell", "");
        tester.assertLabel("form:content:table:rows:3:cells:4:cell", "");
        tester.assertLabel("form:content:table:rows:3:cells:5:cell", "");
        tester.assertLabel("form:content:table:rows:3:cells:6:cell", "");

        tester.clickLink("form:content:table:tHead:cell:3:link");

        tester.assertBookmarkablePageLink("form:content:table:tHead:cell:1:link", CmsPage.class, "page=1,sort1=dLevel");
        tester.assertLabel("form:content:table:tHead:cell:1:link:label", "Level");
        tester.assertBookmarkablePageLink("form:content:table:tHead:cell:2:link", CmsPage.class, "page=1,sort1=dUser");
        tester.assertLabel("form:content:table:tHead:cell:2:link:label", "User");
        tester.assertBookmarkablePageLink("form:content:table:tHead:cell:3:link", CmsPage.class, "page=1,sort1=aTime");
        tester.assertLabel("form:content:table:tHead:cell:3:link:label", "Time");
        tester.assertBookmarkablePageLink("form:content:table:tHead:cell:4:link", CmsPage.class, "page=1,sort1=dBase Name");
        tester.assertLabel("form:content:table:tHead:cell:4:link:label", "Base Name");
        tester.assertBookmarkablePageLink("form:content:table:tHead:cell:5:link", CmsPage.class, "page=1,sort1=dItems");
        tester.assertLabel("form:content:table:tHead:cell:5:link:label", "Items");
        tester.assertLabel("form:content:table:tHead:cell:6", "Money");

        tester.assertLabel("form:content:table:rows:1:cells:1:cell", "TEST_LEVEL_2_REAL");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "aaa");
        tester.assertLabel("form:content:table:rows:1:cells:3:cell", "1:00:00");
        tester.assertLabel("form:content:table:rows:1:cells:4:cell", "Base 1");
        tester.assertLabel("form:content:table:rows:1:cells:5:cell", "2");
        tester.assertLabel("form:content:table:rows:1:cells:6:cell", "1234");

        tester.assertLabel("form:content:table:rows:2:cells:1:cell", "TEST_LEVEL_2_REAL");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell", "xxx");
        tester.assertLabel("form:content:table:rows:2:cells:3:cell", "0:01:00");
        tester.assertLabel("form:content:table:rows:2:cells:4:cell", "RegUser");
        tester.assertLabel("form:content:table:rows:2:cells:5:cell", "5");
        tester.assertLabel("form:content:table:rows:2:cells:6:cell", "90");

        tester.assertLabel("form:content:table:rows:3:cells:1:cell", "TEST_LEVEL_1_SIMULATED");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell", "-");
        tester.assertLabel("form:content:table:rows:3:cells:3:cell", "");
        tester.assertLabel("form:content:table:rows:3:cells:4:cell", "");
        tester.assertLabel("form:content:table:rows:3:cells:5:cell", "");
        tester.assertLabel("form:content:table:rows:3:cells:6:cell", "");

        tester.clickLink("form:content:table:tHead:cell:2:link");

        tester.assertBookmarkablePageLink("form:content:table:tHead:cell:1:link", CmsPage.class, "page=1,sort1=dLevel");
        tester.assertLabel("form:content:table:tHead:cell:1:link:label", "Level");
        tester.assertBookmarkablePageLink("form:content:table:tHead:cell:2:link", CmsPage.class, "page=1,sort1=aUser");
        tester.assertLabel("form:content:table:tHead:cell:2:link:label", "User");
        tester.assertBookmarkablePageLink("form:content:table:tHead:cell:3:link", CmsPage.class, "page=1,sort1=dTime");
        tester.assertLabel("form:content:table:tHead:cell:3:link:label", "Time");
        tester.assertBookmarkablePageLink("form:content:table:tHead:cell:4:link", CmsPage.class, "page=1,sort1=dBase Name");
        tester.assertLabel("form:content:table:tHead:cell:4:link:label", "Base Name");
        tester.assertBookmarkablePageLink("form:content:table:tHead:cell:5:link", CmsPage.class, "page=1,sort1=dItems");
        tester.assertLabel("form:content:table:tHead:cell:5:link:label", "Items");
        tester.assertLabel("form:content:table:tHead:cell:6", "Money");

        tester.assertLabel("form:content:table:rows:1:cells:1:cell", "TEST_LEVEL_2_REAL");
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", "aaa");
        tester.assertLabel("form:content:table:rows:1:cells:3:cell", "1:00:00");
        tester.assertLabel("form:content:table:rows:1:cells:4:cell", "Base 1");
        tester.assertLabel("form:content:table:rows:1:cells:5:cell", "2");
        tester.assertLabel("form:content:table:rows:1:cells:6:cell", "1234");

        tester.assertLabel("form:content:table:rows:2:cells:1:cell", "TEST_LEVEL_2_REAL");
        tester.assertLabel("form:content:table:rows:2:cells:2:cell", "xxx");
        tester.assertLabel("form:content:table:rows:2:cells:3:cell", "0:01:00");
        tester.assertLabel("form:content:table:rows:2:cells:4:cell", "RegUser");
        tester.assertLabel("form:content:table:rows:2:cells:5:cell", "5");
        tester.assertLabel("form:content:table:rows:2:cells:6:cell", "90");

        tester.assertLabel("form:content:table:rows:3:cells:1:cell", "TEST_LEVEL_1_SIMULATED");
        tester.assertLabel("form:content:table:rows:3:cells:2:cell", "-");
        tester.assertLabel("form:content:table:rows:3:cells:3:cell", "");
        tester.assertLabel("form:content:table:rows:3:cells:4:cell", "");
        tester.assertLabel("form:content:table:rows:3:cells:5:cell", "");
        tester.assertLabel("form:content:table:rows:3:cells:6:cell", "");

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testPaging() throws Exception {
        configureGameMultipleLevel();
        // Mock statistics service
        List<UserState> userStates = new ArrayList<UserState>();

        UserState userState = new UserState();
        userState.setDbLevelId(TEST_LEVEL_1_SIMULATED_ID);
        userStates.add(userState);

        User user = new User();
        user.registerUser("aaa", "2", "");
        userState = new UserState();
        userState.setUser(user);
        Base base1 = new Base(userState, 1);
        base1.setAccountBalance(1234);
        setPrivateField(Base.class, base1, "startTime", new Date(System.currentTimeMillis() - DateUtil.MILLIS_IN_HOUR));
        base1.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(1, 1, 1)));
        base1.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(2, 1, 1)));
        userState.setBase(base1);
        userState.setDbLevelId(TEST_LEVEL_2_REAL_ID);
        userStates.add(userState);

        user = new User();
        user.registerUser("xxx", "1", "");
        userState = new UserState();
        userState.setUser(user);
        Base base2 = new Base(userState, 2);
        base2.setAccountBalance(90);
        setPrivateField(Base.class, base2, "startTime", new Date(System.currentTimeMillis() - DateUtil.MILLIS_IN_MINUTE));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(1, 1, 1)));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(2, 1, 1)));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(3, 1, 1)));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(4, 1, 1)));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(5, 1, 1)));
        userState.setBase(base2);
        userState.setDbLevelId(TEST_LEVEL_2_REAL_ID);
        userStates.add(userState);

        UserService userServiceMock = EasyMock.createMock(UserService.class);
        EasyMock.expect(userServiceMock.getAllUserStates()).andReturn(userStates).times(8);
        EasyMock.replay(userServiceMock);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "userService", userServiceMock);

        BaseService baseService = EasyMock.createMock(BaseService.class);
        EasyMock.expect(baseService.getBaseName(base1.getSimpleBase())).andReturn("Base 1").times(8);
        EasyMock.expect(baseService.getBaseName(base2.getSimpleBase())).andReturn("RegUser").times(8);
        EasyMock.replay(baseService);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "baseService", baseService);

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage1.setName("Home");

        DbContentContainer dbContentContainer = new DbContentContainer();
        dbContentContainer.init(userService);
        dbPage1.setContentAndAccessWrites(dbContentContainer);

        DbContentList dbContentList = (DbContentList) dbContentContainer.getContentCrud().createDbChild(DbContentList.class);
        dbContentList.setRowsPerPage(2);
        dbContentList.setSpringBeanName("statisticsService");
        dbContentList.setContentProviderGetter("currentStatistics");
        DbExpressionProperty level = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        level.setExpression("level.name");
        level.setName("Level1");
        DbExpressionProperty userColumn = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        userColumn.setExpression("user.username");
        userColumn.setName("User1");
        DbExpressionProperty baseUpTime = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        baseUpTime.setExpression("baseUpTime");
        baseUpTime.setOptionalType(DbExpressionProperty.Type.DURATION_HH_MM_SS);
        baseUpTime.setName("Time1");
        DbExpressionProperty baseName = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        baseName.setExpression("baseName");
        baseName.setName("Base Name1");
        DbExpressionProperty itemCount = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        itemCount.setExpression("itemCount");
        itemCount.setName("Items1");
        DbExpressionProperty money = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        money.setExpression("money");
        money.setName("Money1");

        dbContentList = (DbContentList) dbContentContainer.getContentCrud().createDbChild(DbContentList.class);
        dbContentList.setRowsPerPage(2);
        dbContentList.setSpringBeanName("statisticsService");
        dbContentList.setContentProviderGetter("currentStatistics");
        level = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        level.setExpression("level.name");
        level.setName("Level2");
        userColumn = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        userColumn.setExpression("user.username");
        userColumn.setName("User2");
        baseUpTime = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        baseUpTime.setExpression("baseUpTime");
        baseUpTime.setOptionalType(DbExpressionProperty.Type.DURATION_HH_MM_SS);
        baseUpTime.setName("Time2");
        baseName = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        baseName.setExpression("baseName");
        baseName.setName("Base Name2");
        itemCount = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        itemCount.setExpression("itemCount");
        itemCount.setName("Items2");
        money = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        money.setExpression("money");
        money.setName("Money2");

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
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        PageParameters pageParameters = new PageParameters("page=1");
        pageParameters.put("paging2", 0);
        assertBookmarkablePageLink(tester, "form:content:container:1:navigator:navigation:0:pageLink", CmsPage.class, pageParameters);
        tester.assertDisabled("form:content:container:1:navigator:navigation:0:pageLink");
        tester.assertLabel("form:content:container:1:navigator:navigation:0:pageLink:pageNumber", "1");
        pageParameters = new PageParameters("page=1");
        pageParameters.put("paging2", 1);
        assertBookmarkablePageLink(tester, "form:content:container:1:navigator:navigation:1:pageLink", CmsPage.class, pageParameters);
        tester.assertEnabled("form:content:container:1:navigator:navigation:1:pageLink");
        tester.assertLabel("form:content:container:1:navigator:navigation:1:pageLink:pageNumber", "2");
        pageParameters = new PageParameters("page=1");
        pageParameters.put("paging2", 0);
        tester.assertDisabled("form:content:container:1:navigator:first");
        assertBookmarkablePageLink(tester, "form:content:container:1:navigator:first", CmsPage.class, pageParameters);
        pageParameters = new PageParameters("page=1");
        pageParameters.put("paging2", 0);
        tester.assertDisabled("form:content:container:1:navigator:prev");
        assertBookmarkablePageLink(tester, "form:content:container:1:navigator:prev", CmsPage.class, pageParameters);
        pageParameters = new PageParameters("page=1");
        pageParameters.put("paging2", 1);
        tester.assertEnabled("form:content:container:1:navigator:next");
        assertBookmarkablePageLink(tester, "form:content:container:1:navigator:next", CmsPage.class, pageParameters);
        pageParameters = new PageParameters("page=1");
        pageParameters.put("paging2", 1);
        tester.assertEnabled("form:content:container:1:navigator:last");
        assertBookmarkablePageLink(tester, "form:content:container:1:navigator:last", CmsPage.class, pageParameters);

        tester.assertLabel("form:content:container:1:table:rows:1:cells:1:cell", "TEST_LEVEL_1_SIMULATED");
        tester.assertLabel("form:content:container:1:table:rows:2:cells:1:cell", "TEST_LEVEL_2_REAL");

        pageParameters = new PageParameters("page=1");
        pageParameters.put("paging9", 0);
        assertBookmarkablePageLink(tester, "form:content:container:2:navigator:navigation:0:pageLink", CmsPage.class, pageParameters);
        tester.assertDisabled("form:content:container:2:navigator:navigation:0:pageLink");
        tester.assertLabel("form:content:container:2:navigator:navigation:0:pageLink:pageNumber", "1");

        tester.assertLabel("form:content:container:2:table:rows:1:cells:1:cell", "TEST_LEVEL_1_SIMULATED");
        tester.assertLabel("form:content:container:2:table:rows:2:cells:1:cell", "TEST_LEVEL_2_REAL");

        tester.clickLink("form:content:container:1:navigator:next");

        pageParameters = new PageParameters("page=1");
        pageParameters.put("paging2", 0);
        assertBookmarkablePageLink(tester, "form:content:container:1:navigator:navigation:0:pageLink", CmsPage.class, pageParameters);
        tester.assertEnabled("form:content:container:1:navigator:navigation:0:pageLink");
        tester.assertLabel("form:content:container:1:navigator:navigation:0:pageLink:pageNumber", "1");
        pageParameters = new PageParameters("page=1");
        pageParameters.put("paging2", 1);
        assertBookmarkablePageLink(tester, "form:content:container:1:navigator:navigation:1:pageLink", CmsPage.class, pageParameters);
        tester.assertDisabled("form:content:container:1:navigator:navigation:1:pageLink");
        tester.assertLabel("form:content:container:1:navigator:navigation:1:pageLink:pageNumber", "2");
        pageParameters = new PageParameters("page=1");
        pageParameters.put("paging2", 0);
        tester.assertEnabled("form:content:container:1:navigator:first");
        assertBookmarkablePageLink(tester, "form:content:container:1:navigator:first", CmsPage.class, pageParameters);
        pageParameters = new PageParameters("page=1");
        pageParameters.put("paging2", 0);
        tester.assertEnabled("form:content:container:1:navigator:prev");
        assertBookmarkablePageLink(tester, "form:content:container:1:navigator:prev", CmsPage.class, pageParameters);
        pageParameters = new PageParameters("page=1");
        pageParameters.put("paging2", 1);
        tester.assertDisabled("form:content:container:1:navigator:next");
        assertBookmarkablePageLink(tester, "form:content:container:1:navigator:next", CmsPage.class, pageParameters);
        pageParameters = new PageParameters("page=1");
        pageParameters.put("paging2", 1);
        tester.assertDisabled("form:content:container:1:navigator:last");
        assertBookmarkablePageLink(tester, "form:content:container:1:navigator:last", CmsPage.class, pageParameters);

        tester.assertLabel("form:content:container:1:table:rows:1:cells:1:cell", "TEST_LEVEL_2_REAL");

        pageParameters = new PageParameters("page=1");
        pageParameters.put("paging9", 0);
        assertBookmarkablePageLink(tester, "form:content:container:2:navigator:navigation:0:pageLink", CmsPage.class, pageParameters);
        tester.assertDisabled("form:content:container:2:navigator:navigation:0:pageLink");
        tester.assertLabel("form:content:container:2:navigator:navigation:0:pageLink:pageNumber", "1");

        tester.assertLabel("form:content:container:2:table:rows:1:cells:1:cell", "TEST_LEVEL_1_SIMULATED");
        tester.assertLabel("form:content:container:2:table:rows:2:cells:1:cell", "TEST_LEVEL_2_REAL");

        tester.clickLink("form:content:container:2:navigator:next");

        tester.assertLabel("form:content:container:1:table:rows:1:cells:1:cell", "TEST_LEVEL_1_SIMULATED");
        tester.assertLabel("form:content:container:1:table:rows:2:cells:1:cell", "TEST_LEVEL_2_REAL");

        pageParameters = new PageParameters("page=1");
        pageParameters.put("paging9", 0);
        assertBookmarkablePageLink(tester, "form:content:container:2:navigator:navigation:0:pageLink", CmsPage.class, pageParameters);
        tester.assertEnabled("form:content:container:2:navigator:navigation:0:pageLink");
        tester.assertLabel("form:content:container:2:navigator:navigation:0:pageLink:pageNumber", "1");

        tester.assertLabel("form:content:container:2:table:rows:1:cells:1:cell", "TEST_LEVEL_2_REAL");

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void borderWrapper() throws Exception {
        configureItemTypes();
        
        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");

        DbContentList dbContentList = new DbContentList();
        dbContentList.setBorderCss("borderCSS");
        dbContentList.init(userService);
        dbPage.setContentAndAccessWrites(dbContentList);
        dbContentList.setSpringBeanName("itemService");
        dbContentList.setContentProviderGetter("dbItemTypeCrud");

        CrudListChildServiceHelper<DbContent> columnCrud = dbContentList.getColumnsCrud();
        DbExpressionProperty column = (DbExpressionProperty) columnCrud.createDbChild(DbExpressionProperty.class);
        column.setExpression("name");
        DbContentDetailLink detailLink = (DbContentDetailLink) columnCrud.createDbChild(DbContentDetailLink.class);
        detailLink.setName("Details");

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
        tester.startPage(CmsPage.class);
        tester.debugComponentTrees();
        tester.assertVisible("form:content:border");
        tester.assertVisible("form:content:border:borderContent");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testDbExpressionPropertyTypes() throws Exception {
        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        DbContentContainer dbContentContainer = new DbContentContainer();
        dbContentContainer.init(userService);
        dbPage.setContentAndAccessWrites(dbContentContainer);
        // Integer property
        DbExpressionProperty expressionProperty = (DbExpressionProperty) dbContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        expressionProperty.setSpringBeanName("testCmsBean");
        expressionProperty.setExpression("double1");
        expressionProperty.setOptionalType(DbExpressionProperty.Type.ROUNDED_DOWN_INTEGER);

        expressionProperty = (DbExpressionProperty) dbContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        expressionProperty.setSpringBeanName("testCmsBean");
        expressionProperty.setExpression("double2");
        expressionProperty.setOptionalType(DbExpressionProperty.Type.ROUNDED_DOWN_INTEGER);

        expressionProperty = (DbExpressionProperty) dbContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        expressionProperty.setSpringBeanName("testCmsBean");
        expressionProperty.setExpression("double3");
        expressionProperty.setOptionalType(DbExpressionProperty.Type.ROUNDED_DOWN_INTEGER);

        expressionProperty = (DbExpressionProperty) dbContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        expressionProperty.setSpringBeanName("testCmsBean");
        expressionProperty.setExpression("double4");
        expressionProperty.setOptionalType(DbExpressionProperty.Type.ROUNDED_DOWN_INTEGER);

        expressionProperty = (DbExpressionProperty) dbContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        expressionProperty.setSpringBeanName("testCmsBean");
        expressionProperty.setExpression("integer1");
        expressionProperty.setOptionalType(DbExpressionProperty.Type.ROUNDED_DOWN_INTEGER);

        expressionProperty = (DbExpressionProperty) dbContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        expressionProperty.setSpringBeanName("testCmsBean");
        expressionProperty.setExpression("integer2");
        expressionProperty.setOptionalType(DbExpressionProperty.Type.ROUNDED_DOWN_INTEGER);
        
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
        tester.startPage(CmsPage.class);
        tester.assertLabel("form:content:container:1", "1");
        tester.assertLabel("form:content:container:2", "2");
        tester.assertLabel("form:content:container:3", "5");
        tester.assertLabel("form:content:container:4", "4");
        tester.assertLabel("form:content:container:5", "10");
        tester.assertLabel("form:content:container:6", "11");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testUglyBeanIdPathElement4LevelTask() throws Exception {
        configureGameMultipleLevel();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();

        // Setup the level page
        DbPage dbLevelPage = pageCrud.createDbChild();
        dbLevelPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbLevelPage.setName("Level");

        DbContentList dbContentList = new DbContentList();
        dbContentList.init(userService);
        dbLevelPage.setContentAndAccessWrites(dbContentList);
        dbContentList.setSpringBeanName("userGuidanceService");
        dbContentList.setContentProviderGetter("crudQuestHub");
        dbLevelPage.setContentAndAccessWrites(dbContentList);

        CrudChildServiceHelper<DbContentBook> contentBookCrud = dbContentList.getContentBookCrud();
        DbContentBook dbContentBook = contentBookCrud.createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.utg.DbQuestHub");
        CrudListChildServiceHelper<DbContentRow> rowCrud = dbContentBook.getRowCrud();

        DbContentRow dbLevelRow = rowCrud.createDbChild();
        DbContentList levelContentList = new DbContentList();
        levelContentList.setRowsPerPage(5);
        dbLevelRow.setDbContent(levelContentList);
        levelContentList.init(userService);
        levelContentList.setParent(dbLevelRow);
        levelContentList.setContentProviderGetter("levelCrud");

        dbContentBook = levelContentList.getContentBookCrud().createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.utg.DbLevel");
        rowCrud = dbContentBook.getRowCrud();

        DbContentRow dbTaskRow = rowCrud.createDbChild();
        DbContentList taskContentList = new DbContentList();
        dbTaskRow.setDbContent(taskContentList);
        taskContentList.init(userService);
        taskContentList.setParent(dbTaskRow);
        taskContentList.setContentProviderGetter("levelTaskCrud");

        dbContentBook = taskContentList.getContentBookCrud().createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.utg.DbLevelTask");
        rowCrud = dbContentBook.getRowCrud();

        DbContentRow dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Name");
        DbExpressionProperty expProperty = new DbExpressionProperty();
        expProperty.setParent(dbContentRow);
        expProperty.setExpression("name");
        expProperty.setEscapeMarkup(false);
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
        userGuidanceService.getDbLevel(); // set level for new user
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        PageParameters parameters = new PageParameters();
        parameters.add(CmsUtil.SECTION_ID, CmsUtil.LEVEL_TASK_SECTION);
        parameters.add(CmsUtil.CHILD_ID, Integer.toString(TEST_LEVEL_TASK_4_3_SIM_ID));
        tester.startPage(CmsPage.class, parameters);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertLabel("form:content:table:rows:1:cells:2:cell", TEST_LEVEL_TASK_4_3_SIM_NAME);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
