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

package com.btxtech.game.jsre.client.dialogs;

import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: beat
 * Date: Jul 20, 2009
 * Time: 8:30:15 PM
 */
public class SpeechBubble extends PopupPanel implements ClickHandler {
    public static final String CONSTRUCTION_VEHICLE = "CONSTRUCTION_VEHICLE";
    public static final String CONSTRUCTION_VEHICLE_TEXT = "This is your construction vehicle.<br>Select it and build your base.";
    public static final String CONSTRUCTION_VEHICLE_BUILD_MENU = "CONSTRUCTION_VEHICLE_BUILD_MENU";
    public static final String CONSTRUCTION_VEHICLE_BUILD_MENU_TEXT = "Select an item to build";
    static final private HashMap<String, Boolean> alreadyShown = new HashMap<String, Boolean>();
    static final private ArrayList<SpeechBubble> openBubbles = new ArrayList<SpeechBubble>();


    private ClientSyncItemView clientSyncItemView;
    private int x;
    private int y;
    private String text;

    public SpeechBubble(ClientSyncItemView clientSyncItemView, String text) {
        super(true);
        this.clientSyncItemView = clientSyncItemView;
        this.text = text;
        setup();
    }

    public SpeechBubble(int x, int y, String text) {
        super(true);
        this.x = x;
        this.y = y;
        this.text = text;
        setup();
    }

    private void setup() {
        setStyleName("speechBubble");
        VerticalPanel verticalPanel = new VerticalPanel();
        add(verticalPanel);
        SimplePanel closeCell = new SimplePanel();
        closeCell.getElement().getStyle().setProperty("background", "#ecc7c7");
        closeCell.getElement().getStyle().setProperty("padding", "4px");
        PushButton close = new PushButton(new Image("images/cancel.png"));
        close.setPixelSize(15, 16);
        close.addClickHandler(this);
        closeCell.add(close);
        verticalPanel.add(closeCell);
        HTML html = new HTML(text);
        html.getElement().getStyle().setProperty("background", "#ecc7c7");
        html.getElement().getStyle().setProperty("padding", "4px");
        verticalPanel.add(html);
        verticalPanel.add(new Image("images/pointer.gif"));
        getElement().getStyle().setZIndex(Constants.Z_INDEX_SPEECH_BUBBLE);
    }

    @Override
    public void onClick(ClickEvent event) {
        hide();
    }

    public void show() {
        int left;
        int top;
        if (clientSyncItemView != null) {
            left = clientSyncItemView.getRelativeMiddleX() + MapWindow.getAbsolutePanel().getAbsoluteLeft();
            top = clientSyncItemView.getRelativeMiddleY() + MapWindow.getAbsolutePanel().getAbsoluteTop() - clientSyncItemView.getOffsetHeight() / 2;
        } else {
            left = x;
            top = y;
        }
        top -= 90;
        left -= 30;
        setPopupPosition(left, top);
        super.show();
    }

    public static void createBubble(ClientSyncItemView clientSyncItemView, String key, String text) {
        synchronized (alreadyShown) {
            Boolean shown = alreadyShown.get(key);
            if (shown != null && shown) {
                return;
            }
            alreadyShown.put(key, true);


            SpeechBubble speechBubble = new SpeechBubble(clientSyncItemView, text);
            speechBubble.show();
            openBubbles.add(speechBubble);
        }
    }

    public static void createBubble(int x, int y, String key, String text) {
        synchronized (alreadyShown) {
            Boolean shown = alreadyShown.get(key);
            if (shown != null && shown) {
                return;
            }
            alreadyShown.put(key, true);


            SpeechBubble speechBubble = new SpeechBubble(x, y, text);
            speechBubble.show();
            openBubbles.add(speechBubble);
        }
    }


    public static void closeAllBubbles() {
        if (openBubbles.isEmpty()) {
            return;
        }

        synchronized (alreadyShown) {
            for (SpeechBubble openBubble : openBubbles) {
                openBubble.hide();
            }
            openBubbles.clear();
        }
    }

}
