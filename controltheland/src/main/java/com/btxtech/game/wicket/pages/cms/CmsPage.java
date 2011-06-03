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

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.generated.cms.DbMenuItem;
import com.btxtech.game.services.cms.generated.cms.DbPage;
import com.btxtech.game.wicket.uiservices.DetachHashListProvider;

public class CmsPage extends WebPage {
	private static final String ID = "id";
	@SpringBean
	private CmsService cmsService;

	public CmsPage(PageParameters pageParameters) {
		DbPage dbPage;
		if (pageParameters.containsKey(ID)) {
			int pageId = pageParameters.getInt(ID);
			dbPage = cmsService.getPage(pageId);
		} else {
			dbPage = cmsService.getDefaultPage();
		}
		add(CmsCssResource.createCss("css", dbPage));
		setupMenu(dbPage);
	}

	private void setupMenu(DbPage dbPage) {
		final int pageId = dbPage.getId();
		DetachHashListProvider<DbMenuItem> menuProvider = new DetachHashListProvider<DbMenuItem>() {

			@Override
			protected List<DbMenuItem> createList() {
				return cmsService.getPage(pageId).getMenu().getMenuItems();
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
