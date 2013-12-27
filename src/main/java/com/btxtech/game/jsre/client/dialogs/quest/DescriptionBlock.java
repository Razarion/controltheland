package com.btxtech.game.jsre.client.dialogs.quest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DescriptionBlock extends Composite {
    private static DescriptionBlockUiBinder uiBinder = GWT.create(DescriptionBlockUiBinder.class);
    @UiField
    Label titleLabel;
    @UiField
    HTML htmlLabel;

    interface DescriptionBlockUiBinder extends UiBinder<Widget, DescriptionBlock> {
    }

    public DescriptionBlock(String title, String text) {
        initWidget(uiBinder.createAndBindUi(this));
        titleLabel.setText(title);
        htmlLabel.setHTML(text);
    }
}
