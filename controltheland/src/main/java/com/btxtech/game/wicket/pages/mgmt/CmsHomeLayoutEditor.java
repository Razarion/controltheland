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

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.DbCmsHomeLayout;
import javax.swing.ImageIcon;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
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
    private CmsService cmsService;

    public CmsHomeLayoutEditor(final DbCmsHomeLayout cmsHomeLayout) {
        add(new FeedbackPanel("msgs"));
        Form<DbCmsHomeLayout> form = new Form<DbCmsHomeLayout>("form", new CompoundPropertyModel<DbCmsHomeLayout>(cmsHomeLayout)) {

            @Override
            protected void onSubmit() {
                cmsService.saveDbCmsHomeLayout(cmsHomeLayout);
                setResponsePage(CmsEditor.class);
            }
        };
        add(form);

        form.add(new TextField<String>("bodyBackgroundColor"));
        form.add(new FileUploadField("bgImage", new IModel<FileUpload>() {

            @Override
            public FileUpload getObject() {
                return null;
            }

            @Override
            public void setObject(FileUpload fileUpload) {
                ImageIcon image = new ImageIcon(fileUpload.getBytes());
                cmsHomeLayout.setBgImageWidth(image.getIconWidth());
                cmsHomeLayout.setBgImageHeight(image.getIconHeight());
                cmsHomeLayout.setBgImageContentType(fileUpload.getContentType());
                cmsHomeLayout.setBgImage(fileUpload.getBytes());
            }

            @Override
            public void detach() {
            }
        }));
        form.add(new TextField<String>("textColor"));
        form.add(new TextField<Integer>("textTop"));
        form.add(new TextField<Integer>("textLeft"));
        form.add(new TextField<Integer>("textRight"));
        form.add(new FileUploadField("startImage", new IModel<FileUpload>() {

            @Override
            public FileUpload getObject() {
                return null;
            }

            @Override
            public void setObject(FileUpload fileUpload) {
                cmsHomeLayout.setStartImage(fileUpload.getBytes());
                cmsHomeLayout.setStartImageContentType(fileUpload.getContentType());
            }

            @Override
            public void detach() {
                //Ignore
            }
        }));
        form.add(new TextField<Integer>("startLinkLeft"));
        form.add(new TextField<Integer>("startLinkTop"));
        form.add(new FileUploadField("infoImage", new IModel<FileUpload>() {

            @Override
            public FileUpload getObject() {
                return null;
            }

            @Override
            public void setObject(FileUpload fileUpload) {
                cmsHomeLayout.setInfoImage(fileUpload.getBytes());
                cmsHomeLayout.setInfoImageContentType(fileUpload.getContentType());
            }

            @Override
            public void detach() {
                //Ignore
            }
        }));
        form.add(new TextField<Integer>("infoLinkLeft"));
        form.add(new TextField<Integer>("infoLinkTop"));
    }
}
