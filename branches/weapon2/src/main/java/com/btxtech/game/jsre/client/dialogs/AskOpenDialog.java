package com.btxtech.game.jsre.client.dialogs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: 19.06.13
 * Time: 19:23
 */
public class AskOpenDialog extends Dialog {
    private String message;

    public AskOpenDialog(String title, String message, String buttonText, final Runnable onOpenRunnable) {
        super(title);
        this.message = message;
        setDialogWidth(25);
        setShowYesButton(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                close();
                onOpenRunnable.run();
            }
        }, buttonText);
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        dialogVPanel.add(new Label(message));
    }
}
