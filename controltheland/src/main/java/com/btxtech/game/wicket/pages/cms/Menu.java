package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.services.cms.DbMenu;
import com.btxtech.game.services.cms.DbMenuItem;
import com.btxtech.game.services.cms.DbPage;
import com.btxtech.game.wicket.uiservices.DetachHashListProvider;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
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

    public Menu(String id) {
        super(id);
        DetachHashListProvider<DbMenuItem> menuProvider = new DetachHashListProvider<DbMenuItem>() {

            @Override
            protected List<DbMenuItem> createList() {
                DbMenu dbMenu = (DbMenu) Menu.this.getDefaultModelObject();
                if (dbMenu != null) {
                    return filterMenuItems(dbMenu.getMenuItemCrudChildServiceHelper().readDbChildren());
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
                pageParameters.add(CmsPage.ID, Integer.toString(item.getModelObject().getPage().getId()));
                BookmarkablePageLink<CmsPage> link = new BookmarkablePageLink<CmsPage>("menuLink", CmsPage.class, pageParameters);

                Label label = new Label("menuLinkName", item.getModelObject().getName());
                link.add(label);
                item.add(link);

                if (selected) {
                    link.add(new SimpleAttributeModifier("class", StyleConstants.MENU_LINK_SELECTED_CLASS.getStyleName()));
                    label.add(new SimpleAttributeModifier("class", StyleConstants.MENU_LABEL_SELECTED_CLASS.getStyleName()));
                    item.add(new SimpleAttributeModifier("class", StyleConstants.MENU_TABLE_ROW_SELECTED_CLASS.getStyleName()));
                } else {
                    link.add(new SimpleAttributeModifier("class", StyleConstants.MENU_LINK_CLASS.getStyleName()));
                    label.add(new SimpleAttributeModifier("class", StyleConstants.MENU_LABEL_CLASS.getStyleName()));
                    item.add(new SimpleAttributeModifier("class", StyleConstants.MENU_TABLE_ROW_CLASS.getStyleName()));
                }
            }
        };
        add(dataTable);

    }

    private List<DbMenuItem> filterMenuItems(List<DbMenuItem> dbMenuItems) {
        ArrayList<DbMenuItem> filtered = new ArrayList<DbMenuItem>();
        for (DbMenuItem dbMenuItem : dbMenuItems) {
            if (cmsUiService.isPageAccessAllowed(dbMenuItem.getPage())) {
                filtered.add(dbMenuItem);
            }
        }
        return filtered;
    }


    @Override
    public boolean isVisible() {
        DbMenu dbMenu = (DbMenu) Menu.this.getDefaultModelObject();
        return dbMenu != null;
    }
}
