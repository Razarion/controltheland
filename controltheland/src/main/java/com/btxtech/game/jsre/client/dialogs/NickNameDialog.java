package com.btxtech.game.jsre.client.dialogs;

import com.btxtech.game.jsre.client.AdCellProvision;
import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 07.12.12
 * Time: 16:35
 */
public class NickNameDialog extends Dialog implements NickNameField.ValidListener {
    private NickNameField nickNameField;
    private Button connectButton;
    private final String signedRequestParameter;
    private String email;
    private final RegisterDialog registerDialog;

    public NickNameDialog(String signedRequestParameter, String email, RegisterDialog registerDialog) {
        super(ClientI18nHelper.CONSTANTS.chooseNickName());
        this.signedRequestParameter = signedRequestParameter;
        this.email = email;
        this.registerDialog = registerDialog;
        setShowCloseButton(true);
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        FlexTable grid = new FlexTable();
        nickNameField = new NickNameField(this);
        grid.setWidget(0, 0, nickNameField);
        connectButton = new Button(ClientI18nHelper.CONSTANTS.register());
        grid.setWidget(1, 0, connectButton);
        connectButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                connectButton.setEnabled(false);
                if (Connection.getMovableServiceAsync() != null) {
                    Connection.getMovableServiceAsync().createAndLoginFacebookUser(signedRequestParameter, nickNameField.getText(), email, new AsyncCallback<AdCellProvision>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            connectButton.setEnabled(true);
                            if (caught instanceof UserAlreadyExistsException) {
                                DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.registrationFailed(), ClientI18nHelper.CONSTANTS.registrationUser()), DialogManager.Type.STACK_ABLE);
                            } else {
                                ClientExceptionHandler.handleException("NickNameDialog.setupPanel() createAndLoginFacebookUser", caught);
                            }
                        }

                        @Override
                        public void onSuccess(AdCellProvision adCellProvision) {
                            Connection.getInstance().setSimpleUser(adCellProvision.getSimpleUser());
                            SideCockpit.getInstance().setSimpleUser(adCellProvision.getSimpleUser());
                            NickNameDialog.this.hide();
                            registerDialog.hide();
                            DialogManager.showDialog(new FacebookRegisterThanksDialog(adCellProvision), DialogManager.Type.PROMPTLY);
                        }
                    });
                }
            }
        });

        dialogVPanel.add(grid);

        nickNameField.checkName();
    }

    @Override
    protected void setupDialog() {
        super.setupDialog();
        // Field must be added before focus can be set
        nickNameField.setFocus(true);
    }

    @Override
    public void onValidStateChanged(boolean isValid) {
        connectButton.setEnabled(isValid);
    }
}
