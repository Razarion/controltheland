/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.jsre.client.cockpit.chat;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ClientMessageIdPacketHandler;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.NotAGuildMemberException;
import com.btxtech.game.jsre.client.cockpit.ChatListener;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.MessageIdPacket;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;
import java.util.logging.Logger;


/**
 * User: beat
 * Date: 20.02.2010
 * Time: 18:36:49
 */
public class ChatCockpit extends AbsolutePanel implements ChatListener {
    private Logger log = Logger.getLogger(ChatCockpit.class.getName());
    private static final ChatCockpit INSTANCE = new ChatCockpit();
    private boolean resizeMode;
    private static final int CONTENT_BORDER = 2;
    private static final int RESIZE_CURSOR_AREA = 10;
    private ChatMessageFilter chatMessageFilter;
    private ChatPanel chatPanel;

    public static ChatCockpit getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ChatCockpit() {
        chatMessageFilter = ChatMessageFilter.GLOBAL;
        getElement().getStyle().setBackgroundColor("#868684");
        getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        getElement().getStyle().setBorderWidth(1, Unit.PX);
        getElement().getStyle().setBorderColor("#bbbcba");
        getElement().getStyle().setPadding(CONTENT_BORDER, Unit.PX);
        setPixelSize(190, 110);
        addDomHandler(new MouseMoveHandler() {

            @Override
            public void onMouseMove(MouseMoveEvent event) {
                if (isResizeAllowed(event)) {
                    getElement().getStyle().setCursor(Cursor.SE_RESIZE);
                } else {
                    getElement().getStyle().setCursor(Cursor.DEFAULT);
                }
                if (resizeMode) {
                    doResize(event);
                }
            }

        }, MouseMoveEvent.getType());

        addDomHandler(new MouseDownHandler() {

            @Override
            public void onMouseDown(MouseDownEvent event) {
                if (!resizeMode && isResizeAllowed(event)) {
                    DOM.setCapture(getElement());
                    resizeMode = true;
                    doResize(event);
                }
            }

        }, MouseDownEvent.getType());

        addDomHandler(new MouseUpHandler() {

            @Override
            public void onMouseUp(MouseUpEvent event) {
                if (resizeMode) {
                    DOM.releaseCapture(getElement());
                    resizeMode = false;
                }
            }

        }, MouseUpEvent.getType());

        preventEvents(this);
        // Content panel
        chatPanel = new ChatPanel(this);
        add(chatPanel);
        chatPanel.getSendBox().addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    String text = chatPanel.getSendBox().getText();
                    if (text == null || text.trim().length() == 0) {
                        TerrainView.getInstance().setFocus();
                        return;
                    }
                    ClientMessageIdPacketHandler.getInstance().sendMessage(text, chatMessageFilter);
                    chatPanel.getSendBox().setText("");
                    TerrainView.getInstance().setFocus();
                }
            }
        });
    }

    public void addToParent(AbsolutePanel parent) {
        parent.add(this, 0, 0);
        getElement().getStyle().setZIndex(Constants.Z_INDEX_CHAT_COCKPIT);
        getElement().getStyle().clearTop();
        getElement().getStyle().clearLeft();
        getElement().getStyle().setBottom(0, Style.Unit.PX);
        getElement().getStyle().setRight(0, Style.Unit.PX);
    }

    @Override
    public void clearMessages() {
        chatPanel.getReceivingBox().setText("");
    }

    @Override
    public void addMessage(ChatMessage chatMessage) {
        if (chatPanel.getReceivingBox().getHTML().trim().isEmpty()) {
            chatPanel.getReceivingBox().setHTML(generateLine(chatMessage));
        } else {
            chatPanel.getReceivingBox().setHTML(chatPanel.getReceivingBox().getHTML() + "<br />" + generateLine(chatMessage));
        }
        doInnerLayout();
    }

    private String generateLine(ChatMessage chatMessage) {
        StringBuilder builder = new StringBuilder();
        builder.append("<span style='color:");
        switch (chatMessage.getType()) {
            case OWN:
                builder.append(ClientBase.OWN_BASE_COLOR);
                break;
            case GUILD:
                builder.append(ClientBase.GUILD_MEMBER_BASE_COLOR);
                break;
            case ENEMY:
                builder.append(ClientBase.ENEMY_BASE_COLOR);
                break;
            case ADMIN:
                builder.append("#D358F7");
                break;
            default:
                builder.append("#FFFFFF");
                log.warning("ChatCockpit.generateLine() unexpected or unknown ChatMessage.Type: " + chatMessage.getType());
                break;
        }
        builder.append(";font-weight:bold'>[");
        builder.append(SafeHtmlUtils.htmlEscape(chatMessage.getName()));
        builder.append("]</span> ");
        builder.append(SafeHtmlUtils.htmlEscape(chatMessage.getMessage()));
        return builder.toString();
    }

    public void blurFocus() {
        chatPanel.getSendBox().setFocus(false);
    }

    private boolean isResizeAllowed(MouseEvent event) {
        return RESIZE_CURSOR_AREA > event.getX() || RESIZE_CURSOR_AREA > event.getY();
    }

    private void doResize(MouseEvent event) {
        setPixelSize(getOffsetWidth() - event.getX(), getOffsetHeight() - event.getY());
        doInnerLayout();
    }

    private void doInnerLayout() {
        int height = getElement().getClientHeight() - chatPanel.getSendBox().getOffsetHeight() - 6;
        if (height < 0) {
            height = 0;
        }
        chatPanel.getReceivingBox().setHeight(height + "px");
        chatPanel.getReceivingBox().getElement().setScrollTop(chatPanel.getReceivingBox().getElement().getScrollHeight());
    }

    private void preventEvents(Widget widget) {
        widget.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        widget.addDomHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                chatPanel.getSendBox().setFocus(true);
                GwtCommon.preventDefault(event);
            }
        }, MouseUpEvent.getType());

        widget.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                chatPanel.getSendBox().setFocus(true);
                GwtCommon.preventDefault(event);
            }
        }, MouseDownEvent.getType());
    }

    @Override
    public ChatMessageFilter getChatMessageFilter() {
        return chatMessageFilter;
    }

    public void setChatMessageFilter(final ChatMessageFilter chatMessageFilter) {
        if (chatMessageFilter == this.chatMessageFilter) {
            return;
        }
        if (Connection.getMovableServiceAsync() != null) {
            Connection.getMovableServiceAsync().setChatMessageFilter(chatMessageFilter, new AsyncCallback<List<MessageIdPacket>>() {
                @Override
                public void onFailure(Throwable caught) {
                    if (caught instanceof NotAGuildMemberException) {
                        DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.chatMessageFilter(), ClientI18nHelper.CONSTANTS.chatMessageFilterNoGuild()),
                                DialogManager.Type.QUEUE_ABLE);
                    } else {
                        ClientExceptionHandler.handleException("setChatMessageFilter failed: ", caught);
                    }
                }

                @Override
                public void onSuccess(List<MessageIdPacket> messageIdPackets) {
                    chatPanel.setChatMessageFilterIcon(chatMessageFilter);
                    ChatCockpit.this.chatMessageFilter = chatMessageFilter;
                    ClientMessageIdPacketHandler.getInstance().onSetChatMessageFilterChanged(messageIdPackets);
                }
            });
        }
    }
}