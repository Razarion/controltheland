package com.btxtech.game.jsre.client.dialogs.history;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HistoryPanel extends Composite {
    private static final int ENTRY_COUNT = 15;
    private static HistoryPanelUiBinder uiBinder = GWT.create(HistoryPanelUiBinder.class);
    private CellTable.Resources tableRes = GWT.create(TableRes.class);
    private static Logger log = Logger.getLogger(Connection.class.getName());
    @UiField(provided = true)
    CellTable<HistoryElement> cellTable = new CellTable<HistoryElement>(ENTRY_COUNT, tableRes);
    @UiField
    SimplePager simplePager;

    interface HistoryPanelUiBinder extends UiBinder<Widget, HistoryPanel> {
    }

    interface TableRes extends CellTable.Resources {
        @Source({CellTable.Style.DEFAULT_CSS, "com/btxtech/game/jsre/client/dialogs/history/table.css"})
        TableStyle cellTableStyle();

        interface TableStyle extends CellTable.Style {
        }
    }

    public HistoryPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        setupCellTable();
    }

    private void setupCellTable() {
        // Create date column
        Column<HistoryElement, Date> dateColumn = new Column<HistoryElement, Date>(
                new DateCell(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM))) {

            @Override
            public Date getValue(HistoryElement historyElement) {
                return historyElement.getDate();
            }
        };
        cellTable.addColumn(dateColumn, "Date");

        // Create time column
        Column<HistoryElement, Date> timeColumn = new Column<HistoryElement, Date>(
                new DateCell(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.HOUR24_MINUTE_SECOND))) {

            @Override
            public Date getValue(HistoryElement historyElement) {
                return historyElement.getDate();
            }
        };
        cellTable.addColumn(timeColumn, "Time");

        // Create message column
        TextColumn<HistoryElement> messageColumn = new TextColumn<HistoryElement>() {
            @Override
            public String getValue(HistoryElement historyElement) {
                return historyElement.getMessage();
            }
        };
        cellTable.addColumn(messageColumn, "Message");

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
        cellTable.setVisibleRange(0, ENTRY_COUNT);
    }

    private void loadHistoryElements(int start, int length) {
        if (Connection.getMovableServiceAsync() != null) {
            Connection.getMovableServiceAsync().getHistoryElements(start, length,
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
