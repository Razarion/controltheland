package com.btxtech.game.jsre.client.dialogs.history;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 21.06.13
 * Time: 14:02
 */
public class HistoryPanelFacade {
    private CellTable<HistoryElement> cellTable;
    private SimplePager simplePager;
    private HistoryFilter historyFilter;
    private static Logger log = Logger.getLogger(HistoryPanelFacade.class.getName());

    public HistoryPanelFacade(CellTable<HistoryElement> cellTable, SimplePager simplePager, HistoryFilter historyFilter) {
        this.cellTable = cellTable;
        this.simplePager = simplePager;
        this.historyFilter = historyFilter;
    }

    public void setupCellTable() {
        // Create date column
        Column<HistoryElement, Date> dateColumn = new Column<HistoryElement, Date>(
                new DateCell(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM))) {

            @Override
            public Date getValue(HistoryElement historyElement) {
                return historyElement.getDate();
            }
        };
        cellTable.addColumn(dateColumn, ClientI18nHelper.CONSTANTS.date());

        // Create time column
        Column<HistoryElement, Date> timeColumn = new Column<HistoryElement, Date>(
                new DateCell(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.HOUR24_MINUTE_SECOND))) {

            @Override
            public Date getValue(HistoryElement historyElement) {
                return historyElement.getDate();
            }
        };
        cellTable.addColumn(timeColumn, ClientI18nHelper.CONSTANTS.time());

        // Create message column
        TextColumn<HistoryElement> messageColumn = new TextColumn<HistoryElement>() {
            @Override
            public String getValue(HistoryElement historyElement) {
                return historyElement.getMessage();
            }
        };
        cellTable.addColumn(messageColumn, ClientI18nHelper.CONSTANTS.event());

        // Create a data provider.
        AsyncDataProvider<HistoryElement> dataProvider = new AsyncDataProvider<HistoryElement>() {
            @Override
            protected void onRangeChanged(HasData<HistoryElement> display) {
                Range range = display.getVisibleRange();
                loadHistoryElements(range.getStart(), range.getLength());
            }
        };
        dataProvider.addDataDisplay(cellTable);
        simplePager.setDisplay(cellTable);
        cellTable.setVisibleRange(0, cellTable.getPageSize());
    }

    public void loadHistoryElements(int start, int length) {
        if (Connection.getMovableServiceAsync() != null) {
            historyFilter.setStart(start);
            historyFilter.setLength(length);
            Connection.getMovableServiceAsync().getHistoryElements(historyFilter,
                    new AsyncCallback<HistoryElementInfo>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            log.log(Level.WARNING, "MovableService.getHistoryElements()", caught);
                        }

                        @Override
                        public void onSuccess(HistoryElementInfo historyElementInfo) {
                            // Set the total row count. This isn't strictly necessary, but it affects
                            // paging calculations, so its good habit to keep the row count up to date.
                            cellTable.setRowCount(historyElementInfo.getTotalRowCount(), true);
                            cellTable.setRowData(historyElementInfo.getStartRow(), historyElementInfo.getHistoryElements());
                        }
                    });
        }
    }
}
