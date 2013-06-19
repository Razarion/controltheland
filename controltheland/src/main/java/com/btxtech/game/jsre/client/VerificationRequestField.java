package com.btxtech.game.jsre.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 27.12.12
 * Time: 12:57
 */
abstract public class VerificationRequestField extends VerticalPanel {
    private Label errorLabel;
    private TextBox nameBox;
    private ValidListener validListener;
    private Logger log = Logger.getLogger(VerificationRequestField.class.getName());

    public interface ValidListener {
        void onValidStateChanged(boolean isValid);
    }

    public VerificationRequestField(ValidListener validListener) {
        this.validListener = validListener;
        errorLabel = new Label();
        errorLabel.getElement().getStyle().setColor("#FF6464");
        errorLabel.getElement().getStyle().setFontSize(80, Style.Unit.PCT);
        add(errorLabel);
        nameBox = new TextBox();
        add(nameBox);
        nameBox.getElement().getStyle().setWidth(15, Style.Unit.EM);
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

    abstract protected void checkNameRequest(String name, VerificationRequestCallback verificationRequestCallback);

    public void checkName() {
        if (nameBox.getText() == null || nameBox.getText().trim().isEmpty()) {
            fireValidStateChanged(false);
            errorLabel.setText(ClientI18nHelper.CONSTANTS.nameToShort());
        } else {
            checkNameRequest(nameBox.getText(), new VerificationRequestCallback() {

                @Override
                public void onResponse(ErrorResult errorResult) {
                    if (errorResult != null) {
                        fireValidStateChanged(false);
                        switch (errorResult) {
                            case TO_SHORT:
                                errorLabel.setText(ClientI18nHelper.CONSTANTS.nameToShort());
                                break;
                            case ALREADY_USED:
                                errorLabel.setText(ClientI18nHelper.CONSTANTS.nameAlreadyUsed());
                                break;
                            case UNKNOWN_ERROR:
                                errorLabel.setText(ClientI18nHelper.CONSTANTS.unknownErrorReceived());
                                break;
                            default:
                                errorLabel.setText("???");
                                log.warning("VerificationRequestField.VerificationRequestCallback.onResponse() unknown ErrorResult: " + errorResult);
                        }
                    } else {
                        errorLabel.setText("");
                        fireValidStateChanged(true);
                    }
                }
            });
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
