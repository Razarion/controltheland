package com.btxtech.game.jsre.client.dialogs.highscore;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.widget.client.TextButton;

import java.util.Collection;
import java.util.Comparator;

public class HighscoreTable extends Composite {

    private static HighscoreTableUiBinder uiBinder = GWT.create(HighscoreTableUiBinder.class);
    @UiField(provided = true)
    DataGrid<CurrentStatisticEntryInfo> dataGrid = new DataGrid<CurrentStatisticEntryInfo>();
    @UiField
    TextButton findMeButton;
    @UiField
    TextButton refreshButton;
    private ListDataProvider<CurrentStatisticEntryInfo> dataProvider;
    private Column<CurrentStatisticEntryInfo, Number> rankColumn;
    private CurrentStatisticEntryInfo myEntry;
    private HighscoreDialog highscoreDialog;

    interface HighscoreTableUiBinder extends UiBinder<Widget, HighscoreTable> {
    }

    public interface DataGridResource extends DataGrid.Resources {
        @Source({DataGrid.Style.DEFAULT_CSS, "DataGridOverride.css"})
        DataGrid.Style dataGridStyle();
    }

    public HighscoreTable(HighscoreDialog highscoreDialog) {
        this.highscoreDialog = highscoreDialog;
        dataGrid = new DataGrid<CurrentStatisticEntryInfo>(10, (DataGrid.Resources) GWT.create(DataGridResource.class));
        initWidget(uiBinder.createAndBindUi(this));
        dataGrid.setSelectionModel(new SingleSelectionModel<CurrentStatisticEntryInfo>());
        dataProvider = new ListDataProvider<CurrentStatisticEntryInfo>();
        dataProvider.addDataDisplay(dataGrid);
        setupColumns();
    }

    public void setHighscore(Collection<CurrentStatisticEntryInfo> highscore) {
        dataProvider.getList().clear();
        myEntry = null;
        for (CurrentStatisticEntryInfo currentStatisticEntryInfo : highscore) {
            dataProvider.getList().add(currentStatisticEntryInfo);
            if (currentStatisticEntryInfo.isMy()) {
                myEntry = currentStatisticEntryInfo;
            }
        }
        dataProvider.flush();
        dataGrid.setRowCount(highscore.size(), true);
        dataGrid.setPageSize(highscore.size());
        // Default sort column
        dataGrid.getColumnSortList().clear();
        dataGrid.getColumnSortList().push(new ColumnSortList.ColumnSortInfo(rankColumn, true));
        ColumnSortEvent.fire(dataGrid, dataGrid.getColumnSortList());
        showMe(); // TODO not working
    }

