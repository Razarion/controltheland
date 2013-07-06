package com.btxtech.game.jsre.client.dialogs.guild;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.YesNoDialog;
import com.btxtech.game.jsre.client.dialogs.history.HistoryPanel;
import com.btxtech.game.jsre.client.widget.EmptyTableWidget;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

import java.util.List;

public class GuildInvitationsPanel extends Composite {

    private static GuildInvitationsPanelUiBinder uiBinder = GWT.create(GuildInvitationsPanelUiBinder.class);
    private CellTable.Resources tableRes = GWT.create(HistoryPanel.TableRes.class);
    @UiField(provided = true)
    CellTable<GuildDetailedInfo> invitationsTable = new CellTable<GuildDetailedInfo>(1000, tableRes);

    interface GuildInvitationsPanelUiBinder extends UiBinder<Widget, GuildInvitationsPanel> {
    }

    public GuildInvitationsPanel(final Dialog dialog) {
        initWidget(uiBinder.createAndBindUi(this));
        // Name column
        invitationsTable.addColumn(new TextColumn<GuildDetailedInfo>() {
            @Override
            public String getValue(GuildDetailedInfo guildDetailedInfo) {
                return guildDetailedInfo.getName();
            }
        }, ClientI18nHelper.CONSTANTS.name());
        // Text column
        Column<GuildDetailedInfo, SafeHtml> textColumn = new Column<GuildDetailedInfo, SafeHtml>(new SafeHtmlCell()) {

            @Override
            public SafeHtml getValue(GuildDetailedInfo guildDetailedInfo) {
                if (guildDetailedInfo.getText() != null) {
                    return SafeHtmlUtils.fromTrustedString(guildDetailedInfo.getText());
                } else {
                    return null;
                }
            }
        };
        invitationsTable.addColumn(textColumn, ClientI18nHelper.CONSTANTS.guildText());
        // Members column
        invitationsTable.addColumn(new TextColumn<GuildDetailedInfo>() {
            @Override
            public String getValue(GuildDetailedInfo guildDetailedInfo) {
                return Integer.toString(guildDetailedInfo.getMembers());
            }
        }, ClientI18nHelper.CONSTANTS.guildMembers());
        // Enter guild column
        Column<GuildDetailedInfo, String> requestColumn = new Column<GuildDetailedInfo, String>(new ButtonCell()) {

            @Override
            public String getValue(GuildDetailedInfo guildMemberInfo) {
                return ClientI18nHelper.CONSTANTS.joinGuild();
            }
        };
        requestColumn.setFieldUpdater(new FieldUpdater<GuildDetailedInfo, String>() {
            @Override
            public void update(int index, final GuildDetailedInfo guildDetailedInfo, String value) {
                DialogManager.showDialog(new YesNoDialog(ClientI18nHelper.CONSTANTS.joinGuild(),
                        ClientI18nHelper.CONSTANTS.joinGuildMessage(guildDetailedInfo.getName()),
                        ClientI18nHelper.CONSTANTS.yes(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        if (Connection.getMovableServiceAsync() != null) {
                            Connection.getMovableServiceAsync().joinGuild(guildDetailedInfo.getId(), new AsyncCallback<SimpleGuild>() {

                                @Override
                                public void onFailure(Throwable caught) {
                                    ClientExceptionHandler.handleException(caught);
                                }

                                @Override
                                public void onSuccess(SimpleGuild simpleGuild) {
                                    ClientBase.getInstance().updateMySimpleGuild(simpleGuild);
                                    dialog.close();
                                }
                            });
                        }
                    }
                },
                        ClientI18nHelper.CONSTANTS.no(), null), DialogManager.Type.STACK_ABLE);
            }
        });
        invitationsTable.addColumn(requestColumn, ClientI18nHelper.CONSTANTS.joinGuild());
        // Dismiss guild column
        Column<GuildDetailedInfo, String> dismissColumn = new Column<GuildDetailedInfo, String>(new ButtonCell()) {

            @Override
            public String getValue(GuildDetailedInfo guildMemberInfo) {
                return ClientI18nHelper.CONSTANTS.dismiss();
            }
        };
        dismissColumn.setFieldUpdater(new FieldUpdater<GuildDetailedInfo, String>() {
            @Override
            public void update(int index, final GuildDetailedInfo guildDetailedInfo, String value) {
                DialogManager.showDialog(new YesNoDialog(ClientI18nHelper.CONSTANTS.joinGuild(),
                        ClientI18nHelper.CONSTANTS.dismissGuildMessage(guildDetailedInfo.getName()),
                        ClientI18nHelper.CONSTANTS.yes(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        if (Connection.getMovableServiceAsync() != null) {
                            Connection.getMovableServiceAsync().dismissGuildInvitation(guildDetailedInfo.getId(), new AsyncCallback<List<GuildDetailedInfo>>() {
                                @Override
                                public void onFailure(Throwable caught) {
                                    ClientExceptionHandler.handleException("MovableServiceAsync.dismissGuildInvitation()", caught);
                                }

                                @Override
                                public void onSuccess(List<GuildDetailedInfo> detailedInfos) {
                                    fillTableData(detailedInfos);
                                }
                            });

                        }
                    }
                }, ClientI18nHelper.CONSTANTS.no(), null), DialogManager.Type.STACK_ABLE);
            }
        });
        invitationsTable.addColumn(dismissColumn, ClientI18nHelper.CONSTANTS.dismiss());
        // Create a data provider.
        AsyncDataProvider<GuildDetailedInfo> dataProvider = new AsyncDataProvider<GuildDetailedInfo>() {
            @Override
            protected void onRangeChanged(HasData<GuildDetailedInfo> display) {
                if (Connection.getMovableServiceAsync() != null) {
                    Connection.getMovableServiceAsync().getGuildInvitations(new AsyncCallback<List<GuildDetailedInfo>>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            ClientExceptionHandler.handleException("getGuildInvitations", caught);
                        }

                        @Override
                        public void onSuccess(List<GuildDetailedInfo> guildDetailedInfos) {
                            fillTableData(guildDetailedInfos);
                        }

                    });
                }
            }
        };
        dataProvider.addDataDisplay(invitationsTable);
        invitationsTable.setVisibleRange(0, 1000);
        invitationsTable.setEmptyTableWidget(new EmptyTableWidget(ClientI18nHelper.CONSTANTS.noGuildInvitations()));
    }

    private void fillTableData(List<GuildDetailedInfo> guildDetailedInfos) {
        invitationsTable.setRowCount(guildDetailedInfos.size(), true);
        invitationsTable.setRowData(0, guildDetailedInfos);
    }
}