package com.btxtech.game.jsre.client.dialogs.news;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class NewsCell extends AbstractCell<NewsEntryInfo> {

	@Override
	public void render(Context context,	NewsEntryInfo newsEntryInfo, SafeHtmlBuilder sb) {
	      if (newsEntryInfo == null) {
	          return;
	        }
	      
	      sb.appendHtmlConstant("<table width='100%' cellspacing='0'>");

	      // Add title.
	      sb.appendHtmlConstant("<tr style='background-color: #cfcfcf;font-weight:bold'><td>");
	      sb.appendHtmlConstant(newsEntryInfo.getTitle());
	      sb.appendHtmlConstant("</td>");
	      // Add date.
	      sb.appendHtmlConstant("<td align='right'>");
	      sb.appendHtmlConstant(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM).format(newsEntryInfo.getDate()));
	      sb.appendHtmlConstant(" ");
	      sb.appendHtmlConstant(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.HOUR24_MINUTE).format(newsEntryInfo.getDate()));
	      sb.appendHtmlConstant("</td></tr>");
	      // Add content.
	      sb.appendHtmlConstant("<tr><td colspan='2' style='padding-top: 5px;'>");
	      sb.appendHtmlConstant(newsEntryInfo.getContent());
	      sb.appendHtmlConstant("</td></tr>");
	      
	      sb.appendHtmlConstant("</table>");
	}
}
