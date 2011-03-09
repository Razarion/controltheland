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
import com.btxtech.game.services.cms.DbCmsHomeText;
import com.btxtech.game.wicket.uiservices.ListProvider;
import java.util.List;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 07.07.2010
 * Time: 21:23:15
 */
public class CmsEditor extends MgmtWebPage {
    @SpringBean
    private CmsService cmsService;

    public CmsEditor() {
        add(new FeedbackPanel("msgs"));
        createTextTable();
        createLayoutTable();
        add(new Form("activateForm") {

            @Override
            protected void onSubmit() {
                cmsService.activateHome();
            }
        });
    }

    private void createTextTable() {
        Form form = new Form("textForm");
        add(form);

        final ListProvider<DbCmsHomeText> textProvider = new ListProvider<DbCmsHomeText>() {
            @Override
            protected List<DbCmsHomeText> createList() {
                return cmsService.getDbCmsHomeTexts();
            }
        };
        form.add(new DataView<DbCmsHomeText>("textTable", textProvider) {
            @Override
            protected void populateItem(final Item<DbCmsHomeText> item) {
                item.add(new TextField<String>("internalName"));
                item.add(new CheckBox("isActive"));
                item.add(new Button("edit") {

                    @Override
                    public void onSubmit() {
                        setResponsePage(new CmsHomeTextEditor(item.getModelObject()));
                    }
                });
                item.add(new Button("delete") {

                    @Override
                    public void onSubmit() {
                        cmsService.removeDbCmsHomeText(item.getModelObject());
                    }
                });

            }
        });

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                cmsService.saveDbCmsHomeTexts(textProvider.getLastModifiedList());
            }
        });
        form.add(new Button("add") {

            @Override
            public void onSubmit() {
                cmsService.createDbCmsHomeText();
            }
        });
    }

    private void createLayoutTable() {
        Form form = new Form("layoutForm");
        add(form);

        final ListProvider<DbCmsHomeLayout> layoutProvider = new ListProvider<DbCmsHomeLayout>() {
            @Override
            protected List<DbCmsHomeLayout> createList() {
                return cmsService.getDbCmsHomeLayouts();
            }
        };
        form.add(new DataView<DbCmsHomeLayout>("layoutTable", layoutProvider) {
            @Override
            protected void populateItem(final Item<DbCmsHomeLayout> item) {
                item.add(new TextField<String>("internalName"));
                item.add(new CheckBox("isActive"));
                item.add(new Button("edit") {

                    @Override
                    public void onSubmit() {
                        setResponsePage(new CmsHomeLayoutEditor(item.getModelObject()));
                    }
                });
                item.add(new Button("delete") {

                    @Override
                    public void onSubmit() {
                        cmsService.removeDbCmsHomeLayout(item.getModelObject());
                    }
                });

            }
        });

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                cmsService.saveDbCmsHomeLayouts(layoutProvider.getLastModifiedList());
            }
        });
        form.add(new Button("add") {

            @Override
            public void onSubmit() {
                cmsService.createDbCmsHomeLayout();
            }
        });
    }

}
