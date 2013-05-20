package com.btxtech.game.jsre.client.cockpit.menu;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.SimpleUser;
import com.btxtech.game.jsre.client.dialogs.*;
import com.btxtech.game.jsre.common.CmsUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
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

    interface MenuBarPanelUiBinder extends UiBinder<Widget, MenuBarPanel> {
    }

    public MenuBarPanel() {
        initWidget(uiBinder.createAndBindUi(this));

        logout.setVisible(false);

        register.setCommand(new Command() {

            @Override
            public void execute() {
                DialogManager.showDialog(new RegisterDialog(), DialogManager.Type.PROMPTLY);
            }
        });
        login.setCommand(new Command() {

            @Override
            public void execute() {
                DialogManager.showDialog(new LoginDialog(), DialogManager.Type.PROMPTLY);
            }
        });
        logout.setCommand(new Command() {

            @Override
            public void execute() {
                DialogManager.showDialog(new YesNoDialog(ClientI18nHelper.CONSTANTS.logout(),
                        ClientI18nHelper.CONSTANTS.logoutText(Connection.getInstance().getSimpleUser().getName()),
                        ClientI18nHelper.CONSTANTS.logout(),
                        new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                Connection.getInstance().logout();
                                Window.Location.replace(GwtCommon.getPredefinedUrl(CmsUtil.CmsPredefinedPage.HOME));
                            }
                        },
                        ClientI18nHelper.CONSTANTS.cancel(),
                        null
                ), DialogManager.Type.PROMPTLY);
            }
        });
        newBase.setCommand(new Command() {

            @Override
            public void execute() {
                DialogManager.showDialog(new StartNewBaseDialog(), DialogManager.Type.STACK_ABLE);
            }
        });
        news.setCommand(new Command() {

            @Override
            public void execute() {
                DialogManager.showDialog(new NewsDialog(), DialogManager.Type.STACK_ABLE);
            }
        });
        history.setCommand(new Command() {

            @Override
            public void execute() {
                DialogManager.showDialog(new HistoryDialog(), DialogManager.Type.STACK_ABLE);
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

    public void initRealGame() {
        newBase.setEnabled(true);
        newBase.setTitle(ClientI18nHelper.CONSTANTS.tooltipMenuNewBaseRealGame());
    }

    public void initSimulated() {
        newBase.setEnabled(false);
        newBase.setTitle(ClientI18nHelper.CONSTANTS.tooltipMenuNewBaseSimulated());
    }
}
