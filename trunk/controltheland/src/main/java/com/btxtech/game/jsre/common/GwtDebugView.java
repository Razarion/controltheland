package com.btxtech.game.jsre.common;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.Date;

/**
 * User: beat
 * Date: 26.04.13
 * Time: 21:29
 */
public class GwtDebugView {
    private static final GwtDebugView INSTANCE = new GwtDebugView();
    private DialogBox dialogBox;
    private FlexTable textTable;

    /**
     * Singleton
     */
    private GwtDebugView() {
        dialogBox = new DialogBox(false,false);
        VerticalPanel verticalPanel = new VerticalPanel();
        dialogBox.setText("Debug View");
        textTable = new FlexTable();
        textTable.getElement().getStyle().setColor("#000000");
        textTable.setWidth("500px");
        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.setWidth("300px");
        scrollPanel.setHeight("500px");
        scrollPanel.setWidget(textTable);
        verticalPanel.add(scrollPanel);
        dialogBox.setWidget(verticalPanel);
        Button button = new Button("Clear");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                textTable.removeAllRows();
            }
        });
        verticalPanel.add(button);
        dialogBox.center();
        dialogBox.show();
    }

    public static void printLn(String s) {
        INSTANCE.privatePrintLn(s);
    }

    private void privatePrintLn(String s) {
        int row = textTable.getRowCount();
        textTable.setText(row, 0, new Date().toString());
        textTable.setText(row, 1, s);
    }
}
