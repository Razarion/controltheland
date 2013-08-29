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

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.common.Constants;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: Jul 2, 2009
 * Time: 3:19:00 PM
 */
public abstract class Dialog extends DialogBox {
    private boolean showCloseButton = true;
    private ClickHandler yesButtonClickHandler;
    private String yesButtonText;
    private Button yesButton;

    protected Dialog(String title) {
        super(false, false);
        setText(title);
        setStyleName("dialogBox");
    }

    public void setShowCloseButton(boolean showCloseButton) {
        this.showCloseButton = showCloseButton;
    }

    public void setShowYesButton(ClickHandler yesButtonClickHandler, String buttonText) {
        this.yesButtonClickHandler = yesButtonClickHandler;
        yesButtonText = buttonText;
    }

    protected void setupDialog() {
        VerticalPanel dialogVPanel = new VerticalPanel();
        if (yesButtonClickHandler != null) {
            yesButton = new Button(yesButtonText, new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    close();
                    yesButtonClickHandler.onClick(event);
                }
            });
            yesButton.getElement().getStyle().setMarginTop(20, Style.Unit.PX);
        }
        setupPanel(dialogVPanel);
        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
        setWidget(dialogVPanel);
        if (showCloseButton && yesButtonClickHandler != null) {
            HorizontalPanel horizontalPanel = new HorizontalPanel();
            horizontalPanel.setWidth("100%");
            dialogVPanel.add(horizontalPanel);
            // Add now button
            Button noButton = new Button(ClientI18nHelper.CONSTANTS.close(), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    close();
                }
            });
            noButton.getElement().getStyle().setMarginTop(20, Style.Unit.PX);
            noButton.setTitle(ClientI18nHelper.CONSTANTS.tooltipCloseDialog());
            horizontalPanel.add(noButton);
            horizontalPanel.setCellHorizontalAlignment(noButton, HasHorizontalAlignment.ALIGN_CENTER);
            noButton.setFocus(true);
            // Add yes button
            horizontalPanel.add(yesButton);
            horizontalPanel.setCellHorizontalAlignment(yesButton, HasHorizontalAlignment.ALIGN_CENTER);
       } else if (!showCloseButton && yesButtonClickHandler != null) {
            dialogVPanel.add(yesButton);
            dialogVPanel.setCellHorizontalAlignment(yesButton, HasHorizontalAlignment.ALIGN_CENTER);
            yesButton.setFocus(true);
        } else if (showCloseButton) {
            Button noButton = new Button(ClientI18nHelper.CONSTANTS.close(), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    close();
                }
            });
            noButton.getElement().getStyle().setMarginTop(20, Style.Unit.PX);
            noButton.setTitle(ClientI18nHelper.CONSTANTS.tooltipCloseDialog());
            dialogVPanel.add(noButton);
            dialogVPanel.setCellHorizontalAlignment(noButton, HasHorizontalAlignment.ALIGN_CENTER);
            noButton.setFocus(true);
        }
        center();
        getElement().getStyle().setZIndex(getZIndex());
    }

    protected int getZIndex() {
        return Constants.Z_INDEX_DIALOG;
    }

    public void close() {
        hide(true);
    }

    public void setYesButtonEnabled(boolean enabled) {
        if (yesButton != null) {
            yesButton.setEnabled(enabled);
        }
    }

    /**
     * Does not make the dialog really bigger
     * Put the size in the root-panel in the UiBinder
     * See HistoryPanel
     * @param em
     */
    protected void setDialogWidth(int em) {
        getElement().getStyle().setWidth(em, Style.Unit.EM);
    }

    abstract protected void setupPanel(VerticalPanel dialogVPanel);
}
