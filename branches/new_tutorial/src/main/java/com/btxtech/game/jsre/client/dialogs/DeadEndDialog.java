package com.btxtech.game.jsre.client.dialogs;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: 22.04.13
 * Time: 23:24
 */
public class DeadEndDialog extends PeriodicDialog {
    private String message;

    public DeadEndDialog(String message) {
        super(ClientI18nHelper.CONSTANTS.reachedDeadEnd());
        this.message = message;
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        Label label = new Label(message);
        label.getElement().getStyle().setWidth(17, Style.Unit.EM);
        dialogVPanel.add(label);
        dialogVPanel.add(new HTML("&nbsp;"));
        Button button = new Button(ClientI18nHelper.CONSTANTS.newBase());
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                close();
                StartNewBaseDialog.show();
            }
        });
        dialogVPanel.add(button);
        dialogVPanel.setCellHorizontalAlignment(button, HasHorizontalAlignment.ALIGN_CENTER);
    }

}
