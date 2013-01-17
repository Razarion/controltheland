package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.page.DbMenu;
import com.btxtech.game.services.cms.page.DbMenuItem;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.I18nStringEditor;
import com.btxtech.game.wicket.uiservices.PageSelector;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 22.07.2011
 * Time: 21:03:07
 */
public class MenuItemEditor extends MgmtWebPage {
    @SpringBean
    private CmsService cmsService;
    @SpringBean
    private UserService userService;
    @SpringBean
    private RuServiceHelper<DbMenuItem> ruServiceHelper;

    public MenuItemEditor(DbMenuItem dbMenuItem) {
        add(new FeedbackPanel("msgs"));

        final Form<DbMenuItem> form = new Form<DbMenuItem>("form", new CompoundPropertyModel<DbMenuItem>(new RuModel<DbMenuItem>(dbMenuItem, DbMenuItem.class) {
            @Override
            protected RuServiceHelper<DbMenuItem> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new I18nStringEditor("dbI18nName"));
        form.add(new PageSelector("page"));
        form.add(new TextField("cssClass"));
        form.add(new TextField("selectedCssClass"));
        form.add(new TextField("cssLinkClass"));
        form.add(new TextField("selectedCssLinkClass"));
        form.add(new TextField("cssTrClass"));
        form.add(new TextField("selectedCssTrClass"));

        setupSubMenu(form);

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });
        form.add(new Button("cms") {

            @Override
            public void onSubmit() {
                setResponsePage(Cms.class);
            }
        });
    }

    private void setupSubMenu(final Form<DbMenuItem> form) {
        form.add(new Button("createSubMenu") {

            @Override
            public void onSubmit() {
                // Should be in a service
                DbMenu subMenu = new DbMenu();
                subMenu.init(userService);
                form.getModelObject().setSubMenu(subMenu);
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }

            @Override
            public boolean isVisible() {
                return form.getModelObject().getSubMenu() == null;
            }
        });
        form.add(new Button("editSubMenu") {

            @Override
            public void onSubmit() {
                setResponsePage(new MenuEditor(form.getModelObject().getSubMenu()));
            }

            @Override
            public boolean isVisible() {
                return form.getModelObject().getSubMenu() != null;
            }
        });
        form.add(new Button("deleteSubMenu") {

            @Override
            public void onSubmit() {
                // Should be in a service
                form.getModelObject().setSubMenu(null);
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }

            @Override
            public boolean isVisible() {
                return form.getModelObject().getSubMenu() != null;
            }
        });
    }


}
