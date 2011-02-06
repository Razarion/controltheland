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

package com.btxtech.game.wicket.pages.messenger;

import com.btxtech.game.services.messenger.MessengerService;
import com.btxtech.game.wicket.pages.BorderPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 01.04.2010
 * Time: 23:44:04
 */
public class MessengerOverviewPanel extends BorderPanel {
    @SpringBean
    private MessengerService messengerService;

    public MessengerOverviewPanel(String id) {
        super(id);
        Label label = new Label("text", new IModel<String>() {
            @Override
            public String getObject() {
                int unread = messengerService.getUnreadMails();
                if (unread == 0) {
                    return "You have no new mails";
                } else if (unread == 1) {
                    return "You have 1 new mail";
                } else {
                    return "You have " + unread + " new mails";
                }
            }

            @Override
            public void setObject(String s) {
                // Ignore
            }

            @Override
            public void detach() {
                // Ignore
            }
        });
        label.add(new AttributeModifier("style", true, new IModel<String>(){

            @Override
            public String getObject() {
                int unread = messengerService.getUnreadMails();
                if(unread > 0) {
                    return "text-decoration:blink";
                }   else {
                    return "";
                }
            }

            @Override
            public void setObject(String s) {
                // Ignore
            }

            @Override
            public void detach() {
                // Ignore
            }
        }));
        add(label);
        add(new Form("form") {
            @Override
            protected void onSubmit() {
                setResponsePage(new Messenger());
            }
        });
    }
}
