package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.DbMenu;
import com.btxtech.game.services.cms.DbPage;
import com.btxtech.game.services.cms.DbPageStyle;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import org.apache.wicket.markup.html.form.Button;
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
