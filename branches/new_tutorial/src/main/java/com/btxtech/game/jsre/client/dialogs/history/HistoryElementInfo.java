package com.btxtech.game.jsre.client.dialogs.history;

import java.io.Serializable;
import java.util.List;

public class HistoryElementInfo implements Serializable {
	private List<HistoryElement> historyElements;
	private int startRow;
	private int totalRowCount;

	/**
	 * Used by GWT
	 */
	HistoryElementInfo() {
	}

	public HistoryElementInfo(List<HistoryElement> historyElements, int startRow, int totalRowCount) {
		this.historyElements = historyElements;
		this.startRow = startRow;
		this.totalRowCount = totalRowCount;
	}

	public List<HistoryElement> getHistoryElements() {
		return historyElements;
	}

	public int getStartRow() {
		return startRow;
	}

	public int getTotalRowCount() {
		return totalRowCount;
	}

}
