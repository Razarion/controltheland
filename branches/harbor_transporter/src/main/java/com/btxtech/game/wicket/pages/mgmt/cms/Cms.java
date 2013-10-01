package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.cms.layout.DbContentRow;
import com.btxtech.game.services.cms.page.DbAds;
import com.btxtech.game.services.cms.page.DbMenu;
import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.cms.page.DbPageStyle;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 05.06.2011
 * Time: 18:38:58
 */
public class Cms extends MgmtWebPage {
    @SpringBean
    private CmsService cmsService;

    public Cms() {
        add(new FeedbackPanel("msgs"));

        Form form = new Form("form");
        add(form);

        new CrudRootTableHelper<DbPageStyle>("styles", "saveStyles", "createStyle", true, form, false) {

            @Override
            protected void extendedPopulateItem(Item<DbPageStyle> dbPageStyleItem) {
                displayId(dbPageStyleItem);
                super.extendedPopulateItem(dbPageStyleItem);
            }

            @Override
            protected CrudRootServiceHelper<DbPageStyle> getCrudRootServiceHelperImpl() {
                return cmsService.getPageStyleCrudRootServiceHelper();
            }

            @Override
            protected void onEditSubmit(DbPageStyle dbPageStyle) {
                setResponsePage(new PageStyleEditor(dbPageStyle));
            }
        };

        new CrudRootTableHelper<DbPage>("pages", "savePages", "createPage", true, form, false) {

            @Override
            protected void extendedPopulateItem(Item<DbPage> dbPageItem) {
                displayId(dbPageItem);
                super.extendedPopulateItem(dbPageItem);
            }

            @Override
            protected CrudRootServiceHelper<DbPage> getCrudRootServiceHelperImpl() {
                return cmsService.getPageCrudRootServiceHelper();
            }

            @Override
            protected void onEditSubmit(DbPage dbPage) {
                setResponsePage(new PageEditor(dbPage));
            }
        };

        new CrudRootTableHelper<DbMenu>("menus", "saveMenus", "createMenu", true, form, false) {

            @Override
            protected void extendedPopulateItem(Item<DbMenu> dbMenuItem) {
                displayId(dbMenuItem);
                super.extendedPopulateItem(dbMenuItem);
            }

            @Override
            protected CrudRootServiceHelper<DbMenu> getCrudRootServiceHelperImpl() {
                return cmsService.getMenuCrudRootServiceHelper();
            }

            @Override
            protected void onEditSubmit(DbMenu dbMenu) {
                setResponsePage(new MenuEditor(dbMenu));
            }
        };

        new CrudRootTableHelper<DbAds>("ads", "saveAds", "createAds", true, form, false) {

            @Override
            protected void extendedPopulateItem(Item<DbAds> dbMenuItem) {
                displayId(dbMenuItem);
                super.extendedPopulateItem(dbMenuItem);
                dbMenuItem.add(new CheckBox("active"));
            }

            @Override
            protected CrudRootServiceHelper<DbAds> getCrudRootServiceHelperImpl() {
                return cmsService.getAdsCrud();
            }

            @Override
            protected void onEditSubmit(DbAds dbAds) {
                setResponsePage(new AdsEditor(dbAds));
            }
        };

        new CrudRootTableHelper<DbContent>("contents", "saveContent", "createContent", true, form, false) {

            @Override
            protected void extendedPopulateItem(Item<DbContent> contentItem) {
                displayId(contentItem);
                super.extendedPopulateItem(contentItem);
            }

            @Override
            protected CrudRootServiceHelper<DbContent> getCrudRootServiceHelperImpl() {
                return cmsService.getContentCrud();
            }

            @Override
            protected void setupCreate(WebMarkupContainer markupContainer, String createId) {
                markupContainer.add(new CreateDbContentPanel(createId) {

                    @Override
                    public void onDbContentSelected(Class<? extends DbContent> selected) {
                        createDbChild(selected);
                        refresh();
                    }
                });
            }

            @Override
            protected void onEditSubmit(DbContent dbContent) {
                dbContent = HibernateUtil.deproxy(dbContent, DbContent.class);
                if (!(dbContent instanceof DbContentRow)) {
                    setResponsePage(ContentEditorFactory.createContentEditor(dbContent));
                }
            }
        };


        form.add(new Button("repository") {

            @Override
            public void onSubmit() {
                setResponsePage(ImageRepository.class);
            }
        });
        form.add(new Button("activate") {

            @Override
            public void onSubmit() {
                cmsService.activateCms();
            }
        });

    }
}
