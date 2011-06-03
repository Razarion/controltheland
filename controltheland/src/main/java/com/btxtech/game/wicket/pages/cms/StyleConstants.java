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
