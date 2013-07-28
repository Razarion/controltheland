package com.btxtech.game.jsre.client.cockpit.menu;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.LoginDialog;
import com.btxtech.game.jsre.client.dialogs.RegisterDialog;
import com.btxtech.game.jsre.client.dialogs.StartNewBaseDialog;
import com.btxtech.game.jsre.client.dialogs.YesNoDialog;
import com.btxtech.game.jsre.client.dialogs.guild.CreateGuildDialog;
import com.btxtech.game.jsre.client.dialogs.guild.GuildInvitationsDialog;
import com.btxtech.game.jsre.client.dialogs.guild.MyGuildDialog;
import com.btxtech.game.jsre.client.dialogs.guild.SearchGuildDialog;
import com.btxtech.game.jsre.client.dialogs.history.HistoryDialog;
import com.btxtech.game.jsre.client.dialogs.incentive.InviteFriendsDialog;
import com.btxtech.game.jsre.client.dialogs.news.NewsDialog;
import com.btxtech.game.jsre.common.CmsUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

public class MenuBarPanel extends Composite {
    private static MenuBarPanelUiBinder uiBinder = GWT.create(MenuBarPanelUiBinder.class);
    @UiField
    MenuItem register;
    @UiField
    MenuItem login;
    @UiField
    MenuItem registerSubMenuBar;
    @UiField
    MenuItem logout;
    @UiField
    MenuItem newBase;
    @UiField
    MenuItem news;
    @UiField
    MenuItem history;
    @UiField
    MenuItem myGuild;
    @UiField
    MenuItem guilds;
    @UiField
    MenuItem searchGuild;
    @UiField
    MenuItem createGuild;
    @UiField
    MenuItem guildInvitation;
    @UiField
    MenuItem inviteFriends;

    interface MenuBarPanelUiBinder extends UiBinder<Widget, MenuBarPanel> {
    }