    private void setupColumns() {
        // rank column
        rankColumn = new Column<CurrentStatisticEntryInfo, Number>(new NumberCell()) {

            @Override
            public Number getValue(CurrentStatisticEntryInfo statInfo) {
                return statInfo.getRank();
            }
        };
        rankColumn.setSortable(true);
        dataGrid.addColumn(rankColumn, ClientI18nHelper.CONSTANTS.rank());
        // score column
        Column<CurrentStatisticEntryInfo, Number> scoreColumn = new Column<CurrentStatisticEntryInfo, Number>(new NumberCell()) {
            @Override
            public Number getValue(CurrentStatisticEntryInfo statInfo) {
                return statInfo.getScore();
            }
        };
        scoreColumn.setSortable(true);
        dataGrid.addColumn(scoreColumn, "Score");
        // user name column
        Column<CurrentStatisticEntryInfo, String> userNameColumn = new Column<CurrentStatisticEntryInfo, String>(new TextCell()) {

            @Override
            public String getValue(CurrentStatisticEntryInfo statInfo) {
                return statInfo.getUserName();
            }
        };
        dataGrid.setColumnWidth(userNameColumn, "15em");
        dataGrid.addColumn(userNameColumn, ClientI18nHelper.CONSTANTS.player());
        // planet column
        Column<CurrentStatisticEntryInfo, String> planetColumn = new Column<CurrentStatisticEntryInfo, String>(new TextCell()) {

            @Override
            public String getValue(CurrentStatisticEntryInfo statInfo) {
                return statInfo.getPlanet();
            }
        };
        dataGrid.addColumn(planetColumn, ClientI18nHelper.CONSTANTS.planet());
        // itemCount column
        Column<CurrentStatisticEntryInfo, Number> itemColumn = new Column<CurrentStatisticEntryInfo, Number>(new NumberCell()) {

            @Override
            public Number getValue(CurrentStatisticEntryInfo statInfo) {
                return statInfo.getItemCount();
            }
        };
        itemColumn.setSortable(true);
        dataGrid.addColumn(itemColumn, ClientI18nHelper.CONSTANTS.items());
        // money column
        Column<CurrentStatisticEntryInfo, Number> moneyColumn = new Column<CurrentStatisticEntryInfo, Number>(new NumberCell()) {

            @Override
            public Number getValue(CurrentStatisticEntryInfo statInfo) {
                return statInfo.getMoney();
            }
        };
        moneyColumn.setSortable(true);
        dataGrid.addColumn(moneyColumn, ClientI18nHelper.CONSTANTS.money());
        // killed column
        Column<CurrentStatisticEntryInfo, Number> killedColumn = new Column<CurrentStatisticEntryInfo, Number>(new NumberCell()) {

            @Override
            public Number getValue(CurrentStatisticEntryInfo statInfo) {
                return statInfo.getKilled();
            }
        };
        killedColumn.setSortable(true);
        dataGrid.addColumn(killedColumn, ClientI18nHelper.CONSTANTS.killed());
        // killed pve column
        Column<CurrentStatisticEntryInfo, Number> killedPveColumn = new Column<CurrentStatisticEntryInfo, Number>(new NumberCell()) {

            @Override
            public Number getValue(CurrentStatisticEntryInfo statInfo) {
                return statInfo.getKilledPve();
            }
        };
        killedPveColumn.setSortable(true);
        dataGrid.addColumn(killedPveColumn, ClientI18nHelper.CONSTANTS.killedPve());
        // killed pvp column
        Column<CurrentStatisticEntryInfo, Number> killedPvpColumn = new Column<CurrentStatisticEntryInfo, Number>(new NumberCell()) {

            @Override
            public Number getValue(CurrentStatisticEntryInfo statInfo) {
                return statInfo.getKilledPvp();
            }
        };
        killedPvpColumn.setSortable(true);
        dataGrid.addColumn(killedPvpColumn, ClientI18nHelper.CONSTANTS.killedPvp());
        // bases killed column
        Column<CurrentStatisticEntryInfo, Number> basesKilled = new Column<CurrentStatisticEntryInfo, Number>(new NumberCell()) {

            @Override
            public Number getValue(CurrentStatisticEntryInfo statInfo) {
                return statInfo.getBasesKilled();
            }
        };
        basesKilled.setSortable(true);
        dataGrid.addColumn(basesKilled, ClientI18nHelper.CONSTANTS.basesKilled());
        // bases lost column
        Column<CurrentStatisticEntryInfo, Number> basesLost = new Column<CurrentStatisticEntryInfo, Number>(new NumberCell()) {

            @Override
            public Number getValue(CurrentStatisticEntryInfo statInfo) {
                return statInfo.getBasesLost();
            }
        };
        basesLost.setSortable(true);
        dataGrid.addColumn(basesLost, ClientI18nHelper.CONSTANTS.basesLost());
        // created column
        Column<CurrentStatisticEntryInfo, Number> created = new Column<CurrentStatisticEntryInfo, Number>(new NumberCell()) {

            @Override
            public Number getValue(CurrentStatisticEntryInfo statInfo) {
                return statInfo.getCreated();
            }
        };
        created.setSortable(true);
        dataGrid.addColumn(created, ClientI18nHelper.CONSTANTS.created());

        // Rank sorting
        ColumnSortEvent.ListHandler<CurrentStatisticEntryInfo> rankSortHandler = new ColumnSortEvent.ListHandler<CurrentStatisticEntryInfo>(dataProvider.getList());
        rankSortHandler.setComparator(rankColumn, new HighscoreComparatorInt() {

            @Override
            protected int getInt(CurrentStatisticEntryInfo currentStatisticEntryInfo) {
                return currentStatisticEntryInfo.getRank();
            }
        });
        dataGrid.addColumnSortHandler(rankSortHandler);
        // Score sorting
        ColumnSortEvent.ListHandler<CurrentStatisticEntryInfo> scoreSortHandler = new ColumnSortEvent.ListHandler<CurrentStatisticEntryInfo>(dataProvider.getList());
        scoreSortHandler.setComparator(scoreColumn, new HighscoreComparatorInt() {

            @Override
            protected int getInt(CurrentStatisticEntryInfo currentStatisticEntryInfo) {
                return currentStatisticEntryInfo.getScore();
            }
        });
        dataGrid.addColumnSortHandler(scoreSortHandler);
        // itemColumn sorting
        ColumnSortEvent.ListHandler<CurrentStatisticEntryInfo> itemColumnSortHandler = new ColumnSortEvent.ListHandler<CurrentStatisticEntryInfo>(dataProvider.getList());
        itemColumnSortHandler.setComparator(itemColumn, new HighscoreComparatorInteger() {

            @Override
            protected Integer getInteger(CurrentStatisticEntryInfo currentStatisticEntryInfo) {
                return currentStatisticEntryInfo.getItemCount();
            }
        });
        dataGrid.addColumnSortHandler(itemColumnSortHandler);
        // moneyColumn sorting
        ColumnSortEvent.ListHandler<CurrentStatisticEntryInfo> moneyColumnSortHandler = new ColumnSortEvent.ListHandler<CurrentStatisticEntryInfo>(dataProvider.getList());
        moneyColumnSortHandler.setComparator(moneyColumn, new HighscoreComparatorInteger() {

            @Override
            protected Integer getInteger(CurrentStatisticEntryInfo currentStatisticEntryInfo) {
                return currentStatisticEntryInfo.getMoney();
            }
        });
        dataGrid.addColumnSortHandler(moneyColumnSortHandler);
        // killedColumn sorting
        ColumnSortEvent.ListHandler<CurrentStatisticEntryInfo> killedColumnSortHandler = new ColumnSortEvent.ListHandler<CurrentStatisticEntryInfo>(dataProvider.getList());
        killedColumnSortHandler.setComparator(killedColumn, new HighscoreComparatorInt() {

            @Override
            protected int getInt(CurrentStatisticEntryInfo currentStatisticEntryInfo) {
                return currentStatisticEntryInfo.getKilled();
            }
        });
        dataGrid.addColumnSortHandler(killedColumnSortHandler);
        // killedPveColumn sorting
        ColumnSortEvent.ListHandler<CurrentStatisticEntryInfo> killedPveColumnSortHandler = new ColumnSortEvent.ListHandler<CurrentStatisticEntryInfo>(dataProvider.getList());
        killedPveColumnSortHandler.setComparator(killedPveColumn, new HighscoreComparatorInt() {

            @Override
            protected int getInt(CurrentStatisticEntryInfo currentStatisticEntryInfo) {
                return currentStatisticEntryInfo.getKilledPve();
            }
        });
        dataGrid.addColumnSortHandler(killedPveColumnSortHandler);
        // killedPvpColumn sorting
        ColumnSortEvent.ListHandler<CurrentStatisticEntryInfo> killedPvpColumnSortHandler = new ColumnSortEvent.ListHandler<CurrentStatisticEntryInfo>(dataProvider.getList());
        killedPvpColumnSortHandler.setComparator(killedPvpColumn, new HighscoreComparatorInt() {

            @Override
            protected int getInt(CurrentStatisticEntryInfo currentStatisticEntryInfo) {
                return currentStatisticEntryInfo.getKilledPvp();
            }
        });
        dataGrid.addColumnSortHandler(killedPvpColumnSortHandler);
        // basesKilled sorting
        ColumnSortEvent.ListHandler<CurrentStatisticEntryInfo> basesKilledColumnSortHandler = new ColumnSortEvent.ListHandler<CurrentStatisticEntryInfo>(dataProvider.getList());
        basesKilledColumnSortHandler.setComparator(basesKilled, new HighscoreComparatorInt() {

            @Override
            protected int getInt(CurrentStatisticEntryInfo currentStatisticEntryInfo) {
                return currentStatisticEntryInfo.getBasesKilled();
            }
        });
        dataGrid.addColumnSortHandler(basesKilledColumnSortHandler);
        // basesLost sorting
        ColumnSortEvent.ListHandler<CurrentStatisticEntryInfo> basesLostColumnSortHandler = new ColumnSortEvent.ListHandler<CurrentStatisticEntryInfo>(dataProvider.getList());
        basesLostColumnSortHandler.setComparator(basesLost, new HighscoreComparatorInt() {

            @Override
            protected int getInt(CurrentStatisticEntryInfo currentStatisticEntryInfo) {
                return currentStatisticEntryInfo.getBasesLost();
            }
        });
        dataGrid.addColumnSortHandler(basesLostColumnSortHandler);
        // created sorting
        ColumnSortEvent.ListHandler<CurrentStatisticEntryInfo> createdColumnSortHandler = new ColumnSortEvent.ListHandler<CurrentStatisticEntryInfo>(dataProvider.getList());
        createdColumnSortHandler.setComparator(created, new HighscoreComparatorInt() {

            @Override
            protected int getInt(CurrentStatisticEntryInfo currentStatisticEntryInfo) {
                return currentStatisticEntryInfo.getCreated();
            }
        });
        dataGrid.addColumnSortHandler(createdColumnSortHandler);
    }

