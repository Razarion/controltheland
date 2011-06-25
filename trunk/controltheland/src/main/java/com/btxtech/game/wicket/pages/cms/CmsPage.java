/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.DbMenu;
import com.btxtech.game.services.cms.DbMenuItem;
import com.btxtech.game.services.cms.DbPage;
import com.btxtech.game.wicket.pages.user.LoggedinBox;
import com.btxtech.game.wicket.pages.user.LoginBox;
import com.btxtech.game.wicket.uiservices.DetachHashListProvider;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Collections;
import java.util.List;

public class CmsPage extends WebPage {
    public static final String ID = "id";
    public static final String CHILD_ID = "childId";
    @SpringBean
    private CmsService cmsService;
    @SpringBean
    private CmsUiService cmsUiService;
    private int pageId;

    public CmsPage(final PageParameters pageParameters) {
        setDefaultModel(new CompoundPropertyModel<DbPage>(new LoadableDetachableModel<DbPage>() {

            @Override
            protected DbPage load() {
                DbPage dbPage;
                if (pageParameters.containsKey(ID)) {
                    pageId = pageParameters.getInt(ID);
                    dbPage = cmsService.getPage(pageId);
                } else {
                    dbPage = cmsService.getHomePage();
                    pageId = dbPage.getId();
                }
                return dbPage;
            }
        }));
        DbPage dbPage = (DbPage) getDefaultModelObject();
        add(CmsCssResource.createCss("css", dbPage));
        setupMenu();
        setupLoginBox();
        Form form = new Form("form");
        add(form);
        form.add(cmsUiService.getRootComponent(dbPage, "content", pageParameters));
    }

    private void setupLoginBox() {
        add(new LoggedinBox("loggedinBox"));
        add(new LoginBox("loginBox", true));
    }

    private void setupMenu() {
        DetachHashListProvider<DbMenuItem> menuProvider = new DetachHashListProvider<DbMenuItem>() {

            @Override
            protected List<DbMenuItem> createList() {
                DbMenu dbMenu = cmsService.getPage(pageId).getMenu();
                if (dbMenu != null) {
                    return dbMenu.getMenuItemCrudChildServiceHelper().readDbChildren();
                } else {
                    return Collections.emptyList();
                }
            }
        };

        DataView<DbMenuItem> dataTable = new DataView<DbMenuItem>("menuTable", menuProvider) {
            protected void populateItem(final Item<DbMenuItem> item) {
                boolean selected = item.getModelObject().getPage().getId() == pageId;
                PageParameters pageParameters = new PageParameters();
                pageParameters.add(ID, Integer.toString(item.getModelObject().getPage().getId()));
                BookmarkablePageLink<CmsPage> link = new BookmarkablePageLink<CmsPage>("menuLink", CmsPage.class, pageParameters);

                // TODO security if (item.getModelObject().isAdminOnly()) {
                // link = new AdminBookmarkablePageLink<WebPage>("link",
                // linkItem.getModelObject().destination);
                // } else {

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

}
