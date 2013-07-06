package com.btxtech.game.jsre.client.cockpit.chat;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class ChatConfigurationPanel extends Composite {

    private static ChatConfigurationPanelUiBinder uiBinder = GWT.create(ChatConfigurationPanelUiBinder.class);
    @UiField
    Button closeButton;
    @UiField
    RadioButton globalChatRadioButton;
    @UiField
    RadioButton guildChatRadioButton;
    private ChatPanel chatPanel;
    private PopupPanel popup;

    interface ChatConfigurationPanelUiBinder extends UiBinder<Widget, ChatConfigurationPanel> {
    }

    public ChatConfigurationPanel(ChatPanel chatPanel, PopupPanel popup) {
        this.chatPanel = chatPanel;
        this.popup = popup;
        initWidget(uiBinder.createAndBindUi(this));
        globalChatRadioButton.setValue(chatPanel.getChatCockpit().getChatMessageFilter() == ChatMessageFilter.GLOBAL);
        guildChatRadioButton.setValue(chatPanel.getChatCockpit().getChatMessageFilter() == ChatMessageFilter.GUILD);
        globalChatRadioButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                onCloseButtonClick(null);
            }
        });
        guildChatRadioButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                onCloseButtonClick(null);
            }
        });
    }

    @UiHandler("closeButton")
    void onCloseButtonClick(ClickEvent event) {
        if (globalChatRadioButton.getValue()) {
            chatPanel.getChatCockpit().setChatMessageFilter(ChatMessageFilter.GLOBAL);
        } else {
            chatPanel.getChatCockpit().setChatMessageFilter(ChatMessageFilter.GUILD);
        }
        popup.hide();
    }

}
