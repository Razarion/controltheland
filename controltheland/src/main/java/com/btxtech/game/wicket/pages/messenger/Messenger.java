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

import com.btxtech.game.services.messenger.Mail;
import com.btxtech.game.services.messenger.MessengerService;
import com.btxtech.game.wicket.WebCommon;
import com.btxtech.game.wicket.pages.basepage.BasePage;
import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 02.04.2010
 * Time: 13:19:58
 */
public class Messenger extends BasePage {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(WebCommon.DATE_TIME_FORMAT_STRING);
    @SpringBean
    private MessengerService messengerService;

    public Messenger() {
        this(null);
    }

    public Messenger(Mail displayMail) {
        add(new Form("newMail") {

            @Override
            protected void onSubmit() {
                setResponsePage(new WriteMail());
            }
        });


        ListView<Mail> mails = new ListView<Mail>("mails", new IModel<List<Mail>>() {

            @Override
            public List<Mail> getObject() {
                return messengerService.getMails();
            }

            @Override
            public void setObject(List<Mail> mails) {
                //Ignore
            }

            @Override
            public void detach() {
                //Ignore
            }
        }) {
            @Override
            protected void populateItem(final ListItem<Mail> mailListItem) {
                if (mailListItem.getModelObject().isRead()) {
                    mailListItem.add(new Image("read", new Model<String>("mail-open.png")));
                } else {
                    mailListItem.add(new Image("read", new Model<String>("mail.png")));
                }
                mailListItem.add(new Label("date", simpleDateFormat.format(mailListItem.getModelObject().getSent())));
                mailListItem.add(new Label("from", mailListItem.getModelObject().getFromUser()));
                Link link = new Link("subjectLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(new Messenger(mailListItem.getModelObject()));
                    }
                };
                link.add(new Label("subject", mailListItem.getModelObject().getSubject()));
                mailListItem.add(link);
            }
        };
        add(mails);
        if (displayMail != null) {
            add(new ReadMail("readMail", displayMail));
        } else {
            add(new Label("readMail", ""));
        }
    }
}
