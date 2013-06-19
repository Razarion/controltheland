package com.btxtech.game.jsre.client.dialogs.guild;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.VerificationRequestField;
import com.btxtech.game.jsre.client.common.info.RazarionCostInfo;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CreateGuildDialog extends Dialog {

    public static void startCreateDialog() {
        if (Connection.getInstance().getGameEngineMode() != GameEngineMode.SLAVE) {
            throw new IllegalStateException("CreateGuildDialog wrong game engine mode: " + Connection.getInstance().getGameEngineMode());
        }
        if (!Connection.getInstance().isRegisteredAndVerified()) {
            throw new IllegalStateException("CreateGuildDialog user is not registered");
        }
        if (ClientBase.getInstance().getMySimpleGuild() != null) {
            throw new IllegalStateException("CreateGuildDialog user is already member of a guild");
        }
        if (Connection.getMovableServiceAsync() != null) {
            Connection.getMovableServiceAsync().getCreateGuildRazarionCost(new AsyncCallback<RazarionCostInfo>() {

                @Override
                public void onFailure(Throwable caught) {
                    ClientExceptionHandler.handleException("MovableServiceAsync.getCreateGuildRazarionCost()", caught);
                }

                @Override
                public void onSuccess(RazarionCostInfo razarionCostInfo) {
                    if (razarionCostInfo.isAfordable()) {
                        DialogManager.showDialog(new CreateGuildDialog(razarionCostInfo), DialogManager.Type.QUEUE_ABLE);
                    } else {
                        DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.createGuildDialogTitle(),
                                ClientI18nHelper.CONSTANTS.createGuildInsufficientRazarion(razarionCostInfo.getCost(), razarionCostInfo.getRazarionAmount())),
                                DialogManager.Type.QUEUE_ABLE);
                    }
                }
            });
        }
    }

    private RazarionCostInfo razarionCostInfo;
    private CreateGuildPanel createGuildPanel;

    private CreateGuildDialog(RazarionCostInfo razarionCostInfo) {
        super(ClientI18nHelper.CONSTANTS.createGuildDialogTitle());
        this.razarionCostInfo = razarionCostInfo;
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
        createGuildPanel = new CreateGuildPanel(razarionCostInfo, new VerificationRequestField.ValidListener() {
            @Override
            public void onValidStateChanged(boolean isValid) {
                setYesButtonEnabled(isValid);
            }
        });
        dialogVPanel.add(createGuildPanel);
    }
}
