package com.btxtech.game.jsre.client.cockpit.menu;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ClientUserService;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.StartNewBaseDialog;
import com.btxtech.game.jsre.client.dialogs.guild.CreateGuildDialog;
import com.btxtech.game.jsre.client.dialogs.guild.GuildInvitationsDialog;
import com.btxtech.game.jsre.client.dialogs.guild.MyGuildDialog;
import com.btxtech.game.jsre.client.dialogs.guild.SearchGuildDialog;
import com.btxtech.game.jsre.client.dialogs.history.HistoryDialog;
import com.btxtech.game.jsre.client.dialogs.incentive.InviteFriendsDialog;
import com.btxtech.game.jsre.client.dialogs.news.NewsDialog;
import com.btxtech.game.jsre.client.dialogs.razarion.BuyRazarionPaypalDialog;
import com.btxtech.game.jsre.client.dialogs.razarion.HowToGetRazarionPanel;
import com.btxtech.game.jsre.client.dialogs.starmap.StarMapDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
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
    @UiField
    MenuItem buyPaypal;
    @UiField
    MenuItem overviewRazarion;
    @UiField
    MenuItem starMap;

    interface MenuBarPanelUiBinder extends UiBinder<Widget, MenuBarPanel> {
    }

    public MenuBarPanel() {
        initWidget(uiBinder.createAndBindUi(this));

        logout.setVisible(false);

        register.setScheduledCommand(new Command() {

            @Override
            public void execute() {
                ClientUserService.getInstance().promptRegister();
            }
        });
        login.setScheduledCommand(new Command() {

            @Override
            public void execute() {
                ClientUserService.getInstance().promptLogin();
            }
        });
        logout.setScheduledCommand(new Command() {

            @Override
            public void execute() {
                ClientUserService.getInstance().logout();
            }
        });
        newBase.setScheduledCommand(new Command() {

            @Override
            public void execute() {
                StartNewBaseDialog.show();
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
        buyPaypal.setScheduledCommand(new Command() {
            @Override
            public void execute() {
                DialogManager.showDialog(new BuyRazarionPaypalDialog(), DialogManager.Type.STACK_ABLE);
            }
        });
        overviewRazarion.setScheduledCommand(new Command() {
            @Override
            public void execute() {
                DialogManager.showDialog(new HowToGetRazarionPanel(ClientI18nHelper.CONSTANTS.howToGetRazarion()), DialogManager.Type.STACK_ABLE);
            }
        });
        starMap.setScheduledCommand(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                DialogManager.showDialog(new StarMapDialog(), DialogManager.Type.QUEUE_ABLE);
            }
        });
        updateUser();
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
        setupRegister();
        newBase.setEnabled(true);
        newBase.setTitle(ClientI18nHelper.CONSTANTS.tooltipMenuNewBaseRealGame());
        starMap.setEnabled(true);
        starMap.setTitle(ClientI18nHelper.CONSTANTS.menuStarMapTooltip());
        if (ClientUserService.getInstance().isRegisteredAndVerified()) {
            history.setEnabled(true);
            history.setTitle(ClientI18nHelper.CONSTANTS.menuTooltipHistory());
            updateGuild(gameInfo.getMySimpleGuild());
        } else if (ClientUserService.getInstance().isRegistered()) {
            history.setEnabled(false);
            history.setTitle(ClientI18nHelper.CONSTANTS.menuTooltipHistoryOnlyRegisteredVerified());
            myGuild.setVisible(false);
            guilds.setVisible(true);
            guilds.setTitle(ClientI18nHelper.CONSTANTS.guildsOnlyRegisteredVerified());
            guilds.setEnabled(false);
        } else {
            history.setEnabled(false);
            history.setTitle(ClientI18nHelper.CONSTANTS.menuTooltipHistoryOnlyRegistered());
            myGuild.setVisible(false);
            guilds.setVisible(true);
            guilds.setTitle(ClientI18nHelper.CONSTANTS.guildsOnlyRegistered());
            guilds.setEnabled(false);
        }
    }

    public void initSimulated() {
        setupRegister();
        newBase.setEnabled(false);
        newBase.setTitle(ClientI18nHelper.CONSTANTS.tooltipMenuNewBaseSimulated());
        history.setEnabled(false);
        history.setTitle(ClientI18nHelper.CONSTANTS.menuTooltipHistoryOnlyRealGame());
        myGuild.setVisible(false);
        guilds.setVisible(true);
        guilds.setEnabled(false);
        guilds.setTitle(ClientI18nHelper.CONSTANTS.menuTooltipGuildsMission());
        starMap.setEnabled(false);
        starMap.setTitle(ClientI18nHelper.CONSTANTS.menuStarMapTooltipMission());
    }

    private void setupRegister() {
        if (ClientUserService.getInstance().isRegisteredAndVerified()) {
            logout.setVisible(true);
            logout.setTitle(ClientI18nHelper.CONSTANTS.tooltipLoggedIn(ClientUserService.getInstance().getSimpleUser().getName()));
            registerSubMenuBar.setVisible(false);
        } else {
            logout.setVisible(false);
            registerSubMenuBar.setVisible(true);
            if (ClientUserService.getInstance().isRegistered()) {
                registerSubMenuBar.setEnabled(false);
                registerSubMenuBar.setVisible(true);
                registerSubMenuBar.setTitle(ClientI18nHelper.CONSTANTS.tooltipNotVerified(ClientUserService.getInstance().getSimpleUser().getName()));
            } else {
                registerSubMenuBar.setEnabled(true);
                registerSubMenuBar.setVisible(true);
                registerSubMenuBar.setTitle(ClientI18nHelper.CONSTANTS.tooltipNotRegistered());
            }
        }
    }

    public void updateUser() {
        if (Connection.getInstance().getGameEngineMode() == GameEngineMode.SLAVE) {
            initRealGame((RealGameInfo) Connection.getInstance().getGameInfo());
        }
        setupRegister();
    }
}
