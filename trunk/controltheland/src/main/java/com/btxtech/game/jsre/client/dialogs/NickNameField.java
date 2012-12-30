package com.btxtech.game.jsre.client.dialogs;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.InvalidNickName;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 27.12.12
 * Time: 12:57
 */
public class NickNameField extends VerticalPanel {
    private Label errorLabel;
    private TextBox nameBox;
    private ValidListener validListener;

    public interface ValidListener {
        void onValidStateChanged(boolean isValid);
    }

    public NickNameField(ValidListener validListener) {
        this.validListener = validListener;
        errorLabel = new Label();
        add(errorLabel);
        nameBox = new TextBox();
        add(nameBox);
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
    }


    public void checkName() {
        if (nameBox.getText() == null || nameBox.getText().trim().isEmpty()) {
            fireValidStateChanged(false);
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
                            fireValidStateChanged(false);
                            errorLabel.setText(invalidNickName.getErrorMsg());
                        } else {
                            errorLabel.setText("");
                            fireValidStateChanged(true);
                        }
                    }
                });
            }
        }
    }

    private void fireValidStateChanged(boolean isValid) {
        if (validListener != null) {
            validListener.onValidStateChanged(isValid);
        }
    }

    public void setFocus(boolean focused) {
        nameBox.setFocus(focused);
    }

    public String getText() {
        return nameBox.getText();
    }
}
