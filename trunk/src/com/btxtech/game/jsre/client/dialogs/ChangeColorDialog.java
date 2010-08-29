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

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.ExtendedAbsolutePanel;
import com.btxtech.game.jsre.client.GwtCommon;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.List;

/**
 * User: beat
 * Date: 25.08.2010
 * Time: 15:53:14
 */
public class ChangeColorDialog extends Dialog {
    private static final int COLUMNS = 5;
    private static final int ROWS = 5;
    private static final int COUNT = COLUMNS * ROWS;
    private Grid colorGrid;
    private int index = 0;

    private class ColorListener implements MouseDownHandler {
        private String color;

        public ColorListener(String color) {
            this.color = color;
        }

        @Override
        public void onMouseDown(MouseDownEvent event) {
            Connection.getMovableServiceAsync().setBaseColor(color, new AsyncCallback<Void>() {

                @Override
                public void onFailure(Throwable caught) {
                    GwtCommon.handleException("setBaseColor failed", caught);

                }

                @Override
                public void onSuccess(Void result) {
                    close();
                }
            });
        }
    }

    public ChangeColorDialog() {
        setupDialog("Choose your color");
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        colorGrid = new Grid(ROWS, COLUMNS);
        dialogVPanel.add(colorGrid);
        setupColors();
        Button more = new Button("More");
        dialogVPanel.add(more);
        more.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setupColors();
            }
        });
    }

    private void setupColors() {
        Connection.getMovableServiceAsync().getFreeColors(index, COUNT, new AsyncCallback<List<String>>() {

            @Override
            public void onFailure(Throwable caught) {
                GwtCommon.handleException("getFreeColors failed", caught);
            }

            @Override
            public void onSuccess(List<String> result) {
                index += result.size();
                int count = 0;
                for (int row = 0; row < ROWS; row++) {
                    for (int column = 0; column < COLUMNS; column++) {
                        ExtendedAbsolutePanel absolutePanel = new ExtendedAbsolutePanel();
                        absolutePanel.setPixelSize(20, 20);
                        if (count < result.size()) {
                            absolutePanel.getElement().getStyle().setBackgroundColor(result.get(count));
                            absolutePanel.addMouseDownHandler(new ColorListener(result.get(count)));
                            absolutePanel.getElement().getStyle().setCursor(Style.Cursor.POINTER);
                        } else {
                            absolutePanel.getElement().getStyle().setBackgroundColor("#FFFFFF");
                            index = 0;
                        }
                        colorGrid.setWidget(row, column, absolutePanel);
                        count++;
                    }
                }
            }
        });
    }

    public static void doShow() {
        new ChangeColorDialog();
    }


}
