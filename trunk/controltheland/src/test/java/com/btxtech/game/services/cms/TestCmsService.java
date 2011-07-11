package com.btxtech.game.services.cms;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.cms.impl.CmsServiceImpl;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.forum.ForumService;
import com.btxtech.game.services.forum.TestForum;
import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.services.user.DbContentAccessControl;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.WebCommon;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: 04.06.2011
 * Time: 12:37:56
 */
public class TestCmsService extends AbstractServiceTest {
    @Autowired
    private CmsService cmsService;
    @Autowired
    private ContentService contentService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private UserService userService;
    @Autowired
    private MovableService movableService;
    @Autowired
    private ServerMarketService serverMarketService;
    @Autowired
    private ForumService forumService;

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
    public void testPages() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setHome(true);
        dbPage1.setName("Home");
        DbPage dbPage2 = pageCrud.createDbChild();
        dbPage2.setHome(false);
        dbPage2.setName("Market");
        DbPage dbPage3 = pageCrud.createDbChild();
        dbPage3.setHome(false);
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
        DbPage cachePageHome = cmsService.getHomePage();
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

    private int setupBlogPage() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setHome(true);
        dbPage1.setName("Home");

        DbContentList dbContentList = new DbContentList();
        dbContentList.init();
        dbPage1.setContent(dbContentList);
        dbContentList.setRowsPerPage(5);
        dbContentList.setSpringBeanName("contentService");
        dbContentList.setContentProviderGetter("getBlogEntryCrudRootServiceHelper");

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
    public void testBlogRead() {
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

        DbBlogEntry dbBlogEntry2 = blogCrud.createDbChild();
        dbBlogEntry2.setHtml("Blog 2");
        dbBlogEntry2.setName("News 2");
        blogCrud.updateDbChild(dbBlogEntry2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPage cachePage = cmsService.getPage(id);
        DbContentList cacheContentList = (DbContentList) cachePage.getContent();

        Assert.assertEquals("contentService", cacheContentList.getSpringBeanName());
        Assert.assertEquals("getBlogEntryCrudRootServiceHelper", cacheContentList.getContentProviderGetter());
        Assert.assertEquals(5, (int) cacheContentList.getRowsPerPage());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        tester.assertLabel("form:content:rows:1:cells:1:cell:listView:0:content", "News 2");
        tester.assertLabel("form:content:rows:1:cells:1:cell:listView:1:content", WebCommon.formatDateTime(new Date(dbBlogEntry2.getTimeStamp())));
        tester.assertLabel("form:content:rows:1:cells:1:cell:listView:2:content", "Blog 2");

        tester.assertLabel("form:content:rows:2:cells:1:cell:listView:0:content", "News 1");
        tester.assertLabel("form:content:rows:2:cells:1:cell:listView:1:content", WebCommon.formatDateTime(new Date(dbBlogEntry1.getTimeStamp())));
        tester.assertLabel("form:content:rows:2:cells:1:cell:listView:2:content", "Blog 1");

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testBlogWrite() throws Exception {
        configureMinimalGame();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setHome(true);
        dbPage1.setName("Home");

        DbContentList dbContentList = new DbContentList();
        dbContentList.init();
        dbPage1.setContent(dbContentList);
        dbContentList.setRowsPerPage(5);
        dbContentList.setSpringBeanName("contentService");
        dbContentList.setContentProviderGetter("getBlogEntryCrudRootServiceHelper");

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
        formTester.setValue("content:rows:1:cells:1:cell:listView:0:content:field", "Blog 1");
        formTester.setValue("content:rows:1:cells:1:cell:listView:2:content:textArea", "Bla Bla");
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
    public void testWiki() throws Exception {
        configureMinimalGame();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setHome(true);
        dbPage1.setName("Home");

        DbContentList dbContentList = new DbContentList();
        dbContentList.init();
        dbPage1.setContent(dbContentList);
        dbContentList.setRowsPerPage(5);
        dbContentList.setSpringBeanName("contentService");
        dbContentList.setContentProviderGetter("getWikiSectionCrudRootServiceHelper");

        CrudListChildServiceHelper<DbContent> columnCrud = dbContentList.getColumnsCrud();
        DbExpressionProperty nameProperty = (DbExpressionProperty) columnCrud.createDbChild(DbExpressionProperty.class);
        nameProperty.setExpression("name");
        DbContentDetailLink detailLink = (DbContentDetailLink) columnCrud.createDbChild(DbContentDetailLink.class);
        detailLink.setName("Details");

        CrudChildServiceHelper<DbContentBook> contentBookCrud = dbContentList.getContentBookCrud();
        DbContentBook dbContentBook = contentBookCrud.createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.cms.DbWikiSection");
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
        User user = userService.getUser();
        DbContentAccessControl control = user.getContentCrud().createDbChild();
        control.setDbContent(dbContentList);
        control.setCreateAllowed(true);
        control.setDeleteAllowed(true);
        control.setWriteAllowed(true);
        control = user.getContentCrud().createDbChild();
        control.setDbContent(nameProperty);
        control.setCreateAllowed(true);
        control.setDeleteAllowed(true);
        control.setWriteAllowed(true);
        control = user.getContentCrud().createDbChild();
        control.setDbContent(expProperty);
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
        formTester.setValue("content:rows:1:cells:1:cell:field", "TEST 1");
        formTester.submit("content:edit:save");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        tester.clickLink("form:content:rows:2:cells:2:cell:link");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        formTester = tester.newFormTester("form");
        formTester.setValue("content:dataTable:body:rows:1:cells:2:cell:textArea", "Content Content Content");
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
        configureMinimalGame();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setHome(true);
        dbPage1.setName("Home");

        DbContentDynamicHtml dbContentDynamicHtml = new DbContentDynamicHtml();
        dbContentDynamicHtml.init();
        dbPage1.setContent(dbContentDynamicHtml);
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
        configureMinimalGame();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setHome(true);
        dbPage1.setName("Home");

        DbContentDynamicHtml dbContentDynamicHtml = new DbContentDynamicHtml();
        dbContentDynamicHtml.init();
        dbPage1.setContent(dbContentDynamicHtml);

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
        control.setDbContent(dbContentDynamicHtml);
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
        //tester.debugComponentTrees();
        formTester = tester.newFormTester("form");
        formTester.setValue("content:htmlTextArea", "qaywsxedc");
        formTester.submit("content:edit:save");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertEquals("qaywsxedc", contentService.getDynamicHtml(dbContentDynamicHtml.getId()));
    }

    @Test
    @DirtiesContext
    public void testMarket() throws Exception {
        configureMinimalGame();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setHome(true);
        dbPage1.setName("Home");

        DbContentList dbContentList = new DbContentList();
        dbContentList.init();
        dbPage1.setContent(dbContentList);
        dbContentList.setRowsPerPage(5);
        dbContentList.setSpringBeanName("marketService");
        dbContentList.setContentProviderGetter("getAvailableCrud");

        DbContentContainer dbContentContainer = (DbContentContainer) dbContentList.getColumnsCrud().createDbChild(DbContentContainer.class);
        DbExpressionProperty image = (DbExpressionProperty) dbContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        image.setExpression("dbMarketEntry.itemType");
        DbExpressionProperty price = (DbExpressionProperty) dbContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        price.setExpression("dbMarketEntry.price");
        DbExpressionProperty name = (DbExpressionProperty) dbContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        name.setExpression("dbMarketEntry.itemType.name");
        DbContentActionButton buyButton = (DbContentActionButton) dbContentContainer.getContentCrud().createDbChild(DbContentActionButton.class);
        buyButton.setName("buy");
        buyButton.setParameterExpression("dbMarketEntry");
        buyButton.setMethodName("buy");
        buyButton.setSpringBeanName("marketService");
        buyButton.setLeftSideSpringBeanName("marketService");
        buyButton.setLeftSideOperandExpression("userItemTypeAccess.xp");
        buyButton.setRightSideOperandExpression("dbMarketEntry.price");
        buyButton.setUnfilledHtml("UnfilledText");

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
        // No market
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        endHttpRequestAndOpenSessionInViewFilter();
        // Enter game
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        // Verify
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertFalse(serverMarketService.getUserItemTypeAccess().contains(TEST_SIMPLE_BUILDING_ID));
        endHttpRequestAndOpenSessionInViewFilter();
        // Not enough XPs
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.assertLabel("form:content:rows:1:cells:1:cell:listView:2:content", TEST_SIMPLE_BUILDING);
        tester.debugComponentTrees();
        tester.assertInvisible("form:content:rows:1:cells:1:cell:listView:3:content:button");
        tester.assertLabel("form:content:rows:1:cells:1:cell:listView:3:content:label", "UnfilledText");
        tester.assertVisible("form:content:rows:1:cells:1:cell:listView:3:content:label");
        endHttpRequestAndOpenSessionInViewFilter();
        // Get Some XP
        beginHttpRequestAndOpenSessionInViewFilter();
        serverMarketService.getUserItemTypeAccess().setXp(10);
        endHttpRequestAndOpenSessionInViewFilter();
        // Buy in market
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.assertLabel("form:content:rows:1:cells:1:cell:listView:2:content", TEST_SIMPLE_BUILDING);
        tester.assertInvisible("form:content:rows:1:cells:1:cell:listView:3:content:label");
        FormTester formTester = tester.newFormTester("form");
        formTester.submit("content:rows:1:cells:1:cell:listView:3:content:button");
        endHttpRequestAndOpenSessionInViewFilter();
        // Verify
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue(serverMarketService.getUserItemTypeAccess().contains(TEST_SIMPLE_BUILDING_ID));
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForum() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Setup CMS content
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setHome(true);
        dbPage1.setName("Forum");

        DbContentList dbContentList = new DbContentList();
        dbContentList.init();
        dbPage1.setContent(dbContentList);
        dbContentList.setSpringBeanName("forumService");
        dbContentList.setContentProviderGetter("getSubForumCrud");

        DbContentContainer subForums = (DbContentContainer) dbContentList.getColumnsCrud().createDbChild(DbContentContainer.class);
        DbExpressionProperty name = (DbExpressionProperty) subForums.getContentCrud().createDbChild(DbExpressionProperty.class);
        name.setExpression("name");
        DbExpressionProperty content = (DbExpressionProperty) subForums.getContentCrud().createDbChild(DbExpressionProperty.class);
        content.setExpression("content");
        DbContentList categories = (DbContentList) subForums.getContentCrud().createDbChild(DbContentList.class);
        categories.setName("Categories");
        categories.setContentProviderGetter("getCategoryCrud");

        DbExpressionProperty nameCat = (DbExpressionProperty) categories.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        nameCat.setExpression("name");
        DbExpressionProperty lastPostCat = (DbExpressionProperty) categories.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        lastPostCat.setExpression("lastPost");
        DbContentDetailLink categoryLink = (DbContentDetailLink) categories.getColumnsCrud().createDbChild(DbContentDetailLink.class);
        categoryLink.setName("details");

        DbContentBook categoryContentBook = categories.getContentBookCrud().createDbChild();
        categoryContentBook.setClassName("com.btxtech.game.services.forum.Category");
        DbContentRow categoryNameRow = categoryContentBook.getRowCrud().createDbChild();
        DbExpressionProperty categoryName = new DbExpressionProperty();
        categoryName.setParent(categoryNameRow);
        categoryName.setExpression("name");
        categoryNameRow.setDbContent(categoryName);
        DbContentRow categoryDetailRow = categoryContentBook.getRowCrud().createDbChild();
        DbContentList threadList = new DbContentList();
        threadList.setParent(categoryDetailRow);
        threadList.init();
        threadList.setContentProviderGetter("getForumThreadCrud");
        categoryDetailRow.setDbContent(threadList);
        DbExpressionProperty threadColumnName = (DbExpressionProperty) threadList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        threadColumnName.setExpression("name");
        DbContentDetailLink threadLink = (DbContentDetailLink) threadList.getColumnsCrud().createDbChild(DbContentDetailLink.class);
        threadLink.setName("details");

        DbContentBook postContentBook = threadList.getContentBookCrud().createDbChild();
        postContentBook.setClassName("com.btxtech.game.services.forum.ForumThread");
        DbContentRow threadNameRow = postContentBook.getRowCrud().createDbChild();
        DbExpressionProperty postColumnName = new DbExpressionProperty();
        postColumnName.setParent(threadNameRow);
        postColumnName.setExpression("name");
        threadNameRow.setDbContent(postColumnName);


        DbContentRow postsNameRow = postContentBook.getRowCrud().createDbChild();
        DbContentList postName = new DbContentList();
        postName.setParent(postsNameRow);
        postName.init();
        postName.setContentProviderGetter("getPostCrud");
        postsNameRow.setDbContent(postName);
        DbContentRow postsContentRow = postContentBook.getRowCrud().createDbChild();
        DbExpressionProperty postContent = (DbExpressionProperty) postName.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        postContent.setExpression("content");
        postContent.setParent(postsContentRow);
        postName.setContentProviderGetter("getPostCrud");
        postsContentRow.setDbContent(postContent);


        pageCrud.updateDbChild(dbPage1);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        // Setup und login user and fill forum
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        TestForum.fillForum(forumService, userService);
        endHttpRequestAndOpenSessionInViewFilter();

        // Activate
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();

        // Verify
        beginHttpRequestAndOpenSessionInViewFilter();
        tester.startPage(CmsPage.class);
        tester.assertLabel("form:content:rows:1:cells:1:cell:listView:0:content", "SubForumName1");
        tester.assertLabel("form:content:rows:1:cells:1:cell:listView:1:content", "SubForumContent1");
        tester.assertLabel("form:content:rows:1:cells:1:cell:listView:2:content:rows:1:cells:1:cell", "CategoryName1");
        // Click the category link
        tester.clickLink("form:content:rows:1:cells:1:cell:listView:2:content:rows:1:cells:3:cell:link");
        tester.assertLabel("form:content:dataTable:body:rows:1:cells:2:cell", "CategoryName1");
        tester.assertLabel("form:content:dataTable:body:rows:2:cells:2:cell:rows:1:cells:1:cell", "ForumThreadName1");
        // Click the thread link
        tester.clickLink("form:content:dataTable:body:rows:2:cells:2:cell:rows:1:cells:2:cell:link");
        tester.assertLabel("form:content:dataTable:body:rows:1:cells:2:cell", "ForumThreadName1");
        tester.assertLabel("form:content:dataTable:body:rows:2:cells:2:cell:rows:1:cells:1:cell", "PostContent1");
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }


    @Test
    @DirtiesContext
    public void testBeanTableWithContentBookWithBeanTable() {
        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setHome(true);
        dbPage.setName("Home");

        DbContentList dbContentList = new DbContentList();
        dbContentList.setRowsPerPage(5);
        dbContentList.init();
        dbPage.setContent(dbContentList);
        dbContentList.setSpringBeanName("userGuidanceService");
        dbContentList.setContentProviderGetter("getDbLevelCrudServiceHelper");

        CrudListChildServiceHelper<DbContent> columnCrud = dbContentList.getColumnsCrud();
        DbExpressionProperty column = (DbExpressionProperty) columnCrud.createDbChild(DbExpressionProperty.class);
        column.setExpression("name");
        column = (DbExpressionProperty) columnCrud.createDbChild(DbExpressionProperty.class);
        column.setExpression("internalDescription");
        DbContentDetailLink detailLink = (DbContentDetailLink) columnCrud.createDbChild(DbContentDetailLink.class);
        detailLink.setName("Details");

        CrudChildServiceHelper<DbContentBook> contentBookCrud = dbContentList.getContentBookCrud();
        DbContentBook dbContentBook = contentBookCrud.createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.utg.DbSimulationLevel");

        CrudListChildServiceHelper<DbContentRow> rowCrud = dbContentBook.getRowCrud();
        DbContentRow dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Name");
        DbExpressionProperty expProperty = new DbExpressionProperty();
        expProperty.setExpression("name");
        dbContentRow.setDbContent(expProperty);

        dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Description");
        expProperty = new DbExpressionProperty();
        expProperty.setExpression("html");
        expProperty.setEscapeMarkup(false);
        dbContentRow.setDbContent(expProperty);

        dbContentBook = contentBookCrud.createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.utg.DbRealGameLevel");

        dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Name");
        expProperty = new DbExpressionProperty();
        expProperty.setExpression("name");
        dbContentRow.setDbContent(expProperty);

        dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Description");
        expProperty = new DbExpressionProperty();
        expProperty.setExpression("html");
        expProperty.setEscapeMarkup(false);
        dbContentRow.setDbContent(expProperty);

        dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Allowed Items");
        DbContentList dbContentListItems = new DbContentList();
        dbContentListItems.init();
        dbContentListItems.setContentProviderGetter("getDbItemTypeLimitationCrudServiceHelper");
        columnCrud = dbContentListItems.getColumnsCrud();
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
        // TODO check for labels
        tester.startPage(CmsPage.class);
        tester.assertRenderedPage(CmsPage.class);
        // TODO tester.assertLabel("content:dataTable:body:rows:1:cells:1:cell", "Hallo");

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

}
