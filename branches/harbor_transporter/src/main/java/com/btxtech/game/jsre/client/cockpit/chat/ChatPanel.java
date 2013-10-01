package com.btxtech.game.jsre.client.cockpit.chat;

import com.btxtech.game.jsre.client.cockpit.chat.images.ChatImages;
import com.btxtech.game.jsre.client.common.Constants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ChatPanel extends Composite {
    private static final int INSET = 10;
    private static ChatPanelUiBinder uiBinder = GWT.create(ChatPanelUiBinder.class);
    private static ChatImages images = GWT.create(ChatImages.class);
    private ChatCockpit chatCockpit;
    @UiField
    PushButton chatConfigButton;
    @UiField
    HTML receivingBox;
    @UiField
    TextBox sendBox;

    interface ChatPanelUiBinder extends UiBinder<Widget, ChatPanel> {
    }

    public ChatPanel(ChatCockpit chatCockpit) {
        this.chatCockpit = chatCockpit;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("chatConfigButton")
    void onChatConfigButton(ClickEvent event) {
        final int componentMiddleX = chatConfigButton.getAbsoluteLeft() + chatConfigButton.getOffsetWidth() / 2;
        final int componentMiddleY = chatConfigButton.getAbsoluteTop() + chatConfigButton.getOffsetHeight() / 2;
        final PopupPanel popup = new PopupPanel(true);
        popup.setWidget(new ChatConfigurationPanel(this, popup));
        popup.getElement().getStyle().setZIndex(Constants.Z_INDEX_POPUP);
        popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                int left = componentMiddleX - offsetWidth / 2;
                int top = componentMiddleY - offsetHeight / 2;
                if (componentMiddleX + offsetWidth / 2 + INSET > Window.getClientWidth()) {
                    left -= componentMiddleX + offsetWidth / 2 + INSET - Window.getClientWidth();
                }
                if (componentMiddleY + offsetHeight / 2 + INSET > Window.getClientHeight()) {
                    top -= componentMiddleY + offsetHeight / 2 + INSET - Window.getClientHeight();
                }
                popup.setPopupPosition(left, top);
            }
        });

    }

    public ChatCockpit getChatCockpit() {
        return chatCockpit;
    }

    public void setChatMessageFilterIcon(ChatMessageFilter chatMessageFilter) {
        switch (chatMessageFilter) {
            case GLOBAL: {
                chatConfigButton.getUpFace().setImage(new Image(images.globe()));
                break;
            }
            case GUILD: {
                chatConfigButton.getUpFace().setImage(new Image(images.guild()));
                break;
            }
            default:
                throw new IllegalArgumentException("ChatPanel.setChatMessageFilter() unknown ChatMessageFilter: " + chatMessageFilter);
        }
    }

    public HTML getReceivingBox() {
        return receivingBox;
    }

    public TextBox getSendBox() {
        return sendBox;
    }

    @UiHandler("chatConfigButton")
    void onMouseMoveEvent(MouseMoveEvent event) {
        // Prevent chat windows resize
        event.stopPropagation();
    }

    @UiHandler("chatConfigButton")
    void onMouseDownEvent(MouseDownEvent event) {
        // Prevent chat windows resize
        event.stopPropagation();
    }

    @UiHandler("chatConfigButton")
    void onMouseUpEvent(MouseUpEvent event) {
        // Prevent chat windows resize
        event.stopPropagation();
    }
}
