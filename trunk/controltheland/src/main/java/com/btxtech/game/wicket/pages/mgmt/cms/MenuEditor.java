package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.DbMenu;
import com.btxtech.game.services.cms.DbMenuItem;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CrudListChildTableHelper;
import com.btxtech.game.wicket.uiservices.PageSelector;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 06.06.2011
 * Time: 01:15:00
 */
public class MenuEditor extends MgmtWebPage {
    @SpringBean
    private CmsService cmsService;
    @SpringBean
    private UserService userService;
    @SpringBean
    private RuServiceHelper<DbMenu> ruServiceHelper;

    public MenuEditor(DbMenu dbMenu) {
        add(new FeedbackPanel("msgs"));

        final Form<DbMenu> form = new Form<DbMenu>("form", new CompoundPropertyModel<DbMenu>(new RuModel<DbMenu>(dbMenu, DbMenu.class) {
            @Override
            protected RuServiceHelper<DbMenu> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        new CrudListChildTableHelper<DbMenu, DbMenuItem>("menuItems", "saveMenuItems", "createMenuItem", false, form, true) {

            @Override
            protected void extendedPopulateItem(final Item<DbMenuItem> dbMenuItemItem) {
                super.extendedPopulateItem(dbMenuItemItem);
                dbMenuItemItem.add(new PageSelector("page"));
                dbMenuItemItem.add(new Button("createSubMenu") {

                    @Override
                    public void onSubmit() {
                        // Should be in a service
                        DbMenu subMenu = new DbMenu();
                        subMenu.init(userService);
                        dbMenuItemItem.getModelObject().setSubMenu(subMenu);
                        ruServiceHelper.updateDbEntity(form.getModelObject());
                    }

                    @Override
                    public boolean isVisible() {
                        return dbMenuItemItem.getModelObject().getSubMenu() == null;
                    }
                });
                dbMenuItemItem.add(new Button("editSubMenu") {

                    @Override
                    public void onSubmit() {
                        setResponsePage(new MenuEditor(dbMenuItemItem.getModelObject().getSubMenu()));
                    }

                    @Override
                    public boolean isVisible() {
                        return dbMenuItemItem.getModelObject().getSubMenu() != null;
                    }
                });
                dbMenuItemItem.add(new Button("deleteSubMenu") {

                    @Override
                    public void onSubmit() {
                        // Should be in a service
                        dbMenuItemItem.getModelObject().setSubMenu(null);
                        ruServiceHelper.updateDbEntity(form.getModelObject());
                    }

                    @Override
                    public boolean isVisible() {
                        return dbMenuItemItem.getModelObject().getSubMenu() != null;
                    }
                });

            }

            @Override
            protected RuServiceHelper<DbMenu> getRuServiceHelper() {
                return ruServiceHelper;
            }

            @Override
            protected DbMenu getParent() {
                return form.getModelObject();
            }

            @Override
            protected CrudListChildServiceHelper<DbMenuItem> getCrudListChildServiceHelperImpl() {
                return getParent().getMenuItemCrudChildServiceHelper();
            }
        };

        form.add(new Button("cms") {

            @Override
            public void onSubmit() {
                setResponsePage(Cms.class);
            }
        });
    }
}
