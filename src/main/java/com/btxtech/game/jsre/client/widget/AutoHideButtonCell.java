package com.btxtech.game.jsre.client.widget;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;

public class AutoHideButtonCell extends ButtonCell {

    public AutoHideButtonCell() {
    }

    public AutoHideButtonCell(SafeHtmlRenderer<String> renderer) {
        super(renderer);
    }

    @Override
    public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
        if (data != null) {
            super.render(context, data, sb);
        }
    }
}
