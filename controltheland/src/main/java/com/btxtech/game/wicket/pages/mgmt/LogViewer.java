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

package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.mgmt.MgmtService;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 03.11.2009
 * Time: 22:13:26
 */
public class LogViewer extends MgmtWebPage {
    @SpringBean
    private MgmtService mgmtService;
    private String logFileText;
    private int lines = 100;

    public LogViewer() {
        Form form = new Form("form");
        add(form);

        form.add(new TextField<>("lines", new IModel<Integer>() {

            @Override
            public void detach() {
                //Ignore
            }

            @Override
            public Integer getObject() {
                return lines;
            }

            @Override
            public void setObject(Integer lines) {
                LogViewer.this.lines = lines;
            }
        }, Integer.class));

        Button view = new Button("view") {
            @Override
            public void onSubmit() {
            }
        };
        form.add(view);

        form.add(new TextArea<>("log", new IModel<String>() {
            @Override
            public String getObject() {
                if (logFileText == null) {
                    logFileText = mgmtService.getLogFileText(lines);
                }
                return logFileText;
            }

            @Override
            public void setObject(String object) {
                //Ignore
            }

            @Override
            public void detach() {
                logFileText = null;
            }
        }));

    }
}
