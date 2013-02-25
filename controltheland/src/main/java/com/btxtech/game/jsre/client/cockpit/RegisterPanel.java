package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.SimpleUser;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.RegisterDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class RegisterPanel extends Composite {

    private static RegisterUiBinder uiBinder = GWT.create(RegisterUiBinder.class);
    @UiField
    Button button;
    @UiField
    Label label;

    interface RegisterUiBinder extends UiBinder<Widget, RegisterPanel> {
    }

    public RegisterPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setSimpleUser(SimpleUser simpleUser) {
        if (simpleUser != null) {
            button.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
            label.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
            label.setText(simpleUser.getName());
        } else {
            button.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
            label.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        }
    }

    @UiHandler("button")
    void onButtonClick(ClickEvent event) {
        DialogManager.showDialog(new RegisterDialog(), DialogManager.Type.PROMPTLY);
    }

}
