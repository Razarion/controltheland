package com.btxtech.game.jsre.client.dialogs.guild;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.btxtech.game.jsre.client.dialogs.DialogUiBinderWrapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class MembershipRequestPanel extends DialogUiBinderWrapper {

    private static MembershipRequestPanelUiBinder uiBinder = GWT.create(MembershipRequestPanelUiBinder.class);
    @UiField
    TextArea textField;
    @UiField
    Label requestLabel;
    private int guildId;

    interface MembershipRequestPanelUiBinder extends UiBinder<Widget, MembershipRequestPanel> {
    }

    public MembershipRequestPanel(int guildId, String guildName) {
        this.guildId = guildId;
        initWidget(uiBinder.createAndBindUi(this));
        requestLabel.setText(ClientI18nHelper.CONSTANTS.guildMembershipRequestSent(guildName));
    }

    @Override
    public String getDialogTitle() {
        return ClientI18nHelper.CONSTANTS.guildMembershipRequestTitle();
    }

    @Override
    public void init(final Dialog dialog) {
        dialog.setShowYesButton(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (Connection.getMovableServiceAsync() != null) {
                    Connection.getMovableServiceAsync().guildMembershipRequest(guildId, textField.getText(), new AsyncCallback<Void>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            ClientExceptionHandler.handleException("MovableServiceAsync.guildMembershipRequest()", caught);
                            dialog.hide();
                        }

                        @Override
                        public void onSuccess(Void result) {
                            dialog.hide();
                        }
                    });
                }
            }
        }, ClientI18nHelper.CONSTANTS.send());
    }
}
