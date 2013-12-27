package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.page.DbMenu;
import com.btxtech.game.services.cms.page.DbMenuItem;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CrudListChildTableHelper;
import com.btxtech.game.wicket.uiservices.DbContentSelector;
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
        form.add(new DbContentSelector("bottom"));

        new CrudListChildTableHelper<DbMenu, DbMenuItem>("menuItems", "saveMenuItems", "createMenuItem", true, form, true) {

            @Override
            protected void extendedPopulateItem(final Item<DbMenuItem> dbMenuItemItem) {
                super.extendedPopulateItem(dbMenuItemItem);
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

            @Override
            protected void onEditSubmit(DbMenuItem dbMenuItem) {
                setResponsePage(new MenuItemEditor(dbMenuItem));
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
