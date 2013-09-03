package com.btxtech.game.jsre.client.dialogs.news;

import com.btxtech.game.jsre.client.Connection;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NewsPanel extends Composite {
    private static final int ENTRIES = 1;
    private static CellTable.Resources tableRes = GWT.create(TableRes.class);
    private static NewsPanelUiBinder uiBinder = GWT.create(NewsPanelUiBinder.class);
    @UiField(provided = true)
    CellTable<NewsEntryInfo> cellTable = new CellTable<NewsEntryInfo>(ENTRIES, tableRes);
    @UiField
    SimplePager simplePager;
    private static Logger log = Logger.getLogger(NewsPanel.class.getName());

    interface NewsPanelUiBinder extends UiBinder<Widget, NewsPanel> {
    }

    interface TableRes extends CellTable.Resources {
        interface CellStyle extends CellTable.Style {
        }

        @Source({CellTable.Style.DEFAULT_CSS, "com/btxtech/game/jsre/client/dialogs/news/table.css"})
        CellStyle cellTableStyle();
    }


    public NewsPanel() {
        initWidget(uiBinder.createAndBindUi(this));

        cellTable.addColumn(new Column<NewsEntryInfo, NewsEntryInfo>(new NewsCell()) {
            @Override
            public NewsEntryInfo getValue(NewsEntryInfo newsEntryInfo) {
                return newsEntryInfo;
            }

        });

        AsyncDataProvider<NewsEntryInfo> dataProvider = new AsyncDataProvider<NewsEntryInfo>() {

            @Override
            protected void onRangeChanged(HasData<NewsEntryInfo> display) {
                final int start = display.getVisibleRange().getStart();
                if (Connection.getMovableServiceAsync() != null) {
                    Connection.getMovableServiceAsync().getNewsEntry(start, new AsyncCallback<NewsEntryInfo>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            log.log(Level.WARNING, "MovableService.getNewsEntry()", caught);
                        }

                        @Override
                        public void onSuccess(NewsEntryInfo newsEntry) {
                            if (newsEntry != null) {
                                cellTable.setRowData(start, Collections.singletonList(newsEntry));
                                cellTable.setRowCount(newsEntry.getTotalEntries(), true);
                            } else {
                                cellTable.setRowData(start, Collections.<NewsEntryInfo>emptyList());
                                cellTable.setRowCount(0, true);
                            }
                        }

                    });
                }
            }
        };

        cellTable.setVisibleRange(0, ENTRIES);
        dataProvider.addDataDisplay(cellTable);
        simplePager.setDisplay(cellTable);
    }

}
