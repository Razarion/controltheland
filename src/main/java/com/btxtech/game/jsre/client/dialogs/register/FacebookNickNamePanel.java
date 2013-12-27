package com.btxtech.game.jsre.client.dialogs.register;

import com.btxtech.game.jsre.client.ClientUserService;
import com.btxtech.game.jsre.client.VerificationRequestField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class FacebookNickNamePanel extends Composite {
    private static FacebookNickNamePanelUiBinder uiBinder = GWT.create(FacebookNickNamePanelUiBinder.class);
    @UiField(provided = true)
    NickNameField nickNameField;

    interface FacebookNickNamePanelUiBinder extends UiBinder<Widget, FacebookNickNamePanel> {
    }

    public FacebookNickNamePanel(VerificationRequestField.ValidListener validListener) {
        nickNameField = new NickNameField(validListener);
        initWidget(uiBinder.createAndBindUi(this));
        nickNameField.checkName();
    }

    public NickNameField getNickNameField() {
        return nickNameField;
    }


}