    public MenuBarPanel() {
        initWidget(uiBinder.createAndBindUi(this));

        logout.setVisible(false);

        register.setScheduledCommand(new Command() {

            @Override
            public void execute() {
                DialogManager.showDialog(new RegisterDialog(), DialogManager.Type.PROMPTLY);
            }
        });
        login.setScheduledCommand(new Command() {

            @Override
            public void execute() {
                DialogManager.showDialog(new LoginDialog(), DialogManager.Type.PROMPTLY);
            }
        });
        logout.setScheduledCommand(new Command() {

            @Override
            public void execute() {
                DialogManager.showDialog(new YesNoDialog(ClientI18nHelper.CONSTANTS.logout(),
                        ClientI18nHelper.CONSTANTS.logoutText(Connection.getInstance().getSimpleUser().getName()),
                        ClientI18nHelper.CONSTANTS.logout(),
                        new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                if (Connection.getMovableServiceAsync() != null) {
                                    Connection.getMovableServiceAsync().logout(new AsyncCallback<Void>() {
                                        @Override
                                        public void onFailure(Throwable caught) {
                                            ClientExceptionHandler.handleException("MovableServiceAsync.logout()", caught);
                                        }

                                        @Override
                                        public void onSuccess(Void result) {
                                            Window.Location.replace(GwtCommon.getPredefinedUrl(CmsUtil.CmsPredefinedPage.HOME));
                                        }
                                    });
                                }
                            }
                        },
                        ClientI18nHelper.CONSTANTS.cancel(),
                        null
                ), DialogManager.Type.PROMPTLY);
            }
        });
        newBase.setScheduledCommand(new Command() {

            @Override
            public void execute() {
                DialogManager.showDialog(new StartNewBaseDialog(), DialogManager.Type.STACK_ABLE);
            }
        });
        news.setScheduledCommand(new Command() {

            @Override
            public void execute() {
                DialogManager.showDialog(new NewsDialog(), DialogManager.Type.STACK_ABLE);
            }
        });
        history.setScheduledCommand(new Command() {

            @Override
            public void execute() {
                DialogManager.showDialog(new HistoryDialog(), DialogManager.Type.STACK_ABLE);
            }
        });

        myGuild.setScheduledCommand(new Command() {

            @Override
            public void execute() {
                DialogManager.showDialog(new MyGuildDialog(), DialogManager.Type.STACK_ABLE);
            }
        });

        searchGuild.setScheduledCommand(new Command() {

            @Override
            public void execute() {
                DialogManager.showDialog(new SearchGuildDialog(), DialogManager.Type.STACK_ABLE);
            }
        });

        createGuild.setScheduledCommand(new Command() {

            @Override
            public void execute() {
                CreateGuildDialog.startCreateDialog();
            }
        });

        guildInvitation.setScheduledCommand(new Command() {

            @Override
            public void execute() {
                DialogManager.showDialog(new GuildInvitationsDialog(), DialogManager.Type.STACK_ABLE);
            }
        });
        inviteFriends.setScheduledCommand(new Command() {
            @Override
            public void execute() {
                InviteFriendsDialog.showDialog();
            }
        });

        setSimpleUser(null);
    }

    public void setSimpleUser(SimpleUser simpleUser) {
        if (simpleUser != null && simpleUser.isVerified()) {
            logout.setVisible(true);
            registerSubMenuBar.setVisible(false);
            if (simpleUser.isFacebook()) {
                logout.setEnabled(false);
                logout.setTitle(ClientI18nHelper.CONSTANTS.tooltipLoggedInViaFacebook(simpleUser.getName()));
            } else {
                logout.setEnabled(true);
                logout.setTitle(ClientI18nHelper.CONSTANTS.tooltipLoggedIn(simpleUser.getName()));
            }
        } else {
            logout.setVisible(false);
            registerSubMenuBar.setVisible(true);
            if (simpleUser == null) {
                registerSubMenuBar.setTitle(ClientI18nHelper.CONSTANTS.tooltipNotRegistered());
            } else {
                registerSubMenuBar.setTitle(ClientI18nHelper.CONSTANTS.tooltipNotVerified(simpleUser.getName()));
            }
        }
    }

    public void blinkNewBase(boolean blink) {
        if (blink) {
            newBase.addStyleName("gwt-MenuBar-blink");
        } else {
            newBase.removeStyleName("gwt-MenuBar-blink");
        }
    }

    public void blinkNews(boolean blink) {
        if (blink) {
            news.addStyleName("gwt-MenuBar-blink");
        } else {
            news.removeStyleName("gwt-MenuBar-blink");
        }
    }

    public void updateGuild(SimpleGuild mySimpleGuild) {
        if (mySimpleGuild != null) {
            myGuild.setVisible(true);
            myGuild.setTitle(ClientI18nHelper.CONSTANTS.menuTooltipMyGuild());
            myGuild.setEnabled(true);
            guilds.setVisible(false);
        } else {
            myGuild.setVisible(false);
            guilds.setVisible(true);
            guilds.setTitle(ClientI18nHelper.CONSTANTS.menuTooltipGuilds());
            guilds.setEnabled(true);
        }
    }

    public void initRealGame(RealGameInfo gameInfo) {
        newBase.setEnabled(true);
        newBase.setTitle(ClientI18nHelper.CONSTANTS.tooltipMenuNewBaseRealGame());
        history.setEnabled(true);
        if (Connection.getInstance().isRegisteredAndVerified()) {
            history.setTitle(ClientI18nHelper.CONSTANTS.menuTooltipHistory());
            updateGuild(gameInfo.getMySimpleGuild());
        } else if (Connection.getInstance().isRegistered()) {
            history.setTitle(ClientI18nHelper.CONSTANTS.menuTooltipHistoryOnlyRegisteredVerified());
            myGuild.setVisible(false);
            guilds.setVisible(true);
            guilds.setTitle(ClientI18nHelper.CONSTANTS.guildsOnlyRegisteredVerified());
        } else {
            history.setTitle(ClientI18nHelper.CONSTANTS.menuTooltipHistoryOnlyRegistered());
            myGuild.setVisible(false);
            guilds.setVisible(true);
            guilds.setTitle(ClientI18nHelper.CONSTANTS.guildsOnlyRegistered());
        }
    }

    public void initSimulated() {
        newBase.setEnabled(false);
        newBase.setTitle(ClientI18nHelper.CONSTANTS.tooltipMenuNewBaseSimulated());
        history.setEnabled(false);
        history.setTitle(ClientI18nHelper.CONSTANTS.menuTooltipHistoryOnlyRealGame());
        myGuild.setVisible(false);
        guilds.setVisible(true);
        guilds.setEnabled(false);
        guilds.setTitle(ClientI18nHelper.CONSTANTS.menuTooltipGuildsMission());
    }
}
