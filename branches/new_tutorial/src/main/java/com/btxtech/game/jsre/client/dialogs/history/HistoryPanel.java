package com.btxtech.game.jsre.client.dialogs.history;

import com.btxtech.game.jsre.client.Connection;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import java.util.logging.Logger;

public class HistoryPanel extends Composite {
    private static final int ENTRY_COUNT = 15;
    private static HistoryPanelUiBinder uiBinder = GWT.create(HistoryPanelUiBinder.class);
    private CellTable.Resources tableRes = GWT.create(TableRes.class);
    @UiField(provided = true)
    CellTable<HistoryElement> cellTable = new CellTable<HistoryElement>(ENTRY_COUNT, tableRes);
    @UiField
    SimplePager simplePager;

    interface HistoryPanelUiBinder extends UiBinder<Widget, HistoryPanel> {
    }

    public interface TableRes extends CellTable.Resources {
        @Source({"com/btxtech/game/jsre/client/dialogs/history/table.css"})
        TableStyle cellTableStyle();

        interface TableStyle extends CellTable.Style {
        }
    }

    public HistoryPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        HistoryPanelFacade historyPanelFacade = new HistoryPanelFacade(cellTable, simplePager, HistoryFilter.createUserFilter());
        historyPanelFacade.setupCellTable();
    }
}
