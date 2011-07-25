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

import com.btxtech.game.services.messenger.DbMail;
import com.btxtech.game.services.messenger.InvalidFieldException;
import com.btxtech.game.services.messenger.MessengerService;
import com.btxtech.game.wicket.pages.basepage.BasePage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.settings.TinyMCESettings;

/**
 * User: beat
 * Date: 02.04.2010
 * Time: 15:31:24
 */
public class WriteMail extends BasePage {
    public static final String RE = "RE: ";
    @SpringBean
    private MessengerService messengerService;
    private Model<String> toModel = new Model<String>();
    private Model<String> subjectModel = new Model<String>();
    private Model<String> bodyModel = new Model<String>();

    public WriteMail() {
        this(null);
    }

    public WriteMail(final DbMail replayDbMail) {
        add(new FeedbackPanel("msgs"));
        if (replayDbMail != null) {
            toModel.setObject(replayDbMail.getFromUser());
            subjectModel.setObject(RE + replayDbMail.getSubject());
            bodyModel.setObject(replayDbMail.getBody());
        }

        Form form = new Form("form") {
            @Override
            protected void onSubmit() {
                try {
                    messengerService.sendMail(toModel.getObject(), subjectModel.getObject(), bodyModel.getObject());
                    setResponsePage(new Messenger());
                } catch (InvalidFieldException e) {
                    error(e.getMessage());
                }
            }
        };
        add(form);
        TextField<String> to = new TextField<String>("to", toModel);
        form.add(to);

        TextField<String> subject = new TextField<String>("subject", subjectModel);
        form.add(subject);

        TextArea<String> body = new TextArea<String>("body", bodyModel);
        TinyMCESettings tinyMCESettings = new TinyMCESettings();
        body.add(new TinyMceBehavior(tinyMCESettings));
        form.add(body);
    }

}
