package com.btxtech.game.jsre.client.dialogs;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.InvalidNickName;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 07.12.12
 * Time: 16:35
 */
public class NickNameDialog extends Dialog {
    private Label errorLabel;
    private TextBox nameBox;
    private Button connectButton;
    private final String signedRequestParameter;
    private final RegisterDialog registerDialog;

    public NickNameDialog(String signedRequestParameter, RegisterDialog registerDialog) {
        super("Choose a nickname");
        this.signedRequestParameter = signedRequestParameter;
        this.registerDialog = registerDialog;
        setShowCloseButton(true);
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        FlexTable grid = new FlexTable();
        errorLabel = new Label();
        grid.setWidget(0, 0, errorLabel);
        nameBox = new TextBox();
        nameBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                checkName();
            }
        });
        nameBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                checkName();
            }
        });
        nameBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                checkName();
            }
        });

        grid.setWidget(1, 0, nameBox);
        connectButton = new Button("Register");
        grid.setWidget(2, 0, connectButton);
        connectButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                connectButton.setEnabled(false);
                if (Connection.getMovableServiceAsync() != null) {
                    Connection.getMovableServiceAsync().createAndLoginFacebookUser(signedRequestParameter, nameBox.getText(), new AsyncCallback<Void>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            connectButton.setEnabled(true);
                            if (caught instanceof UserAlreadyExistsException) {
                                DialogManager.showDialog(new MessageDialog("Registration failed", RegisterDialog.REGISTRATION_EXISTS), DialogManager.Type.STACK_ABLE);
                            } else {
                                ClientExceptionHandler.handleException("NickNameDialog.setupPanel() createAndLoginFacebookUser", caught);
                            }
                        }

                        @Override
                        public void onSuccess(Void result) {
                            Connection.getInstance().setUserName(nameBox.getText());
                            NickNameDialog.this.hide();
                            registerDialog.hide();
                        }
                    });
                }
            }
        });

        dialogVPanel.add(grid);

        checkName();
    }

    @Override
    protected void setupDialog() {
        super.setupDialog();
        // Field must be added before focus can be set
        nameBox.setFocus(true);
    }

    private void checkName() {
        if (nameBox.getText() == null || nameBox.getText().trim().isEmpty()) {
            connectButton.setEnabled(false);
            errorLabel.setText(InvalidNickName.TO_SHORT.getErrorMsg());
        } else {
            if (Connection.getMovableServiceAsync() != null) {
                Connection.getMovableServiceAsync().isNickNameValid(nameBox.getText(), new AsyncCallback<InvalidNickName>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        ClientExceptionHandler.handleException("NickNameDialog.checkName()", caught);
                    }

                    @Override
                    public void onSuccess(InvalidNickName invalidNickName) {
                        if (invalidNickName != null) {
                            connectButton.setEnabled(false);
                            errorLabel.setText(invalidNickName.getErrorMsg());
                        } else {
                            errorLabel.setText("");
                            connectButton.setEnabled(true);
                        }
                    }
                });
            }
        }
    }
}
