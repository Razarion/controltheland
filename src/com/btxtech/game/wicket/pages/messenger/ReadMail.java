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
import java.text.SimpleDateFormat;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 02.04.2010
 * Time: 18:35:01
 */
public class ReadMail extends Panel {
    @SpringBean
    private MessengerService messengerService;

    public ReadMail(String id, final Mail mail) {
        super(id);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(WebCommon.DATE_TIME_FORMAT_STRING);
        add(new Label("date", simpleDateFormat.format(mail.getSent())));
        add(new Label("from", mail.getFromUser()));
        add(new Label("to", mail.getToUsers()));
        add(new Label("subject", mail.getSubject()));
        add(new Form("replayMail") {

            @Override
            protected void onSubmit() {
                setResponsePage(new WriteMail(mail));
            }
        });
        add(new Label("mailBody", mail.getBody()).setEscapeModelStrings(false));
        messengerService.setMailRead(mail);
    }
}
