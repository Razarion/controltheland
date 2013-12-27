package com.btxtech.game.jsre.client.dialogs.guild;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ClientUserService;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.VerificationRequestField;
import com.btxtech.game.jsre.client.common.info.CrystalCostInfo;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.crystals.AffordableCallback;
import com.btxtech.game.jsre.client.dialogs.crystals.CrystalHelper;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CreateGuildDialog extends Dialog {

    public static void startCreateDialog() {
        if (Connection.getInstance().getGameEngineMode() != GameEngineMode.SLAVE) {
            throw new IllegalStateException("CreateGuildDialog wrong game engine mode: " + Connection.getInstance().getGameEngineMode());
        }
        if (!ClientUserService.getInstance().isRegisteredAndVerified()) {
            throw new IllegalStateException("CreateGuildDialog user is not registered");
        }
        if (ClientBase.getInstance().isGuildMember()) {
            throw new IllegalStateException("CreateGuildDialog user is already member of a guild");
        }
        new CrystalHelper(ClientI18nHelper.CONSTANTS.createGuildDialogTitle(), ClientI18nHelper.CONSTANTS.createGuildInsufficientCrystals()) {

            @Override
            protected void askAffordable(final AffordableCallback affordableCallback) {
                Connection.getMovableServiceAsync().getCreateGuildCrystalCost(new AsyncCallback<CrystalCostInfo>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        ClientExceptionHandler.handleException("MovableServiceAsync.getCreateGuildCrystalCost()", caught);
                    }

                    @Override
                    public void onSuccess(CrystalCostInfo crystalCostInfo) {
                        affordableCallback.onDetermined(crystalCostInfo.getCost(), crystalCostInfo.getCrystalAmount());
                    }
                });
            }

            @Override
            protected Dialog createBuyDialog(int crystalCost, int crystalBalance) {
                return new CreateGuildDialog(crystalCost);
            }
        };
    }

    private CreateGuildPanel createGuildPanel;
    private int crystals;

    private CreateGuildDialog(int crystals) {
        super(ClientI18nHelper.CONSTANTS.createGuildDialogTitle());
        this.crystals = crystals;
        setShowYesButton(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (Connection.getMovableServiceAsync() != null) {
                    Connection.getMovableServiceAsync().createGuild(createGuildPanel.getGuildName(), new AsyncCallback<SimpleGuild>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            ClientExceptionHandler.handleException("MovableService.createGuild()", caught);
                        }

                        @Override
                        public void onSuccess(SimpleGuild simpleGuild) {
                            ClientBase.getInstance().updateMySimpleGuild(simpleGuild);
                            close();
                            DialogManager.showDialog(new MyGuildDialog(), DialogManager.Type.QUEUE_ABLE);
                        }
                    });
                }
            }
        }, ClientI18nHelper.CONSTANTS.create());
        setDialogWidth(30);
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        createGuildPanel = new CreateGuildPanel(crystals, new VerificationRequestField.ValidListener() {
            @Override
            public void onValidStateChanged(boolean isValid) {
                setYesButtonEnabled(isValid);
            }
        });
        dialogVPanel.add(createGuildPanel);
    }
}
