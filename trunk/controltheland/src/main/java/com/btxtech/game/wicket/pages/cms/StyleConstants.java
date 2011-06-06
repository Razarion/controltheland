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

public enum StyleConstants {
	MENU_TABLE_CLASS("menuTableClass"),
	MENU_TABLE_ROW_CLASS("menuTableRowClass"),
	MENU_TABLE_ROW_SELECTED_CLASS("menuTableRowSelectedClass"),
	MENU_LINK_CLASS("menuLinkClass"),
	MENU_LINK_SELECTED_CLASS("menuLinkSelectedClass"),
	MENU_LABEL_CLASS("menuLabelClass"),
	MENU_LABEL_SELECTED_CLASS("menuLabelSelectedClass");

	private String styleName;

	StyleConstants(String styleName) {
		this.styleName = styleName;
	}

	public String getStyleName() {
		return styleName;
	}

}
