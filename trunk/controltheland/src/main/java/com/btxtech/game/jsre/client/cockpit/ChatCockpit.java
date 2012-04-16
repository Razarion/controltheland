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

package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.ClientChatHandler;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.ChatMessage;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Position;
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
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


/**
 * User: beat
 * Date: 20.02.2010
 * Time: 18:36:49
 */
public class ChatCockpit extends AbsolutePanel implements ChatListener {
    private static final ChatCockpit INSTANCE = new ChatCockpit();
    private boolean resizeMode;
    private AbsolutePanel contentPanel;
    private static final int CONTENT_BORDER = 2;
    private static final int RESIZE_CURSOR_AREA = 10;
    private HTML receiving;
    private TextBox send;

    public static ChatCockpit getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ChatCockpit() {
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
        contentPanel = new AbsolutePanel();
        preventEvents(contentPanel);
        contentPanel.setWidth("100%");
        contentPanel.setHeight("100%");
        contentPanel.getElement().getStyle().setFontSize(11, Unit.PX);
        contentPanel.getElement().getStyle().setProperty("fontFamily", "Arial,Helvetica,sans-serif");
        add(contentPanel);
        // Receiving area
        receiving = new HTML();
        receiving.setWidth("100%");
        receiving.getElement().getStyle().setOverflowY(Style.Overflow.SCROLL);
        receiving.getElement().getStyle().setOverflowX(Style.Overflow.HIDDEN);
        preventEvents(receiving);
        receiving.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        receiving.getElement().getStyle().setBorderWidth(1, Unit.PX);
        receiving.getElement().getStyle().setBorderColor("#bbbcba");
        receiving.getElement().getStyle().setBackgroundColor("#031723");
        receiving.getElement().getStyle().setColor("#c0bdb2");
        contentPanel.add(receiving, 0, 0);
        // Send field
        send = new TextBox();
        send.setWidth("100%");
        send.getElement().getStyle().setProperty("boxSizing", "border-box");
        send.getElement().getStyle().setPosition(Position.ABSOLUTE);
        send.getElement().getStyle().clearTop();
        send.getElement().getStyle().clearRight();
        send.getElement().getStyle().setBottom(0, Unit.PX);
        send.getElement().getStyle().setLeft(0, Unit.PX);
        send.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        send.getElement().getStyle().setBorderWidth(1, Unit.PX);
        send.getElement().getStyle().setBorderColor("#bbbcba");
        send.getElement().getStyle().setBackgroundColor("#031723");
        send.getElement().getStyle().setColor("#c0bdb2");
        send.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    ClientChatHandler.getInstance().sendMessage(send.getText());
                    send.setText("");
                }
            }
        });
        contentPanel.add(send);
    }

    @Override
    protected void onLoad() {
        doInnerLayout();
    }

    public void addToParent(AbsolutePanel parent) {
        parent.add(this, 0, 0);
        getElement().getStyle().setZIndex(Constants.Z_INDEX_CHAT_COCKPIT);
        getElement().getStyle().clearTop();
        getElement().getStyle().clearLeft();
        getElement().getStyle().setBottom(10, Style.Unit.PX);
        getElement().getStyle().setRight(10, Style.Unit.PX);
    }

    @Override
    public void clearMessages() {
        receiving.setText("");
    }

    @Override
    public void addMessage(ChatMessage chatMessage) {
        if (receiving.getHTML().trim().isEmpty()) {
            receiving.setHTML(generateLine(chatMessage));
        } else {
            receiving.setHTML(receiving.getHTML() + "<br />" + generateLine(chatMessage));
        }
        receiving.getElement().setScrollTop(receiving.getElement().getScrollHeight());
    }

    private String generateLine(ChatMessage chatMessage) {
        StringBuilder builder = new StringBuilder();
        builder.append("<span style='color:#FF6464;font-weight:bold'>[");
        builder.append(SafeHtmlUtils.htmlEscape(chatMessage.getName()));
        builder.append("]</span> ");
        builder.append(SafeHtmlUtils.htmlEscape(chatMessage.getMessage()));
        return builder.toString();
    }

    public void blurFocus() {
        send.setFocus(false);
    }

    private boolean isResizeAllowed(MouseEvent event) {
        return RESIZE_CURSOR_AREA > event.getX() || RESIZE_CURSOR_AREA > event.getY();
    }

    private void doResize(MouseEvent event) {
        setPixelSize(getOffsetWidth() - event.getX(), getOffsetHeight() - event.getY());
        doInnerLayout();
    }

    private void doInnerLayout() {
        int height = contentPanel.getOffsetHeight() - send.getOffsetHeight() - 3;
        if (height < 0) {
            height = 0;
        }
        receiving.setHeight(height + "px");
        receiving.getElement().setScrollTop(receiving.getElement().getScrollHeight());
    }

    private void preventEvents(Widget widget) {
        widget.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        widget.addDomHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                send.setFocus(true);
                GwtCommon.preventDefault(event);
            }
        }, MouseUpEvent.getType());

        widget.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                send.setFocus(true);
                GwtCommon.preventDefault(event);
            }
        }, MouseDownEvent.getType());
    }

    public Rectangle getArea() {
        return new Rectangle(getAbsoluteLeft(), getAbsoluteTop(), getOffsetWidth(), getOffsetHeight());
    }
}