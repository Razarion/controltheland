package com.btxtech.game.jsre.client.dialogs;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: 27.06.12
 * Time: 01:15
 */
public class YesNoDialog extends Dialog {
    private String message;
    private String yesText;
    private ClickHandler yesHandler;
    private String noText;
    private ClickHandler noHandler;

    public YesNoDialog(String title, String message, String yesText, ClickHandler yesHandler, String noText, ClickHandler noHandler) {
        super(title);
        this.message = message;
        this.yesText = yesText;
        this.yesHandler = yesHandler;
        this.noText = noText;
        this.noHandler = noHandler;
        setShowCloseButton(false);
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        HTML messageWidget = new HTML(message, true);
        messageWidget.getElement().getStyle().setWidth(17, Style.Unit.EM);
        dialogVPanel.add(messageWidget);

        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.setWidth("100%");
        dialogVPanel.add(horizontalPanel);

        Button noButton = new Button(noText, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                close();
                if (noHandler != null) {
                    noHandler.onClick(event);
                }
            }
        });
        noButton.getElement().getStyle().setMarginTop(20, Style.Unit.PX);
        horizontalPanel.add(noButton);
        horizontalPanel.setCellHorizontalAlignment(noButton, HasHorizontalAlignment.ALIGN_CENTER);

        Button yesButton = new Button(yesText, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                close();
                if (yesHandler != null) {
                    yesHandler.onClick(event);
                }
            }
        });
        yesButton.getElement().getStyle().setMarginTop(20, Style.Unit.PX);
        horizontalPanel.add(yesButton);
        horizontalPanel.setCellHorizontalAlignment(yesButton, HasHorizontalAlignment.ALIGN_CENTER);
    }
}
