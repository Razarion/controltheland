package com.btxtech.game.jsre.client.dialogs.guild;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ClientUserService;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.YesNoDialog;
import com.btxtech.game.jsre.client.dialogs.history.HistoryElement;
import com.btxtech.game.jsre.client.dialogs.history.HistoryFilter;
import com.btxtech.game.jsre.client.dialogs.history.HistoryPanel;
import com.btxtech.game.jsre.client.dialogs.history.HistoryPanelFacade;
import com.btxtech.game.jsre.client.widget.AutoHideButtonCell;
import com.btxtech.game.jsre.client.widget.EmptyTableWidget;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public class MyGuildPanel extends Composite {
    private static final int HISTORY_PAGE_SIZE = 8;
    private static MyGuildPanelUiBinder uiBinder = GWT.create(MyGuildPanelUiBinder.class);
    @UiField
    TabPanel tabPanel;
    @UiField
    Label guildName;
    @UiField
    HTML guildTextRo;
    @UiField
    HorizontalPanel guildTextPanelRw;
    @UiField
    RichTextArea guildTextRw;
    @UiField
    Button saveGuildTextBtn;
    private CellTable.Resources tableRes = GWT.create(HistoryPanel.TableRes.class);
    @UiField(provided = true)
    CellTable<GuildMemberInfo> memberTable = new CellTable<GuildMemberInfo>(1000, tableRes);
    @UiField
    Button inviteButton;
    @UiField(provided = true)
    SuggestBox userSearch;
    @UiField(provided = true)
    CellTable<GuildMembershipRequest> requestTable = new CellTable<GuildMembershipRequest>(1000, tableRes);
    @UiField
    Button leaveGuildBtn;
    @UiField
    Button closeGuildBtn;
    @UiField(provided = true)
    CellTable<HistoryElement> historyTable = new CellTable<HistoryElement>(HISTORY_PAGE_SIZE, tableRes);
    @UiField
    SimplePager historyPager;
    private Logger log = Logger.getLogger(MyGuildPanel.class.getName());
    private GuildMemberInfo.Rank myRank;
    private Dialog dialog;

    interface MyGuildPanelUiBinder extends UiBinder<Widget, MyGuildPanel> {
    }

    public MyGuildPanel(Dialog dialog) {
        this.dialog = dialog;
        userSearch = new SuggestBox(new SuggestOracle() {
            @Override
            public void requestSuggestions(Request request, Callback callback) {
                Connection.getInstance().getSuggestedUserName(request, callback);
            }
        },
                new TextBox(),
                new SuggestBox.DefaultSuggestionDisplay() {
                    @Override
                    protected PopupPanel createPopup() {
                        PopupPanel popupPanel = super.createPopup();
                        popupPanel.getElement().getStyle().setZIndex(Constants.Z_INDEX_DIALOG + 1);
                        return popupPanel;
                    }
                }
        );
        initWidget(uiBinder.createAndBindUi(this));
        HistoryPanelFacade historyPanelFacade = new HistoryPanelFacade(historyTable, historyPager, HistoryFilter.createGuildFilter(ClientBase.getInstance().getMySimpleGuild().getId()));
        historyPanelFacade.setupCellTable();
        setupRequestTable();
        tabPanel.selectTab(0);
        if (Connection.getMovableServiceAsync() != null) {
            Connection.getMovableServiceAsync().getFullGuildInfo(ClientBase.getInstance().getMySimpleGuild().getId(), new AsyncCallback<FullGuildInfo>() {

                @Override
                public void onFailure(Throwable caught) {
                    ClientExceptionHandler.handleException("MovableServiceAsync.getFullGuildInfo() failed", caught);
                }

                @Override
                public void onSuccess(FullGuildInfo fullGuildInfo) {
                    onFullGuildInfo(fullGuildInfo);
                }
            });
        }
        requestTable.setEmptyTableWidget(new EmptyTableWidget(ClientI18nHelper.CONSTANTS.noGuildRequests()));
    }

    public void onFullGuildInfo(FullGuildInfo fullGuildInfo) {
        myRank = fullGuildInfo.getMember(ClientUserService.getInstance().getSimpleUser()).getRank();
        if (myRank == null) {
            throw new IllegalStateException("MyGuildPanel.onFullGuildInfo(): My rank has not been set");
        }
        guildName.setText(fullGuildInfo.getGuildInfo().getName());
        if (myRank == GuildMemberInfo.Rank.PRESIDENT) {
            guildTextRo.setVisible(false);
            guildTextPanelRw.setVisible(true);
            saveGuildTextBtn.setVisible(true);
            if (fullGuildInfo.getGuildInfo().getText() != null) {
                guildTextRw.setHTML(fullGuildInfo.getGuildInfo().getText());
            } else {
                guildTextRw.setHTML("");
            }
            leaveGuildBtn.setVisible(false);
            closeGuildBtn.setVisible(true);
        } else {
            guildTextRo.setVisible(true);
            guildTextRo.setHTML(fullGuildInfo.getGuildInfo().getText());
            guildTextPanelRw.setVisible(false);
            saveGuildTextBtn.setVisible(false);
            leaveGuildBtn.setVisible(true);
            closeGuildBtn.setVisible(false);
        }
        setupMemberTable(fullGuildInfo);
        requestTable.setRowCount(fullGuildInfo.getRequests().size(), true);
        requestTable.setRowData(0, fullGuildInfo.getRequests());
    }

    private void setupMemberTable(FullGuildInfo fullGuildInfo) {
        if (myRank == null) {
            throw new IllegalStateException("MyGuildPanel.setupMemberTable(): My rank has not been set");
        }
        while (memberTable.getColumnCount() > 0) {
            memberTable.removeColumn(0);
        }
        // Create name
        memberTable.addColumn(new TextColumn<GuildMemberInfo>() {
            @Override
            public String getValue(GuildMemberInfo guildMember) {
                return guildMember.getDetailedUser().getSimpleUser().getName();
            }
        }, ClientI18nHelper.CONSTANTS.member());
        // Level Column
        memberTable.addColumn(new TextColumn<GuildMemberInfo>() {
            @Override
            public String getValue(GuildMemberInfo guildMemberInfo) {
                return Integer.toString(guildMemberInfo.getDetailedUser().getLevel());
            }
        }, ClientI18nHelper.CONSTANTS.level());
        // Create name
        memberTable.addColumn(new TextColumn<GuildMemberInfo>() {
            @Override
            public String getValue(GuildMemberInfo guildMember) {
                return guildMember.getDetailedUser().getPlanet();
            }
        }, ClientI18nHelper.CONSTANTS.planet());
        // Create rank column
        memberTable.addColumn(new TextColumn<GuildMemberInfo>() {
            @Override
            public String getValue(GuildMemberInfo guildMember) {
                switch (guildMember.getRank()) {
                    case PRESIDENT:
                        return ClientI18nHelper.CONSTANTS.guildPresident();
                    case MANAGEMENT:
                        return ClientI18nHelper.CONSTANTS.guildManagement();
                    case MEMBER:
                        return ClientI18nHelper.CONSTANTS.guildMember();
                    default:
                        log.warning("Unknown guild rank: " + guildMember.getRank());
                        return "???";
                }
            }
        }, ClientI18nHelper.CONSTANTS.guildRank());
        if (myRank == GuildMemberInfo.Rank.PRESIDENT) {
            Column<GuildMemberInfo, String> kickColumn = new Column<GuildMemberInfo, String>(new AutoHideButtonCell()) {

                @Override
                public String getValue(GuildMemberInfo guildMemberInfo) {
                    if (guildMemberInfo.getDetailedUser().getSimpleUser().equals(ClientUserService.getInstance().getSimpleUser())) {
                        return null;
                    } else {
                        return ClientI18nHelper.CONSTANTS.change();
                    }
                }
            };
            kickColumn.setFieldUpdater(new FieldUpdater<GuildMemberInfo, String>() {
                @Override
                public void update(int index, final GuildMemberInfo guildMemberInfo, String value) {
                    DialogManager.showDialog(new ChangeRankPanel(guildMemberInfo), DialogManager.Type.STACK_ABLE);
                }
            });
            memberTable.addColumn(kickColumn, ClientI18nHelper.CONSTANTS.guildRank());
        }
        // Create kick column
        if (myRank.isHigher(GuildMemberInfo.Rank.MEMBER)) {
            Column<GuildMemberInfo, String> kickColumn = new Column<GuildMemberInfo, String>(new AutoHideButtonCell()) {

                @Override
                public String getValue(GuildMemberInfo guildMemberInfo) {
                    if (myRank == null) {
                        throw new IllegalStateException("MyGuildPanel.setupMemberTable() kick Column: My rank has not been set");
                    }
                    if (guildMemberInfo.getDetailedUser().getSimpleUser().equals(ClientUserService.getInstance().getSimpleUser())) {
                        return null;
                    } else if (myRank.isHigher(guildMemberInfo.getRank())) {
                        return ClientI18nHelper.CONSTANTS.kick();
                    } else {
                        return null;
                    }
                }
            };
            kickColumn.setFieldUpdater(new FieldUpdater<GuildMemberInfo, String>() {
                @Override
                public void update(int index, final GuildMemberInfo guildMemberInfo, String value) {
                    YesNoDialog yesNoDialog = new YesNoDialog(ClientI18nHelper.CONSTANTS.guildKickMember(),
                            ClientI18nHelper.CONSTANTS.guildKickMemberMessage(guildMemberInfo.getDetailedUser().getSimpleUser().getName()),
                            ClientI18nHelper.CONSTANTS.yes(),
                            new ClickHandler() {
                                @Override
                                public void onClick(ClickEvent event) {
                                    if (Connection.getMovableServiceAsync() != null) {
                                        Connection.getMovableServiceAsync().kickGuildMember(guildMemberInfo.getDetailedUser().getSimpleUser().getId(), new AsyncCallback<FullGuildInfo>() {
                                            @Override
                                            public void onFailure(Throwable caught) {
                                                ClientExceptionHandler.handleException("MovableServiceAsync.kickGuildMember()", caught);
                                            }

                                            @Override
                                            public void onSuccess(FullGuildInfo fullGuildInfo) {
                                                onFullGuildInfo(fullGuildInfo);
                                            }
                                        });
                                    }
                                }
                            }, ClientI18nHelper.CONSTANTS.no(),
                            null);
                    DialogManager.showDialog(yesNoDialog, DialogManager.Type.STACK_ABLE);
                }
            });
            memberTable.addColumn(kickColumn, ClientI18nHelper.CONSTANTS.kick());
        }
        // Fill data
        List<GuildMemberInfo> memberData = new ArrayList<GuildMemberInfo>(fullGuildInfo.getMembers());
        Collections.sort(memberData, new Comparator<GuildMemberInfo>() {
            @Override
            public int compare(GuildMemberInfo o1, GuildMemberInfo o2) {
                if (o1.getRank() != o2.getRank()) {
                    switch (o1.getRank()) {
                        case PRESIDENT:
                            return -1;
                        case MANAGEMENT:
                            return (o2.getRank() == GuildMemberInfo.Rank.MEMBER) ? -1 : 1;
                        case MEMBER:
                            return 1;
                        default: {
                            log.warning("MyGuildPanel.Comparator.compare(): unknown rank: " + o1.getRank());
                            return 0;
                        }
                    }
                } else {
                    return o1.getDetailedUser().getSimpleUser().getName().compareTo(o2.getDetailedUser().getSimpleUser().getName());
                }
            }
        });

        memberTable.setRowCount(memberData.size(), true);
        memberTable.setRowData(0, memberData);
    }

    private void setupRequestTable() {
        requestTable.addColumn(new TextColumn<GuildMembershipRequest>() {
            @Override
            public String getValue(GuildMembershipRequest guildMembershipRequest) {
                return guildMembershipRequest.getDetailedUser().getSimpleUser().getName();
            }
        }, ClientI18nHelper.CONSTANTS.userName());
        // Level column
        requestTable.addColumn(new TextColumn<GuildMembershipRequest>() {
            @Override
            public String getValue(GuildMembershipRequest guildMembershipRequest) {
                return Integer.toString(guildMembershipRequest.getDetailedUser().getLevel());
            }
        }, ClientI18nHelper.CONSTANTS.level());
        // Name column
        requestTable.addColumn(new TextColumn<GuildMembershipRequest>() {
            @Override
            public String getValue(GuildMembershipRequest guildMembershipRequest) {
                return guildMembershipRequest.getDetailedUser().getPlanet();
            }
        }, ClientI18nHelper.CONSTANTS.planet());
        // Text column
        requestTable.addColumn(new TextColumn<GuildMembershipRequest>() {
            @Override
            public String getValue(GuildMembershipRequest guildMembershipRequest) {
                return guildMembershipRequest.getText();
            }
        }, ClientI18nHelper.CONSTANTS.message());
        // Invite column
        Column<GuildMembershipRequest, String> inviteColumn = new Column<GuildMembershipRequest, String>(new ButtonCell()) {

            @Override
            public String getValue(GuildMembershipRequest guildMembershipRequest) {
                return ClientI18nHelper.CONSTANTS.inviteMember();
            }
        };
        inviteColumn.setFieldUpdater(new FieldUpdater<GuildMembershipRequest, String>() {
            @Override
            public void update(int index, final GuildMembershipRequest guildMembershipRequest, String value) {
                Connection.getInstance().inviteGuildMember(guildMembershipRequest.getDetailedUser().getSimpleUser().getName());
            }
        });
        requestTable.addColumn(inviteColumn, ClientI18nHelper.CONSTANTS.inviteMember());
        // Dismiss column
        Column<GuildMembershipRequest, String> dismissColumn = new Column<GuildMembershipRequest, String>(new ButtonCell()) {

            @Override
            public String getValue(GuildMembershipRequest guildMembershipRequest) {
                return ClientI18nHelper.CONSTANTS.dismiss();
            }
        };
        dismissColumn.setFieldUpdater(new FieldUpdater<GuildMembershipRequest, String>() {
            @Override
            public void update(int index, final GuildMembershipRequest guildMembershipRequest, String value) {
                Connection.getInstance().dismissGuildMemberRequest(guildMembershipRequest.getDetailedUser().getSimpleUser());
            }
        });
        requestTable.addColumn(dismissColumn, ClientI18nHelper.CONSTANTS.dismiss());

    }

    @UiHandler("saveGuildTextBtn")
    void onSaveGuildTextBtnClick(ClickEvent event) {
        if (myRank == null) {
            throw new IllegalStateException("MyGuildPanel.onFullGuildInfo(): My rank has not been set");
        }
        if (myRank != GuildMemberInfo.Rank.PRESIDENT) {
            throw new IllegalStateException("MyGuildPanel.onSaveGuildTextBtnClick() user is not guild president");
        }
        Connection.getInstance().saveGuildText(guildTextRw.getHTML());
    }

    @UiHandler("inviteButton")
    void onInviteButtonClick(ClickEvent event) {
        if (!userSearch.getValue().trim().isEmpty()) {
            Connection.getInstance().inviteGuildMember(userSearch.getValue());
        }
    }

    @UiHandler("leaveGuildBtn")
    void onLeaveGuildClick(ClickEvent event) {
        YesNoDialog yesNoDialog = new YesNoDialog(ClientI18nHelper.CONSTANTS.leaveGuild(),
                ClientI18nHelper.CONSTANTS.leaveGuildMessage(),
                ClientI18nHelper.CONSTANTS.yes(),
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        if (Connection.getMovableServiceAsync() != null) {
                            Connection.getMovableServiceAsync().leaveGuild(new AsyncCallback<Void>() {
                                @Override
                                public void onFailure(Throwable caught) {
                                    ClientExceptionHandler.handleException("MovableServiceAsync.leaveGuild()", caught);
                                }

                                @Override
                                public void onSuccess(Void aVoid) {
                                    ClientBase.getInstance().updateMySimpleGuild(null);
                                    dialog.close();
                                }
                            });
                        }
                    }
                }, ClientI18nHelper.CONSTANTS.no(),
                null);
        DialogManager.showDialog(yesNoDialog, DialogManager.Type.STACK_ABLE);
    }

    @UiHandler("closeGuildBtn")
    void onCloseGuildClick(ClickEvent event) {
        YesNoDialog yesNoDialog = new YesNoDialog(ClientI18nHelper.CONSTANTS.closeGuild(),
                ClientI18nHelper.CONSTANTS.closeGuildMessage(),
                ClientI18nHelper.CONSTANTS.yes(),
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        if (Connection.getMovableServiceAsync() != null) {
                            Connection.getMovableServiceAsync().closeGuild(new AsyncCallback<Void>() {
                                @Override
                                public void onFailure(Throwable caught) {
                                    ClientExceptionHandler.handleException("MovableServiceAsync.closeGuildMessage()", caught);
                                }

                                @Override
                                public void onSuccess(Void aVoid) {
                                    ClientBase.getInstance().updateMySimpleGuild(null);
                                    dialog.close();
                                }
                            });
                        }
                    }
                }, ClientI18nHelper.CONSTANTS.no(),
                null);
        DialogManager.showDialog(yesNoDialog, DialogManager.Type.STACK_ABLE);
    }
}