    private abstract class HighscoreComparatorInt implements Comparator<CurrentStatisticEntryInfo> {
        public int compare(CurrentStatisticEntryInfo o1, CurrentStatisticEntryInfo o2) {
            if (o1 == o2) {
                return 0;
            }
            // Compare the name columns.
            if (o1 != null) {
                return (o2 != null) ? (getInt(o1) < getInt(o2)) ? -1 : ((getInt(o1) == getInt(o2)) ? 0 : 1) : -1;
            }
            return -1;
        }

        protected abstract int getInt(CurrentStatisticEntryInfo currentStatisticEntryInfo);
    }

    private abstract class HighscoreComparatorInteger implements Comparator<CurrentStatisticEntryInfo> {
        public int compare(CurrentStatisticEntryInfo o1, CurrentStatisticEntryInfo o2) {
            if (o1 == o2) {
                return 0;
            }
            // Compare the name columns.
            if (o1 != null) {
                if (o2 != null) {
                    if (getInteger(o1) != null && getInteger(o2) != null) {
                        return getInteger(o1).compareTo(getInteger(o2));
                    } else if (getInteger(o1) != null) {
                        return 1;
                    } else if (getInteger(o2) != null) {
                        return -1;
                    } else {
                        return 0;
                    }
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        }

        protected abstract Integer getInteger(CurrentStatisticEntryInfo currentStatisticEntryInfo);
    }

    @UiHandler("findMeButton")
    void onFindMeButtonClick(ClickEvent event) {
        showMe();
    }

    private void showMe() {
        if (myEntry != null) {
            dataGrid.getRowElement(dataProvider.getList().indexOf(myEntry)).scrollIntoView();
            dataGrid.getSelectionModel().setSelected(myEntry, true);
        }
    }

    @UiHandler("refreshButton")
    void onRefreshButtonClick(ClickEvent event) {
        Connection.getInstance().loadCurrentStatisticEntryInfos(highscoreDialog);
    }
}
