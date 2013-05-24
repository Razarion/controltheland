package com.btxtech.game.jsre.client.dialogs.history;

import java.io.Serializable;
import java.util.Date;

public class HistoryElement implements Serializable {
    private Date date;
    private String message;
    
    /**
     * Used by GWT
     */
    HistoryElement() {
	}

	public HistoryElement(Date date, String message) {
		this.date = date;
		this.message = message;
	}

	public Date getDate() {
		return date;
	}

	public String getMessage() {
		return message;
	}
}
