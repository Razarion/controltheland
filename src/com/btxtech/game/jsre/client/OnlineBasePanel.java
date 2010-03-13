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

package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.common.OnlineBaseUpdate;
import com.btxtech.game.jsre.client.common.UserMessage;
import com.btxtech.game.jsre.common.SimpleBase;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 10.03.2010
 * Time: 20:19:04
 */
public class OnlineBasePanel extends TopMapPanel {
    public static final int FLASHING_COUNT = 3;
    public static final int FLASHING_DELAY = 500;
    public static final int MAX_CHARS_RECEIVED_BOX = 1000;
    public static final OnlineBasePanel INSTANCE = new OnlineBasePanel();
    private FlexTable flexTable;
    private TextArea receivedText;
    private int currentFlashingCount = 0;
    private Timer timer;

    public static OnlineBasePanel getInstance() {
        return INSTANCE;
    }

    @Override
    protected Widget createBody() {
        VerticalPanel verticalPanel = new VerticalPanel();
        setupBases(verticalPanel);
        verticalPanel.add(new HTML("<hr>"));
        setupMessanger(verticalPanel);
        return verticalPanel;
    }

    private void setupBases(VerticalPanel verticalPanel) {
        HTML html = new HTML("<b>Bases online</b>");
        html.getElement().getStyle().setColor("darkorange");
        verticalPanel.add(html);

        flexTable = new FlexTable();
        verticalPanel.add(flexTable);
    }

    public void setOnlineBases(OnlineBaseUpdate onlineBaseUpdate) {
        while (flexTable.getRowCount() > 0) {
            flexTable.removeRow(0);
        }
        for (SimpleBase onlineBase : onlineBaseUpdate.getOnlineBases()) {
            addOnlineBase(onlineBase);
        }
    }

    private void addOnlineBase(SimpleBase simpleBase) {
        InlineLabel label = new InlineLabel(simpleBase.getName());
        label.getElement().getStyle().setColor(simpleBase.getHtmlColor());
        flexTable.setWidget(flexTable.getRowCount() + 1, 0, label);
    }

    public void onMessageReceived(UserMessage userMessage) {
        StringBuffer buffer = new StringBuffer();
        if (receivedText.getText().length() > MAX_CHARS_RECEIVED_BOX) {
            buffer.append(receivedText.getText().substring(receivedText.getText().length() - MAX_CHARS_RECEIVED_BOX));
            buffer.append("\n");
        } else if (!receivedText.getText().isEmpty()) {
            buffer.append(receivedText.getText());
            buffer.append("\n");
        }
        buffer.append("Base: ");
        buffer.append(userMessage.getBaseName());
        buffer.append("\n");
        buffer.append(userMessage.getMessage());
        buffer.append("\n");
        receivedText.setText(buffer.toString());
        receivedText.getElement().setScrollTop(receivedText.getElement().getScrollHeight());
        flashReceivingText();
    }

    private void setupMessanger(VerticalPanel verticalPanel) {
        HTML html = new HTML("<b>Messages received</b>");
        html.getElement().getStyle().setColor("darkorange");
        verticalPanel.add(html);
        receivedText = new TextArea();
        receivedText.getElement().getStyle().setColor("black");
        receivedText.setCharacterWidth(30);
        receivedText.setReadOnly(true);
        receivedText.setVisibleLines(5);
        verticalPanel.add(receivedText);
        html = new HTML("<b>Message to send</b>");
        html.getElement().getStyle().setColor("darkorange");
        verticalPanel.add(html);
        final TextArea sendText = new TextArea();
        sendText.setVisibleLines(2);
        sendText.setCharacterWidth(30);
        verticalPanel.add(sendText);
        Button button = new Button("Send");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (sendText.getText().isEmpty()) {
                    return;
                }
                Connection.getInstance().sendUserMessage(sendText.getText());
                sendText.setText("");
            }
        });
        verticalPanel.add(button);
    }

    private void flashReceivingText() {
        if (currentFlashingCount > 0) {
            return;
        }
        timer = new Timer() {
            @Override
            public void run() {
                if (currentFlashingCount <= 0) {
                    receivedText.getElement().getStyle().setBackgroundColor("white");
                    timer.cancel();
                } else {
                    currentFlashingCount--;
                    if(currentFlashingCount % 2 == 1) {
                        receivedText.getElement().getStyle().setBackgroundColor("white");
                    }   else {
                        receivedText.getElement().getStyle().setBackgroundColor("red");
                    }
                }
            }
        };
        currentFlashingCount = FLASHING_COUNT;
        timer.scheduleRepeating(FLASHING_DELAY);
    }
}
