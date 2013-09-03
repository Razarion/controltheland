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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ExceptionDialog extends Dialog {
    private Throwable throwable;

    public ExceptionDialog(Throwable throwable) {
        super("Exception");
        this.throwable = throwable;
    }

    protected void setupPanel(VerticalPanel parent) {
        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.setSize("400px", "400px");
        parent.add(scrollPanel);
        VerticalPanel exceptionPanel = new VerticalPanel();
        scrollPanel.add(exceptionPanel);
        exceptionPanel.add(new HTML("<b>Exception (GWT: " + GWT.getVersion() + ")</b>", false));
        exceptionPanel.add(new HTML("<b>" + throwable.getMessage() + "</b>", false));
        boolean isCause = false;
        Throwable tmpThrowable = throwable;
        while (true) {
            addStackTrace(exceptionPanel, tmpThrowable, isCause);
            Throwable inner = tmpThrowable.getCause();
            if (inner == null || inner == tmpThrowable) {
                break;
            }
            tmpThrowable = inner;
            isCause = true;
        }

    }

    private void addStackTrace(VerticalPanel dialogVPanel, Throwable t, boolean isCause) {
        if (isCause) {
            dialogVPanel.add(new HTML("Caused by: " + t.toString()));
        } else {
            dialogVPanel.add(new HTML(t.toString()));
        }
        for (Object element : t.getStackTrace()) {
            HTML label = new HTML("&nbsp;&nbsp;at " + element.toString() + "</b>");
            dialogVPanel.add(label);
        }
    }

}

