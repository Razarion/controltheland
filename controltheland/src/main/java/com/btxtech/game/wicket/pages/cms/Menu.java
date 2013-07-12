package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.cms.page.DbMenu;
import com.btxtech.game.services.cms.page.DbMenuItem;
import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.mgmt.ServerI18nHelper;
import com.btxtech.game.wicket.uiservices.DetachHashListProvider;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: beat
 * Date: 02.07.2011
 * Time: 00:05:56
 */
public class Menu extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    @SpringBean
    private ServerI18nHelper serverI18nHelper;
    private ContentContext contentContext;

    public Menu(String id, DbMenu dbMenu, ContentContext contentContext) {
        super(id);
        this.contentContext = contentContext;
        DetachHashListProvider<DbMenuItem> menuProvider = new DetachHashListProvider<DbMenuItem>() {

            @Override
            protected List<DbMenuItem> createList() {
                DbMenu dbMenu = (DbMenu) Menu.this.getDefaultModelObject();
                if (dbMenu != null) {
                    List<DbMenuItem> menuItemList = new ArrayList<>(dbMenu.getMenuItemCrudChildServiceHelper().readDbChildren());
                    addSubMenuItems(menuItemList);
                    return filterMenuItems(menuItemList);
                } else {
                    return Collections.emptyList();
                }
            }
        };

        DataView<DbMenuItem> dataTable = new DataView<DbMenuItem>("menuTable", menuProvider) {
            protected void populateItem(final Item<DbMenuItem> item) {
                DbPage dbPage = (DbPage) Menu.this.getParent().getDefaultModelObject();
                boolean selected = item.getModelObject().getPage().equals(dbPage);
                PageParameters pageParameters = new PageParameters();
                pageParameters.add(CmsUtil.ID, Integer.toString(item.getModelObject().getPage().getId()));
                BookmarkablePageLink<CmsPage> link = new BookmarkablePageLink<>("menuLink", CmsPage.class, pageParameters);

                Label label = new Label("menuLinkName", serverI18nHelper.getString(item.getModelObject().getDbI18nName().getString(getLocale())));
                link.add(label);
                item.add(link);

                if (selected) {
                    setStyle(link, item.getModelObject().getSelectedCssLinkClass());
                    setStyle(label, item.getModelObject().getSelectedCssClass());
                    setStyle(item, item.getModelObject().getSelectedCssTrClass());
                } else {
                    setStyle(link, item.getModelObject().getCssLinkClass());
                    setStyle(label, item.getModelObject().getCssClass());
                    setStyle(item, item.getModelObject().getCssTrClass());
                }
            }
        };
        add(dataTable);
        setupBottom(dbMenu);
    }

    private void setupBottom(DbMenu dbMenu) {
        if (dbMenu != null && dbMenu.getBottom() != null) {
            add(cmsUiService.getComponent(dbMenu.getBottom(), null, "bottom", null, contentContext));
        } else {
            add(new Label("bottom", "").setVisible(false));
        }
    }

    private void setStyle(Component component, String style) {
        if (style != null) {
            component.add(new AttributeModifier("class", style));
        }
    }

    private void addSubMenuItems(List<DbMenuItem> menuItemList) {
        DbPage dbPage = (DbPage) Menu.this.getParent().getDefaultModelObject();
        List<DbMenuItem> subMenuItems = null;
        DbMenuItem menuWithSubMenu = null;
        for (DbMenuItem dbMenuItem : menuItemList) {
            List<DbMenuItem> tmpSubMenu = new ArrayList<>();
            if (dbMenuItem.getSubMenu() != null) {
                menuWithSubMenu = dbMenuItem;
                for (DbMenuItem subMenuItem : dbMenuItem.getSubMenu().getMenuItemCrudChildServiceHelper().readDbChildren()) {
                    tmpSubMenu.add(subMenuItem);
                    if (subMenuItem.getPage().equals(dbPage)) {
                        subMenuItems = tmpSubMenu;
                    }
                }
                if (subMenuItems != null) {
                    break;
                }
                if (dbMenuItem.getPage().equals(dbPage)) {
                    subMenuItems = tmpSubMenu;
                    break;
                }
            }
        }
        if (subMenuItems != null) {
            menuItemList.addAll(menuItemList.indexOf(menuWithSubMenu) + 1, subMenuItems);
        }
    }

    private List<DbMenuItem> filterMenuItems(List<DbMenuItem> dbMenuItems) {
        ArrayList<DbMenuItem> filtered = new ArrayList<>();
        for (DbMenuItem dbMenuItem : dbMenuItems) {
            if (cmsUiService.isPageAccessAllowed(dbMenuItem.getPage())) {
                filtered.add(dbMenuItem);
            }
        }
        return filtered;
    }


    @Override
    public boolean isVisible() {
        DbMenu dbMenu = (DbMenu) getDefaultModelObject();
        return dbMenu != null;
    }
}
