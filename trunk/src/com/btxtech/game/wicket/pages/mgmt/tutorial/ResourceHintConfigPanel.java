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

package com.btxtech.game.wicket.pages.mgmt.tutorial;

import com.btxtech.game.services.tutorial.DbResourceHintConfig;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * User: beat
 * Date: 27.07.2010
 * Time: 22:48:35
 */
public class ResourceHintConfigPanel extends Panel {
    public ResourceHintConfigPanel(String id, final DbResourceHintConfig dbResourceHintConfig) {
        super(id);
        add(new TextField<Integer>("position.x"));
        add(new TextField<Integer>("position.y"));

        add(new FileUploadField("upload", new IModel<FileUpload>() {

            @Override
            public FileUpload getObject() {
                return null;
            }

            @Override
            public void setObject(FileUpload fileUpload) {
                dbResourceHintConfig.setData(fileUpload.getBytes());
                dbResourceHintConfig.setContentType(fileUpload.getContentType());
            }

            @Override
            public void detach() {
            }
        }));
    }
}
