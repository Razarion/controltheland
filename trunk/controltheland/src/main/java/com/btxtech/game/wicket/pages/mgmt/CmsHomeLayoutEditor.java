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

import com.btxtech.game.services.cms.DbCmsHomeLayout;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 10.07.2010
 * Time: 13:34:11
 */
public class CmsHomeLayoutEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbCmsHomeLayout> ruServiceHelper;

    public CmsHomeLayoutEditor(DbCmsHomeLayout cmsHomeLayout) {
        add(new FeedbackPanel("msgs"));
        final Form<DbCmsHomeLayout> form = new Form<DbCmsHomeLayout>("form", new CompoundPropertyModel<DbCmsHomeLayout>(new RuModel<DbCmsHomeLayout>(cmsHomeLayout, DbCmsHomeLayout.class) {

            @Override
            protected RuServiceHelper<DbCmsHomeLayout> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);
        form.add(new FileUploadField("bgImage", new IModel<FileUpload>() {

            @Override
            public FileUpload getObject() {
                return null;
            }

            @Override
            public void setObject(FileUpload fileUpload) {
                if (fileUpload == null) {
                    // I don't know why this is null
                    return;
                }
                form.getModelObject().setBgImageContentType(fileUpload.getContentType());
                form.getModelObject().setBgImage(fileUpload.getBytes());
            }

            @Override
            public void detach() {
            }
        }));
        form.add(new FileUploadField("startImage", new IModel<FileUpload>() {

            @Override
            public FileUpload getObject() {
                return null;
            }

            @Override
            public void setObject(FileUpload fileUpload) {
                if (fileUpload == null) {
                    // I don't know why this is null
                    return;
                }
                form.getModelObject().setStartImage(fileUpload.getBytes());
                form.getModelObject().setStartImageContentType(fileUpload.getContentType());
            }

            @Override
            public void detach() {
                //Ignore
            }
        }));
        form.add(new FileUploadField("infoImage", new IModel<FileUpload>() {

            @Override
            public FileUpload getObject() {
                return null;
            }

            @Override
            public void setObject(FileUpload fileUpload) {
                if (fileUpload == null) {
                    // I don't know why this is null
                    return;
                }
                form.getModelObject().setInfoImage(fileUpload.getBytes());
                form.getModelObject().setInfoImageContentType(fileUpload.getContentType());
            }

            @Override
            public void detach() {
                //Ignore
            }
        }));
        form.add(new FileUploadField("registerImage", new IModel<FileUpload>() {

            @Override
            public FileUpload getObject() {
                return null;
            }

            @Override
            public void setObject(FileUpload fileUpload) {
                if (fileUpload == null) {
                    // I don't know why this is null
                    return;
                }
                form.getModelObject().setRegisterImage(fileUpload.getBytes());
                form.getModelObject().setRegisterImageContentType(fileUpload.getContentType());
            }

            @Override
            public void detach() {
                //Ignore
            }
        }));
        form.add(new FileUploadField("borderImage", new IModel<FileUpload>() {

            @Override
            public FileUpload getObject() {
                return null;
            }

            @Override
            public void setObject(FileUpload fileUpload) {
                if (fileUpload == null) {
                    // I don't know why this is null
                    return;
                }                
                form.getModelObject().setBorderImage(fileUpload.getBytes());
                form.getModelObject().setBorderImageContentType(fileUpload.getContentType());
            }

            @Override
            public void detach() {
                //Ignore
            }
        }));
        form.add(new TextArea("cssString"));

        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });

        form.add(new Button("back") {
            @Override
            public void onSubmit() {
                setResponsePage(CmsEditor.class);
            }
        });

    }
}
