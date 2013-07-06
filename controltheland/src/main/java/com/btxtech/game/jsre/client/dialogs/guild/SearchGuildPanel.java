package com.btxtech.game.jsre.client.dialogs.guild;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.history.HistoryPanel;
import com.btxtech.game.jsre.client.widget.EmptyTableWidget;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;

public class SearchGuildPanel extends Composite {
    public static final int ENTRY_COUNT = 20;
    private static SearchGuildPanelUiBinder uiBinder = GWT.create(SearchGuildPanelUiBinder.class);
    private CellTable.Resources tableRes = GWT.create(HistoryPanel.TableRes.class);
    @UiField(provided = true)
    CellTable<GuildDetailedInfo> guildTable = new CellTable<GuildDetailedInfo>(ENTRY_COUNT, tableRes);
    @UiField
    TextBox guildName;
    @UiField
    SimplePager simplePager;

    interface SearchGuildPanelUiBinder extends UiBinder<Widget, SearchGuildPanel> {
    }

    public SearchGuildPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        // Name column
        guildTable.addColumn(new TextColumn<GuildDetailedInfo>() {
            @Override
            public String getValue(GuildDetailedInfo guildDetailedInfo) {
                return guildDetailedInfo.getName();
            }
        }, ClientI18nHelper.CONSTANTS.name());
        // Text column
        Column<GuildDetailedInfo, SafeHtml> textColumn = new Column<GuildDetailedInfo, SafeHtml>(new SafeHtmlCell()) {

            @Override
            public SafeHtml getValue(GuildDetailedInfo guildDetailedInfo) {
                if(guildDetailedInfo.getText() != null) {
                    return SafeHtmlUtils.fromTrustedString(guildDetailedInfo.getText());
                } else {
                    return null;
                }
            }
        };
        guildTable.addColumn(textColumn, ClientI18nHelper.CONSTANTS.guildTextShort());
        // Members column
        guildTable.addColumn(new TextColumn<GuildDetailedInfo>() {
            @Override
            public String getValue(GuildDetailedInfo guildDetailedInfo) {
                return Integer.toString(guildDetailedInfo.getMembers());
            }
        }, ClientI18nHelper.CONSTANTS.guildMembers());
        // Request membership column
        Column<GuildDetailedInfo, String> requestColumn = new Column<GuildDetailedInfo, String>(new ButtonCell()) {

            @Override
            public String getValue(GuildDetailedInfo guildMemberInfo) {
                return ClientI18nHelper.CONSTANTS.guildMembershipRequest();
            }
        };
        requestColumn.setFieldUpdater(new FieldUpdater<GuildDetailedInfo, String>() {
            @Override
            public void update(int index, GuildDetailedInfo guildDetailedInfo, String value) {
                DialogManager.showDialog(new MembershipRequestPanel(guildDetailedInfo.getId(), guildDetailedInfo.getName()), DialogManager.Type.STACK_ABLE);
            }
        });
        guildTable.addColumn(requestColumn, ClientI18nHelper.CONSTANTS.guildMembershipRequestTitle());
        // Create a data provider.
        AsyncDataProvider<GuildDetailedInfo> dataProvider = new AsyncDataProvider<GuildDetailedInfo>() {
            @Override
            protected void onRangeChanged(HasData<GuildDetailedInfo> display) {
                Range range = display.getVisibleRange();
                loadGuilds(range.getStart(), range.getLength(), "");
            }
        };
        dataProvider.addDataDisplay(guildTable);
        simplePager.setDisplay(guildTable);
        guildTable.setVisibleRange(0, ENTRY_COUNT);
        guildTable.setEmptyTableWidget(new EmptyTableWidget(ClientI18nHelper.CONSTANTS.noGuilds()));
    }

    private void loadGuilds(int start, int length, String guildNameQuery) {
        if (Connection.getMovableServiceAsync() != null) {
            Connection.getMovableServiceAsync().searchGuilds(start, length, guildNameQuery, new AsyncCallback<SearchGuildsResult>() {

                @Override
                public void onFailure(Throwable caught) {
                    ClientExceptionHandler.handleException("MovableServiceAsync.searchGuildes()", caught);
                }

                @Override
                public void onSuccess(SearchGuildsResult searchGuildsResult) {
                    guildTable.setRowCount(searchGuildsResult.getTotalRowCount(), true);
                    guildTable.setRowData(searchGuildsResult.getStartRow(), searchGuildsResult.getGuildDetailedInfos());
                }
            });
        }
    }

    @UiHandler("guildName")
    void onGuildNameKeyUp(KeyUpEvent event) {
        loadGuilds(0, ENTRY_COUNT, guildName.getText());
    }

    @UiHandler("guildName")
    void onGuildNameChange(ChangeEvent event) {
        loadGuilds(0, ENTRY_COUNT, guildName.getText());
    }

    @UiHandler("guildName")
    void onGuildNameValueChange(ValueChangeEvent<String> event) {
        loadGuilds(0, ENTRY_COUNT, guildName.getText());
    }
}
