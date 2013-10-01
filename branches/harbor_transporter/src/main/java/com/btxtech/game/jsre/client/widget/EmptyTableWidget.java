package com.btxtech.game.jsre.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class EmptyTableWidget extends Composite {

    private static EmptyTableWidgetUiBinder uiBinder = GWT.create(EmptyTableWidgetUiBinder.class);
    @UiField
    Label text;

    interface EmptyTableWidgetUiBinder extends UiBinder<Widget, EmptyTableWidget> {
    }

    public EmptyTableWidget(String s) {
        initWidget(uiBinder.createAndBindUi(this));
        text.setText(s);
    }

}
