package com.btxtech.game.jsre.client.dialogs.news;

import java.io.Serializable;
import java.util.Date;

public class NewsEntryInfo implements Serializable{
	private String title;
	private Date date;
	private String content;
    private int totalEntries;
	
	/**
	 * Used by GWT
	 */
	NewsEntryInfo() {
	}
	
	public NewsEntryInfo(String title, Date date, String content, int totalEntries) {
		this.title = title;
		this.date = date;
		this.content = content;
		this.totalEntries = totalEntries;
	}

	public String getTitle() {
		return title;
	}

	public Date getDate() {
		return date;
	}

	public String getContent() {
		return content;
	}

	public int getTotalEntries() {
		return totalEntries;
	}
}
